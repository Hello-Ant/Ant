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

package com.ant.leetcode.tree

import java.util.*

class BinaryTreeTraversal {
    val root = TreeNode(0, null, null)

    fun <T> preorderTraversalWithRecursion(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        print("${root.value} ")
        preorderTraversalWithRecursion(root.left)
        preorderTraversalWithRecursion(root.right)
    }

    fun <T> preorderTraversalWithStack(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        var pNode = root
        val stack = Stack<TreeNode<T>>()
        while (pNode != null || stack.isNotEmpty()) {
            pNode = if (pNode != null) {
                print("${pNode.value} ")
                stack.push(pNode)
                root.left
            } else {
                val node = stack.pop()
                node.right
            }
        }
    }

    fun <T> inorderTraversalWithRecursion(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        inorderTraversalWithRecursion(root.left)
        print("${root.value} ")
        inorderTraversalWithRecursion(root.right)
    }

    fun <T> inorderTraversalWithStack(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        val stack = Stack<TreeNode<T>>()
        var pNode = root
        while (pNode != null || stack.isNotEmpty()) {
            pNode = if (pNode != null) {
                stack.push(pNode)
                pNode.left
            } else {
                val node = stack.pop()
                print("${node.value} ")
                node.right
            }
        }
    }

    fun <T> postorderTraversalWithRecursion(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        postorderTraversalWithRecursion(root.left)
        postorderTraversalWithRecursion(root.right)
        print("${root.value} ")
    }

    fun <T> postorderTraversalWithStack(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        val stack = Stack<TreeNode<T>>()
        var pNode = root
        var pre: TreeNode<T>? = null
        while (pNode != null || stack.isNotEmpty()) {
            if (pNode != null) {
                stack.push(pNode)
                pNode = pNode.left
            } else {
                val node = stack.peek()
                val right = node.right
                if (right == null || right == pre) {
                    print("${node.value} ")
                    stack.pop()
                    pre = node
                } else {
                    stack.push(right)
                    pNode = right.left
                }
            }
        }
    }

    /**
     * 层次遍历
     *          1
     *        /   \
     *       2     3
     *      / \   / \
     *     4   5 6   7
     *
     * result: 1 2 3 4 5 6 7
     */
    fun <T> levelTraversal(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        val queue = LinkedList<TreeNode<T>>()
        queue.offer(root)
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            print("${node.value} ")
            if (node.left != null) {
                queue.offer(node.left)
            }
            if (node.right != null) {
                queue.offer(node.right)
            }
        }
    }

    /**
     * 深度优先遍历 == 前序遍历
     */
    fun <T> depthFirstTraversal(root: TreeNode<T>?) {
        if (root == null) {
            return
        }
        val stack = LinkedList<TreeNode<T>>()
        stack.push(root)
        while (stack.isNotEmpty()) {
            val node = stack.pop()
            print("${node.value} ")
            if (node.right != null) {
                stack.push(node.right)
            }
            if (node.left != null) {
                stack.push(node.left)
            }
        }
    }
}
