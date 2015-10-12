package com.athena.mis.accounting.actions.report.supplierwisepayable

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * download supplier wise payable details in csv format
 * For details go through Use-Case doc named 'AccDownloadForSupplierWisePayableCsvActionService'
 */
class AccDownloadForSupplierWisePayableCsvActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download supplier wise payable report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'supplierWisePayable'
    private static final String OUTPUT_FILE_NAME = 'SupplierWisePayable'
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT_TITLE = 'SupplierWisePayable'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_IDs = "projectIds"
    private static final String SUPPLIER_WISE_PAYABLE_REPORT_CSV = 'supplierWisePayableCSV.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COMPANY_ID = "companyId"
    private static final String TRANSACTION_TYPE_IN = "transactionTypeIn"
    private static final String ENTITY_TYPE_SUPPLIER = "entityTypeSupplier"
    private static final String SOURCE_TYPE_ID = "sourceTypeId"

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

            if (!params.fromDate || !params.toDate || !params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

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
     * @param obj -N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            Long projectId = Long.parseLong(params.projectId.toString())
            Project project = null
            List<Long> projectIdList = []
            if (projectId > 0) {
                projectIdList << new Long(projectId)
                project = (Project) projectCacheUtility.read(projectId)
            } else {
                projectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                // get project list
            }

            Map report = getSupplierWisePayableReport(project, projectIdList, fromDate, toDate)
            // get generated report against given params

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
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast object received from previous method

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
     * @param project - project name
     * @param projectIdList - all project ids
     * @param fromDate - report starting date
     * @param toDate - report ending date
     * @return - generated report with required params
     */
    private Map getSupplierWisePayableReport(Project project, List<Long> projectIdList, Date fromDate, Date toDate) {
        Map reportParams = new LinkedHashMap()
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        // common directory for accounting plugin
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        // generated report name(e.g-supplierWisePayable.csv)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)       // set report title
        reportParams.put(PROJECT_IDs, projectIdList)             // project ids
        reportParams.put(PROJECT_NAME, project ? project.name : Tools.ALL)  // project name
        reportParams.put(FROM_DATE, fromDate.toTimestamp())
        reportParams.put(TO_DATE, toDate.toTimestamp())
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(TRANSACTION_TYPE_IN, inventoryImplService.getInvTransactionTypeIdIn())
        reportParams.put(ENTITY_TYPE_SUPPLIER, inventoryImplService.getTransactionEntityTypeIdSupplier())
        reportParams.put(SOURCE_TYPE_ID, accSourceTypeSupplier.id)

        JasperReportDef reportDef = new JasperReportDef(name: SUPPLIER_WISE_PAYABLE_REPORT_CSV, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
