package com.athena.mis.inventory.actions.report.inventorytransactionlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.application.entity.ItemType
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download inventory transaction report in csv format
 * For details go through Use-Case doc named 'DownloadForInventoryTransactionListCsvActionService'
 */
class DownloadForInventoryTransactionListCsvActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to generate inventory transaction csv Report."
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String INVENTORY_NOT_FOUND = "Inventory not found"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"
    private static final String INVENTORY_IDS = "inventoryIds"
    private static final String INVENTORY_NAME = "inventoryName"
    private static final String INVENTORY_OBJ = "inventoryObj"
    private static final String TRANSACTION_TYPE_IDS = "transactionTypeIds"
    private static final String ITEM_TYPE_IDS = "itemTypeIds"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String TRANSACTION_TYPE_IN = "transactionTypeIn"
    private static final String TRANSACTION_TYPE_OUT = "transactionTypeOut"
    private static final String TRANSACTION_TYPE_PRODUCTION = "transactionTypeProduction"
    private static final String TRANSACTION_TYPE_ADJUSTMENT = "transactionTypeAdjustment"
    private static final String TRANSACTION_TYPE_REVERSE_ADJUSTMENT = "transactionTypeReverseAdjustment"
    private static final String TRANSACTION_TYPE_NAME = "transactionTypeName"
    private static final String ITEM_TYPE_NAME = "itemTypeName"
    private static final String ALL_TRANSACTION = "All Transactions"
    private static final String ALL_ITEM_TYPE = "ALL"
    private static final String ENTITY_TYPE_INVENTORY = "entityTypeInventory"
    private static final String ENTITY_TYPE_SUPPLIER = "entityTypeSupplier"
    private static final String ENTITY_TYPE_CUSTOMER = "entityTypeCustomer"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'inventoryTransaction'
    private static final String OUTPUT_FILE_NAME = 'InventoryTransactionListReport'
    private static final String REPORT_TITLE = 'All Inventory Transaction List'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_CSV = 'inventoryTransactionListCSV.jasper'
    private static final String REPORT = "report"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String PROJECT_IDS = "projectIds"
    private static final String PROJECT_NAME = "projectName"
    private static final String LABEL_ALL = "ALL"
    private static final String CSV_EXTENSION = ".csv"

    JasperService jasperService
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * Check and get required parameters from UI
     * Get inventory object from cache utility by inventoryId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if (!params.startDate || !params.endDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            InvInventory invInventory = null
            if (!params.inventoryId.equals(Tools.EMPTY_SPACE)) {
                // get inventory object from cache utility by inventoryId
                long inventoryId = Long.parseLong(params.inventoryId.toString())
                invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
                if (!invInventory) {
                    result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                    return result
                }
            }

            result.put(INVENTORY_OBJ, invInventory)
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
     * 1. get all required parameters
     * 2. generate report
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method

            InvInventory invInventory = (InvInventory) preResult.get(INVENTORY_OBJ)
            Date startDate = DateUtility.parseMaskedFromDate(params.startDate.toString())
            Date endDate = DateUtility.parseMaskedToDate(params.endDate.toString())

            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
            SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
            SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
            SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
            SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)
            SystemEntity transactionTypeReAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)

            List transactionTypeIds = []
            String transactionTypeName

            if (params.transactionTypeId.equals(Tools.EMPTY_SPACE)) {
                transactionTypeIds << transactionTypeIn.id
                transactionTypeIds << transactionTypeOut.id
                transactionTypeIds << transactionTypeConsumption.id
                transactionTypeIds << transactionTypeProduction.id
                transactionTypeIds << transactionTypeAdj.id
                transactionTypeIds << transactionTypeReAdj.id

                transactionTypeName = ALL_TRANSACTION
            } else {
                long transactionTypeId = Long.parseLong(params.transactionTypeId.toString())
                transactionTypeIds << transactionTypeId
                SystemEntity transactionType = (SystemEntity) invTransactionTypeCacheUtility.read(transactionTypeId)
                transactionTypeName = transactionType.key
            }

            List<Long> lstItemTypeIds = []
            String itemTypeName
            if(params.itemTypeId.equals(Tools.EMPTY_SPACE)){
                lstItemTypeIds = itemTypeCacheUtility.getAllItemTypeIds()
                itemTypeName = ALL_ITEM_TYPE
            }else{
                long itemTypeId = Long.parseLong(params.itemTypeId.toString())
                lstItemTypeIds << itemTypeId
                ItemType itemTypObject = itemTypeCacheUtility.read(itemTypeId)
                itemTypeName = itemTypObject.name
            }

            // generate report
            Map report = getInventoryTransactionReport(params, invInventory, startDate, endDate, transactionTypeIds, transactionTypeName, companyId, lstItemTypeIds, itemTypeName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build successful result
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Generate inventory transaction report
     * 1. pull project object from projectCacheUtility by projectId for specific project
     * 2. get project id for specific project all project ids mapped with user
     * 3. get specific inventory id and name if exists or get all inventory ids by type mapped with user
     * 4. put required parameters to generate report
     * @param projectId -id of project
     * @param invInventory -object of InvInventory
     * @param inventoryTypeId -id of SystemEntity(Store/Site)
     * @param startDate -starting date
     * @param endDate -end date
     * @param transactionTypeIds -list of transaction type ids(IN, OUT, PRODUCTION etc.)
     * @param transactionTypeName -name of transaction type
     * @param companyId -id of company
     * @return -generated report
     */
    private Map getInventoryTransactionReport(GrailsParameterMap params, InvInventory invInventory, Date startDate, Date endDate, List transactionTypeIds, String transactionTypeName, long companyId, List<Long> lstItemTypeIds, String itemTypeName) {
        String projectName = LABEL_ALL
        long projectId
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)
        SystemEntity transactionTypeReAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)

        if (!params.projectId.equals(Tools.EMPTY_SPACE)) {
            projectId = Long.parseLong(params.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId) // get project object
            projectName = project.name
        }
        // get project project id for specific project all project ids mapped with user
        List projectIds = []
        if (!params.projectId.equals(Tools.EMPTY_SPACE)) {
            projectId = Long.parseLong(params.projectId.toString())
            projectIds << new Long(projectId)
        } else {
            projectIds = invSessionUtil.appSessionUtil.getUserProjectIds()
        }

        List inventoryIds = []
        // get specific inventory id if exists or get all inventory ids by type mapped with user
        inventoryIds = getUserInventoryIdList(params, invInventory)
        if (inventoryIds.size() <= 0) {
            inventoryIds << 0
        }

        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION

        Map reportParams = new LinkedHashMap()
        // put required parameters to generate report
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(INVENTORY_IDS, inventoryIds)
        reportParams.put(INVENTORY_NAME, invInventory ? invInventory.name : LABEL_ALL)
        reportParams.put(START_DATE, startDate)
        reportParams.put(END_DATE, endDate)
        reportParams.put(TRANSACTION_TYPE_IDS, transactionTypeIds)
        reportParams.put(ITEM_TYPE_IDS, lstItemTypeIds)
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, transactionTypeConsumption.id)
        reportParams.put(TRANSACTION_TYPE_IN, transactionTypeIn.id)
        reportParams.put(TRANSACTION_TYPE_OUT, transactionTypeOut.id)
        reportParams.put(TRANSACTION_TYPE_PRODUCTION, transactionTypeProduction.id)
        reportParams.put(TRANSACTION_TYPE_ADJUSTMENT, transactionTypeAdj.id)
        reportParams.put(TRANSACTION_TYPE_REVERSE_ADJUSTMENT, transactionTypeReAdj.id)
        reportParams.put(TRANSACTION_TYPE_NAME, transactionTypeName)
        reportParams.put(ITEM_TYPE_NAME, itemTypeName)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT_CSV)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT_CSV)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(ENTITY_TYPE_INVENTORY, transactionEntityInventory.id)
        reportParams.put(ENTITY_TYPE_SUPPLIER, transactionEntitySupplier.id)
        reportParams.put(ENTITY_TYPE_CUSTOMER, transactionEntityCustomer.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_CSV, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    /**
     * Get specific inventory id if exists or get all inventory ids by type mapped with user
     * @param projectId -id of project
     * @param inventoryTypeId -id of inventory type(Store/Site)
     * @param invInventory -object of InvInventory
     * @return -a list of inventory ids
     */
    private List<Long> getUserInventoryIdList(GrailsParameterMap params, InvInventory invInventory) {
        List<Long> lstUserInventoryId = []
        long projectId
        long inventoryTypeId

        // Specific Project & All inventories(Side, Store)
        if ((!params.projectId.equals(Tools.EMPTY_SPACE)) && (params.inventoryTypeId.equals(Tools.EMPTY_SPACE)) && (!invInventory)) {
            projectId = Long.parseLong(params.projectId.toString())
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByProject(projectId)
            return lstUserInventoryId
        }

        // Specific Project & Specific InventoryType
        if ((!params.projectId.equals(Tools.EMPTY_SPACE)) && (!params.inventoryTypeId.equals(Tools.EMPTY_SPACE)) && (!invInventory)) {
            projectId = Long.parseLong(params.projectId.toString())
            inventoryTypeId = Long.parseLong(params.inventoryTypeId.toString())
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId)
            return lstUserInventoryId
        }

        // All projects & Specific InventoryType
        if ((params.projectId.equals(Tools.EMPTY_SPACE)) && (!params.inventoryTypeId.equals(Tools.EMPTY_SPACE)) && (!invInventory)) {
            inventoryTypeId = Long.parseLong(params.inventoryTypeId.toString())
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            return lstUserInventoryId
        }

        // All Projects & All Inventories(Get userInventoryList)
        if ((params.projectId.equals(Tools.EMPTY_SPACE)) && (params.inventoryTypeId.equals(Tools.EMPTY_SPACE)) && (!invInventory)) {
            lstUserInventoryId = invSessionUtil.getUserInventoryIds()
            return lstUserInventoryId
        }

        // Specific inventory
        if (invInventory) {
            lstUserInventoryId << invInventory.id
            return lstUserInventoryId
        }
        return lstUserInventoryId
    }
}
