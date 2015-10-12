package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Fixed Asset Details.
 * For details go through Use-Case doc named 'SearchFixedAssetDetailsActionService'
 */
class SearchFixedAssetDetailsActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Can't Get Fixed Asset Details List"
    private static final String FIXED_ASSET_DETAILS_LIST_WRAP = "fixedAssetDetailsList"

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
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
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - wrapped fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initSearch(parameterMap)

            List<Long> userInventoryIdList = inventoryImplService.getUserInventoryIds()
            if (userInventoryIdList.size() <= 0) {
                userInventoryIdList << 0L
            }

            LinkedHashMap serviceReturn = search(userInventoryIdList)
            List<GroovyRowResult> fixedAssetDetailsList = (List<GroovyRowResult>) serviceReturn.fixedAssetDetailsList
            int total = (int) serviceReturn.count

            List faDetailsListWrap = wrapFADetailsGridEntityList(fixedAssetDetailsList, start)

            result.put(FIXED_ASSET_DETAILS_LIST_WRAP, faDetailsListWrap)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * @param obj- object returned from execute method
     * @return - a map containing wrapped fixed asset details for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List fixedAssetDetails = (List) receiveResult.get(FIXED_ASSET_DETAILS_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: fixedAssetDetails]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
    private LinkedHashMap search(List<Long> userInventoryIdList) {
        String lstUserInventoryIds = Tools.buildCommaSeparatedStringOfIds(userInventoryIdList)
        String strQuery = """
            SELECT fad.id, to_char(fad.purchase_date, 'dd-Mon-yyyy') AS purchase_date,
                   fad.name, fad.po_id, inventory.name AS inventory_name,
                   item.name AS item_name, fad.cost, se.key AS owner_type
            FROM fxd_fixed_asset_details  fad
            LEFT JOIN item ON item.id = fad.item_id
            LEFT JOIN inv_inventory inventory ON inventory.id = fad.current_inventory_id
            LEFT JOIN system_entity se ON se.id = fad.owner_type_id
            WHERE inventory.id IN(${lstUserInventoryIds}) AND
                  ${queryType} ilike :query
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT ${resultPerPage}  OFFSET ${start}
        """

        Map queryParams = [query: Tools.PERCENTAGE + query + Tools.PERCENTAGE]

        String queryCount = """
        SELECT COUNT(fad.id) count
        FROM fxd_fixed_asset_details fad
        LEFT JOIN item ON item.id = fad.item_id
        LEFT JOIN inv_inventory inventory ON inventory.id = fad.current_inventory_id
        WHERE inventory.id IN(${lstUserInventoryIds}) AND
              ${queryType} ilike :query
        """

        List<GroovyRowResult> result = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [fixedAssetDetailsList: result, count: total]
    }
}
