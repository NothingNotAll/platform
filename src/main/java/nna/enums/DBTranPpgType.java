package nna.enums;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/13-21:51
 */
public enum  DBTranPpgType {
    DEFAULT,
    NONE,
    PROPAGATION_REQUIRED,//如果当前没有事务，就创建一个新事务，如果当前存在事务，就加入该事务，该设置是最常用的设置。
    PROPAGATION_SUPPORTS,//支持当前事务，如果当前存在事务，就加入该事务，如果当前不存在事务，就以非事务执行。‘
    PROPAGATION_MANDATORY,//支持当前事务，如果当前存在事务，就加入该事务，如果当前不存在事务，就抛出异常。
    PROPAGATION_REQUIRES_NEW,//创建新事务，无论当前存不存在事务，都创建新事务。
    PROPAGATION_NOT_SUPPORTED,//以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
    PROPAGATION_NEVER,//以非事务方式执行，如果当前存在事务，则抛出异常。
    PROPAGATION_NESTED//如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与PROPAGATION_REQUIRED类似的操作。
}
