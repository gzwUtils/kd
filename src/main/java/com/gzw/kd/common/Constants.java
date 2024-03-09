package com.gzw.kd.common;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public interface Constants {

    /**
     * 空字符串
     */
    String STRING_EMPTY = "";

    /**
     * 英文点
     */
    String STRING_DOT = ".";

    /**
     * 英文冒号
     */
    String STRING_COLON = ":";

    /**
     * 英文逗号
     */
    String STRING_COMMA = ",";

    /**
     * 英文分号
     */
    String STRING_SEMICOLON = ";";

    /**
     * 字符串0
     */
    String STRING_ZERO = "0";

    /**
     * 字符串1
     */
    String STRING_ONE = "1";

    /**
     * 字符串2
     */
    String STRING_TWO = "2";

    /**
     * 字符串3
     */
    String STRING_THREE = "3";

    /**
     * 字符串4
     */
    String STRING_FOUR = "4";

    /**
     * 英文星号
     */
    String STRING_STAR = "*";

    /**
     * 英文百分号
     */
    String STRING_PERCENT = "%";

    /**
     * 英文下划线
     */
    String STRING_UNDERLINE = "_";

    /**
     * 加号
     */
    String STRING_PLUS = "+";

    /**
     * 减号
     */
    String STRING_MINUS = "-";

    /**
     * 英文and符号
     */
    String STRING_AMP = "&";

    /**
     * 英文等于符号
     */
    String STRING_EQUAL_SIGN = "=";


    /**
     * 空json
     */
    String EMPTY_JSON_OBJECT = "{}";


    String EMPTY_VALUE_JSON_ARRAY = "[]";

    /***
     * 人民币标志
     */
    String RMB = "¥";

    /**
     * 通用的特殊分隔符
     */
    String COMMON_SEPARATOR = String.valueOf('\u0001');


    /** 字符串unknown */
    String STRING_UNKNOWN = "unknown";

    /**
     * long 0
     */
    long LONG_ZERO = 0L;

    /**
     * long 1
     */
    long LONG_ONE = 1L;

    /**
     * long 2
     */
    long LONG_TWO = 2L;

    /**
     * long 1000
     */
    long LONG_THOUSAND = 1000L;

    /**
     * int -1
     */
    int INT_NEGATIVE_ONE = -1;

    /**
     * int -2
     */
    int INT_NEGATIVE_TWO = -2;

    /**
     * int -3
     */
    int INT_NEGATIVE_THREE = -3;

    /**
     * int 0
     */
    int INT_ZERO = 0;

    /**
     * int 1
     */
    int INT_ONE = 1;

    /**
     * int 2
     */
    int INT_TWO = 2;

    /**
     * int 3
     */
    int INT_THREE = 3;

    /**
     * int 4
     */
    int INT_FOUR = 4;

    /**
     * int 5
     */
    int INT_FIVE = 5;

    /**
     * int 6
     */
    int INT_SIX = 6;

    /**
     * int 7
     */
    int INT_SEVEN = 7;

    /**
     * int 8
     */
    int INT_EIGHT = 8;

    /**
     * int 10
     */
    int INT_TEN = 10;

    /**
     * int 20
     */
    int INT_20 = 20;

    /**
     * int 30
     */
    int INT_30 = 30;

    /**
     * int 40
     */
    int INT_40 = 40;

    /**
     * int 50
     */
    int INT_50 = 50;

    /**
     * int 60
     */
    int INT_60 = 60;

    /**
     * int 70
     */
    int INT_70 = 70;

    /**
     * int 32
     */
    int INT_32 = 32;

    /**
     * int 100
     */
    int INT_100 = 100;

    /**
     * int 170
     */
    int INT_170 = 170;

    /**
     * int 180
     */
    int INT_180 = 180;


    /**
     * int 200
     */
    int INT_200 = 200;

    /**
     * int 300
     */
    int INT_300 = 300;

    /**
     * int 500
     */
    int INT_500 = 500;

    /**
     * int 1000
     */
    int INT_1000 = 1000;

    /**
     * int 3000
     */
    int INT_3000 = 3000;

    /**
     * int 10000
     */
    int INT_10000 = 10_000;

    /**
     * int 100000
     */
    int INT_100_000 = 100_000;


    /**
     * excel 单个文件最大导出条目数
     */
    int XLSX_MAX_EXPORT_ROW_SIZE = 50_000;

    /**
     * es 分页限制, from+size <= 10_000(default)
     */
    int ELASTIC_PAGINATION_LIMIT = 10_000;

    /**
     * 默认使用es 获取文档条目size
     */
    int ELASTIC_DEFAULT_FETCH_SIZE = 5_000;


    /**
     * int 1024
     */
    int INT_1K = 1024;

    /** int 1M */
    int INT_1M = 1024 * INT_1K;

    /** int 15M */
    int INT_15M = 15 * INT_1M;

    /**
     * 一天的秒数
     */
    int HOUR_2_SECONDS = 2 * 3600;

    /**
     * 一天的秒数
     */
    int DAY_SECONDS = 24 * 3600;

    /**
     * 一小时的秒
     */
    int HOUR_SECONDS = 3600;

    /**
     * 手机号判重周期固定为60天
     */
    int DAY_60_SECONDS = 60 * 24 * 3600;

    /**
     * 手机号判重周期固定为60天
     */
    int DAY_90_SECONDS = 90 * 24 * 3600;
    /**
     * 一年的秒数
     */
    int YEAR_SECONDS = 365 * 24 * 3600;

    /**
     * double 0
     */
    double DOUBLE_ZERO = 0D;

    /**
     * 日期格式，只保留  时:分
     */
    String PURE_DATE_HM_PATTERN = "HH:mm";

    /**
     * 日期格式，只保留  时
     */
    String PURE_DATE_YMDH_PATTERN = "HH";

    /**
     * 日期格式  yyyy-MM-dd
     */
    String PURE_DATE_YMD_PATTERN = "yyyy-MM-dd";

    /**
     * 日期格式  yyyyMMdd HHmm
     */
    String PURE_DATE_YMDHM_PATTERN = "yyyyMMdd HH:mm";

    /**
     * 日期格式  yyyyMMddHHmm
     */
    String PURE_DATE_YMDH_INT_PATTERN = "yyyyMMddHH";

    /**
     * 日期格式  yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    String PURE_DATE_YMDHMSS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * 日期格式  yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    String PURE_DATE_YMDHMSSZ_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 日期格式  yyyy-MM-dd'T'HH:mm:ss
     */
    String PURE_DATE_YMDHMS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 日期格式  yyyy年MM月dd日 HH:mm:ss
     */
    String PURE_DATE_C_YMDHMS_PATTERN = "yyyy年MM月dd日 HH:mm:ss";

    /**
     * 日期格式  yyyy年MM月dd日 HH
     */
    String PURE_DATE_C_YMDH_PATTERN = "yyyy年MM月dd日 HH";

    String PURE_DATE_C_YMDHMSSW_PATTERN = "yyyyMMddHHmmssSSSW";

    /**
     * 日期时间格式化常量
     */
    DateTimeFormatter DATE_TIME_FORMAT_MS = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MS_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_S = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_M = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_MINUTE_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_H = DateTimeFormatter.ofPattern(PURE_DATE_YMDH_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_HM = DateTimeFormatter.ofPattern(PURE_DATE_HM_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_Y_M = DateTimeFormatter.ofPattern(PURE_DATE_YMDHM_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_YMDH_INT = DateTimeFormatter.ofPattern(PURE_DATE_YMDH_INT_PATTERN);
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
    DateTimeFormatter DATE_PATTERN_CHINESE = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_PATTERN);
    DateTimeFormatter DATE_FORMAT_P = DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_YMDHHmmss = DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_YMDHMS = DateTimeFormatter.ofPattern(PURE_DATE_YMDHMS_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_C_YMDHMS = DateTimeFormatter.ofPattern(PURE_DATE_C_YMDHMS_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_T_YMDHMS = DateTimeFormatter.ofPattern(PURE_DATE_YMDHMSSZ_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_C_YMDH = DateTimeFormatter.ofPattern(PURE_DATE_C_YMDH_PATTERN);
    DateTimeFormatter DATE_TIME_FORMAT_C_YMDHMSSW = DateTimeFormatter.ofPattern(PURE_DATE_C_YMDHMSSW_PATTERN);


    /** 接口请求限制频次redis key */
    public  static final String VISIT_LIMIT_REDIS_KEY = "VISIT_LIMIT:SERVICE.";
    /** 黑名单redis key */
    public static final String VISIT_LIMIT_BLACKLIST_REDIS_KEY = "VISIT_LIMIT:SERVICE:BLACKLIST";

    /** 内部接口Api的签名验证字符串 */
    public static final String INNER_MD5_ENC_STR = "timestamp=%s&token=%s&body=%s";

    public static final String TRACE_ID = "TRACE_ID";

    public static final String TRACE_ID_FLAG = "kd";

    public static final String REQUEST_ID_HEADER = "RequestId";

    public static final String TRACE_ID_EXECUTOR_FLAG = "kd.executor.";

    public static final String LOGIN_USER_SESSION_KEY = "user_key";

    public static final String LOGIN_USER_Name = "userName";

    public static final String LOCK_USER_SESSION_KEY = "user_lock_key.";


    public static final String DEFAULT_PASSWORD = "666145AB861C2C24D7B114CB82622117";

    public static final long LOCK_USER_SESSION_EXPIRE_TIME = 60 * 30;

    public static final String SYSTEM_ERROR_INFO_SESSION_KEY = "system_error_key";

    public static final String LOGIN_USER_SESSION_ON_LINE_TIME = "user_online_time";

    public static final String REQUEST_SCHEMA = "https";

    public static final String SCHEME = "http";

    public static final int  SESSION_EXPIRE_TIME = 60 * 120;

    public  static final String REGISTER_REDIS_KEY="redis_register_key_";

    public  static final String ADMIN_MANAGER_OPEN_ID="oFGSd57gwTPOBO_5NzN4BVLn372k";



    /**
     * wx-----------------------------
     */

    public  static final String WX_CREATE_MENU_URL="https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";

    public  static final String WX_QUERY_MENU_URL="https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=";

    public  static final String WX_DELETE_MENU_URL="https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";

    public  static final String WX_ACCESS_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/stable_token";

    public  static final String WX_AUTH_ACCESS_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=authorization_code&appid=";

    public  static final String WX_CUSTOMER_SERVICE_URL="https://api.weixin.qq.com/customservice/kfaccount/add?access_token=";

    public  static final String WX_ADD_BLACK_LIST_URL = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=";

    public  static final String WX_USER_URL  = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=";

    public  static final String WX_MODEL_MSG_URL  = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    public  static final String WX_APP_SECRET="secret";

    public  static final String WX_APP_ID="appid";

    public  static final String WX_APP_ACCESS_TOKEN="access_token";

    public  static final String WX_USER_AUTH_OPENID="openid";

    public  static final String WX_USER_AUTH_REFRESH_TOKEN="refreshToken";

    public  static final String WX_USER_AUTH_ACCESS_TOKEN="access_auth_token";

    public  static final int  WX_APP_EXPIRE_IN = 5400;

    public  static final String WX_APP_LOGIN_FLAG="wx_gzw_@14$";

    public  static final String WX_APP_LOGIN_KEY="wx";

    public  static final String FLAG="gzw";

    public static final String  MODEL_ID = "shJziwUfdNvrCvB8LfxuwCJwZw63SUC5tHWYYrvSJ5s";


    /**
     * sign
     */

   public static final String PSN_AUTH_UTL = "/v3/psn-auth-url";

   public static final String API_UTL = "/v3/persons/identity-info?psnAccount=";


    /**
     * 验证码
     */

    public static final String KAPTCHA_SESSION_KEY = "kaptcha_session_key_gao";

    /**
     * 支付宝
     */

    public final static String A_LI_TRADE_SUCCESS = "TRADE_SUCCESS";
    public final static String TRADE_FINISHED = "TRADE_FINISHED";

    public final static String A_LI_OUT_TRADE_NO= "out_trade_no";
    public final static String TRADE_STATUS = "trade_status";

    public final static String SUCCESS_CODE = "10000";



    public final static String ASSIGN_INFO_KEY_ = "assign_info_key_";

    public final static long ASSIGN_INFO_EXPIRE_TIME = 3600 * 6;


    public final static String ORDER_INFO_KEY_ = "order_info_key_";

    public final static long ORDER_INFO_EXPIRE_TIME = 3600 * 6;


    public final static String SERVICE_COFIG = "service_config_key_";



    public final static String CONTENT_DISPOSITION_NAME="Content-Disposition";

    public final static String CONTENT_DISPOSITION_VALUE="attachment;filename=";

    String ATTACHMENT = "attachment";

    public final static String XLSX_EXPORT_FILE_SUFFIX=".xlsx";

    public final static String XLSX_DOWNLOAD_CONTENT_TYPE="application/vnd.ms-excel";

    public final static String DOWNLOAD_FILE_PREFIX="kd";
    /**
     * canal同步到Redis中Key的前缀
     */
    String LEARN_REDIS_KEY_PREFIX = "LEARN_CANAL:";


    /**
     * canal监听的库表
     */
    String LEARN_SCHEMA_TEST_LEARN = "doc.config";


    String COL_ID = "id" ;





    /**
     * 获取canal同步learn表到redis中的key
     * @param learn id
     * @return redis key
     */
    static String getRedisLearnKey(String learnId) {
        return LEARN_REDIS_KEY_PREFIX + LEARN_SCHEMA_TEST_LEARN + STRING_COLON + StrUtil.DELIM_START + learnId + StrUtil.DELIM_END;
    }

    /**
     * 手机号的正则表达式
     */
    String MOBILE_PATTERN = "^1[3-9][0-9]{9}$";

    /**
     * 用户名的正则表达式
     */
    String USERNAME_PATTERN = "^[\u4e00-\u9fa5]*$";


    /**
     * 密码的正则表达式
     */
    String PASSWORD_PATTERN = "/^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[~!@#$%^&*.])[\\da-zA-Z~!@#$%^&*.]{8,}$/";


    /**
     * 特殊字符判断 [全角+半角+中文]
     */
    String USERNAME_FILTER_PATTERN = "[ 　＇'\\\\%％+＋|｜,，/／＼\"＂“”‘’]";

    /**
     * 手机号正则
     */
    String PHONE_REGEX_EXP = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[1,8,9]))\\d{8}$";

    /**
     * 邮箱正则
     */
    String EMAIL_REGEX_EXP = "^[A-Za-z0-9-_\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";


    /**
     * 姓名特殊符号判断
     */
    Pattern usernamePattern = Pattern.compile(USERNAME_FILTER_PATTERN);

    /**
     * es test_data_all index docs type field infos
     */
    String DOCS = "docs";

    /**
     * 过滤姓名中的特殊符号
     *
     * @param username 初始名字
     * @return 过滤后
     */
    static String filterSpecialSymbol(String username) {
        Matcher demandIdMatcher = usernamePattern.matcher(username);
        return demandIdMatcher.replaceAll(StrUtil.EMPTY);
    }


    /** elasticsearch */
    String TARGET_ELASTICSEARCH = "elasticsearch";

    /** elasticsearch */
    String SCHEMA_TEST = "test.learn";

    /**  接口token **/
    String TOKEN_PUSH_KD="3705564dd0b774b746beeabdf5064640";

    /**
     * mail
     */
   public static final String SYSTEM_MAIL_ACCOUNT = "2876533492@qq.com";

   public static final String SYSTEM_MAIL_HOST = "smtp.qq.com";

   public static final String SYSTEM_MAIL_USER = "2876533492@qq.com";

   public static final String SYSTEM_MAIL_PASSWORD = "jcpkpjmpzagwdddj";

    /**
     * SMS
     */
  public static final String SMS_SEND_URL = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";

  public static final String FILE_SUFFIX = ".jpg,.jpeg,.xlsx,.txt,.doc,.png,.pdf,.xls,.docx,.sh,pptx,.mp4,.zip,.sql,.mp3";


    /**
     *  other http  request token
     */


    public static final String   HTTP_USER_TOKEN =  "other-token";

    public static final String KEY_TOKEN_FILTER = "tokenFilter";

    public static final String URL_PATTERN_ALL = "/base/*";


    /**
     * flow controll
     */
    public static final String FLOW_CONTROL_KEY = "flowControlRule";

    public static final String FLOW_CONTROL_PREFIX = "flow_control_";


    public static final String LINK_NAME = "url";


    /**
     * 接口限制 最多的人数
     */
    public static final Integer BATCH_RECEIVER_SIZE = 100;


    /**
     * boolean转换
     */
    public final static Integer TRUE = 1;

    public final static Integer FALSE = 0;


    /**
     * nickName
     */
  public final static String [] DEFAULT_NICK_NAME= {"静谧之夜","心城风起潮落"};

}
