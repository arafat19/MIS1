package com.athena.mis.inventory.actions.report.itemreceivedstock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Download Inventory item received (from supplier) stock status report in CSV format
 *  For details go through Use-Case doc named 'DownloadForItemReceivedStockCsvActionService'
 */
class DownloadForItemReceivedStockCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private static final String FAILURE_MSG = "Fail to generate item received stock report"
    private static final String INVALID_INPUT_MESSAGE = "Can't not get item received stock report due to invalid input"
    private static final String LST_SUPPLIER_IDS = "lstSupplierIds"
    private static final String LST_PROJECT_IDS = "lstProjectIds"
    private static final String LST_ITEM_TYPE_IDS = "lstItemTypeIds"
    private static final String ITEM_TYPE_NAME = "itemTypeName"
    private static final String TRANSACTION_ENTITY_TYPE_ID = "transactionEntityTypeId"
    private static final String TRANSACTION_TYPE_ID = "transactionTypeId"
    private static final String SUPPLIER_NOT_FOUND = "Please select a supplier"
    private static final String NO_PROJECT_FOUND = "User has no associated project"
    private static final String REPORT_FOLDER = 'itemReceivedStock'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'received-stock'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Item Received Stock'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String CSV_EXTENSION = '.csv'
    private static final String JASPER_FILE_CSV = 'itemReceivedStockCSV.jasper'
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_NAME_ALL = "All"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String REPORT_FILE_FORMAT = "csv"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Checking existence of required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if ((!parameterMap.fromDate) || (!parameterMap.toDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }
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
     * Method to get map that contains required parameters to generate report in CSV format
     * @param parameters -N/A
     * @param obj -parameters from UI
     * @return -map contains parameters to generate report & isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            String projectName = PROJECT_NAME_ALL //default value
            List<Long> lstSupplierIds = []
            List supplierList
            if (parameterMap.supplierId.equals(Tools.EMPTY_SPACE)) {
                //if no specific supplier is selected then get all supplierList of login user company
                supplierList = supplierCacheUtility.list()
                if (supplierList.size() == 0) {//if no supplier found then return with message
                    result.put(Tools.MESSAGE, SUPPLIER_NOT_FOUND)
                    return result
                } else {//get supplierIds
                    for (int i = 0; i < supplierList.size(); i++) {
                        lstSupplierIds << supplierList[i].id
                    }
                }
            } else {//if specific supplier is selected
                long supplierId = Long.parseLong(parameterMap.supplierId.toString())
                lstSupplierIds << new Long(supplierId)
            }

            List<Long> lstProjectIds = []
            List projectList
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                //if no specific project is selected then get user-mapped projectList
                projectList = invSessionUtil.appSessionUtil.getUserProjects()
                if (projectList.size() == 0) { //if no user-mapped project found then return with message
                    result.put(Tools.MESSAGE, NO_PROJECT_FOUND)
                    return result
                } else {//get supplierIds
                    for (int i = 0; i < projectList.size(); i++) {
                        lstProjectIds << projectList[i].id
                    }
                    projectName = PROJECT_NAME_ALL
                }
            } else {   //if specific project is selected
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                lstProjectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            List<Long> itemTypeIds = []
            String itemTypeName = Tools.ALL
            if (parameterMap.itemTypeId.equals(Tools.EMPTY_SPACE)) {
                //if no specific item type is selected then get all item types
                SystemEntity itemCategoryInventory = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
                itemTypeIds = itemTypeCacheUtility.listIdsByCategoryId(itemCategoryInventory.id)
            } else { //if specific item type is given then put given item type id in list
                long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
                itemTypeIds << new Long(itemTypeId)
                ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemTypeId)
                itemTypeName = itemType.name
            }

            Date fromDate = DateUtility.parseMaskedDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedDate(parameterMap.toDate.toString())

            Map report = getInventoryStockReport(lstProjectIds, lstSupplierIds, projectName, fromDate, toDate, itemTypeIds, itemTypeName)
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
     *
     * @param lstProjectIds -list of projectIds(Project.id)
     * @param lstSupplierIds -list of supplierIds(Supplier.id)
     * @param fromDate -Start date
     * @param dateTo -end date
     * @param projectName -project.name
     * @param lstItemTypeIds -list of itemTypeIds(ItemType.id)
     * @param itemTypeName -ItemType.name
     * @return -a map contains parameters for jasper report
     */
    private Map getInventoryStockReport(List<Long> lstProjectIds, List<Long> lstSupplierIds, String projectName, Date fromDate, Date toDate, List<Long> lstItemTypeIds, String itemTypeName) {
        Map reportParams = new LinkedHashMap()
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(LST_SUPPLIER_IDS, lstSupplierIds)
        reportParams.put(LST_PROJECT_IDS, lstProjectIds)
        reportParams.put(LST_ITEM_TYPE_IDS, lstItemTypeIds)
        reportParams.put(ITEM_TYPE_NAME, itemTypeName)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(TRANSACTION_TYPE_ID, transactionTypeIn.id)
        reportParams.put(TRANSACTION_ENTITY_TYPE_ID, transactionEntitySupplier.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_CSV, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
