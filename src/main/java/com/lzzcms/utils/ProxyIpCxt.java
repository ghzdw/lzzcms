package com.lzzcms.utils;



/*static确保了threadLocal总是一个对象(threadLocal的hashcode一样),即threadlocalmap(key,value)的key是一样的
 * 点了一个请求，就是一个线程,如http-8080-6,当前后两次使用了相同的线程名如http-8080-6时，这就决定了threadlocalmap(=getMap(t))是一样的,
 * 这时取到的PagableUtil的值也就是getPagableUtil()得到的值(实质是threadlocalmap.get(key))也就一样了。
 * threadlocalmap=getMap(t)==>一个线程对应一个threadlocalmap
 * 静态的threadLocal对象==>threadlocalmap.get(threadLocal.hashcode)得到的值是一致的,一个对象是一个entry
 */
public class ProxyIpCxt {
	private static  ThreadLocal<String> threadLocal=new ThreadLocal<String>();
	
	public static void setIp(String ip){
		threadLocal.set(ip);
	}
	public static String getIp(){
		return threadLocal.get();
	}
	public static void removeIp(){
		threadLocal.remove();
	}
}
