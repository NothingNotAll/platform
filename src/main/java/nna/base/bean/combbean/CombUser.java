package nna.base.bean.combbean;/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/14-23:20
 */

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformResource;
import nna.base.bean.dbbean.PlatformRole;
import nna.base.bean.dbbean.PlatformUser;

import java.util.HashMap;
import java.util.HashSet;

/**
 * s
 * @author NNA-SHUAI
 * @create 2017-05-14 23:20
 **/

public class CombUser extends Clone {
    private static final long serialVersionUID = -10L;
    private PlatformUser platformUser;

    private PlatformRole[] platformRoles;
    private HashMap<String,PlatformResource> resoruces;

    public PlatformUser getPlatformUser() {
        return platformUser;
    }

    public void setPlatformUser(PlatformUser platformUser) {
        this.platformUser = platformUser;
    }

    public PlatformRole[] getPlatformRoles() {
        return platformRoles;
    }

    public void setPlatformRoles(PlatformRole[] platformRoles) {
        this.platformRoles = platformRoles;
    }

    public HashMap<String, PlatformResource> getResoruces() {
        return resoruces;
    }

    public void setResoruces(HashMap<String, PlatformResource> resoruces) {
        this.resoruces = resoruces;
    }
}
