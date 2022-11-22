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
public enum ValidateType {
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

    @NotNull
    public static List<ValidateMsg> validateParam(String key, Object value, ValidateConfig validate) {
        return validate(key, value, validate);
    }

    @NotNull
    public static List<ValidateMsg> validateArray(String key, Collection<?> value, ValidateConfig validate) {
        List<ValidateMsg> list = new ArrayList<>();
        int i = 0;
        for (Object node:value) {
            List<ValidateMsg> vs = validate(String.format("%s.%s", key, i++), node, validate);
            if (ObjectUtils.isEmpty(vs)) {
                continue;
            }
            list.addAll(vs);
        }

        int size = value.size();
        if (!(validate.getMinSize() <= size && size <= validate.getMaxSize())) {
            ValidateMsg v = new ValidateMsg(createMessage(validate, "长度超出阈值"), key, value);
            list.add(v);
        }
        return list;
    }

    private static String createMessage(ValidateConfig validate, String defaultMessage) {
        String result;
        if (StringUtils.isBlank(validate.getValidateMsg()) && StringUtils.isBlank(validate.getValidateMsgKey()) && defaultMessage == null) {
            result = String.format("不符合%s格式", validate.getValidateMsg() == null ? "自定义" : validate.getValidateMsg());
        } else if (!StringUtils.isBlank(validate.getValidateMsg())) {
            result = validate.getValidateMsg();
        } else if (!StringUtils.isBlank(validate.getValidateMsgKey())) {
            result = MessageUtil.message(validate.getValidateMsgKey(), null, validate.getValidateMsgParams());
        } else {
            result = defaultMessage;
        }
        return result;
    }

    private static List<ValidateMsg> validate(String key, Object value, ValidateConfig validate) {
        List<ValidateMsg> list = new ArrayList<>();

        if (validateNullable(key, value, validate, list)
                && validateIsBlank(key, value, validate, list)
                && validateBeanClass(key, value, validate, list)
                && validateRegex(key, value, validate, list)
                && validateRange(key, value, validate, list)
                && validateSize(key, value, validate, list)) {
            validateCustomBusiness(key, value, validate, list);
        }

        return list;
    }

    /**
     * 验证空值
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     */
    private static boolean validateNullable(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        //不为空不进行拦截
        if (value != null) {
            return true;
        }
        //可以空，并且确实空的时候短路处理
        if (validate.isNullable()) {
            return true;
        }
        ValidateMsg v = new ValidateMsg(key, null);
        v.setMessage(createMessage(validate, "不允许为空值"));
        list.add(v);
        return false;
    }

    /**
     * 长度验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private static boolean validateSize(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        if (value == null) {
            return true;
        }
        int size = String.valueOf(value).length();
        if (validate.getMinSize() <= size && size <= validate.getMaxSize()) {
            return true;
        }

        ValidateMsg v = new ValidateMsg(key, value);

        v.setMessage(createMessage(validate, "长度超出阈值"));
        list.add(v);
        return false;
    }

    /**
     * 正则验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private static boolean validateRegex(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        if (value == null) {
            return true;
        }
        boolean result = true;
        final String text = String.valueOf(value);
        if (validate.getValidateType() != NO && !PatternUtil.matches(validate.getValidateRegex(), text)) {
            result = false;
        }
        if (!StringUtils.isEmpty(validate.getValidateRegex()) && !PatternUtil.matches(validate.getValidateRegex(), text)) {
            result = false;
        }

        if (!result) {
            ValidateMsg v = new ValidateMsg(key, value);

            v.setMessage(createMessage(validate, "格式错误"));
            list.add(v);
            return false;
        }

        return true;
    }

    /**
     * 验证数值类型范围
     *
     * @param key      参数
     * @param value    参数值
     * @param validate 验证注解
     * @param list     验证结果
     * @return 是否继续
     */
    private static boolean validateRange(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        if (value == null) {
            return true;
        }
        if (validate.getMin() == Integer.MIN_VALUE && validate.getMax() == Double.MAX_VALUE) {
            return true;
        }
        final String string = value.toString();
        if (!NumberUtils.isCreatable(string) || StringUtils.isBlank(string)) {
            ValidateMsg v = new ValidateMsg(key, value);

            v.setMessage(createMessage(validate, "格式错误，要求为数值类型"));
            list.add(v);
            return false;
        } else {
            Number n = NumberUtils.createNumber(string);
            if (validate.getMin() > n.doubleValue() || n.doubleValue() > validate.getMax()) {
                ValidateMsg v = new ValidateMsg(key, value);

                v.setMessage(createMessage(validate, "大小超出阈值"));
                list.add(v);
                return false;
            }
        }

        return true;
    }

    /**
     * 空串验证
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private static boolean validateIsBlank(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        // 验证空字符串
        if (value == null || !StringUtils.isBlank(value.toString())) {
            return true;
        }
        if (validate.isBlank()) {
            return false;
        }
        ValidateMsg v = new ValidateMsg(key, value);
        v.setMessage(createMessage(validate, "不允许为空值"));
        list.add(v);
        return false;
    }

    /**
     * 自定义业务方式验证
     *
     * @param value    值
     * @param validate 注解
     * @param list     异常信息容器
     */
    private static boolean validateCustomBusiness(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        Class<? extends ValidateCustomBusiness>[] customBusiness = validate.getCustomBusiness();
        if (customBusiness ==null || customBusiness.length == 0) {
            return true;
        }
        Set<ValidateMsg> set = Arrays.stream(customBusiness).map(custom -> {
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
            List<ValidateMsg> vr = bean.validate(key, value);
            if (vr == null) {
                return new ArrayList<ValidateMsg>(0);
            }
            return vr;
        }).flatMap(Collection::stream).collect(Collectors.toSet());
        list.addAll(set);
        return false;
    }

    /**
     * 验证hibernate-validate
     *
     * @param key      参数索引
     * @param value    值
     * @param validate 注解
     * @param list     错误信息容器
     */
    private static boolean validateBeanClass(String key, Object value, ValidateConfig validate, List<ValidateMsg> list) {
        Class<?> beanClass = validate.getBeanClass();
        if (beanClass==null || beanClass == Class.class) {
            return true;
        }
        Object bean = ObjectUtil.to(value, new TypeReference<>(beanClass));
        if (bean == null) {
            bean = ClassUtil.newInstance(beanClass);
            ObjectUtil.setAllFieldNull(bean);
        }
        if (bean != null) {
            Validator validator = ValidateUtil.VALIDATOR;
            Set<ConstraintViolation<Object>> set = validator.validate(bean, validate.getValidateGroups());
            for (ConstraintViolation<Object> m : set) {
                ValidateMsg r = new ValidateMsg(m.getMessage(), StringUtils.isBlank(key) ? m.getPropertyPath().toString() : String.format("%s.%s", key, m.getPropertyPath()), m.getInvalidValue());
                list.add(r);
            }
            return list.isEmpty();
        }
        return true;
    }
}
