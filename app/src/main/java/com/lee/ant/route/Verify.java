package com.lee.ant.route;

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * 验证
 * <p>
 *
 * @author Ant
 * @date on  2020/9/24 16:32.
 */
public interface Verify {

    /**
     * 判断是否已经验证通过 如果有耗时操作需要开启子线程执行
     */
    boolean isVerified();

    /**
     * 执行验证
     */
    void invoke();
}
