package cn.xianyum.common.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author zhangwei
 * @date 2019/1/31 14:03
 */
public class DateUtils {
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** 时间格式(HH:mm:ss) */
    public final static String TIME_PATTERN = "HH:mm:ss";

    public final static String START_DATE_PATTERN = DATE_PATTERN +" 00:00:00";

    public final static String END_DATE_PATTERN = DATE_PATTERN +" 23:59:59";

    public final static String YYYY_MM = "yyyy-MM";

    public final static String YYYY = "yyyy";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return  返回yyyy-MM-dd格式日期
     */
    public static String format(Date date) {
        return format(date, DATE_TIME_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return  返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 字符串转换成日期
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtil.isBlank(strDate)){
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 字符串转换成日期
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate) {
        return stringToDate(strDate,DATE_TIME_PATTERN);
    }
    /**
     * 根据周数，获取开始日期、结束日期
     * @param week  周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return  返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date 日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date 日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date 日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date 日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date 日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date 日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }
    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 将秒转为时分秒字符串
     * @param second
     * @return
     */
    public static String getDatePoor(Long second) {
        if(Objects.isNull(second)){
            return null;
        }
        // 计算小时、分钟和剩余秒数
        long hours = second / 3600;  // 1小时 = 3600秒
        long minutes = (second % 3600) / 60;  // 剩余的秒数转成分钟
        long remainingSeconds = second % 60;  // 剩余的秒数
        // 设置格式化的字符串，包含小时、分钟和秒
        return hours + "小时" + minutes + "分" + remainingSeconds + "秒";
    }

    /**
     * 获取过去的时间字符串
     * @param date
     * @param pattern
     * @param day
     * @return
     */
    public static List<String> minusDate(Date date, String pattern, int day) {
        List<String> dateStrs = new ArrayList<>();
        DateTime dateTime = new DateTime(date);
        for(int i = 1 ;i <= day;i++){
            DateTime minusDateTime = dateTime.minusDays(i);
            dateStrs.add(minusDateTime.toString(pattern));
        }
        return dateStrs;
    }

    /**
     * 判断当前时间是否在两个时间之内（以HH:mm:ss为准）
     * @param limitSendStartTime
     * @param limitSendEndTime
     * @return
     */
    public static boolean checkNowBetweenTime(Date limitSendStartTime, Date limitSendEndTime) {
        Date nowTime = new Date();
        Date nowDate = DateUtils.stringToDate(DateUtils.format(nowTime, TIME_PATTERN), TIME_PATTERN);
        Date startDate = DateUtils.stringToDate(DateUtils.format(limitSendStartTime, TIME_PATTERN), TIME_PATTERN);
        Date endDate = DateUtils.stringToDate(DateUtils.format(limitSendEndTime, TIME_PATTERN), TIME_PATTERN);
        // startDate<nowDate<endDate
        if(startDate.before(nowDate) && endDate.after(nowDate)){
            return true;
        }
        return false;
    }

    /**
     * 判断是否周末
     * @return
     */
    public static boolean isWeek(){
        DateTime dateTime = new DateTime();
        int dayOfWeek = dateTime.getDayOfWeek();
        // 判断是否为周末
        return  (dayOfWeek == 6 || dayOfWeek == 7);
    }
    /**
     * 获取本月的开始时间（第一天的 00:00:00）
     * @return Date 本月开始时间
     */
    public static Date getMonthStartTime() {
        return new DateTime(DateTimeZone.getDefault())
                .dayOfMonth().withMinimumValue()  // 设置为当月第一天
                .hourOfDay().withMinimumValue()   // 小时: 0
                .minuteOfHour().withMinimumValue() // 分钟: 0
                .secondOfMinute().withMinimumValue() // 秒: 0
                .millisOfSecond().withMinimumValue() // 毫秒: 0
                .toDate();
    }

    /**
     * 获取本月的结束时间（最后一天的 23:59:59）
     * @return Date 本月结束时间
     */
    public static Date getMonthEndTime() {
        return new DateTime(DateTimeZone.getDefault())
                .dayOfMonth().withMaximumValue()  // 设置为当月最后一天
                .hourOfDay().withMaximumValue()   // 小时: 23
                .minuteOfHour().withMaximumValue() // 分钟: 59
                .secondOfMinute().withMaximumValue() // 秒: 59
                .millisOfSecond().withMaximumValue() // 毫秒: 999
                .toDate();
    }

    /**
     * 获取上个月的开始时间：上个月1号 00:00:00.000
     * @return Date 类型
     */
    public static Date getLastMonthStartTime() {
        return new DateTime(DateTimeZone.getDefault())
                .dayOfMonth().setCopy(1)          // 先设置为当月1号
                .minusMonths(1)                  // 减去1个月 → 上个月1号
                .hourOfDay().setCopy(0)          // 小时：00
                .minuteOfHour().setCopy(0)       // 分钟：00
                .secondOfMinute().setCopy(0)     // 秒：00
                .millisOfSecond().setCopy(0)     // 毫秒：000
                .toDate();
    }

    /**
     * 获取上个月的结束时间：上个月最后1天 23:59:59.999
     * @return Date 类型
     */
    public static Date getLastMonthEndTime() {
        return new DateTime(DateTimeZone.getDefault())
                .dayOfMonth().setCopy(1)          // 先设置为当月1号
                .minusMonths(1)                  // 减去1个月 → 上个月1号
                .dayOfMonth().withMaximumValue() // 上个月最后1天
                .hourOfDay().withMaximumValue()  // 小时：23
                .minuteOfHour().withMaximumValue() // 分钟：59
                .secondOfMinute().withMaximumValue() // 秒：59
                .millisOfSecond().withMaximumValue() // 毫秒：999
                .toDate();
    }

    /**
     * 获取近一年的开始日期（当前日期往前推12个月的日期，时间为00:00:00）
     * @return Date 近一年开始日期
     */
    public static Date getLastYearStartTime() {
        return new DateTime(DateTimeZone.getDefault())
                .minusYears(1)                      // 往前推12个月
                .withTimeAtStartOfDay()             // 设置时间为00:00:00
                .toDate();
    }

    /**
     * 获取近一年的结束日期（当前日期，时间为23:59:59）
     * @return Date 近一年结束日期
     */
    public static Date getLastYearEndTime() {
        return new DateTime(DateTimeZone.getDefault())
                .withHourOfDay(23)                  // 小时设置为23
                .withMinuteOfHour(59)               // 分钟设置为59
                .withSecondOfMinute(59)             // 秒设置为59
                .withMillisOfSecond(999)            // 毫秒设置为999
                .toDate();
    }
}
