package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * for db table of user priv
 * @author NNA-SHUAI
 * @create 2017-05-13 16:27
 **/

public class PlatformUserRole extends Clone {
    private static final Long serialVersionUID=11L;

    private int userId;
    private int roleId;

    public PlatformUserRole(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

}
