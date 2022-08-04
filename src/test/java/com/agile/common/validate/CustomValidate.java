package com.agile.common.validate;

import cloud.agileframework.validate.ValidateCustomBusiness;
import cloud.agileframework.validate.ValidateMsg;

import java.util.Collections;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00021 11:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomValidate implements ValidateCustomBusiness {
    @Override
    public List<ValidateMsg> validate(String key, Object params) {
        ValidateMsg validateMsg = new ValidateMsg();
        validateMsg.setItem(key);
        validateMsg.setItemValue("tu");
        validateMsg.setMessage("aaaa");
        return Collections.singletonList(validateMsg);
    }
}
