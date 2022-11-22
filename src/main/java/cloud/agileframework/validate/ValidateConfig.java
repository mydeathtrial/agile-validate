package cloud.agileframework.validate;

import cloud.agileframework.validate.annotation.Validate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateConfig implements Serializable {
    /**
     * 参数名
     */
    private String value;

    /**
     * 内置验证类型，如邮箱、电话号、浮点数、域名、ip等等
     */
    private ValidateType validateType = ValidateType.NO;

    /**
     * 自定义验证正则
     */
    private String validateRegex;

    /**
     * 验证失败后的错误消息
     */
    private String validateMsg;

    /**
     * 验证失败后的错误消息的国际化messages_*.properties中的key值
     * 需要配合spring message使用
     */
    private String validateMsgKey;

    /**
     * 验证失败后的错误消息的国际化messages_*.properties中的{n}占位参数
     * 需要配合spring message使用
     */
    private String[] validateMsgParams;

    /**
     * 配合beanClass参数使用，对应功能为hibernate-validate的group场景，使用方式不变
     */
    private Class<?>[] validateGroups;

    /**
     * 对应功能为hibernate-validate方式的pojo验证，对应的beanClass属性中应该包含hibernate-validate注解
     */
    private Class<?> beanClass;

    /**
     * 能否为null，默认可以
     */
    private boolean nullable = true;

    /**
     * 能够为空字符串，默认为不可以
     */
    private boolean isBlank = false;

    /**
     * 数字类型参数的最大值
     */
    private double max = Double.MAX_VALUE;

    /**
     * 数字类型参数的最小值
     */
    private double min = Integer.MIN_VALUE;

    /**
     * 最大长度
     */
    private int maxSize = Integer.MAX_VALUE;

    /**
     * 最小值
     */
    private int minSize = -1;

    /**
     * 自定义验证过程
     */
    private Class<? extends ValidateCustomBusiness>[] customBusiness;

    public ValidateConfig(Validate validate) {
        this.value = validate.value();
        this.validateType = validate.validateType();
        this.validateRegex = validate.validateRegex();
        this.validateMsg = validate.validateMsg();
        this.validateMsgKey = validate.validateMsgKey();
        this.validateMsgParams = validate.validateMsgParams();
        this.validateGroups = validate.validateGroups();
        this.beanClass = validate.beanClass();
        this.nullable = validate.nullable();
        this.isBlank = validate.isBlank();
        this.max = validate.max();
        this.min = validate.min();
        this.maxSize = validate.maxSize();
        this.minSize = validate.minSize();
        this.customBusiness = validate.customBusiness();
    }
}
