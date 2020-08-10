package cloud.agileframework.validate;

/**
 * @author 佟盟 on 2017/9/2
 */
public class Constant {
    /**
     * 正则表达式
     */
    public static class RegularAbout {
        public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        public static final String DOMAIN = "(?i)(http://|https://)?(\\w+\\.){1,3}(com(\\.cn)?|com|cn|net|org|gov|edu|int|mil|biz|info|tv|pro|name|" +
                "museum|coop|aero|CC|SH|ME|asia|kim|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|" +
                "by|bz|ca|cc|cf|cg|ch|ci|ck|cl|cm|cn|co|cq|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|es|et|ev|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|" +
                "gi|gl|gm|gn|gp|gr|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|in|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|" +
                "ls|lt|lu|lv|ly|ma|mc|md|mg|mh|ml|mm|mn|mo|mp|mq|mr|ms|mt|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|" +
                "pm|pn|pr|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|" +
                "tz|ua|ug|uk|us|uy|va|vc|ve|vg|vn|vu|wf|ws|ye|yu|za|zm|zr|zw)\\b";
        public static final String INTERNET_UTL = "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
        public static final String MOBILE_PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|1[8|7][0|1|2|3|5|6|7|8|9])\\d{8}$";
        public static final String PHONE = "(^((13[4-9])|(14[7-8])|(15[0-2,7-9])|(165)|(178)|(18[2-4,7-8])|(19[5,8]))\\d{8}|(170[3,5,6])\\d{7}$)" +
                "|(^((13[0-2])|(14[5,6])|(15[5-6])|(16[6-7])|(17[1,5,6])|(18[5,6]))\\d{8}|(170[4,7-9])\\d{7}$)|(^((133)|(149)|(153)|(162)|(17[3,7])" +
                "|(18[0,1,9])|(19[1,3,9]))\\d{8}|((170[0-2])|(174[0-5]))\\d{7}$)";
        public static final String CHINA_PHONE = "\\d{3}-\\d{8}|\\d{4}-\\d{7}";
        public static final String ID_CARD = "^\\d{15}|\\d{18}$";
        public static final String SHORT_ID_CARD = "^\\d{8,18}|[0-9x]{8,18}|[0-9X]{8,18}?$";
        public static final String ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        public static final String PASSWORD = "^[a-zA-Z]\\w{5,17}$";
        public static final String STRONG_PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}$";
        public static final String DATE_YYYY_MM_DD = "^((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)" +
                "|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$";
        public static final String DATE_YYYYMMDD = "^((((19|20)\\d{2})(0?[13-9]|1[012])(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})(0?[13578]|1[02])31)|(((19|20)\\d{2})0?2(0?[1-9]|1\\d|2[0-8]))" +
                "|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))0?229))$";
        public static final String DATE_YYYYIMMIDD = "^((((19|20)\\d{2})/(0?[13-9]|1[012])/(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})/(0?[13578]|1[02])/31)" +
                "|(((19|20)\\d{2})/0?2/(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))/0?2/29))$";
        public static final String MONTH = "^(0?[1-9]|1[0-2])$";
        public static final String DAY = "^((0?[1-9])|((1|2)[0-9])|30|31)$";
        public static final String MONEY = "^(0|-?[1-9][0-9]*)$";
        public static final String XML_FILE_NAME = "^([a-zA-Z]+-?)+[a-zA-Z0-9]+\\.[x|X][m|M][l|L]$";
        public static final String CHINESE_LANGUAGE = "[\\u4e00-\\u9fa5]";
        public static final String TWO_CHAR = "[^\\x00-\\xff]";
        public static final String QQ = "[1-9][0-9]{4,}";
        public static final String MAIL_NO = "[1-9]\\d{5}(?!\\d)";
        public static final String IP = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
        public static final String INT = "^[-]?[0-9]*$";
        public static final String NUMBER = "^[-+]?[0-9]*[.]?[0-9]*[fFL]?$";
        public static final String DOUBLE = "^[-+]?[0-9]*[.]?[0-9]*$";
        public static final String FLOAT = "^[-+]?[0-9]*[.]?[0-9]*[fFL]$";
        public static final String ENGLISH_NUMBER = "^[A-Za-z0-9]+$";
        public static final String MAC = "(([A-Fa-f0-9]{2}:)|([A-Fa-f0-9]{2}-)){5}[A-Fa-f0-9]{2}";
    }
}