ActivityThread  --  main
-----

**应用进程启动之后的入口函数，初始化上下文，内容提供者，并开启主线程消息循环**

###### 1.创建Looper

Looper.prerapareMianLooper()

###### 2.新建ActivityThread对象，执行attach

thread.attach(false,startSeq)

ActivityManagerService.attachApplication(mAppThread,startSeq) - 主线程挂起等待服务端执行完成
ActivityManagerService extends IActivityManager.Stub - 服务端的binder服务，用于客户端跨进程调用至服务端
mAppThread：ApplicationThread extends IApplicationThread.Stub - 客户端的binder服务，用于服务端回调客户端

移除进程启动超时消息 -- 10s超时
***延迟发送发布内容提供者超时消息 -- 10s超时***
mAppThread.bindApplication -- 跨进程调用客户端的绑定上下文 - 服务端binder线程挂起等待客户端执行完成
发送H.BIND_APPLICATION消息 -- 切换至主线程进行上下文的绑定操作

 - `此时并未开启消息处理，是如何执行的？难道是等执行了第三步开启消息循环再执行该消息？`

ActivityThread.handleBindApplication
LoadedApk.makeApplication
创建ContextImpl -- ContextImpl.createAppContext
创建Application   -- Instrumentation.newApplication
更加类名反射创建Application对象，并调用Application的attach(ContextImpl)方法，ContextWrapper.attachBaseContext(Context)
ContextImpl.setOutContext(Application)

##### 初始化内容提供者，installContentProviders

遍历ContentProvider列表，逐个执行installProvider
根据类名，反射创建ContentProvider对象，执行attachInfo方法，该方法只会执行一次，最后执行onCreate方法
发布内容提供者，ActivityManagerService.publishContentProviders
***移除内容提供者发布超时消息***

执行Application的onCreate方法，Instrumentation.callApplicationOnCreate(Application)

###### 3.开始循环处理消息

Looper.loop()