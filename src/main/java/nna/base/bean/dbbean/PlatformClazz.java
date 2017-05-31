package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * d
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 17:30
 **/

public class PlatformClazz extends Clone {
    private static final Long serialVersionUID=21L;

    private String clazz;
    private int clazzId;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public int getClazzId() {
        return clazzId;
    }

    public void setClazzId(int clazzId) {
        this.clazzId = clazzId;
    }

}
