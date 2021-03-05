package cloud.agileframework.validate;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.clazz.ClassUtil;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.common.util.pattern.PatternUtil;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.MessageUtil;
import cloud.agileframework.validate.annotation.Validate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2018/11/16
 */
public enum ValidateType implements ValidateInterface {
    /**
     * 正则校验类型
     */
    NO,
    EMAIL(Constant.RegularAbout.EMAIL, "邮箱"),
    DOMAIN(Constant.RegularAbout.DOMAIN, "域名"),
    INTERNET_UTL(Constant.RegularAbout.INTERNET_UTL, "Internet地址"),
    MOBILE_PHONE(Constant.RegularAbout.MOBILE_PHONE, "手机号码"),
    PHONE(Constant.RegularAbout.PHONE, "电话号码"),
    /**
     * 国内电话号码(0511-4405222、021-87888822)
     */
    CHINA_PHONE(Constant.RegularAbout.CHINA_PHONE, "国内电话号码"),
    /**
     * 身份证号(15位、18位数字)
     */
    ID_CARD(Constant.RegularAbout.ID_CARD, " 身份证号"),
    /**
     * 短身份证号码(数字、字母x结尾)
     */
    SHORT_ID_CARD(Constant.RegularAbout.SHORT_ID_CARD, "短身份证号码"),
    /**
     * 帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)
     */
    ACCOUNT(Constant.RegularAbout.ACCOUNT, "帐号"),
    /**
     * 密码(以字母开头，长度在6~18之间，只能包含字母、数字和下划线)
     */
    PASSWORD(Constant.RegularAbout.PASSWORD, "密码"),
    /**
     * 强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)
     */
    STRONG_PASSWORD(Constant.RegularAbout.STRONG_PASSWORD, "强密码"),
    DATE_YYYY_MM_DD(Constant.RegularAbout.DATE_YYYY_MM_DD, "日期（yyyy-mm-dd）"),
    MONTH(Constant.RegularAbout.MONTH, "月份"),
    DAY(Constant.RegularAbout.DAY, "日期天"),
    MONEY(Constant.RegularAbout.MONEY, "钱"),
    XML_FILE_NAME(Constant.RegularAbout.XML_FILE_NAME, "xml文件名"),
    CHINESE_LANGUAGE(Constant.RegularAbout.CHINESE_LANGUAGE, "中文"),
    TWO_CHAR(Constant.RegularAbout.TWO_CHAR, "双字节字符"),
    QQ(Constant.RegularAbout.QQ, "QQ号"),
    MAIL_NO(Constant.RegularAbout.MAIL_NO, "中国邮政编码"),
    IP(Constant.RegularAbout.IP, "IP地址"),
    NUMBER(Constant.RegularAbout.NUMBER, "数字"),
    FLOAT(Constant.RegularAbout.FLOAT, "浮点数"),
    INT(Constant.RegularAbout.INT, "证书"),
    DOUBLE(Constant.RegularAbout.DOUBLE, "双精度"),
    ENGLISH_NUMBER(Constant.RegularAbout.ENGLISH_NUMBER, "英文数字"),
    MAC(Constant.RegularAbout.MAC, "MAC地址");

    private String regex;
    private String info;

    ValidateType() {
    }

    ValidateType(String regex, String info) {
        this.regex = regex;
        this.info = info;
    }

    @Override
    @NotNull
    public List<ValidateMsg> validateParam(String key, Object value, Validate validate) {
        return validate(key, value, validate);
    }

    @Override
    @NotNull
    public List<ValidateMsg> validateArray(String key, List<Object> value, Validate validate) {
        List<ValidateMsg> list = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            Object node = value.get(i);
            List<ValidateMsg> vs = validate(String.format("%s.%s", key, i), node, validate);
            if (ObjectUtils.isEmpty(vs)) {
                continue;
            }
            list.addAll(vs);
        }

        int size = value.size();
        if (!(validate.minSize() <= size && size <= validate.maxSize())) {
            ValidateMsg v = new ValidateMsg(createMessage(validate, "长度超出阈值"), key, value);
            list.add(v);
        }
        return list;
    }

    private String createMessage(Validate validate, String defaultMessage) {
        String result;
        if (StringUtils.isBlank(validate.validateMsg()) && StringUtils.isBlank(validate.validateMsgKey()) && defaultMessage == null) {
            result = String.format("不符合%s格式", info == null ? "自定义" : info);
        } else if (!StringUtils.isBlank(validate.validateMsg())) {
            result = validate.validateMsg();
        } else if (!StringUtils.isBlank(validate.validateMsgKey())) {
            result = MessageUtil.message(validate.validateMsgKey(), null, (Object[]) validate.validateMsgParams());
        } else {
            result = defaultMessage;
        }
        return result;
    }

    private List<ValidateMsg> validate(String key, Object value, Validate validate) {
        List<ValidateMsg> list = new ArrayList<>();

        //hibernate-validate验证
        validateBeanClass(key, value, validate, list);

        //验证isBlank
        validateIsBlank(key, value, validate, list);

        //非空时验证正则
        validateRegex(key, value, validate, list);

        //长度验证
        validateSize(key, value, validate, list);

        //空值验证
        validateNullable(key, value, validate, list);

        //自定义业务验证
        validateCustomBusiness(value, validate, list);

        return list;
    }

    /**
     * 验证空值
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private void validateNullable(String key, Object value, Validate validate, List<ValidateMsg> list) {
        if (value != null || validate.nullable()) {
            return;
        }
        ValidateMsg v = new ValidateMsg(key, null);

        v.setMessage(createMessage(validate, "不允许为空值"));
        list.add(v);
    }

    /**
     * 长度验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private void validateSize(String key, Object value, Validate validate, List<ValidateMsg> list) {
        if (value == null) {
            return;
        }
        int size = String.valueOf(value).length();
        if (validate.minSize() <= size && size <= validate.maxSize()) {
            return;
        }

        ValidateMsg v = new ValidateMsg(key, value);

        v.setMessage(createMessage(validate, "长度超出阈值"));
        list.add(v);
    }

    /**
     * 正则验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private void validateRegex(String key, Object value, Validate validate, List<ValidateMsg> list) {
        if (value == null) {
            return;
        }
        boolean result = true;
        final String text = String.valueOf(value);
        if (validate.validateType() != NO && !PatternUtil.matches(regex, text)) {
            result = false;
        }
        if (!StringUtils.isEmpty(validate.validateRegex()) && !PatternUtil.matches(validate.validateRegex(), text)) {
            result = false;
        }

        if (!result) {
            ValidateMsg v = new ValidateMsg(key, value);

            v.setMessage(createMessage(validate, "格式错误"));
            list.add(v);
        }

        final boolean needContinue = result && validate.validateType() == NUMBER
                || validate.validateType() == FLOAT
                || validate.validateType() == INT
                || validate.validateType() == DOUBLE;
        if (!needContinue) {
            return;
        }
        Number n = "".equals(value) ? 0 : NumberUtils.createNumber(text);
        if (validate.min() > n.doubleValue() || n.doubleValue() > validate.max()) {
            ValidateMsg v = new ValidateMsg(key, value);

            v.setMessage(createMessage(validate, "大小超出阈值"));
            list.add(v);
        }
    }

    /**
     * 空串验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private void validateIsBlank(String key, Object value, Validate validate, List<ValidateMsg> list) {
        // 验证空字符串
        if (value == null || !validate.isBlank() || !StringUtils.isBlank(value.toString())) {
            return;
        }
        ValidateMsg v = new ValidateMsg(key, value);

        v.setMessage(createMessage(validate, "不允许为空值"));
        list.add(v);
    }

    /**
     * 自定义业务方式验证
     *
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private void validateCustomBusiness(Object value, Validate validate, List<ValidateMsg> list) {
        if (validate.customBusiness().length == 0) {
            return;
        }
        Set<ValidateMsg> set = Arrays.stream(validate.customBusiness()).map(custom -> {
            ValidateCustomBusiness bean = BeanUtil.getBean(custom);
            if (bean == null) {
                bean = ClassUtil.newInstance(custom);
            }
            if (bean == null) {
                try {
                    throw new InstantiationException("Class " + custom.getCanonicalName() + " cannot create an object through a constructor");
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return new ArrayList<ValidateMsg>(0);
                }
            }
            List<ValidateMsg> vr = bean.validate(value);
            if (vr == null) {
                return new ArrayList<ValidateMsg>(0);
            }
            return vr;
        }).flatMap(Collection::stream).collect(Collectors.toSet());
        list.addAll(set);
    }

    /**
     * 验证hibernate-validate
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     错误信息容器
     */
    private void validateBeanClass(String key, Object value, Validate validate, List<ValidateMsg> list) {
        Class<?> beanClass = validate.beanClass();
        if (value == null || beanClass == Class.class) {
            return;
        }
        Object bean = ObjectUtil.to(value, new TypeReference<>(beanClass));
        if (bean == null) {
            bean = ClassUtil.newInstance(beanClass);
            ObjectUtil.setAllFieldNull(bean);
        }
        if (bean != null) {
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> set = validator.validate(bean, validate.validateGroups());
            for (ConstraintViolation<Object> m : set) {
                ValidateMsg r = new ValidateMsg(m.getMessage(), StringUtils.isBlank(key) ? m.getPropertyPath().toString() : String.format("%s.%s", key, m.getPropertyPath()), m.getInvalidValue());
                list.add(r);
            }
        }
    }
}
