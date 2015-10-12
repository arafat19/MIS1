package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show Fixed Asset Details in the Grid.
 * For details go through Use-Case doc named 'ShowFixedAssetDetailsActionService'
 */
class ShowFixedAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Fixed Asset Details"
    private static final String FIXED_ASSET_DETAILS_LIST = "fixedAssetDetailsList"
    private static final String PURCHASE_ORDER_LIST = "poList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize for pagination
     * 2. pull user inventory list
     * 3. get fixed asset details object
     * 4. wrap fixed asset details for grid show
     * 5. pull purchase order list
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - wrapped fixed asset details object and po list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)

            List<Long> userInventoryIdList = inventoryImplService.getUserInventoryIds()
            if (userInventoryIdList.size() <= 0) {
                userInventoryIdList << 0L
            }
            Map serviceReturn = list(userInventoryIdList)
            List<GroovyRowResult> fixedAssetDetailsList = serviceReturn.fixedAssetDetailsList
            int total = (int) serviceReturn.count

            List fixedAssetDetailsListWrap = wrapFADetailsGridEntityList(fixedAssetDetailsList, start)
            List<GroovyRowResult> purchaseOrderList = procurementImplService.getPOListOfFixedAsset()

            result.put(FIXED_ASSET_DETAILS_LIST, fixedAssetDetailsListWrap)
            result.put(PURCHASE_ORDER_LIST, Tools.listForKendoDropdown(purchaseOrderList, null, null))
            result.put(Tools.COUNT, total)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive fixed asset details from execute method
     * 2. get id of SystemEntity object(owner type rental)
     * @param obj - object returned from execute method
     * @return - a map containing wrapped fixed asset details for grid show
     *  and purchase order, owner type for drop-down
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            Integer count = (Integer) receiveResult.get(Tools.COUNT)
            List wrapFAD = (List) receiveResult.get(FIXED_ASSET_DETAILS_LIST)
            Map gridObjQs = [page: pageNumber, total: count, rows: wrapFAD]
            result.put(FIXED_ASSET_DETAILS_LIST, gridObjQs)
            result.put(PURCHASE_ORDER_LIST, receiveResult.get(PURCHASE_ORDER_LIST))
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
     * wrap fixed asset details for grid entity
     * @param fixedAssetDetailsList - list of fixed asset details
     * @param start - starting point of index
     * @return - wrapped fixed asset details for grid entity
     */
    private List wrapFADetailsGridEntityList(List<GroovyRowResult> fixedAssetDetailsList, int start) {
        List newFixedAssetDetailsList = []
        GroovyRowResult fixedAssetDetails
        GridEntity obj
        int counter = start + 1
        for (int i = 0; i < fixedAssetDetailsList.size(); i++) {
            fixedAssetDetails = fixedAssetDetailsList[i]
            obj = new GridEntity()
            obj.id = fixedAssetDetails.id
            obj.cell = [
                    counter,
                    fixedAssetDetails.id,
                    fixedAssetDetails.item_name,
                    fixedAssetDetails.name,
                    fixedAssetDetails.inventory_name,
                    Tools.makeAmountWithThousandSeparator(fixedAssetDetails.cost),
                    fixedAssetDetails.purchase_date,
                    fixedAssetDetails.po_id,
                    fixedAssetDetails.owner_type
            ]
            newFixedAssetDetailsList << obj
            counter++
        }
        return newFixedAssetDetailsList
    }
    /**
     * @param userInventoryIdList - list of user inventory
     * @return - list of fixed asset details
     */
    private LinkedHashMap list(List<Long> userInventoryIdList) {
        //@todo:model adjust using FixedAssetDetailsModel.list()
        String lstUserInventoryIds = Tools.buildCommaSeparatedStringOfIds(userInventoryIdList)
        String queryStr = """
            SELECT fad.id, to_char(fad.purchase_date, 'dd-Mon-yyyy') AS purchase_date,
                   fad.name, fad.po_id, inventory.name AS inventory_name,
                   item.name AS item_name, fad.cost, se.key AS owner_type
            FROM fxd_fixed_asset_details  fad
            LEFT JOIN item ON item.id = fad.item_id
            LEFT JOIN inv_inventory inventory ON inventory.id = fad.current_inventory_id
            LEFT JOIN system_entity se ON se.id = fad.owner_type_id
            WHERE inventory.id IN(${lstUserInventoryIds})
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT ${resultPerPage}  OFFSET ${start}
        """
        //@todo:model adjust using FxdFixedAssetDetails.count()
        String queryCount = """
        SELECT COUNT(fad.id) count
        FROM fxd_fixed_asset_details fad
        LEFT JOIN inv_inventory inventory ON inventory.id = fad.current_inventory_id
        WHERE inventory.id IN(${lstUserInventoryIds})
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount)

        int total = (int) countResult[0].count
        return [fixedAssetDetailsList: result, count: total]
    }
}