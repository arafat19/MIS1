package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download Customer in CSV format for Admin
 * For details go through Use-Case doc named 'ExhDownloadCustomerCsvActionService'
 */
class ExhDownloadCustomerCsvActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'exhCustomer'
    private static final String OUTPUT_FILE_NAME = 'Customer'
    private static final String REPORT_TITLE = 'Customer'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'customerCsv.jasper'
    private static final String ERROR_EXCEPTION = "Failed to generate customer report"
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"
    private static final String COMPANY_NAME = "companyName"
    private static final String COMPANY_ID = "companyId"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get desired report providing all required parameters
     * Report contains such columns are Name, Phone, Email
     * Build a map for customer by given parameters
     * @param parameters - N/jA
     * @param obj - N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map report = buildReportParamsMap()          // build a map for report
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
     * Generate remittance transaction report with assigned parameters
     * @param params - serialized parameters from UI
     * @return - generated report with required params
     */
    private Map buildReportParamsMap() {
        String reportDir = Tools.getExchangeHouseReportDirectory() + File.separator + REPORT_FOLDER      // get report directory
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION        // set file name with csv extension

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
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
