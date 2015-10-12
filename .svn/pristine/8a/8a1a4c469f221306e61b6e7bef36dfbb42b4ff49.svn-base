package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility
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
 * Download Remittance Summary in pdf format for Admin
 * For details go through Use-Case doc named 'ExhDownloadForCashierWiseRemittanceSummaryReportActionService'
 */
class ExhDownloadForCashierWiseRemittanceSummaryReportActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility


    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'remittanceSummary'
    private static final String OUTPUT_FILE_NAME = 'RemittanceSummaryReport'
    private static final String REPORT_TITLE = 'Remittance Summary(s)'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUB_REPORT_FOLDER = 'subreports'
    private static final String SUB_REPORT_DIR = 'SUBREPORT_DIR'
    private static final String JASPER_FILE = 'RemittanceSummaryRpt.jasper'
    private static final String ERROR_EXCEPTION = "Failed to generate Cashier Wise Task report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"

    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String TASK_STATUS_LIST_IDS = 'taskStatusListIds'
    private static final String PAID_BY_CASH = "paidByCash"
    private static final String PAID_BY_ONLINE = "paidByOnline"

    private static final String EXCHANGE_HOUSE_NAME = "exchangeHouseName"

    /**
     * 1. check required parameters
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget object & isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get desired report providing all required parameters
     * 1. Build remittance summary report map by given parameters
     * @param parameters - - serialized parameters from UI
     * @param obj - receive map from executePreCondition
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Map report = getCashierWiseRemittanceSummaryReport(params)
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
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Generate Invoice report with assigned parameters
     * @param parameters - serialized parameters from UI
     * @return - generated report with required params
     */
    private Map getCashierWiseRemittanceSummaryReport(GrailsParameterMap params) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paidByCashObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_CASH, companyId)
        SystemEntity paidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator + SUB_REPORT_FOLDER + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        Date fromDate = DateUtility.parseMaskedFromDate(params.formDate)
        Date toDate = DateUtility.parseMaskedToDate(params.toDate)

        fromDate = DateUtility.setFirstHour(fromDate)
        toDate = DateUtility.setLastHour(toDate)

        List<Long> taskStatusListIds = [
                exhNewTaskSysEntityObject.id,
                exhSentToBankSysEntityObject.id,
                exhSentToOtherBankSysEntityObject.id,
                exhResolvedByOtherBankSysEntityObject.id]

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(SUB_REPORT_DIR, subReportDir)

        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())


        Long paidByCashId = paidByCashObj.id
        Long paidByOnlineId = paidByOnlineObj.id

        reportParams.put(TASK_STATUS_LIST_IDS, taskStatusListIds)
        reportParams.put(PAID_BY_CASH, paidByCashId)
        reportParams.put(PAID_BY_ONLINE, paidByOnlineId)

        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        Company company = (Company) companyCacheUtility.read(user.companyId)
        reportParams.put(EXCHANGE_HOUSE_NAME, company.name)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)            // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
