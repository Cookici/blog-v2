package com.lrh.identity.constants;

public class RoleConstant {

    /**
     * 锁前缀
     */
    public static final String LOCK_PREFIX = "permission:lock:";

    /**
     * 角色权限锁
     */
    public static final String ROLE_PERMISSION_LOCK = LOCK_PREFIX + "role:";

    /**
     * 角色模块API缓存键
     */
    public static final String ROLE_MODULE_APIS = "role:module:apis";

    public static final String NO_LOGIN_ROLE= "anonymous";
}
