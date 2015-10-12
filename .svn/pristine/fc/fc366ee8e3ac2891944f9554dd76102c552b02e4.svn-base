package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * download group ledger in csv format
 * For details go through Use-Case doc named 'DownloadForGroupLedgerCsvActionService'
 */
class DownloadForGroupLedgerCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download Group ledger"
    private static final String LEDGER_NOT_FOUND_ERROR_MESSAGE = "Group Ledger not found"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accLedger'
    private static final String OUTPUT_FILE_NAME = 'accGroupLedger'
    private static final String REPORT_TITLE = 'accGroupLedger'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"
    private static final String GROUP_ID = "groupId"
    private static final String GROUP_NAME = "groupName"
    private static final String ACC_GROUP_OBJ = "accGroup"
    private static final String JASPER_FILE = 'accGroupLedgerCsv.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String IS_POSITIVE = "isPositive"
    private static final String PROJECT_IDS = "projectIds"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"

    /**
     * Check input fields from UI
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            // check required fields
            if (!params.fromDate || !params.toDate || !params.groupId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedDate(params.toDate)
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }

            int groupId = Integer.parseInt(params.groupId.toString())
            AccGroup accGroup = (AccGroup) accGroupCacheUtility.read(groupId)
            // check whether accGroup  object exists or not. If not then "Group Ledger not found" is shown
            if (!accGroup) {
                result.put(Tools.MESSAGE, LEDGER_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            result.put(ACC_GROUP_OBJ, accGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get desired report providing all required parameters
     * @param parameters - serialized parameters from UI
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            AccGroup accGroup = (AccGroup) preResult.get(ACC_GROUP_OBJ)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            List<Long> projectIds = []
            projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                return result
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getGroupLedgerReport(accGroup, fromDate, toDate, projectIds, postedByParam)   // get group ledger CSV report
            result.put(REPORT, report)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Generate report by given data
     * @param accGroup - AccGroup object
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param projectIds - all project ids
     * @return - generated report with required params
     */
    private Map getGroupLedgerReport(AccGroup accGroup, Date fromDate, Date toDate, List projectIds, long postedByParam) {

        Map reportParams = new LinkedHashMap()
        List prevLedgerBalanceList = AccVoucherModel.previousBalanceByGroup(accGroup.id, fromDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
        double prevLedgerBalance = prevLedgerBalanceList[0] ? prevLedgerBalanceList[0] : 0d
        String previousLedgerBalanceStr = Tools.makeAmountWithThousandSeparator(prevLedgerBalance)
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(GROUP_ID, accGroup.id)
        reportParams.put(GROUP_NAME, accGroup.name)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PREVIOUS_BALANCE, prevLedgerBalanceList[0] ? prevLedgerBalanceList[0] : new BigDecimal(0))
        reportParams.put(IS_POSITIVE, isPositiveBalance(previousLedgerBalanceStr))
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getCompanyId())
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    private static final NEGATIVE_SIGN = "-"

    /**
     * Give Boolean value
     * @param balance - balance from getLedgerReport method
     * @return boolean value
     */
    private boolean isPositiveBalance(String balance) {
        if (balance.indexOf(NEGATIVE_SIGN) != -1) {     // -ve balance
            return false
        } else {                                        // +ve balance
            return true
        }
    }
}
