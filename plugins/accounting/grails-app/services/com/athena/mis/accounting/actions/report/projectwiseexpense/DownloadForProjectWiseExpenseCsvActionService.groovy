package com.athena.mis.accounting.actions.report.projectwiseexpense

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
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
 * Download all expenses of specific project in csv format.
 * For details go through Use-Case doc named 'DownloadForProjectWiseExpenseCsvActionService'
 */
class DownloadForProjectWiseExpenseCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download Project wise expense."
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'projectWiseExpense'
    private static final String OUTPUT_FILE_NAME = 'projectWiseExpense'
    private static final String REPORT_TITLE = 'projectWiseExpenseCSV'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String PROJECT_WISE_EXPENSE_REPORT = 'projectWiseExpenseCSV.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String HEAD_NOT_FOUND = "Account Code not found"
    private static final String COA_OBJ = "coa"
    private static final String PROJECT_IDS = "projectIds"
    private static final String COA_IDS = "coaIds"
    private static final String GROUP_ID_BANK = "groupIdBank"
    private static final String GROUP_ID_CASH = "groupIdCash"
    private static final String COA_NAME = "coaName"
    private static final String USER_HAS_NO_PROJECT = "User is not mapped with any project"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String LABEL_ALL_BANK = "All Bank"
    private static final String LABEL_ALL_CASH = "All Cash"
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String LABEL_ALL_BANK_CASH = "All Bank & Cash"

    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.coaId || !params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            Date startDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.toDate.toString())
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }

            long coaId = Long.parseLong(params.coaId.toString())
            AccChartOfAccount accChartOfAccount = null
            if (coaId > 0) {
                accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(coaId)
                if (!accChartOfAccount) {
                    result.put(Tools.MESSAGE, HEAD_NOT_FOUND)
                    return result
                }
            }
            result.put(COA_OBJ, accChartOfAccount)
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
     * @param parameters -serialized parameters from UI
     * @param obj - object received from pre condition
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> projectIds = []

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                return result
            }

            List<Long> groupIds = []
            long groupId
            if (parameterMap.accGroupId.equals(Tools.EMPTY_SPACE)) {
                groupIds = accGroupCacheUtility.listOfAccGroupBankCashId()
            } else {
                groupId = Long.parseLong(parameterMap.accGroupId.toString())
                groupIds << new Long(groupId)
            }

            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(COA_OBJ)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            /*if postedByParam  = 0 the show Only Posted Voucher
             if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getProjectWiseExpenseReport(groupIds, projectIds, accChartOfAccount, fromDate, toDate, postedByParam)
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
     * do nothing for success operation
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
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Generate report by given data
     * @param accGroupId - acc group id
     * @param projectIds - project ids
     * @param coa - coa object
     * @param fromDate - start date
     * @param toDate - current date
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - generated report with required params
     */
    private Map getProjectWiseExpenseReport(List accGroupIds, List<Long> projectIds, AccChartOfAccount coa, Date fromDate, Date toDate, long postedByParam) {
        List coaIds = []
        if (coa) {
            coaIds << new Long(coa.id)
        } else {
            coaIds = accChartOfAccountCacheUtility.listIdsByAccGroupIds(accGroupIds)
        }

        Map reportParams = new LinkedHashMap()
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(COA_IDS, coaIds)
        reportParams.put(GROUP_ID_BANK, accGroupBank.id)
        reportParams.put(GROUP_ID_CASH, accGroupCash.id)
        if (coa) {
            reportParams.put(COA_NAME, coa.description + Tools.PARENTHESIS_START + coa.code + Tools.PARENTHESIS_END)
        } else {
            if (accGroupIds.size() > 1) {
                reportParams.put(COA_NAME, LABEL_ALL_BANK_CASH)
            } else {
                for (int i = 0; i < accGroupIds.size(); i++) {
                    if (accGroupIds[i] == accGroupBank.id) {
                        reportParams.put(COA_NAME, LABEL_ALL_BANK)
                    } else if (accGroupIds[i] == accGroupCash.id) {
                        reportParams.put(COA_NAME, LABEL_ALL_CASH)
                    }
                }
            }

        }
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: PROJECT_WISE_EXPENSE_REPORT, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}

