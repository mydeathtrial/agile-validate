# agile-validate ： 参数验证器
[![hibernate-validator](https://img.shields.io/badge/hibernate--validator-LATEST-green)](https://img.shields.io/badge/hibernate--validator-LATEST-green)
[![maven](https://img.shields.io/badge/build-maven-green)](https://img.shields.io/badge/build-maven-green)
## 它有什么作用

* **注解形式声明方法入参验证**

* **POJO类型参数验证**

* **基本类型参数验证**

* **自定义验证规则**

* **支持验证场景**

* **集合类型参数验证**

* **自定义错误消息与国际化消息**

* **自定义业务验证**
-------
## 快速入门
开始你的第一个项目是非常容易的。

#### 步骤 1: 下载包
您可以从[最新稳定版本]下载包(https://github.com/mydeathtrial/agile-validate/releases).
该包已上传至maven中央仓库，可在pom中直接声明引用

以版本agile-validate-0.1.jar为例。
#### 步骤 2: 添加maven依赖
```xml
<dependency>
    <groupId>cloud.agileframework</groupId>
    <artifactId>agile-validate</artifactId>
    <version>0.1</version>
</dependency>
```
#### 步骤 3: 开箱即用

##### 注解
用于要进行参数验证的方法，声明参数验证规则
```
1、com.agile.common.annotation.Validate，支持多注解
2、com.agile.common.annotation.Validates，可包含多Validate
```
例子：
```java
    @Validate(value = "param1", validateType = ValidateType.EMAIL)
    @Validate(value = "param1", validateRegex = "[\\d]+",validateMsg = "必须是数字")
    @Validate(value = "param2", nullable = false, validateMsgKey = "messageKey", validateMsgParams = "cu")
    @Validate(value = "params.param1.param2", nullable = false, validateMsg = "自定义错误")
    @Validate(value = "param3", beanClass = Ob.class, validateGroups = {Group1.class})
    @Validate(value = "a",customBusiness = {CustomValidate.class})
    public void yourMethod() {
        ...
    }
```
##### 调用
获取验证结果，工具类cloud.agileframework.validate.ValidateUtil
```java
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
```java
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

    /**
     * 自定义业务验证过程
     */
    Class<? extends ValidateCustomBusiness>[] customBusiness() default {};
}
```

验证结果（例）
```json
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

##### 自定义业务代码验证
```java
    //使用注解中的customBusiness属性，声明要使用的业务验证类，业务验证类必须实现ValidateCustomBusiness接口
    //该属性支持声明多个业务验证类，验证组件调用业务验证类的validate方法时，会有限尝试使用spring托管的bean去调用
    //当bean不存在时将自动创建业务验证类对象调用。所以业务验证类中支持使用任何spring的所有功能。
    @Validate(value = "paramKey",customBusiness = {CustomValidate.class})
    public void yourMethod() {

    }

    public class CustomValidate implements ValidateCustomBusiness {
    
    
        @Override
        public List<ValidateMsg> validate(Object params) {
            ...
            ValidateMsg validateMsg = new ValidateMsg();
            validateMsg.setItem("paramKey");
            validateMsg.setItemValue(params);
            validateMsg.setMessage("业务验证出错了");
            validateMsg.setState(false);
            return Collections.singletonList(validateMsg);
        }
    }
```