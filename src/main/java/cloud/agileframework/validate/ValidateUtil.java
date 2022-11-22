package cloud.agileframework.validate;

import cloud.agileframework.common.util.json.JSONUtil;
import cloud.agileframework.validate.annotation.Validate;
import cloud.agileframework.validate.annotation.Validates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 佟盟
 * 日期 2020/7/30 10:23
 * 描述 验证工具
 * @version 1.0
 * @since 1.0
 */
public class ValidateUtil {

    public static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    public static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private ValidateUtil() {
    }

    /**
     * 入参验证
     *
     * @param method 方法
     * @param params 参数集
     * @return 验证信息集
     */
    public static List<ValidateMsg> handleInParamValidate(Method method, Object params) {
        Optional<Validates> validatesOptional = Optional.ofNullable(method.getAnnotation(Validates.class));
        Optional<Validate> validateOptional = Optional.ofNullable(method.getAnnotation(Validate.class));

        List<ValidateConfig> validateList = Lists.newArrayList();
        validatesOptional.ifPresent(validates -> validateList.addAll(Stream.of(validates.value()).map(ValidateConfig::new).collect(Collectors.toList())));
        validateOptional.ifPresent(validate -> validateList.add(new ValidateConfig(validate)));

        return handleValidateData(params, validateList);
    }

    /**
     * 根据validateList校验规则校验数据data
     *
     * @param data         准备校验的数据
     * @param validateList 校验规则
     * @return 校验结果
     */
    public static List<ValidateMsg> handleValidateData(Object data, List<ValidateConfig> validateList) {
        return validateList.stream().map(validateConfig -> {
            String key = validateConfig.getValue().trim();
            Object value;
            if (StringUtils.isBlank(key)) {
                value = data;
            } else {
                value = JSONUtil.pathGet(key, data);
            }
            return handleValidate(value, validateConfig);
        }).reduce((all, validateMsgList) -> {
            all.addAll(validateMsgList);
            return all;
        }).orElse(Lists.newArrayList());
    }

    /**
     * 根据参数验证注解取验证信息集
     *
     * @param v Validate注解
     * @return 验证信息集
     */
    private static List<ValidateMsg> handleValidate(Object value, ValidateConfig v) {
        if (v == null) {
            return Lists.newArrayList();
        }

        String key = v.getValue().trim();
        if (value != null && Collection.class.isAssignableFrom(value.getClass())) {
            return ValidateType.validateArray(key, (Collection<?>) value, v);
        } else {
            return ValidateType.validateParam(key, value, v);
        }
    }

    /**
     * 参数校验信息根据相同参数聚合错误信息
     *
     * @param list 聚合之前的错误信息
     * @return 聚合后的信息
     */
    public static List<ValidateMsg> aggregation(List<ValidateMsg> list) {

        Map<String, ValidateMsg> cache = Maps.newHashMapWithExpectedSize(list.size());

        list.forEach(validateMsg -> {
            String key = validateMsg.getItem();
            if (cache.containsKey(key)) {
                cache.get(key).addMessage(validateMsg.getMessage());
            } else {
                cache.put(key, validateMsg);
            }
        });
        if (cache.size() > 0) {
            return new ArrayList<>(cache.values());
        }
        return new ArrayList<>(0);
    }

    /**
     * 验证POJO对象
     *
     * @param pojo   要验证的对象
     * @param groups 验证场景，需要是接口
     * @return 验证结果
     */
    public static List<ValidateMsg> validate(Object pojo, Class<?>... groups) {
        if (pojo == null) {
            return Lists.newArrayList();
        }
        Set<ConstraintViolation<Object>> set = VALIDATOR.validate(pojo, groups);

        if (set == null || set.isEmpty()) {
            return Lists.newArrayList();
        }
        List<ValidateMsg> list = Lists.newArrayList();
        for (ConstraintViolation<Object> m : set) {
            ValidateMsg r = new ValidateMsg(m.getMessage(), m.getPropertyPath().toString(), m.getInvalidValue());
            list.add(r);
        }
        return list;
    }
}
