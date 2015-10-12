package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download remittance transaction report in CSV format for Admin or Cashier
 * For details go through Use-Case doc named 'ExhDownloadRemittanceTransactionCsvActionService'
 */
class ExhDownloadRemittanceTransactionCsvActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility


    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'remittanceTransaction'
    private static final String OUTPUT_FILE_NAME = 'RemittanceTransaction'
    private static final String REPORT_TITLE = 'Remittance Transaction'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'remittanceTransactionCsv.jasper'
    private static final String ERROR_EXCEPTION = "Failed to generate remittance transaction report"
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COMPANY_NAME = "companyName"
    private static final String COMPANY_ID = "companyId"
    private static final String LOCAL_CURRENCY_NAME = "localCurrencyName"
    private static final String TASK_STATUS_ID_LIST = 'taskStatusIdList'
    private static final String SEARCH_AMOUNT = 'amount'

    /**
     * Check required parameters
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!(DateUtility.parseMaskedFromDate(params.formDate))) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!(DateUtility.parseMaskedToDate(params.toDate))) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    /**
     * Get desired report providing all required parameters
     * 1. Build a map for remittance transaction report by given parameters
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Map report = getRemittanceTransactionList(params)          // build a map for report
            result.put(REPORT, report)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
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
     * do nothing for failure operation
     */
    Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Generate remittance transaction report with assigned parameters
     * @param params - serialized parameters from UI
     * @return - generated report with required params
     */
    private Map getRemittanceTransactionList(GrailsParameterMap params) {

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER      // get report directory
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION        // set file name with csv extension

        Date fromDate = DateUtility.parseMaskedFromDate(params.formDate)          // date parsing
        Date toDate = DateUtility.parseMaskedToDate(params.toDate)
        double amount = Double.parseDouble(params.amount)

        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        List<Long> taskStatusListIds = [
                exhNewTaskSysEntityObject.id,             // list task status
                exhSentToBankSysEntityObject.id,
                exhSentToOtherBankSysEntityObject.id,
                exhResolvedByOtherBankSysEntityObject.id]

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory() + File.separator)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(SEARCH_AMOUNT, amount)
        reportParams.put(TASK_STATUS_ID_LIST, taskStatusListIds)
        reportParams.put(LOCAL_CURRENCY_NAME, currencyCacheUtility.localCurrency.symbol)
        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        Company exchangeHouse = (Company) companyCacheUtility.read(user.companyId)
        reportParams.put(COMPANY_NAME, exchangeHouse.name)
        reportParams.put(COMPANY_ID, exchangeHouse.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)    // generate report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
