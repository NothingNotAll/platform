package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.PriveType;

/**
 * s
 * @author NNA-SHUAI
 * @create 2017-05-14 23:12
 **/

public class PlatformRoleResource extends Clone {
    private static final Long serialVersionUID=5L;

    private int roleId;
    private int resourceId;
    private PriveType privType;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public PriveType getPrivType() {
        return privType;
    }

    public void setPrivType(PriveType privType) {
        this.privType = privType;
    }
}
