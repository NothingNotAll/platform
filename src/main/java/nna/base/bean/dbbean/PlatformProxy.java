package nna.base.bean.dbbean;


import nna.base.bean.Clone;
import nna.enums.ProxyType;

/**
 * for db table of proxy
 * @author NNA-SHUAI
 * @create 2017-05-13 16:37
 **/

public class PlatformProxy extends Clone {
    private static final Long serialVersionUID=2L;

    private String beenproxyClassRegex;
    private String beenproxyMethodRegex;
    private String proxyClass;
    private String proxyMethod;
    private ProxyType proxyType;
    private int proxySeq;
    private String proxyDesc;


    public PlatformProxy(){

    }

    public String getProxyClass() {
        return proxyClass;
    }

    public void setProxyClass(String proxyClass) {
        this.proxyClass = proxyClass;
    }

    public String getProxyMethod() {
        return proxyMethod;
    }

    public void setProxyMethod(String proxyMethod) {
        this.proxyMethod = proxyMethod;
    }

    public ProxyType getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public String getProxyDesc() {
        return proxyDesc;
    }

    public void setProxyDesc(String proxyDesc) {
        this.proxyDesc = proxyDesc;
    }

    public int getProxySeq() {
        return proxySeq;
    }

    public void setProxySeq(int proxySeq) {
        this.proxySeq = proxySeq;
    }

    public String getBeenproxyClassRegex() {
        return beenproxyClassRegex;
    }

    public void setBeenproxyClassRegex(String beenproxyClassRegex) {
        this.beenproxyClassRegex = beenproxyClassRegex;
    }

    public String getBeenproxyMethodRegex() {
        return beenproxyMethodRegex;
    }

    public void setBeenproxyMethodRegex(String beenproxyMethodRegex) {
        this.beenproxyMethodRegex = beenproxyMethodRegex;
    }
}
