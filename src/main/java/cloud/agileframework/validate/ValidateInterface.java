package cloud.agileframework.validate;

import cloud.agileframework.validate.annotation.Validate;

import java.util.List;

/**
 * @author 佟盟 on 2018/11/15
 */
public interface ValidateInterface {
    List<ValidateMsg> validateParam(String key, Object value, Validate validate);

    List<ValidateMsg> validateArray(String key, List<Object> value, Validate validate);
}
