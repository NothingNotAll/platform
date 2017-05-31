package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.UserRole;

/**
 * for db table of develop user privilege
 * @author NNA-SHUAI
 * @create 2017-05-13 16:25
 **/

public class PlatformRole extends Clone {
    private static final Long serialVersionUID=4L;

    private int roleId;
    private UserRole roleName;

    public PlatformRole(){

    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public UserRole getRoleName() {
        return roleName;
    }

    public void setRoleName(UserRole roleName) {
        this.roleName = roleName;
    }
}
