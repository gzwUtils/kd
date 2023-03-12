package com.gzw.kd.common.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.listener.MysessionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.gzw.kd.common.Constants.*;

/**
 * 系统工具类
 */

@SuppressWarnings("all")
@Slf4j
public class ToolUtil {



    public static JsonNode getJsonNode(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            log.error("解析json错误：",e);
        }
        return null;
    }

    /**
     * 对象转json（list,map）
     *
     * @param object object
     * @return string
     */
    public static String objectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("解析json错误:",e);
        }
        return json;
    }

    /**
     * 对象转json（list,map）
     *
     * @param object      json对象
     * @param excludeNull true 排除值为null的属性，false 默认
     * @return
     */
    public static String objectToJson(Object object, boolean excludeNull) {
        ObjectMapper mapper = new ObjectMapper();
        if (excludeNull) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("解析json错误：",e);
        }
        return json;
    }

    /**
     * json 转 list
     *
     * @param json
     * @param elementClasses
     * @return
     * @throws JsonMappingException
     * @throws IOException
     */
    public static List<?> jsonToList(String json, Class<?>... elementClasses)
            throws JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = getCollectionType(mapper, ArrayList.class, elementClasses);
        return (List<?>) mapper.readValue(json, javaType);
    }

    /**
     * 获取泛型的Collection Type
     *
     * @param mapper
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     */
    public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * date 转 String
     *
     * @param date
     * @return yyyy-MM-dd
     */
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = date;
        if (d == null) {
            d = new Timestamp(System.currentTimeMillis());
        }
        return format.format(d);
    }

    /**
     * date 转 String yyyy-MM
     *
     * @param date
     * @return yyyy-MM
     */
    public static String dateToStringYYYYMM(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date d = date;
        if (d == null) {
            d = new Timestamp(System.currentTimeMillis());
        }
        return format.format(d);
    }

    /**
     * 获取今天日期
     *
     * @return yyyy-MM-dd
     */
    public static String today() {
        Date t = new Date(System.currentTimeMillis());
        return dateToString(t);
    }

    /**
     * timestamp 转 String
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String timestampToString(Timestamp date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * date 转 String
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String timeTransitionString(Date time) {
        if (time != null) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(time);
        }
        return "";

    }


    /**
     * yyyy-MM-dd HH:mm:ss 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date StringToDate(String date, String pattern) {
        final ZoneId zoneId = ZoneId.systemDefault();
        if (StringUtils.isBlank(date)) {
            return new Date();
        } else {
            LocalDateTime ldt = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
            ZonedDateTime zdt = ldt.atZone(zoneId);
            return Date.from(zdt.toInstant());
        }
    }

    /**
     * String 转 date
     *
     * @param dateStr
     * @return Date
     */
    public static Date stringToDate(String dateStr) {

        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        if (dateStr.length() < 19) {
            dateStr = dateStr + ":00";
        }
        dateStr = dateStr.replace("/", "-");
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = dateStr.trim();
        if (str.length() < 8) {
            return new Date(System.currentTimeMillis());
        } else {
            return new Date(sp.parse(dateStr, new ParsePosition(0)).getTime());
        }
    }

    /**
     * 文件大小计算
     *
     * @param fileSize
     * @return
     */
    public static String unitConvert(Long fileSize) {
        if (fileSize == null) {
            return "";
        }
        if (fileSize < 1024 && fileSize >= 0) {
            return fileSize + " B";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            return String.format("%.2f", (fileSize.doubleValue() / 1024)) + " KB";
        } else if (fileSize >= (1024 * 1024) && fileSize <= (1024 * 1024 * 1024)) {
            return String.format("%.2f", (fileSize.doubleValue() / (1024 * 1024))) + " MB";
        }
        return "";
    }

    public static String getTomorrowDate() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);

    }

    /**
     * 生成日期编号
     *
     * @param date
     * @return
     */
    public static String dateNumber(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }


    /**
     * 百分比计算
     *
     * @param divisor
     * @param dividend
     * @return
     */
    public static String percentageCalculation(double divisor, double dividend) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        return dividend == 0 ? String.valueOf(0) : numberFormat.format(divisor / dividend * 100);
    }

    /**
     * 返回开始日期和结束日期之间所有的年和月
     *
     * @param startDate
     * @param endDate
     * @param dateFormat yyyyMM或yyyy-MM
     * @return list
     */
    public static List<String> getBetweenTheDateAllMonth(String startDate, String endDate, String dateFormat) {
        SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = new Date();
        Date end = new Date();
        try {
            begin = sdfS.parse(startDate);
            end = sdfS.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> dateList = new ArrayList<String>();
        if (begin.after(end)) {
            return dateList;
        }

        Calendar cal_begin = Calendar.getInstance();
        cal_begin.setTime(begin);

        Calendar cal_end = Calendar.getInstance();
        cal_end.setTime(end);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        while (true) {
            if (cal_begin.get(Calendar.YEAR) == cal_end.get(Calendar.YEAR)
                    && cal_begin.get(Calendar.MONTH) == cal_end.get(Calendar.MONTH)) {
                dateList.add(sdf.format(cal_begin.getTime()));
                break;
            }
            String date = sdf.format(cal_begin.getTime());
            cal_begin.add(Calendar.MONTH, 1);
            cal_begin.set(Calendar.DAY_OF_MONTH, 1);
            dateList.add(date);
        }

        return dateList;
    }

    /**
     * 计算begin和end相差的天数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long dateInterval(String begin, String end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date beginDate = formatter.parse(begin);
            Date endDate = formatter.parse(end);
            return (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000) + 1;
        } catch (ParseException e) {
            log.error("dateInterval error ");
            return 0;
        }
    }

    /**
     * 遍历日期格式处理
     *
     * @param date
     * @param day
     * @return
     */
    public static String addDay(String date, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date beginDate = formatter.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(beginDate);
            cal.add(Calendar.DAY_OF_YEAR, day);
            return formatter.format(cal.getTime());
        } catch (ParseException e) {
            log.error("addDay error");
            return null;
        }
    }

    /**
     * 本周的第一天（星期一的日期）
     */
    public static String thisWeekFirstDay() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        return formatter.format(calendar.getTime());

    }

    /**
     * 本周的最后一天（星期天的日期）
     */
    public static String thisWeekLastDay() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);

        return formatter.format(calendar.getTime());

    }

    /**
     * 本月的最后一天
     */
    public static String thisMonthLastDay() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return formatter.format(calendar.getTime());
    }

    /**
     * 本月的第一天
     */
    public static String thisMonthFirstDay() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return formatter.format(calendar.getTime());

    }

    /**
     * 上周的第一天（星期一的日期）
     */
    public static String lastWeekFirstDay() {
        LocalDate localDate = LocalDate.now().plusWeeks(-1);
        LocalDate lastDay = localDate.with(DayOfWeek.MONDAY);
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    /**
     * 上周的最后一天（星期天的日期）
     */
    public static String lastWeekLastDay() {

        LocalDate localDate = LocalDate.now().plusWeeks(-1);
        LocalDate lastDay = localDate.with(DayOfWeek.SUNDAY);
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    /**
     * 上月的第一天
     *
     * @return
     */
    public static String lastMonthFirstDay() {
        LocalDateTime date = LocalDateTime.now().minusMonths(1);
        LocalDateTime lastDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 上月的最后一天
     *
     * @return
     */
    public static String lastMonthLastDay() {
        LocalDateTime date = LocalDateTime.now().minusMonths(1);
        LocalDateTime firstDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 昨天日期
     *
     * @return
     */
    public static String yesterday() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now().minus(1, ChronoUnit.DAYS));
    }

    /**
     * 当前时间推移天数 正数代表当前时间往前
     *
     * @param days 天数
     * @return String
     */
    public static String presentTimePushDays(int days) {
        LocalDateTime ldt = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 当前时间推移天数 正数代表当前时间往前   yyyy-MM-dd
     *
     * @param days 天数
     * @return String
     */
    public static String presentTimePushDaysYYYYMMDD(int days) {
        LocalDateTime ldt = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 获取从当前时间开始到指定月份的当前时间
     *
     * @param month
     * @return
     */
    public static String specifiedInBeforeTime(int month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.MONTH, -month);
        return format.format(ca.getTime()) + " 23:59:59";
    }

    /**
     * 当前日期向后退N个月
     *
     * @param month
     * @return yyyy-MM-dd
     */
    public static String nowDateBackMonth(int month) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.add(2, month - (month * 2));
        gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
        return sf.format(gc.getTime());
    }

    /**
     * 最近一个月的开始日期 ,yyyy-MM-dd
     *
     * @return
     */
    public static String latelyOneMonthStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -29);
        return sdf.format(now.getTime());
    }

    /**
     * 最近一年开始日期，yyyy-MM-dd
     *
     * @return
     */
    public static String latelyOneYearStartDate() {
        return nowDateBackMonth(11);
    }


    /**
     * 获取Map中值对应的所有key
     *
     * @param map
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Set<K> getMapKeys(Map<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(kvEntry -> Objects.equals(kvEntry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * 文件hash字符串转long后插入long数组中
     *
     * @param hashString
     * @param longs
     * @param index      开始插入索引位置
     * @return
     */
    public static long[] hashStringToLongs(String hashString, long[] longs, int index) {
        String[] split = hashString.split(",");
        for (int i = 0; i < split.length; i++) {
            longs[index] = Long.parseLong(split[i]);
            index++;
        }
        return longs;
    }

    /**
     * long数组转字符串，用逗号分隔
     *
     * @param longs
     * @return
     */
    public static String hashLongToString(long[] longs) {
        if (longs.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < longs.length; i++) {
            if (0 != i) {
                sb.append(",");
            }
            sb.append(longs[i]);
        }
        return sb.toString();
    }

    /**
     * 二进制转十进制
     *
     * @param number
     * @return
     */
    public static int binary2Decimal(String number) {
        return scale2Decimal(number, 2);
    }

    /**
     * 其他进制转十进制
     *
     * @param number
     * @return
     */
    public static int scale2Decimal(String number, int scale) {
        checkNumber(number);
        if (2 > scale || scale > 32) {
            throw new IllegalArgumentException("溯源码不在刻度范围内！");
        }
        // 不同其他进制转十进制,修改这里即可
        int total = 0;
        String[] ch = number.split("");
        int chLength = ch.length;
        for (int i = 0; i < chLength; i++) {
            total += Integer.parseInt(ch[i]) * Math.pow(scale, chLength - 1 - i);
        }
        return total;
    }

    /**
     * 数字验证
     *
     * @param number
     */
    public static void checkNumber(String number) {
        String regexp = "^\\d+$";
        if (null == number || !number.matches(regexp)) {
            throw new IllegalArgumentException("溯源码格式不正确！");
        }
    }

    /**
     * 使用 JAXB 对象转xml
     *
     * @param object 需要转换的对象
     * @return xml 字符串
     * @throws JAXBException 异常
     */
    public static String objToXmlByJAXB(Object object) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream base = new ByteArrayOutputStream();
        marshaller.marshal(object, base);
        return new String(base.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * JAXB xml 字符串 转 对象
     *
     * @param xml  字符串
     * @param load Class
     * @return Object
     * @throws JAXBException 异常
     */
    public static Object xmlToObjByJAXB(String xml, Class<?> load) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(load);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader stringReader = new StringReader(xml);
        return unmarshaller.unmarshal(stringReader);
    }


    /**
     * 登录成功后赋权限
     *
     * @param request
     * @param account
     * @param loginType
     * @return
     */
    public static Operator loginSuccess(HttpServletRequest request, Operator account, String loginType) {
        HttpSession session = request.getSession();
        if (!"switchFlag".equals(loginType)) {
            // 首先将原session中的数据转移至一临时map中
            Map<String, Object> tempMap = new HashMap<String, Object>();
            Enumeration<String> sessionNames = session.getAttributeNames();
            while (sessionNames.hasMoreElements()) {
                String sessionName = sessionNames.nextElement();
                tempMap.put(sessionName, session.getAttribute(sessionName));
            }
            // 注销原session，为的是重置sessionId
            session.invalidate();
            // 将临时map中的数据转移至新session
            session = request.getSession();
            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
                session.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        account.setLastActiveTime(System.currentTimeMillis());
        account.setLoginWay(STRING_ZERO);
        account.setTarget(INT_ZERO);
        session.setAttribute(LOGIN_USER_SESSION_KEY, account);
        session.setMaxInactiveInterval(SESSION_EXPIRE_TIME);
        session.setAttribute(LOGIN_USER_SESSION_ON_LINE_TIME+account.getAccount(),LocalDateTime.now().format(DATE_TIME_FORMAT_S));
        MysessionListener.sessionContext.getSessionMap().put(account.getAccount(), session);
        return account;
    }

}
