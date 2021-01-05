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

package com.ant.lib_rx.internal.operators.observable

import com.ant.lib_rx.Observable
import com.ant.lib_rx.ObservableSource
import com.ant.lib_rx.internal.fuseable.HasUpstreamObservableSource

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/12 16:15.
 */
abstract class AbstractObservableWithUpstream<T, U>(protected val source: ObservableSource<T>) :
    Observable<U>(), HasUpstreamObservableSource<T> {
    override fun source(): ObservableSource<T> {
        return source
    }
}