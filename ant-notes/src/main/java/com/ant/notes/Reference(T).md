Reference<T>
--

构造方法：
Reference(T referent)
Reference(T referent, ReferenceQueue<? super T> queue)

**对象T被gc之后，引用对象自己，即Reference<T>会被添加至队列queue；**
LeakCanary就是利用这个特性，配合WeakReference，监听对象的生命周期，实现内存泄漏检测；
Glide的内存缓存，也有使用到该特性；内存缓存使用的是WeakReference，配合MessageQueue.IdleHandler，如果缓存被释放，则移除缓存Map里的WeakReference资源

例如：
回调Activity的onDestroy时，先用Reference包裹对象，并设置队列，延迟检测队列是否有该对象的Reference对象，没有则主动触发gc一次，再次检测，如果有则移除监测列表里的该对象，否则提示内存泄漏。

FinalReference：强引用
SoftReference：软引用，内存不足时才会被释放；
WeakReference：弱引用，gc时就会被释放；
PhantomReference：虚引用