# agile-validate（参数验证组件）

简介：负责解析com.agile.common.annotation.Validate注解，实现方法参数验证，支持自定义正则表达式验证、深层参数验证、数组类型参数验证、自定义错误消息、支持spring国际化、支持hibernate-validate方式pojo验证、支持group场景

##使用方法：
注解：用于要进行参数验证的方法，声明参数验证规则
```
1、com.agile.common.annotation.Validate，支持多注解
2、com.agile.common.annotation.Validates，可包含多Validate
```
例子：
```
    @Validate(value = "param1", validateType = ValidateType.EMAIL)
    @Validate(value = "param1", validateRegex = "[\\d]+",validateMsg = "必须是数字")
    @Validate(value = "param2", nullable = false, validateMsgKey = "messageKey", validateMsgParams = "cu")
    @Validate(value = "params.param1.param2", nullable = false, validateMsg = "自定义错误")
    @Validate(value = "param3", beanClass = Ob.class, validateGroups = {Group1.class})
    public void yourMethod() {
        ...
    }
```
调用：获取验证结果，工具类com.agile.common.validate.ValidateUtil
```
    /**
     * 方法入参验证
     *
     * @param method 方法
     * @param params 参数集
     * @return 验证信息集，包含正确与错误的所有入参验证结果，包括参数名、参数值、错误信息
     */
    public static List<ValidateMsg> handleInParamValidate(Method method,Object params) 

    /**
     * 参数校验信息根据相同参数聚合错误信息
     *
     * @param list 聚合之前的错误信息
     * @return 聚合后的信息，过滤掉正确参数，重复的参数验证结果合并为一个，对应的错误消息合并
     */
    public static Optional<List<ValidateMsg>> aggregation(List<ValidateMsg> list) 
```
注解参数说明：
```
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
     * 能够为空字符串，默认为可以
     */
    boolean isBlank() default true;

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
}
```

##验证结果（例）
```
[
	{
		"item":"param1",
		"itemValue":"a",
		"message":"不符合邮箱格式;必须是数字",
		"state":false
	},
	{
		"item":"param3",
		"itemValue":"b1",
		"message":"错了吧",
		"state":false
	},
	{
		"item":"params.param1.param2",
		"message":"自定义错误",
		"state":false
	},
	{
		"item":"param2",
		"message":"errorcu",
		"state":false
	}
]

```