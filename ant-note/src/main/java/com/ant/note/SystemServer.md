**SystemServer**

SystemServer的启动可以分为两个步骤：
* Zygote孵化SystemServer子进程
* 启动服务

*1.SystemServer创建过程*

ZygoteInit.java

main()

--forkSystemServer:Runnable 初始化启动参数(进程ID与组ID '1000'，执行类 '全类名')

----Zygote.forkSystemServer->nativeForkSystemServer

native层启动SystemServer，如果失败，Zygote退出重启(RuntimeAbort)；同时设置SIGCHID信号监听，判断当前crash的进程是否是SystemServer进程，如果是，杀死Zygote进程进行重启 kill(getpid(), SIGKILL)

`// For child process
if (pid == 0) {
    if (hasSecondZygote(abiList)) {
        waitForSecondaryZygote(socketName);
    zygoteServer.closeServerSocket();
    return handleSystemServerProcess(parsedArgs);
}`

// TODO(hasSecondZygote)

孵化进程之后，关闭Socket连接；执行 handleSystemServerProcess:

* 设置文件访问权限，System创建的文件只有文件创建者SystemServer进程可以访问
* 修改进程名称
* 初始化ClassLoader
* ZygoteInit.zygoteInit()：

RuntimeInit.commonInit();在这里系统设置了默认的DefaultUncaughtExceptionHandler

`// set handlers; these apply to all threads in the VM. Apps can replace the default handler, but not the pre handler.
LoggingHandler loggingHandler = new LoggingHandler();
RuntimeHooks.setUncaughtExceptionPreHandler(loggingHandler);
Thread.setDefaultUncaughtExceptionHandler(new KillApplicationHandler(loggingHandler));`

RuntimeInit.applicationInit()->findStaticMain() 查找类里面的main方法，然后执行；初始化启动参数的时候设置了SystemServer的全类名

`Class<?> cl = Class.forName(className, true, classLoader);
Method m = cl.getMethod("main", new Class[] { String[].class } )`

*SystemServer.main()*

* 校验时间，小于1970则将时间设置为1970

System.currentTimeMills(); SystemLock.setCurrentTimeMillis()

* 设置虚拟机堆内存利用率 VMRuntime.getRuntime().setTargetHeapUtilization(0.8f)
* 设置SystemServer进程中的binder最大线程数 BinderInternal.setMaxThreads(sMaxBinderThreads); private static final int sMaxBinderThreads = 31
* 初始化MainLooper Looper.prepareMainLooper() 即当前线程
* 初始化native层SystemServer System.loadLibrary("system_servers")
* 初始化System context createSystemContext()；系统的上下文也是apk的上下文，系统的上下文即framework-res.apk的上下文

>ActivityThread.systemMain().getSystemContext() -> ContextImpl.createSystemContext(ActivityThread mainThread) 获取到上下文，然后设置系统默认主题；

ContextImpl.createSystemContext(ActivityThread mainThread)：

LoadedApk(mainThread)，包名：“android”，LoadedApk保存的是一个apk文件的信息，framework-res.apk的包名就是“android”；所以创建系统的上下文即为创建framework-res.apk的上下文

ActivityThread.systemMain().getSystemUiContext()

* 创建SystemServerManager SystemServerManager(mSystemContext)，将服务添加至LocalServices，LocalServices.addService()
* 启动用于初始化一些列Java服务的线程池 SystemServerInitThreadPool.get()；准备线程池，使服务的初始化可以并行执行
* 启动服务

startBootstrapServices();

startCoreServices();

startOtherServices();

SystemServerInitThreadPool.shutdown();




