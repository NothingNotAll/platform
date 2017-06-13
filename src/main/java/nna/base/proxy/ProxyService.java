package nna.base.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import nna.base.log.Log;
import nna.base.protocol.dispatch.AppUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class ProxyService implements InvocationHandler,MethodInterceptor {

	private Logic[] bLogics;
	private Logic[] aLogics;
	private Logic[] eLogics;
	private Logic[] fLogics;
	
	public Logic[] getbLogics() {
		return bLogics;
	}

	public Logic[] getaLogics() {
		return aLogics;
	}

	public Logic[] geteLogics() {
		return eLogics;
	}

	public Logic[] getfLogics() {
		return fLogics;
	}
	private Object beenProxyObject;
	private Method beenProxyMethod;
	
	private Object executeLogics(Logic[] logics,Object[] args){
		Object returnObject=null;
		for(Logic logic:logics){
			try {
				returnObject=logic.getMethodOfLogic().invoke(logic.getObjectOfLogic(), args);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return returnObject;
	}
	
	public void proxy(Object... args){
		Object[] argsOfProxyMethod=new Object[4];
		argsOfProxyMethod[0]=beenProxyObject;
		argsOfProxyMethod[1]=beenProxyMethod;
		argsOfProxyMethod[2]=args;
		executeLogics(bLogics,argsOfProxyMethod);
		try {
			argsOfProxyMethod[3]=beenProxyMethod.invoke(beenProxyObject, args);
			executeLogics(aLogics, argsOfProxyMethod);
		} catch (Exception e) {
			e.printStackTrace();
			executeLogics(eLogics, argsOfProxyMethod);
		}finally {
			executeLogics(fLogics, argsOfProxyMethod);
		}
	}

	public Object invoke(Object arg0, Method arg1, Object[] arg2) {
		Object[] argsOfProxyMethod=new Object[4];
		argsOfProxyMethod[0]=beenProxyObject;
		argsOfProxyMethod[1]=arg1;
		argsOfProxyMethod[2]=arg2;
		Object isGoOnExe=executeLogics(bLogics,argsOfProxyMethod);
		if (isGoOnExe!=null&&!(Boolean)isGoOnExe) {
			return null;
		}
		try {
			argsOfProxyMethod[3]=arg1.invoke(beenProxyObject, arg2);
			executeLogics(aLogics, argsOfProxyMethod);
		} catch (Exception e) {
			e.printStackTrace();
			logError(e);
			executeLogics(eLogics, argsOfProxyMethod);
		}catch (Throwable e) {
			e.printStackTrace();
			logError(e.getStackTrace());
			executeLogics(eLogics, argsOfProxyMethod);
		}finally {
			executeLogics(fLogics, argsOfProxyMethod);
		}
		return null;
	}

	private void logError(Exception e) {
		Log erroLog= AppUtil.getLog();
		StackTraceElement[] stes= e.getStackTrace();
		logError(e.getStackTrace());
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
		Object[] argsOfProxyMethod=new Object[4];
		argsOfProxyMethod[0]=beenProxyObject;
		argsOfProxyMethod[1]=method;
		argsOfProxyMethod[2]=args;
		executeLogics(bLogics,argsOfProxyMethod);
		try {
			argsOfProxyMethod[3]=proxy.invokeSuper(obj, args);
			executeLogics(aLogics, argsOfProxyMethod);
		} catch (Exception e) {
			e.printStackTrace();
			logError(e);
			executeLogics(eLogics, argsOfProxyMethod);
		} catch (Throwable e) {
			e.printStackTrace();
			logError(e.getStackTrace());
		}finally {
			executeLogics(fLogics, argsOfProxyMethod);
		}
		return null;
	}

	private void logError(StackTraceElement[] stackTrace) {
		Log erroLog= AppUtil.getLog();
		for(StackTraceElement st:stackTrace){
			erroLog.log(st.getClassName()+"-"+st.getMethodName()+"-"+st.getLineNumber(),Log.ERROR);
		}
	}

	public void setBeenProxyObject(Object beenProxyObject) {
		this.beenProxyObject = beenProxyObject;
	}

	public void setbLogics(Logic[] bLogics) {
		this.bLogics = bLogics;
	}

	public void setaLogics(Logic[] aLogics) {
		this.aLogics = aLogics;
	}

	public void seteLogics(Logic[] eLogics) {
		this.eLogics = eLogics;
	}

	public void setfLogics(Logic[] fLogics) {
		this.fLogics = fLogics;
	}

	public void setBeenProxyMethod(Method beenProxyMethod) {
		this.beenProxyMethod = beenProxyMethod;
	}
}
