/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ant.lib_rx.internal.queue

import com.ant.lib_rx.internal.fuseable.SimplePlainQueue
import com.ant.lib_rx.internal.util.Pow2
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/20 16:27.
 */
class SpscLinkedArrayQueue<T>(bufferSize: Int) : SimplePlainQueue<T> {
    val producerIndex = AtomicLong()
    var producerLookAheadStep = 0
    var producerLookAhead: Long
    val producerMask: Int
    var producerBuffer: AtomicReferenceArray<Any>
    val consumerMask: Int
    var consumerBuffer: AtomicReferenceArray<Any>
    val consumerIndex = AtomicLong()

    /**
     * {@inheritDoc}
     *
     *
     * This implementation is correct for single producer thread use only.
     */
    override fun offer(e: T): Boolean {
        if (null == e) {
            throw NullPointerException("Null is not a valid element")
        }
        // local load of field to avoid repeated loads after volatile reads
        val buffer = producerBuffer
        val index = lpProducerIndex()
        val mask = producerMask
        val offset = calcWrappedOffset(index, mask)
        return if (index < producerLookAhead) {
            writeToQueue(buffer, e, index, offset)
        } else {
            val lookAheadStep = producerLookAheadStep
            // go around the buffer or resize if full (unless we hit max capacity)
            val lookAheadElementOffset = calcWrappedOffset(index + lookAheadStep, mask)
            when {
                null == lvElement(buffer, lookAheadElementOffset) -> { // LoadLoad
                    producerLookAhead = index + lookAheadStep - 1 // joy, there's plenty of room
                    writeToQueue(buffer, e, index, offset)
                }
                null == lvElement(buffer, calcWrappedOffset(index + 1, mask)) -> {
                    // buffer is not full
                    writeToQueue(buffer, e, index, offset)
                }
                else -> {
                    // add a buffer and link old to new
                    resize(buffer, index, offset, e, mask.toLong())
                    true
                }
            }
        }
    }

    private fun writeToQueue(
        buffer: AtomicReferenceArray<Any>,
        e: T,
        index: Long,
        offset: Int
    ): Boolean {
        soElement(buffer, offset, e) // StoreStore
        soProducerIndex(index + 1) // this ensures atomic write of long on 32bit platforms
        return true
    }

    private fun resize(
        oldBuffer: AtomicReferenceArray<Any>,
        currIndex: Long,
        offset: Int,
        e: T,
        mask: Long
    ) {
        val capacity = oldBuffer.length()
        val newBuffer = AtomicReferenceArray<Any>(capacity)
        producerBuffer = newBuffer
        producerLookAhead = currIndex + mask - 1
        soElement(newBuffer, offset, e) // StoreStore
        soNext(oldBuffer, newBuffer)
        soElement(oldBuffer, offset, HAS_NEXT) // new buffer is visible after element is
        // inserted
        soProducerIndex(currIndex + 1) // this ensures correctness on 32bit platforms
    }

    private fun soNext(curr: AtomicReferenceArray<Any>, next: AtomicReferenceArray<Any>) {
        soElement(curr, calcDirectOffset(curr.length() - 1), next)
    }

    private fun lvNextBufferAndUnlink(
        curr: AtomicReferenceArray<Any>,
        nextIndex: Int
    ): AtomicReferenceArray<Any> {
        val nextOffset = calcDirectOffset(nextIndex)
        val nextBuffer = lvElement(curr, nextOffset) as AtomicReferenceArray<Any>
        soElement(curr, nextOffset, null) // Avoid GC nepotism
        return nextBuffer
    }

    /**
     * {@inheritDoc}
     *
     *
     * This implementation is correct for single consumer thread use only.
     */
    override fun poll(): T? {
        // local load of field to avoid repeated loads after volatile reads
        val buffer = consumerBuffer
        val index = lpConsumerIndex()
        val mask = consumerMask
        val offset = calcWrappedOffset(index, mask)
        val e = lvElement(buffer, offset) // LoadLoad
        val isNextBuffer = e === HAS_NEXT
        if (null != e && !isNextBuffer) {
            soElement(buffer, offset, null) // StoreStore
            soConsumerIndex(index + 1) // this ensures correctness on 32bit platforms
            return e as T
        } else if (isNextBuffer) {
            return newBufferPoll(lvNextBufferAndUnlink(buffer, mask + 1), index, mask)
        }
        return null
    }

    private fun newBufferPoll(nextBuffer: AtomicReferenceArray<Any>, index: Long, mask: Int): T? {
        consumerBuffer = nextBuffer
        val offsetInNew = calcWrappedOffset(index, mask)
        val n = lvElement(nextBuffer, offsetInNew) as T? // LoadLoad
        if (null != n) {
            soElement(nextBuffer, offsetInNew, null) // StoreStore
            soConsumerIndex(index + 1) // this ensures correctness on 32bit platforms
        }
        return n
    }

    fun peek(): T? {
        val buffer = consumerBuffer
        val index = lpConsumerIndex()
        val mask = consumerMask
        val offset = calcWrappedOffset(index, mask)
        val e = lvElement(buffer, offset) // LoadLoad
        return if (e === HAS_NEXT) {
            newBufferPeek(lvNextBufferAndUnlink(buffer, mask + 1), index, mask)
        } else e as T?
    }

    private fun newBufferPeek(nextBuffer: AtomicReferenceArray<Any>, index: Long, mask: Int): T? {
        consumerBuffer = nextBuffer
        val offsetInNew = calcWrappedOffset(index, mask)
        return lvElement(nextBuffer, offsetInNew) as T? // LoadLoad
    }

    override fun clear() {
        while (poll() != null || !isEmpty()) {
        } // NOPMD
    }

    fun size(): Int {
        /*
         * It is possible for a thread to be interrupted or reschedule between the read of the producer and
         * consumer indices, therefore protection is required to ensure size is within valid range. In the
         * event of concurrent polls/offers to this method the size is OVER estimated as we read consumer
         * index BEFORE the producer index.
         */
        var after = lvConsumerIndex()
        while (true) {
            val before = after
            val currentProducerIndex = lvProducerIndex()
            after = lvConsumerIndex()
            if (before == after) {
                return (currentProducerIndex - after).toInt()
            }
        }
    }

    override fun isEmpty(): Boolean {
        return lvProducerIndex() == lvConsumerIndex()
    }

    private fun adjustLookAheadStep(capacity: Int) {
        producerLookAheadStep = (capacity / 4).coerceAtMost(MAX_LOOK_AHEAD_STEP)
    }

    private fun lvProducerIndex(): Long {
        return producerIndex.get()
    }

    private fun lvConsumerIndex(): Long {
        return consumerIndex.get()
    }

    private fun lpProducerIndex(): Long {
        return producerIndex.get()
    }

    private fun lpConsumerIndex(): Long {
        return consumerIndex.get()
    }

    private fun soProducerIndex(v: Long) {
        producerIndex.lazySet(v)
    }

    private fun soConsumerIndex(v: Long) {
        consumerIndex.lazySet(v)
    }

    /**
     * Offer two elements at the same time.
     *
     * Don't use the regular offer() with this at all!
     * @param first the first value, not null
     * @param second the second value, not null
     * @return true if the queue accepted the two new values
     */
    override fun offer(first: T, second: T): Boolean {
        val buffer = producerBuffer
        val p = lvProducerIndex()
        val m = producerMask
        var pi = calcWrappedOffset(p + 2, m)
        if (null == lvElement(buffer, pi)) {
            pi = calcWrappedOffset(p, m)
            soElement(buffer, pi + 1, second)
            soElement(buffer, pi, first)
            soProducerIndex(p + 2)
        } else {
            val capacity = buffer.length()
            val newBuffer = AtomicReferenceArray<Any>(capacity)
            producerBuffer = newBuffer
            pi = calcWrappedOffset(p, m)
            soElement(newBuffer, pi + 1, second) // StoreStore
            soElement(newBuffer, pi, first)
            soNext(buffer, newBuffer)
            soElement(buffer, pi, HAS_NEXT) // new buffer is visible after element is
            soProducerIndex(p + 2) // this ensures correctness on 32bit platforms
        }
        return true
    }

    companion object {
        val MAX_LOOK_AHEAD_STEP: Int = Integer.getInteger("jctools.spsc.max.lookahead.step", 4096)
        private val HAS_NEXT = Any()
        private fun calcWrappedOffset(index: Long, mask: Int): Int {
            return calcDirectOffset(index.toInt() and mask)
        }

        private fun calcDirectOffset(index: Int): Int {
            return index
        }

        private fun soElement(buffer: AtomicReferenceArray<Any>, offset: Int, e: Any?) {
            buffer.lazySet(offset, e)
        }

        private fun lvElement(buffer: AtomicReferenceArray<Any>, offset: Int): Any? {
            return buffer[offset]
        }
    }

    init {
        val p2capacity: Int = Pow2.roundToPowerOfTwo(8.coerceAtLeast(bufferSize))
        val mask = p2capacity - 1
        val buffer = AtomicReferenceArray<Any>(p2capacity + 1)
        producerBuffer = buffer
        producerMask = mask
        adjustLookAheadStep(p2capacity)
        consumerBuffer = buffer
        consumerMask = mask
        producerLookAhead = mask - 1.toLong() // we know it's all empty to start with
        soProducerIndex(0L)
    }
}
