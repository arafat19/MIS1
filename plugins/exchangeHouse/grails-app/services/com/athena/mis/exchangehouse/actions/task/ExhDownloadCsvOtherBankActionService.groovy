package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
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
 * Download Task in csv format for other bank user
 * For details go through Use-Case doc named 'ExhDownloadCsvOtherBankActionService'
 */
class ExhDownloadCsvOtherBankActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_FOLDER = 'task'
    private static final String OUTPUT_FILE_NAME = 'OtherBankDetailsCSV'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'OtherBankDetailsCSV.jasper'
    private static final String ERROR_EXCEPTION = "Failed to generate report"
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"
    private static final String COMPANY_NAME = "companyName"
    private static final String BANK_NAME = "bankName"
    private static final String COMPANY_ID = "companyId"
    private static final String BANK_ID = "bankId"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String TASK_STATUS_ID = "taskStatusId"


    JasperService jasperService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility

    /**
     * Check input fields from UI
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if ((!parameters.createdDateFrom) ||
                    (!parameters.createdDateTo) ||
                    (!parameters.taskStatus) ||
                    (!parameters.outletBankId)) {
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
     * Build desired report providing all required parameters
     * @param params - serialized parameters from UI
     * @param obj -HttpServletResponse object
     * @return -a desired report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameters = (GrailsParameterMap) params
            Date fromDate = DateUtility.parseMaskedFromDate(parameters.createdDateFrom)
            Date toDate = DateUtility.parseMaskedToDate(parameters.createdDateTo)
            Integer statusId = parameters.taskStatus.toString().toInteger()
            SystemEntity taskStatus = (SystemEntity) exhTaskStatusCacheUtility.read(statusId)
            Long outletBankId = parameters.outletBankId.toString().toLong()
            AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
            Company company = (Company) companyCacheUtility.read(user.companyId)
            Bank bank = (Bank) bankCacheUtility.read(outletBankId)
            Map report = buildReport(fromDate, toDate, taskStatus, outletBankId, company.id, company.name, bank.name)
            // build a map for report
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
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for faulure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null

    }


    private Map buildReport(Date fromDate, Date toDate, SystemEntity taskStatus, Long bankId, Long companyId, String companyName, String bankName) {

        long taskStatusId = taskStatus.id
        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER
        // get report directory
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION        // set file name with csv extension

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(BANK_ID, bankId)
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(TASK_STATUS_ID, taskStatusId)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(COMPANY_NAME, companyName)
        reportParams.put(BANK_NAME, bankName)


        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)    // generate report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}

