/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.lee.ant.aidl

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteCallbackList
import android.util.Log
import com.lee.ant.IBookManager
import com.lee.ant.NewBookObserver
import java.util.concurrent.CopyOnWriteArrayList

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2021/1/7 10:54.
 */
class BookManagerService : Service() {

    companion object {
        private const val TAG = "BookManagerService"

        private const val ACCESS_BOOK_MANAGER_SERVICE =
            "com.lee.ant.permission.ACCESS_BOOK_MANAGER_SERVICE"

        private const val PACKAGE_NAME_PREFIX = "com.lee.ant"
    }

    private val mBooks by lazy {
        CopyOnWriteArrayList<Book>()
    }

    private val mNewBookObservers by lazy {
        RemoteCallbackList<NewBookObserver>()
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "onBind: $intent")
        // 验证权限方式1 这只能检测自己是否有权限，不能检测其他应用访问时是否具有权限
        /*checkCallingOrSelfPermission(ACCESS_BOOK_MANAGER_SERVICE)
            .takeIf {
                it == PackageManager.PERMISSION_DENIED
            }?.let {
                Log.e(TAG, "onBind: permission denied")
                return null
            } ?:*/ return mBinder
    }

    private fun findIndexById(id: Long): Int {
        mBooks.forEachIndexed { index, book ->
            if (book.id == id) return index
        }
        return -1
    }

    private fun dispatchNewBook(book: Book) {
        Log.e(TAG, "dispatchNewBook: ${Thread.currentThread()}")
        val count = mNewBookObservers.beginBroadcast()
        try {
            for (i in 0 until count) {
                // TODO：不做安全保护，测试是否会崩溃
                mNewBookObservers.getBroadcastItem(i).onNewBookArrived(book)
            }
        } finally {
            mNewBookObservers.finishBroadcast()
        }
    }

    private val mBinder = object : IBookManager.Stub() {

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            Log.e(TAG, "onTransact: ${Thread.currentThread()}")
            // 验证权限方式2-1，检测权限
            checkCallingOrSelfPermission(ACCESS_BOOK_MANAGER_SERVICE)
                .takeIf {
                    it == PackageManager.PERMISSION_DENIED
                }?.let {
                    Log.e(TAG, "onBind: permission denied")
                    return false
                }
            // 验证权限方式2-2，验证包名
            baseContext.packageManager
                .getPackagesForUid(getCallingUid())?.takeIf {
                    it.isNotEmpty()
                }?.takeUnless {
                    it[0].startsWith(PACKAGE_NAME_PREFIX)
                }?.let {
                    Log.e(TAG, "onTransact: package name not invalid")
                    return false
                }

            return super.onTransact(code, data, reply, flags)
        }

        override fun addBook(book: Book?) {
            Log.e(TAG, "addBook: ${Thread.currentThread()}")
            Thread.sleep(1000L)
            if (book != null) {
                mBooks.add(book)
                dispatchNewBook(book)
            }
        }

        override fun removeBookById(id: Long) {
            Log.e(TAG, "removeBookById: ${Thread.currentThread()}")
            Thread.sleep(1000L)
            val index = findIndexById(id)
            if (index >= 0) mBooks.removeAt(index)
        }

        override fun getAllBook(): MutableList<Book> {
            Log.e(TAG, "getAllBook: ${Thread.currentThread()}")
            Thread.sleep(1000L)
            return mBooks
        }

        override fun addNewBookObserver(observer: NewBookObserver?) {
            Log.e(TAG, "addNewBookObserver: ${Thread.currentThread()}")
            if (observer != null) {
                val result = mNewBookObservers.register(observer)
                Log.e(TAG, "addNewBookObserver: $result")
            }
        }

        override fun removeNewBookObserver(observer: NewBookObserver?) {
            Log.e(TAG, "removeNewBookObserver: ${Thread.currentThread()}")
            if (observer != null) {
                val result = mNewBookObservers.unregister(observer)
                Log.e(TAG, "removeNewBookObserver: $result")
            }
        }

    }
}