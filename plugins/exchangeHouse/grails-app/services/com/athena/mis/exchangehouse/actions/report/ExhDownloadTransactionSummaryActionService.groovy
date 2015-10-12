package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
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
import org.springframework.transaction.annotation.Transactional

/**
 * Download transaction summary report in pdf format for Admin or Cashier
 * For details go through Use-Case doc named 'ExhDownloadTransactionSummaryActionService'
 */
class ExhDownloadTransactionSummaryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'transactionSummary'
    private static final String CUSTOMER_REMITTANCE_SUMMARY = 'TransactionSummary.jasper'
    private static final String REPORT_TITLE = 'Transaction Summary'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String ERROR_EXCEPTION = "Failed to generate transaction summary report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "TransactionSummary"

    private static final String FROM_DATE = 'fromDate'
    private static final String TO_DATE = 'toDate'
    private static final String SEARCH_AMOUNT = 'amount'
    private static final String CURRENCY_SYMBOL = 'currencySymbol'
    private static final String TASK_STATUS_LIST_IDS = 'taskStatusListIds'

    /**
     * Check required parameters
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing object of isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!(DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom))) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!(DateUtility.parseMaskedToDate(parameterMap.createdDateTo))) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
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
     * Get desired report providing all required parameters
     * 1. Build a map for transaction summary report by given parameters
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Map report = getTransactionSummary(parameterMap)
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
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Generate transaction summary report with assigned parameters
     * @param parameterMap - serialized parameters from UI
     * @return - generated report with required params
     */
    private Map getTransactionSummary(GrailsParameterMap parameterMap) {

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER

        Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom)
        Date toDate = DateUtility.parseMaskedToDate(parameterMap.createdDateTo)
        double amount = Double.parseDouble(parameterMap.amount)

        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        List<Long> taskStatusListIds = [
                exhNewTaskSysEntityObject.id,
                exhSentToBankSysEntityObject.id,
                exhSentToOtherBankSysEntityObject.id,
                exhResolvedByOtherBankSysEntityObject.id]


        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(SEARCH_AMOUNT, amount)
        reportParams.put(CURRENCY_SYMBOL, currencyCacheUtility.localCurrency.symbol)
        reportParams.put(TASK_STATUS_LIST_IDS, taskStatusListIds)

        JasperReportDef reportDef = new JasperReportDef(
                name: CUSTOMER_REMITTANCE_SUMMARY,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)        // generate report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
