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

        List<Validate> validateList = Lists.newArrayList();
        validatesOptional.ifPresent(validates -> validateList.addAll(Stream.of(validates.value()).collect(Collectors.toList())));
        validateOptional.ifPresent(validateList::add);

        return validateList.stream().map(validate -> handleValidateAnnotation(validate, params)).reduce((all, validateMsgList) -> {
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
    private static List<ValidateMsg> handleValidateAnnotation(Validate v, Object params) {
        List<ValidateMsg> list = new ArrayList<>();

        if (v == null) {
            return list;
        }

        String key = v.value().trim();
        Object value;
        if (StringUtils.isBlank(key)) {
            value = params;
        } else {
            value = JSONUtil.pathGet(key, params);
        }

        ValidateType validateType = v.validateType();
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
            list.addAll(validateType.validateArray(key, (List) value, v));
        } else {
            list.addAll(validateType.validateParam(key, value, v));
        }
        return list;
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
        List<ValidateMsg> list = new ArrayList<>();

        if (pojo == null) {
            return list;
        }
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> set = validator.validate(pojo, groups);

        if (set == null || set.isEmpty()) {
            return list;
        }
        for (ConstraintViolation<Object> m : set) {
            ValidateMsg r = new ValidateMsg(m.getMessage(), m.getPropertyPath().toString(), m.getInvalidValue());
            list.add(r);
        }
        return list;
    }
}
