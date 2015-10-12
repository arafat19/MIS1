package com.athena.mis.utility

import java.sql.Timestamp
import java.text.SimpleDateFormat

class DateUtility {

    public static final String dd_MM_yyyy_NONE = "yyyyMMdd"
    public static final String dd_MM_yyyy_SLASH = "dd/MM/yyyy"
    public static final String dd_MMM_yyyy_DASH = "dd-MMM-yyyy"
    public static final String dd_MMMM_yyyy_DASH = "dd-MMMM-yyyy"
    private static final String dd_MMMM_yyyy_hh_mm_a = "dd MMMM, yyyy [hh:mm a]"
    private static final String DATE_TIME_LONG = "dd-MMM-yy [hh:mm:ss]"
    private static final String DATE_TIME_LONG_AM_PM = "dd-MMM-yy [hh:mm:ss a]"
    private static final String DATE_TIME_SHORT = "dd-MM-yy [hh:mm:ss a]"
    private static final String DATE_LONG = "dd-MMM-yy"
    private static final String DATE_SHORT= "dd-MM-yy"
    public static final String yyyy_MM_dd_DASH = "yyyy-MM-dd"
    private static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    private static final String HH_mm_ss = "HH:mm:ss"
    private static final String HH_mm_a = "hh:mm a"
    private static final String TIME_START = "00:00:00"
    private static final String TIME_END = "23:59:59"
    private static final String dd_MMM_yy_hh_mm_ss = "dd-MMM-yy hh:mm:ss"
    private static final String dd_MMM_yy_DASH = "dd-MMM-yy"
    public static final int DATE_RANGE_THIRTY = 30
    public static final int DATE_RANGE_SEVEN = 7
    public static final int DATE_RANGE_HUNDREAD_EIGHTY = 180
    public static final String FINANCIAL_YEAR_EXCEEDS = "Given date range is not within current financial year"
    public static final String FINANCIAL_YEAR_EXCEEDS_MSG = "Given date is not within current financial year"

    public static Date getOnlyDate(Date dtParam) {
        Date dt = dtParam
        Calendar calDate = Calendar.getInstance()
        calDate.setTime(dt);
        calDate.set(Calendar.HOUR, 0);
        calDate.set(Calendar.MINUTE, 0);
        calDate.set(Calendar.SECOND, 0);
        calDate.set(Calendar.MILLISECOND, 0);
        calDate.set(Calendar.AM_PM, Calendar.AM);
        dt = calDate.getTime()
        return dt
    }

    // getStringDateForUI has no usage in Project(upto 19 apr 2011), check & delete

    public static String getStringDateForUI(String sdt) {
        Date dt = getDateFromString(sdt)
        return getDateForUI(dt)
    }

    public static Date getDateFromString(String strDt) {
        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MM_yyyy_SLASH) //"dd/MM/yyyy"
        if (strDt == null) {
            return null
        }
        Date dt = null
        try {
            dt = simpleDF.parse(strDt);
            return dt
        } catch (Exception e) {
            return null
        }
    }

    public static String getDateFormatAsString(Date dt) {
        if (dt != null) {
            return dt.format(dd_MMMM_yyyy_DASH)   //"dd-MMMM-yyyy"
        } else
            return Tools.EMPTY_SPACE
    }

    public static String getDateForUI(Date dt) {
        if (dt != null) {
            return dt.format(dd_MM_yyyy_SLASH)  //'dd/MM/yyyy'
        } else
            return Tools.EMPTY_SPACE
    }

    public static String getDateTimeFormatAsString(Date dt) {
        if (dt != null) {
            return dt.format(dd_MMMM_yyyy_hh_mm_a) // "dd MMMM,yyyy [hh:mm a]"
        } else
            return Tools.EMPTY_SPACE
    }



    //NOTE:  parseMaskedDate() method has 118 usage in project

    public static Date parseMaskedDate(String paramDate) {

        if (!paramDate) {
            return null
        }

        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MM_yyyy_SLASH)  // "dd/MM/yyyy"

        Date retDate;
        try {
            retDate = simpleDF.parse(paramDate);
        } catch (Exception e) {
            return null
        }
        return retDate;
    }

    public static Date parseDateFromBankStatement(String paramDate) {

        if (!paramDate) {
            return null
        }

        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MM_yyyy_NONE)  // "dd/MM/yyyy"

        Date retDate;
        try {
            retDate = simpleDF.parse(paramDate);
        } catch (Exception e) {
            return null
        }
        return retDate;
    }

    // parse masked date and set time  First hour
    public static Date parseMaskedFromDate(String paramDate) {
        if (!paramDate) {
            return null
        }
        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MM_yyyy_SLASH)  // "dd/MM/yyyy"
        Date returnDate;
        try {
            returnDate = simpleDF.parse(paramDate);
            return setFirstHour(returnDate)
        } catch (Exception e) {
            return null
        }
    }

    // parse masked date and set time Last hour
    public static Date parseMaskedToDate(String paramDate) {
        if (!paramDate) {
            return null
        }
        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MM_yyyy_SLASH)  // "dd/MM/yyyy"
        Date returnDate;
        try {
            returnDate = simpleDF.parse(paramDate);
            return setLastHour(returnDate)
        } catch (Exception e) {
            return null
        }
    }

    public static Date setFirstHour(Date paramDate) {
        if (!paramDate) return null
        Calendar cal = Calendar.getInstance()
        cal.setTime(paramDate)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }

    public static Date setLastHour(Date paramDate) {
        if (!paramDate) return null
        Calendar cal = Calendar.getInstance()
        cal.setTime(paramDate)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }

    public static String getDBDateFormat(Date date) {
        String dbDate = ''
        if ((!date) || (!date instanceof Date)) return dbDate
        dbDate = date.format(yyyy_MM_dd_DASH).toString()    // 'yyyy-MM-dd'
        return dbDate
    }

    public static String getDBDateFormatWithSecond(Date date) {
        String dbDate = ''
        if (!date instanceof Date) return dbDate
        dbDate = date.format(yyyy_MM_dd_HH_mm_ss).toString()  // 'yyyy-MM-dd HH:mm:ss'
        return dbDate
    }

    // method should be used in namedParameters of type Date
    public static java.sql.Date getSqlDate(Date dt) {
        if (!dt) return null
        java.sql.Date sqlDate = new java.sql.Date(dt.getTime())
        return sqlDate
    }
    // method should be used in namedParameters of type TimeStamp
    public static Timestamp getSqlDateWithSeconds(Date dt) {
        if (!dt) return null
        Calendar calDate = Calendar.getInstance()
        calDate.setTime(dt)
        calDate.set(Calendar.MILLISECOND, 0);
        Timestamp timestamp = new Timestamp(calDate.timeInMillis)
        return timestamp
    }

    // method should be used in namedParameters of type TimeStamp  (first hour)
    public static Timestamp getSqlFromDateWithSeconds(Date dt) {
        if (!dt) return null
        Calendar calDate = Calendar.getInstance()
        calDate.setTime(dt)
        calDate.set(Calendar.HOUR_OF_DAY, 0)
        calDate.set(Calendar.MINUTE, 0)
        calDate.set(Calendar.SECOND, 0)
        calDate.set(Calendar.MILLISECOND, 0)
        Timestamp timestamp = new Timestamp(calDate.timeInMillis)
        return timestamp
    }

    // method should be used in namedParameters of type TimeStamp  (first hour)
    public static Timestamp getSqlToDateWithSeconds(Date dt) {
        if (!dt) return null
        Calendar calDate = Calendar.getInstance()
        calDate.setTime(dt)
        calDate.set(Calendar.HOUR_OF_DAY, 23)
        calDate.set(Calendar.MINUTE, 59)
        calDate.set(Calendar.SECOND, 59)
        calDate.set(Calendar.MILLISECOND, 0)
        Timestamp timestamp = new Timestamp(calDate.timeInMillis)
        return timestamp
    }

    public static String getFromDateWithSecond(Date date) {
        String dbDate = Tools.EMPTY_SPACE
        if (!date instanceof Date) return dbDate
        dbDate = date.format(yyyy_MM_dd_DASH).toString()  // 'yyyy-MM-dd'
        dbDate = dbDate + Tools.SINGLE_SPACE  + TIME_START              //'00:00:00'
        return dbDate
    }

    public static String getToDateWithSecond(Date date) {
        String dbDate = Tools.EMPTY_SPACE
        if (!date instanceof Date) return dbDate
        dbDate = date.format(yyyy_MM_dd_DASH).toString()    // 'yyyy-MM-dd'
        dbDate = dbDate + Tools.SINGLE_SPACE + TIME_END                    // '23:59:59'
        return dbDate
    }

    // used to read Disbursed mobile task transaction date
    private static SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(dd_MMM_yy_hh_mm_ss)
    // "dd-MMM-yy hh:mm:ss"
    public static Date parseDateFormMobileCsv(String paramDate) {

        if (paramDate == null) {
            return null
        }
        try {
            return simpleDateFormatter.parse(paramDate);
        } catch (Exception ex) {
            return null
        }
    }

    // parse expiration date from mobile csv (Accepted,Rejected,Disbursed)
    public static Date parseExpiryDateFromMobileCsv(String strDt) {
        if (strDt == null) {
            return null
        }
        SimpleDateFormat simpleDF = new SimpleDateFormat(dd_MMM_yyyy_DASH) // "dd-MMM-yyyy"
        Date dt = null
        try {
            dt = simpleDF.parse(strDt);
            return dt
        } catch (Exception e) {
            return null
        }
    }


    public static String getLongDateForUI(Date dt) {
        if (dt != null) {
            return dt.format(dd_MMM_yyyy_DASH)  //'dd-Mon-yyyy'
        } else {
            return Tools.EMPTY_SPACE
        }
    }

    public static String getDateForSMS(Date dt) {
        if (dt != null) {
            return dt.format(dd_MMM_yy_DASH)  //'dd-Mon-yy'
        } else {
            return Tools.EMPTY_SPACE
        }
    }

    //method used to get time from date, it has been used to generate StoreDetails report
    public static String getTimeFromDate(Date dt) {
        if (dt != null) {
            return dt.format(HH_mm_a)  //HH:mm AM/PM
        } else {
            return ""
        }
    }


    public static Date addOneSecond(Date dt) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(dt)
        cal.add(Calendar.SECOND, 1)
        return cal.getTime()
    }

    ///check date  range in financial year

    public static String checkFinancialDateRange(Date startDate, Date endDate, Object financialYear) {
        if ((startDate < financialYear.startDate) || (endDate > financialYear.endDate)) {
            return FINANCIAL_YEAR_EXCEEDS
        }
        return null
    }
    //check single date in financial year

    public static String checkFinancialDate(Date toDate, Object financialYear) {
        if ((toDate < financialYear.startDate) || (toDate > financialYear.endDate)) {
            return FINANCIAL_YEAR_EXCEEDS_MSG
        }
        return null
    }

    private static final String INVALID_DATE_PARAMS = "Invalid date parameters"
    private static final String DAYS = " Day(s) "
    private static final String HOURS = " Hour(s) "
    private static final String MINUTES = " Minute(s) "
    private static final String SECONDS = " Second(s)"
    // get the difference to two dates in explicit readable format
    public static String getDifference(Date fromDate, Date toDate) {
        try {
            if (fromDate > toDate) {
                return INVALID_DATE_PARAMS
            }
            long diffMilli = toDate.getTime() - fromDate.getTime() //in milliseconds
            long diffSeconds = Math.floor(diffMilli / 1000) % 60
            long diffMinutes = Math.floor(diffMilli / (60 * 1000)) % 60
            long diffHours = Math.floor(diffMilli / (60 * 60 * 1000)) % 24
            long diffDays = Math.floor(diffMilli / (24 * 60 * 60 * 1000))

            String strDifference = diffDays > 0 ? (diffDays + DAYS) : Tools.EMPTY_SPACE
            strDifference += diffHours > 0 ? (diffHours + HOURS) : Tools.EMPTY_SPACE
            strDifference += diffMinutes > 0 ? (diffMinutes + MINUTES) : Tools.EMPTY_SPACE
            strDifference += diffSeconds > 0 ? (diffSeconds + SECONDS) : Tools.EMPTY_SPACE
            if (strDifference.length() == 0) strDifference = Tools.STR_ZERO + Tools.SINGLE_SPACE + SECONDS
            return strDifference
        } catch (Exception e) {
            return INVALID_DATE_PARAMS
        }
    }

    public static String getToDateForUI() {
       return new Date().format(dd_MM_yyyy_SLASH)
    }

    public static String getFromDateForUI(int diff) {
        return (new Date()-diff).format(dd_MM_yyyy_SLASH)
    }

    //@todo: keep existing methods and clean all unnecessary methods from projects

    public static String formatDateTimeLong(Date dt) {
        if (dt == null) {
            return Tools.EMPTY_SPACE
        }
        return dt.format(DATE_TIME_LONG) // "dd-MMM-yy [hh:mm:ss a]"

    }
    public static String formatDateTimeShort(Date dt) {
        if (dt == null) {
            return Tools.EMPTY_SPACE
        }
        return dt.format(DATE_TIME_SHORT) // "dd-MM-yy [hh:mm:ss a]"
    }

    public static String formatDateTimeLongAmPm(Date dt) {
        if (dt == null) {
            return Tools.EMPTY_SPACE
        }
        return dt.format(DATE_TIME_LONG_AM_PM) // "dd-MMM-yy [hh:mm:ss a]"

    }

    public static String formatDateLong(Date dt) {
        if (dt == null) {
            return Tools.EMPTY_SPACE
        }
        return dt.format(DATE_LONG) // "dd-MMM-yy"
    }
    public static String formatDateShort(Date dt) {
        if (dt == null) {
            return Tools.EMPTY_SPACE
        }
        return dt.format(DATE_SHORT) // "dd-MM-yy"
    }
}
