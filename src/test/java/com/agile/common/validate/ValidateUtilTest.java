package com.agile.common.validate;

import cloud.agileframework.validate.ValidateMsg;
import cloud.agileframework.validate.ValidateType;
import cloud.agileframework.validate.ValidateUtil;
import cloud.agileframework.validate.annotation.Validate;
import com.agile.App;
import com.alibaba.fastjson.JSON;
import jakarta.validation.constraints.Pattern;
import junit.framework.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class ValidateUtilTest extends TestCase {
    @Test
    public void testHandleInParamValidate() throws NoSuchMethodException {
        List<ValidateMsg> a = ValidateUtil.handleInParamValidate(ValidateUtilTest.class.getMethod("tudou"), param);
        Optional<List<ValidateMsg>> b = ValidateUtil.aggregation(a);
        b.ifPresent(validateMsgs -> System.out.println(JSON.toJSONString(validateMsgs, true)));
    }

    @Validate(value = "a", validateType = ValidateType.EMAIL)
    @Validate(value = "a", validateRegex = "[\\d]+", validateMsg = "必须是数字")
    @Validate(value = "o.a.a", nullable = false, validateMsgKey = "messageKey", validateMsgParams = "cu")
    @Validate(value = "list.a.c", nullable = false, validateMsg = "自定义错误")
    @Validate(value = "o", beanClass = Ob.class, validateGroups = {Group1.class})
    public void tudou() {

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Ob {
        @Length(min = 1, max = 3)
        private String a = "a";
        @Pattern(regexp = "[\\d]+", message = "错了吧", groups = Group1.class)
        private String b = "b";
        private Ob o;
        private List<Ob> list;
    }

    private static final Ob param;

    static {
        param = new Ob();
        param.setO(new Ob("a1", "b1", null, null));
        param.setList(new ArrayList<Ob>() {{
            add(new Ob("list/a0", "list/b0", null, null));
            add(new Ob("list/a1", "list/b1", null, null));
            add(new Ob("list/a2", "list/b2", null, null));
            add(new Ob("list/a3", "list/b3", null, null));
            add(new Ob("list/a4", "list/b4", null, null));
        }});
    }

    public static interface Group1 {
    }
}