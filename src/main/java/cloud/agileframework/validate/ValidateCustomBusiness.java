package cloud.agileframework.validate;

import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00021 10:58
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface ValidateCustomBusiness {
    /**
     * 自定义验证过程
     *
     * @param params 参数
     * @return 验证结果信息
     */
    List<ValidateMsg> validate(String key, Object params);
}
