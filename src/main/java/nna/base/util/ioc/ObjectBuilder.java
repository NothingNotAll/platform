package nna.base.util.ioc;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/6/1-8:09
 */
public interface ObjectBuilder {

    //根据配置生成 Object 对象 首先进行简单对象的生成，然后进行 复杂对象的生成 这是一个递进的过程
    //1：构造器参数配置 构造器参数配置：参数类型；参数值；
    //2：成员字段变量的生成。
    //简单对象的生成 ：

    //判断这个类是否有代理配置如果有代理配置 则加载代理配置，生成代理对象

}
