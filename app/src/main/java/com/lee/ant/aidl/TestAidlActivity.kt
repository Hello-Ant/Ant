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

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lee.ant.IBookManager
import com.lee.ant.NewBookObserver
import com.lee.ant.R
import kotlinx.android.synthetic.main.app_act_aidl.*
import java.util.*

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

    companion object {
        private const val TAG = "TestAidlActivity"
    }

    private var mBookManager: IBookManager? = null

    private val mRandom by lazy { Random() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_act_aidl)

        btn_add_book.setOnClickListener {
            mBookManager?.let {
                val book = Book()
                val id = mRandom.nextLong()
                book.id = id
                book.name = "Book$id"
                book.author = "Author$id"

                it.addBook(book)
            }
        }
        btn_fetch_books.setOnClickListener {
            val books = mBookManager?.allBook
            Log.e(TAG, "fetch books: ${books?.size} \nThread: ${Thread.currentThread()}")
        }
        testBindService(mConnection)
    }

    private fun testBindService(conn: ServiceConnection) {
        val intent = Intent("com.lee.ant.action.BOOK_MANAGER_SERVICE")
        intent.setPackage("com.lee.ant")
        bindService(intent, conn, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        releaseBookService()
        super.onDestroy()
    }

    private fun releaseBookService() {
        // 取消订阅
        try {
            mBookManager?.takeIf {
                it.asBinder().isBinderAlive
            }?.removeNewBookObserver(mNewBookObserver)
        } catch (e: RemoteException) {
            Log.e(TAG, "releaseBookService: ${e.message}")
        }
        // 解绑服务
        unbindService(mConnection)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected: $name \nservice : $service")
            mBookManager = IBookManager.Stub.asInterface(service)

            try {
                // 注册订阅者
                mBookManager?.addNewBookObserver(mNewBookObserver)
                // 设置死亡代理
                mBookManager?.asBinder()?.linkToDeath(mDeathRecipient, 0)
            } catch (e: RemoteException) {
                Log.e(TAG, "onServiceConnected: ${e.message}")
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e(TAG, "onServiceDisconnected: $name \nThread : ${Thread.currentThread()}")
            mBookManager = null
        }
    }

    private val mNewBookObserver = object : NewBookObserver.Stub() {

        @Throws(RemoteException::class)
        override fun onNewBookArrived(book: Book) {
            Log.e(TAG, "onNewBookArrived: $book \nthread: ${Thread.currentThread()}")
        }
    }

    private val mDeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.e(TAG, "binderDied: ${Thread.currentThread()}")
            // 移除死亡代理
            mBookManager?.asBinder()?.unlinkToDeath(this, 0)
            mBookManager = null
        }

    }
}
