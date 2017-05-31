package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformClazz;
import nna.base.util.ObjectFactory;

import java.util.HashMap;

/**
 * s
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 17:37
 **/

public class CombClazz extends Clone {
    private static final long serialVersionUID = -3L;
    private PlatformClazz platformClazz;
    private static HashMap<String,ObjectFactory> fieldFactoryMap;

    public static HashMap<String, ObjectFactory> getFieldFactoryMap() {
        return fieldFactoryMap;
    }

    public static void setFieldFactoryMap(HashMap<String, ObjectFactory> fieldFactoryMap) {
        CombClazz.fieldFactoryMap = fieldFactoryMap;
    }

    public PlatformClazz getPlatformClazz() {
        return platformClazz;
    }

    public void setPlatformClazz(PlatformClazz platformClazz) {
        this.platformClazz = platformClazz;
    }

}
