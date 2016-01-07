package com.athena.mis.utility

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat

import javax.servlet.http.HttpServletRequest
import java.text.DecimalFormat

class Tools {
    public static final int PLEASE_SELECT_VALUE = -1
    public static final String PLZ_SELECT_VALUE = '-1'
    public static final String PLEASE_SELECT_LEVEL = 'Please Select...'
    public static final String DASH = "_"
    public static final String dd_MM_yyyy_SLASH = "dd/MM/yyyy"

    public static final String PAGE = "page"
    public static final String TOTAL = "total"
    public static final String ROWS = "rows"
    public static final String ID = "id"
    public static final String COUNT = "count"
    public static final String FROM = "From "
    public static final String TO = " To "
    public static final String IN = "IN"
    public static final String PER = " per "
    public static final String PNG_EXTENSION = ".png"


    public static final String PLUS = "+"
    public static final String MINUS = "-"

    public static final String PERCENTAGE = "%"
    public static final String COMA = ","
    public static final String SINGLE_QUOTE = "'"
    public static final String GRATER_SIGN = ">"
    public static final String EMPTY_SPACE_COMA = " , "
    public static final String EMPTY_SPACE = ''
    public static final String SINGLE_SPACE = ' '
    public static final String PARENTHESIS_START = " ( "
    public static final String PARENTHESIS_END = " ) "
    public static final String THIRD_BRACKET_START = "["
    public static final String THIRD_BRACKET_END = "]"
    public static final String SEMICOLON = ";"
    public static final String COLON = ":"
    public static final String SINGLE_DOT = "."
    public static final String THREE_DOTS = "..."
    public static final String QUESTION_SIGN = "?"
    public static final String UNDERSCORE = "_"
    public static final String HYPHEN = "-"
    public static final String SLASH = "/"
    public static final String STR_ZERO = "0"
    public static final String STR_ZERO_DECIMAL = "0.00"
    public static final String NOT_APPLICABLE = "N/A"
    public static final String NONE = "None"
    public static final String LABEL_NEW = "New"    // used in place of grid object id
    public static final String HAS_ACCESS = "hasAccess"
    public static final String IS_VALID = "isValid"
    public static final String IS_ERROR = 'isError'
    public static final String MESSAGE = 'message'
    public static final String DELETED = "deleted"
    public static final String MAIL_SENDING_ERR_MSG = 'mailSendingErrMsg'
    public static final String ENTITY = 'entity'
    public static final String GRID_ENTITY = 'gridEntity'
    public static final String VERSION = 'version'
    public static final String HAS_ASSOCIATION = "hasAssociation"
    public static final String YES = "YES"
    public static final String TRUE = 'true'
    public static final String FALSE = 'false'
    public static final String NO = "NO"
    public static final String ALL = "ALL"
    public static final String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid inputs"
    // Date range of searching in Filter panels
    private static String REPORT_DIRECTORY = null;
    private static String REPORT_DIRECTORY_ACC = null;
    private static String REPORT_DIRECTORY_BUDGET = null;
    private static String REPORT_DIRECTORY_FIXED_ASSET = null;
    private static String REPORT_DIRECTORY_PROJECT_TRACK = null;
    private static String REPORT_DIRECTORY_EXCHANGE_HOUSE = null;
    private static String REPORT_DIRECTORY_INV = null;
    private static String REPORT_DIRECTORY_PROC = null;
    private static String REPORT_DIRECTORY_QS = null;
    private static String REPORT_DIRECTORY_ARMS = null;
    private static String REPORT_DIRECTORY_SARB = null;

    public static final String COMMON_REPORT_DIR = 'COMMON_REPORT_DIR'

    public static
    final String BACK_DATED_INVENTORY_TRANSACTION_DELETE_PROHIBITED = "Back dated inventory transaction cannot be deleted, Please contact with administrator.";
    public static
    final String BACK_DATED_INVENTORY_TRANSACTION_UPDATE_PROHIBITED = "Back dated inventory transaction cannot be edited, Please contact with administrator.";

    private static final String REGEX_SYMBOL_START = "\\Q"
    private static final String REGEX_SYMBOL_END = "\\E"
    public static final String NEW_LINE = "\r\n"

    //VAT and AIT for project status report
    public static final float VAT = 0.05 // 5%
    public static final float AIT = 0.055 // 5.5%

    //download file extension
    public static final String PDF_EXTENSION = ".pdf"
    public static final String XLS_EXTENSION = ".xls"
    public static final String CSV_EXTENSION = ".csv"
    public static final String XML_EXTENSION = ".xml"
    public static final String FORMAT_TYPE_NAME_PDF = "pdf"
    public static final String FORMAT_TYPE_NAME_XLS = "xls"
    public static final String FORMAT_TYPE_NAME_CSV = "csv"


    public static String escapeForRegularExpression(String str) {
        if (str == null) return null
        String newStr = REGEX_SYMBOL_START + str + REGEX_SYMBOL_END
        return newStr
    }

    public static String getAccountingReportDirectory() {
        if (!REPORT_DIRECTORY_ACC) {
            String pathDir = ConfigurationHolder.config.application.plugins.accounting.directory + "/reports/accounting";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_ACC = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_ACC;
    }

    public static String getBudgetReportDirectory() {
        if (!REPORT_DIRECTORY_BUDGET) {
            String pathDir = ConfigurationHolder.config.application.plugins.budget.directory + "/reports/budget";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_BUDGET = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_BUDGET;
    }

    public static String getInventoryReportDirectory() {
        if (!REPORT_DIRECTORY_INV) {
            String pathDir = ConfigurationHolder.config.application.plugins.inventory.directory + "/reports/inventory";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_INV = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_INV;
    }

    public static String getProcurementReportDirectory() {
        if (!REPORT_DIRECTORY_PROC) {
            String pathDir = ConfigurationHolder.config.application.plugins.procurement.directory + "/reports/procurement";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_PROC = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_PROC;
    }

    public static String getQsReportDirectory() {
        if (!REPORT_DIRECTORY_QS) {
            String pathDir = ConfigurationHolder.config.application.plugins.qs.directory + "/reports/qs";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_QS = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_QS;
    }

    public static String getFixedAssetReportDirectory() {
        if (!REPORT_DIRECTORY_FIXED_ASSET) {
            String pathDir = ConfigurationHolder.config.application.plugins.fixedasset.directory + "/reports/fixedasset";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_FIXED_ASSET = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_FIXED_ASSET;
    }

    public static String getExchangeHouseReportDirectory() {
        if (!REPORT_DIRECTORY_EXCHANGE_HOUSE) {
            String pathDir = ConfigurationHolder.config.application.plugins.exchangehouse.directory + "/reports/exchangehouse";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_EXCHANGE_HOUSE = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_EXCHANGE_HOUSE;
    }

    public static String getCommonReportDirectory() {
        if (!REPORT_DIRECTORY) {
            String pathDir = ConfigurationHolder.config.application.plugins.applicationplugin.directory + "/reports";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY;
    }

    public static String getProjectTrackReportDirectory() {
        if (!REPORT_DIRECTORY_PROJECT_TRACK) {
            String pathDir = ConfigurationHolder.config.application.plugins.projecttrack.directory + "/reports/projectTrack";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_PROJECT_TRACK = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_PROJECT_TRACK;
    }

    public static String getArmsReportDirectory() {
        if (!REPORT_DIRECTORY_ARMS) {
            String pathDir = ConfigurationHolder.config.application.plugins.arms.directory + "/reports/rms";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_ARMS = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_ARMS;
    }

    public static String getSarbReportDirectory() {
        if (!REPORT_DIRECTORY_SARB) {
            String pathDir = ConfigurationHolder.config.application.plugins.sarb.directory + "/reports/sarb";
            File reportFolder = ApplicationHolder.application.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_SARB = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_SARB;
    }

    public static String buildCommaSeparatedStringOfIds(List ids) {
        String strIds = EMPTY_SPACE
        for (int i = 0; i < ids.size(); i++) {
            strIds = strIds + ids[i]
            if ((i + 1) < ids.size()) strIds = strIds + COMA
        }
        return strIds
    }

    // Build common source dropDown for UI only with Name and ID
    public static List buildSourceDropDown(List lst, String key) {
        List lstDropDown = []
        if ((lst == null) || (lst.size() <= 0))
            return lstDropDown
        for (int i = 0; i < lst.size(); i++) {
            Object obj = lst[i]
            lstDropDown << [id: obj.id, name: obj.getAt(key)]
        }
        return lstDropDown
    }

    public static List<Long> getIds(List lstObjects) {
        List<Long> lstIds = []
        if ((lstObjects == null) || (lstObjects.size() <= 0))
            return lstIds
        for (int i = 0; i < lstObjects.size(); i++) {
            lstIds << (Long) lstObjects[i].id
        }
        return lstIds
    }

    /**
     * Get List of long ids from UI parameter
     * @param params -GrailsParameterMap containing underscore separated string of ids
     * @param key -key to get ids from GrailsParameterMap
     * @return -list of long ids
     */
    public static List<Long> getIdsFromParams(Map params, String key) {
        List<Long> lstIds = []
        String str = (String) params.get(key)
        List lstStringIds = str.split(Tools.UNDERSCORE)
        for (int i = 0; i < lstStringIds.size(); i++) {
            lstIds << Long.parseLong(lstStringIds[i].toString())
        }
        return lstIds
    }

    // Following method receive a list of objects and return List of corresponding long IDs
    private static final String STR_KEY_ID = 'id'

    public static List buildListOfIds(List lstMain) {
        List<Long> lstIds = []
        for (int i = 0; i < lstMain.size(); i++) {
            Long id = (Long) lstMain[i].properties.get(STR_KEY_ID)
            lstIds << id
        }
        return lstIds
    }

    public static final int DEFAULT_LENGTH_DETAILS_OF_BUDGET = 29;
    public static final int DEFAULT_LENGTH_DETAILS_OF_BUDGET_FOR_REPORT = 60;
    public static final int DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG = 100;
    public static final int DEFAULT_LENGTH_DETAILS_OF_AREA_DES = 100;
    public static final int DEFAULT_LENGTH_DETAILS_OF_SMS_BODY = 35;
    public static final int DEFAULT_LENGTH_DETAILS_OF_SMS_DES = 50;
    public static final int DEFAULT_LENGTH_DETAILS_OF_INDENT = 90;


    public static String makeDetailsShort(String details, int length) {
        if (!details) return EMPTY_SPACE
        if (details.length() > length) {
            details = details.substring(0, length)
            details = details + THREE_DOTS
        }
        return details
    }

    private static final String AMOUNT_FORMAT = "৳ ##,##,##0.00"
    private static final String AMOUNT_FORMAT_WITHOUT_CURRENCY = "##,##,##0.00"
    public static final String AMOUNT_FORMAT_CSV = "#0.00"

    // used to pull formatted amount/quantity from DB
    public static final String DB_CURRENCY_FORMAT = "FM৳ 99,99,999,99,99,990.0099"
    public static final String DB_CURRENCY_FORMAT_CSV = "FM99999999999990.00"
    public static final String DB_QUANTITY_FORMAT = "FM99,99,999,99,99,990.0099"
    public static final String DB_QUANTITY_FORMAT_CSV = "FM99999999999990.0099"

    public static String makeAmountWithThousandSeparator(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }

    public static String formatAmountWithoutCurrency(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT_WITHOUT_CURRENCY);
        String output = myFormatter.format(amount);
        return output
    }

    public static String makeAmountWithThousandSeparator(BigDecimal amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }

    //return get file download file extension
    public static String getFileExtension(String formatType) {
        String outputFileExtension
        switch (formatType) {
            case FORMAT_TYPE_NAME_PDF: outputFileExtension = PDF_EXTENSION
                break
            case FORMAT_TYPE_NAME_XLS: outputFileExtension = XLS_EXTENSION
                break
            case FORMAT_TYPE_NAME_CSV: outputFileExtension = CSV_EXTENSION
                break
            default: outputFileExtension = PDF_EXTENSION
        }
        return outputFileExtension

    }
    //return get  download file type
    public static JasperExportFormat getFileType(String formatType) {
        JasperExportFormat jasperExportFormat

        switch (formatType) {
            case FORMAT_TYPE_NAME_PDF: jasperExportFormat = JasperExportFormat.PDF_FORMAT
                break
            case FORMAT_TYPE_NAME_XLS: jasperExportFormat = JasperExportFormat.XLS_FORMAT
                break
            case FORMAT_TYPE_NAME_CSV: jasperExportFormat = JasperExportFormat.CSV_FORMAT
                break
            default: jasperExportFormat = JasperExportFormat.PDF_FORMAT
        }
        return jasperExportFormat
    }

    private static final String BOLD_TAG_START = "<b>"
    private static final String BOLD_TAG_END = "</b>"

    public static String makeBold(String str) {
        return BOLD_TAG_START + str + BOLD_TAG_END
    }

    // Method checks if rate/quantity changed in transaction details (requires for summary update decision)
    public static boolean isRateOrQuantityChanged(def oldTransactionDetails, def newTransactionDetails) {
        if (oldTransactionDetails.rate != newTransactionDetails.rate) {
            return true
        }
        if (oldTransactionDetails.actualQuantity != newTransactionDetails.actualQuantity) {
            return true
        }
        return false
    }


    public static long parseLongInput(String paramsId) {
        try {
            long id = Long.parseLong(paramsId)
            return id
        } catch (Exception e) {
            return 0L
        }
    }

    /**
     * Following method adds the Unselected Object with proper key sets for kendo datasource
     * @param lst -the main list with objects
     * @param textMember - optional value, default is 'name'
     * @return - the main List with unselected value added in first index
     */
    private static final String NAME = 'name'

    public static List listForKendoDropdown(List lst, String textMember, String unselectedText) {
        List lstReturn = []
        Map unseledtedVal = new LinkedHashMap()
        String txtMember = textMember ? textMember : NAME
        String unselectedTxt = unselectedText ? unselectedText : PLEASE_SELECT_LEVEL
        if (lst.size() == 0) {
            unseledtedVal.put(ID, EMPTY_SPACE)
            unseledtedVal.put(txtMember, unselectedTxt)
            lstReturn.add(0, unseledtedVal)
            return lstReturn
        }
        // List is not empty. iterate through each key (except id & textMember)
        // Put these keys (if any) inside unselectedVal (with empty string) for consistency
        Map<String, Object> firstObj
        Object tmp = lst[0]     // pick the first element, assuming all are same
        if (tmp instanceof Map) {
            firstObj = (Map<String, Object>) tmp // groovyRowResult
        } else {
            firstObj = (Map<String, Object>) tmp.properties   // Domains
        }

        for (String key : firstObj.keySet()) {
            unseledtedVal.put(key, EMPTY_SPACE)
        }
        unseledtedVal.put(ID, EMPTY_SPACE)
        unseledtedVal.put(txtMember, unselectedTxt)
        lstReturn.add(0, unseledtedVal)
        lstReturn = lstReturn.plus(1, lst)
        // append the original list (& return a new list in case cache utility object)
        return lstReturn
    }

    //Domain
    public static final String DOMAIN_EXCHANGEHOUSE = "Exchange House"
    public static final String DOMAIN_BANK_BRANCH = "Branch" // "BankBranch"
    public static final String COSTING_DETAILS = "Costing Details" //
    public static final String DOMAIN_BENEFICIARY = "Beneficiary"
    public static final String DOMAIN_TASK = "Task"
    public static final String DOMAIN_CUSTOMER = "Customer"
    public static final String DOMAIN_COUNTRY = "Country"
    public static final String DOMAIN_CURRENCY_CONVERSION = "Currency Conversion"
    private static final String HAS = " has "
    private static final String ASSOCIATED = " associated "
    private static final String S_PLURAL = "(s)"
    // write a message to get associative information
    static String getMessageOfAssociation(String name, int count, String domainName) {
        return name + HAS + count + ASSOCIATED + domainName + S_PLURAL
    }

    public static final char ACTION_CREATE = 'C';
    public static final char ACTION_UPDATE = 'U';
    // Customer Address Verification Status
    public static final int CUSTOMER_ADDRESS_VERIFIED = 1
    public static final int CUSTOMER_ADDRESS_NOT_VERIFIED = 0

    public static final double TASK_REGULAR_FEE = 0.00
    public static final double MOBILE_PAY_AMOUNT_LIMIT = 20000;
    public static final int EXH_DEFAULT_DATE_RANGE = 7;
    public static final int EXH_DEFAULT_DATE_RANGE_FOR_SEARCH_TASK_DETAILS = 30;


    private static final String HTTP = "http"
    private static final String HTTPS = "https"
    private static final String COLON_WITH_SLASH = "://"
    private static final String OPENING_WWW = '//www.'
    private static final String DOUBLE_SLASH = '//'

    public static String getFullUrl(HttpServletRequest request, boolean ignoreWWW) {

        boolean includePort = true
        String scheme = request.getScheme()             // http
        String serverName = request.getServerName()     // localhost
        int serverPort = request.getServerPort()        // 8080
        String contextPath = request.getContextPath()   // root
        boolean inHttp = scheme.equalsIgnoreCase(HTTP)
        boolean inHttps = scheme.equalsIgnoreCase(HTTPS)

        if ((inHttp && (serverPort == 80)) || (inHttps && (serverPort == 443))) {
            includePort = false;
        }
        String fullUrl = scheme + COLON_WITH_SLASH + serverName +
                (includePort ? (COLON + serverPort) : EMPTY_SPACE) + contextPath
        if (ignoreWWW) {
            fullUrl = fullUrl.replace(OPENING_WWW, DOUBLE_SLASH)
        }
        return fullUrl
    }


}


