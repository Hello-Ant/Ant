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

package com.ant.leetcode.recursion

import com.ant.leetcode.tree.TreeNode

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on 2020/5/12 16:33.
 */
fun main() {
    Recursion.hanoi(2, 'A', 'C', 'B')
}

object Recursion {

    /**
     * A 移至 C，B 临时使用
     * 结束条件：N==1 最后一块，直接移入目标位置
     * N-1 从 A 移至 B
     * N 从 A 移至 C
     * N-1 从 B 移至 C
     */
    fun hanoi(n: Int, from: Char, to: Char, temp: Char) {
        if (n == 1) {
            println("$n $from -> $to")
            return
        }
        hanoi(n - 1, from, temp, to)
        println("$n $from -> $to")
        hanoi(n - 1, temp, to, from)
    }

    /**
     * 根节点 + 左子树 + 右子树
     */
    fun <T> binaryTreeNodeCount(root: TreeNode<T>?): Int {
        if (root == null) {
            return 0
        }
        return binaryTreeNodeCount(root.left) + binaryTreeNodeCount(root.right) + 1
    }
}