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



package com.lee.ant

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * <p>------------------------------------------------------
 * <p>Copyright (C) 2020 wasu company, All rights reserved.
 * <p>------------------------------------------------------
 * <p>
 * <p>
 *
 * @author Ant
 * @date on  2020/11/19 15:21.
 */


class TestAidlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        testContentProvider()
//        testBindService(conn)
    }

    private fun testContentProvider() {
        val cursor = contentResolver.query(
            Uri.parse("content://" + "com.wasu.loginProvider" + "/" + "check_token_path"),
            null,
            null,
            null,
            null
        )
        if (cursor == null || !cursor.moveToNext()) {
            Log.e("Ant", "testContentProvider: cursor == null")
            return
        }
        val index = cursor.getColumnIndex("result")
        if (index >= 0) {
            val r = cursor.getString(index)
            Log.e("Ant", "testContentProvider: $r")
        }
        cursor.close()
    }

    override fun onResume() {
        super.onResume()
        /*mUnionLogin?.setCallback(mCallback)
        mUnionLogin?.onLoginResult(true)*/
    }

    override fun onDestroy() {
        super.onDestroy()
//        unbindService(conn)
    }

    /*private var mUnionLogin: UnionLogin? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mUnionLogin = UnionLogin.Stub.asInterface(service)
            Log.e(
                "SyncAccountService",
                "onServiceConnected: thread: ${Thread.currentThread()}"
            )
            try {
                mUnionLogin?.setCallback(mCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
                Log.e("SyncAccountService", "onServiceConnected: ${e.message}")
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mUnionLogin = null
        }
    }*/

    private fun testBindService(conn: ServiceConnection) {
        val intent = Intent("com.youku.passport.union")
        intent.setPackage("com.wasu.launcher")
        bindService(intent, conn, BIND_AUTO_CREATE)
    }

    /*private val mCallback: IUnionDataCallback = object : IUnionDataCallback.Stub() {
        @Throws(RemoteException::class)
        override fun onKumiaoData(params: MutableMap<Any?, Any?>) {
             Log.e(
                "SyncAccountService",
                "onKumiaoData: ${params["checkYtid"]} thread: ${Thread.currentThread()}"
            )
        }
    }*/
}
