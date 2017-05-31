package nna.base.proxy;

import java.lang.reflect.Method;

public class Logic {
   private String className;
   private String methodName;
   private Object objectOfLogic;
   private Method methodOfLogic;

   public Logic(String className,String methodName,Object object,Method method) {
       this.className=className;
       this.methodName=methodName;
       this.objectOfLogic=object;
       this.methodOfLogic=method;
   }

   public String getClassName() {
       return className;
   }

   public String getMethodName() {
       return methodName;
   }

   public Object getObjectOfLogic() {
       return objectOfLogic;
   }

   public Method getMethodOfLogic() {
       return methodOfLogic;
   }
}
