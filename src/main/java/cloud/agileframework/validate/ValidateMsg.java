package cloud.agileframework.validate;

import lombok.Builder;
import lombok.Data;

/**
 * @author 佟盟 on 2018/11/15
 */
@Data
@Builder
public class ValidateMsg {
    private String message;
    private String item;
    private Object itemValue;

    public ValidateMsg(String msg, String paramKey, Object paramValue) {
        this.message = msg;
        this.item = paramKey;
        this.itemValue = paramValue;
    }

    public ValidateMsg(String paramKey, Object paramValue) {
        this.item = paramKey;
        this.itemValue = paramValue;
    }

    public void addMessage(String msg) {
        message = message == null ? msg : String.format("%s;%s", message, msg);
    }
}
