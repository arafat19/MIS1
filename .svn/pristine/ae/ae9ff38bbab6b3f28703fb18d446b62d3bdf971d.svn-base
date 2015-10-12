package com.athena.mis.sarb.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp

class DownloadSarbTransactionSummaryActionService extends BaseService implements ActionIntf{

    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    SarbSessionUtil sarbSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'sarbTransactionSummary'
    private static final String TRANSACTION_SUMMARY = 'sarbTransactionSummaryReport.jasper'
    private static final String REPORT_TITLE = 'Sarb Transaction Summary'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String ERROR_EXCEPTION = "Failed to generate transaction summary report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "SarbTransactionSummary"

    private static final String FROM_DATE = 'fromDate'
    private static final String TO_DATE = 'toDate'
    private static final String CURRENCY_SYMBOL = 'currencySymbol'
    private static final String COMPANY_ID = "companyId"
    private static final String COMPANY_CODE = "companyCode"
    private static final String COMPANY_NAME = "companyName"
    private static final String LIST_TASK_STATUS = "lstTaskStatus"
    private static final String LIST_TASK_EXCLUDING_STATUS = "lstTaskExcludingStatus"

    /**
     * 1. check if params has fromDate & toDate
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing customer object & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if(!params.fromDate || !params.toDate) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            Timestamp fromDate = DateUtility.getSqlDateWithSeconds(startDate)
            Timestamp toDate = DateUtility.getSqlDateWithSeconds(endDate)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
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
     * 1. Build a map for customer history report map by given parameters
     * @param params - N/A
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            Timestamp fromDate = (Timestamp) preResult.get(FROM_DATE)
            Timestamp toDate = (Timestamp) preResult.get(TO_DATE)
            Map report = getSarbTransactionSummary(fromDate, toDate)
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
     * Generate customer history report with assigned parameters
     * @param parameterMap - serialized parameters from UI
     * @param exhCustomer - an object of ExhCustomer
     * @return - generated report with required params
     */
    private Map getSarbTransactionSummary(Timestamp fromDate, Timestamp toDate) {
        long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstTaskStatus= exchangeHouseImplService?.listTaskStatusForSarb()
        List<Long> lstTaskExcludingStatus= exchangeHouseImplService?.listTaskStatusForExcludingSarb()
        Company company = (Company) companyCacheUtility.read(companyId)
        String reportDir = Tools.getSarbReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put('SUBREPORT_DIR', reportDir + File.separator)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(CURRENCY_SYMBOL, currencyCacheUtility.getLocalCurrency().symbol)
        reportParams.put(LIST_TASK_STATUS, lstTaskStatus)
        reportParams.put(LIST_TASK_EXCLUDING_STATUS, lstTaskExcludingStatus)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(COMPANY_NAME, company.name)
        reportParams.put(COMPANY_CODE, company.code)

        JasperReportDef reportDef = new JasperReportDef(
                name: TRANSACTION_SUMMARY,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)        // generate pdf
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
