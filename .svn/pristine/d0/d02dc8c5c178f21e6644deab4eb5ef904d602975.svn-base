package com.athena.mis.inventory.actions.report.inventoryproduction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
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
 * Download inventory production report in pdf format
 * For details go through Use-Case doc named 'DownloadForInventoryProductionActionService'
 */
class DownloadForInventoryProductionActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvInventoryCacheUtility inventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility inventoryTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility productionLineItemCacheUtility

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'inventoryProduction'
    private static final String OUTPUT_FILE_NAME = 'inventoryProduction'
    private static final String REPORT_TITLE = 'Inventory Production'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'inventoryProduction.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String TRANSACTION_ID = "transactionId"
    private static final String FAILURE_MSG = "Fail to generate inventory production"
    private static final String TR_PRODUCTION = "trConsumption"
    private static final String TRANSACTION_MAP = "transactionMap"
    private static final String TRANSACTION_NOT_EXISTS = "Inventory transaction does not exists by this ID"
    private static final String INVALID_ID = "Invalid transaction Id"
    private static final String NOT_PRODUCTION_ID = "This transaction ID is not transaction ID of production"
    private static final String HAS_BEEN_REVERSED = "This production transaction has been reversed"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"

    /**
     * Check input validation
     * Check if inventory transaction(inventory production) object exists or not
     * Check if the inventory transaction object is production or not
     * Check if production has been reversed
     * @param parameters -parameter from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            long transactionId
            // checking input validation
            try {
                transactionId = Long.parseLong(params.transactionId.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_ID)
                log.error(e.getMessage())
                return result
            }
            // checking existence of inventory transaction object(inventory production)
            transactionId = Long.parseLong(params.transactionId.toString())
            InvInventoryTransaction trProduction = invInventoryTransactionService.read(transactionId)
            if (!trProduction) {
                result.put(Tools.MESSAGE, TRANSACTION_NOT_EXISTS)
                return result
            }
            // check if the transaction is production or not
            if (trProduction.invProductionLineItemId <= 0) {
                result.put(Tools.MESSAGE, NOT_PRODUCTION_ID)
                return result
            }
            // check if production has been reversed
            if (trProduction.itemCount <= 0) {
                result.put(Tools.MESSAGE, HAS_BEEN_REVERSED)
                return result
            }
            result.put(TR_PRODUCTION, trProduction)
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
     * Get desired report providing all required parameters
     * 1. build inventory transaction map with necessary properties
     * 2. generate report
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction trProduction = (InvInventoryTransaction) preResult.get(TR_PRODUCTION)
            // build inventory transaction map with necessary properties
            LinkedHashMap transactionMap = buildTransactionMap(trProduction)
            Map report = getProductionReport(transactionMap)    // generate report
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
     * Build inventory transaction map with necessary properties
     * @param inventoryTransaction - object of InvInventoryTransaction
     * @return -a map of with inventory transaction properties
     */
    private LinkedHashMap buildTransactionMap(InvInventoryTransaction inventoryTransaction) {
        InvInventory invInventory = (InvInventory) inventoryCacheUtility.read(inventoryTransaction.inventoryId)
        SystemEntity inventoryType = (SystemEntity) inventoryTypeCacheUtility.read(inventoryTransaction.inventoryTypeId)
        InvProductionLineItem invProductionLineItem = (InvProductionLineItem) productionLineItemCacheUtility.read(inventoryTransaction.invProductionLineItemId)

        LinkedHashMap transactionMap = [
                transactionId: inventoryTransaction.id,
                transactionDate: DateUtility.getDateFormatAsString(inventoryTransaction.transactionDate),
                inventoryName: inventoryType.key + Tools.COLON + Tools.SINGLE_SPACE + invInventory.name,
                productionLineItem: invProductionLineItem.name
        ]
        return transactionMap
    }

    /**
     * Generate inventory production report
     * Put required parameters to generate report
     * @param projectId -id of project
     * @param invInventory -object of InvInventory
     * @param inventoryTypeId -id of SystemEntity(Store/Site)
     * @param startDate -starting date
     * @param endDate -end date
     * @return -generated report
     */
    private Map getProductionReport(Map transactionMap) {
        Map reportParams = new LinkedHashMap()
        // put required parameters to generate report
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(TRANSACTION_MAP, transactionMap)
        reportParams.put(TRANSACTION_ID, transactionMap.transactionId)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
