package cloud.agileframework.validate.annotation;

import cloud.agileframework.validate.ValidateCustomBusiness;
import cloud.agileframework.validate.ValidateType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 佟盟 on 2018/11/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Validates.class)
public @interface Validate {
    /**
     * 参数名
     */
    String value() default "";

    /**
     * 内置验证类型，如邮箱、电话号、浮点数、域名、ip等等
     */
    ValidateType validateType() default ValidateType.NO;

    /**
     * 自定义验证正则
     */
    String validateRegex() default "";

    /**
     * 验证失败后的错误消息
     */
    String validateMsg() default "";

    /**
     * 验证失败后的错误消息的国际化messages_*.properties中的key值
     * 需要配合spring message使用
     */
    String validateMsgKey() default "";

    /**
     * 验证失败后的错误消息的国际化messages_*.properties中的{n}占位参数
     * 需要配合spring message使用
     */
    String[] validateMsgParams() default {};

    /**
     * 配合beanClass参数使用，对应功能为hibernate-validate的group场景，使用方式不变
     */
    Class[] validateGroups() default {};

    /**
     * 对应功能为hibernate-validate方式的pojo验证，对应的beanClass属性中应该包含hibernate-validate注解
     */
    Class<?> beanClass() default Class.class;

    /**
     * 能否为null，默认可以
     */
    boolean nullable() default true;

    /**
     * 能够为空字符串，默认为不可以
     */
    boolean isBlank() default false;

    /**
     * 数字类型参数的最大值
     */
    double max() default Double.MAX_VALUE;

    /**
     * 数字类型参数的最小值
     */
    double min() default Integer.MIN_VALUE;

    /**
     * 最大长度
     */
    int maxSize() default Integer.MAX_VALUE;

    /**
     * 最小值
     */
    int minSize() default -1;

    /**
     * 自定义验证过程
     */
    Class<? extends ValidateCustomBusiness>[] customBusiness() default {};
}
