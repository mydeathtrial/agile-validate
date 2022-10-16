package cloud.agileframework.validate;

import cloud.agileframework.validate.annotation.Validate;

import java.util.Collection;
import java.util.List;

/**
 * @author 佟盟 on 2018/11/15
 */
public interface ValidateInterface {
    /**
     * 验证一个参数
     *
     * @param key      参数的索引
     * @param value    参数值
     * @param validate 验证注解
     * @return 验证结果
     */
    List<ValidateMsg> validateParam(String key, Object value, Validate validate);

    /**
     * 验证集合参数
     *
     * @param key      参数的索引
     * @param value    参数值
     * @param validate 验证注解
     * @return 验证结果
     */
    List<ValidateMsg> validateArray(String key, Collection<?> value, Validate validate);
}
