package nna.base.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 11:14
 **/

public final class ProtocolAdapter {

    private Method serviceMethod;
    private Object serviceObject;

    public ProtocolAdapter(
            Method method,
            Object object
    ){
        serviceMethod=method;
        serviceObject=object;
    }

    public String service(Map<String,String[]> reqMap) throws InvocationTargetException, IllegalAccessException {
        String response=serviceMethod.invoke(serviceObject,reqMap).toString();
        return response;
    }
}
