package com.athena.mis.inventory.actions.report.itemreconciliation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Download Item Reconciliation report in PDF format
 *  For details go through Use-Case doc named 'DownloadForItemReconciliationActionService'
 */
class DownloadForItemReconciliationActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String PROJECT_IDS = "projectIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String FAILURE_MSG = "Fail to generate item reconciliation report"
    private static final String REPORT_FOLDER = 'itemReconciliation'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'itemReconciliation'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String TRANSACTION_TYPE_OUT = "transactionTypeOut"
    private static final String TRANSACTION_TYPE_IN = "transactionTypeIn"
    private static final String ENTITY_TYPE_INVENTORY = "entityTypeInventory"
    private static final String PROJECT_NAME_ALL = 'ALL'
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Item Reconciliation'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'itemReconciliation.jasper'

    /**
     * Check existence of required parameters
     * @param parameters -N/A
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Method to get map that contains required parameters to generate report in PDF format
     * @param parameters -parameters from UI
     * @param obj -ProjectId send from executePreCondition method
     * @return -map contains parameters to generate report & isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map report = getItemReconciliationReport(params)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
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
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Method to get required parameters for jasper report
     * @param projectId -Project.id
     * @return -a map contains parameters for jasper report
     */
    private Map getItemReconciliationReport(GrailsParameterMap params) {
        Map reportParams = new LinkedHashMap()
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        /*if specific project is given then put given projectId in list
           else select all projects then get user-mapped projectIds*/
        List<Long> projectIds = []
        String projectName = PROJECT_NAME_ALL //default value
        if (params.projectId.equals(Tools.EMPTY_SPACE)) {  //if no specific project is selected
            projectIds = invSessionUtil.appSessionUtil.getUserProjectIds()
        } else {//if specific project is given
            long projectId = Long.parseLong(params.projectId.toString())
            projectIds << new Long(projectId)
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name
        }

        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(TRANSACTION_TYPE_IN, transactionTypeIn.id)
        reportParams.put(TRANSACTION_TYPE_OUT, transactionTypeOut.id)
        reportParams.put(ENTITY_TYPE_INVENTORY, transactionEntityInventory.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
