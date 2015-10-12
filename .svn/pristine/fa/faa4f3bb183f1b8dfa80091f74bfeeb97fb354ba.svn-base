package com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.UserProjectCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Consumption Against Asset Details.
 * For details go through Use-Case doc named 'ListForConsumptionAgainstAssetDetailsActionService'
 */
class ListForConsumptionAgainstAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to get consumption list"
    private static final String INVALID_INPUT_MESSAGE = "Error occurred due to invalid input"
    private static final String NOT_FOUND_MESSAGE = "Consumption not found within given date"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not associated with any project"
    private static final String FIXED_ASSET_NOT_FOUND_MESSAGE = "Fixed asset not found"
    private static final String CONSUMPTION_LIST = "consumptionList"
    private static final String DEFAULT_SORT_NAME = "fad.name, iitd.transaction_date"
    private static final String SORT_ORDER_ASCENDING = "ASC"

    /**
     * 1. check input validation
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!parameterMap.fixedAssetDetailsId || !parameterMap.fromDate || !parameterMap.toDate
                    || !parameterMap.itemId || !parameterMap.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Get consumption details for grid show
     * 1. initialize pagination if necessary
     * 2. set transaction type = consumption
     * 3. get project wise fixed asset consumption
     * 4. check consumption list existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing consumption details list and isError msg(True/False) and relevant msg(if any)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            Date fromDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedDate(params.toDate.toString())

            //generate pagination
            generatePagination(params)

            long itemId = Long.parseLong(params.itemId.toString())

            List<Long> userProjectIds = []
            long projectId = Long.parseLong(params.projectId.toString())
            if (projectId <= 0) {
                userProjectIds = userProjectCacheUtility.listUserProjectIds()
                if (userProjectIds.size() <= 0) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                    return result
                }
            } else {
                userProjectIds << projectId
            }
            long transactionTypeConId = inventoryImplService.getInvTransactionTypeIdConsumption()

            List<Long> fixedAssetDetailsIds = []
            long fixedAssetDetailsId = Long.parseLong(params.fixedAssetDetailsId.toString())
            if (fixedAssetDetailsId <= 0) {//For All FixedAsset
                List fixedAssetList = getFixedAssetIdList(fromDate, toDate, userProjectIds, itemId, transactionTypeConId)
                if (fixedAssetList.size() <= 0) {
                    result.put(Tools.MESSAGE, FIXED_ASSET_NOT_FOUND_MESSAGE)
                    return result
                }
                for (int i = 0; i < fixedAssetList.size(); i++) {
                    fixedAssetDetailsIds << fixedAssetList[i].id
                }
            } else {//For exact FixedAsset
                fixedAssetDetailsIds << fixedAssetDetailsId
            }

            Map serviceReturn = getConsumptionList(userProjectIds, fixedAssetDetailsIds, itemId, transactionTypeConId, fromDate, toDate)

            List<GroovyRowResult> consumptionList = (List<GroovyRowResult>) serviceReturn.consumptionList
            if (consumptionList.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            result.put(CONSUMPTION_LIST, consumptionList)
            result.put(Tools.COUNT, serviceReturn.count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
     * Get wrapped grid output of consumption list
     * @param obj - object receive from execute method
     * @return - wrapped grid output of consumption list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> consumptionList = (List<GroovyRowResult>) receiveResult.get(CONSUMPTION_LIST)
            Integer count = (Integer) receiveResult.get(Tools.COUNT)

            List consumptionListWrap = (List) wrapConsumptionListInGrid(consumptionList, this.start)
            result = [page: pageNumber, total: count, rows: consumptionListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: []]
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
            result = [page: pageNumber, total: 0, rows: []]
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                String receiveResultMessage = receiveResult.get(Tools.MESSAGE)
                result.put(Tools.MESSAGE, receiveResultMessage)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result = [page: pageNumber, total: 0, rows: []]
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Wrapped consumption details for grid show
     * @param consumptionList - project wise consumption list
     * @param start - starting point of index
     * @return - Wrapped consumption list
     */
    private List wrapConsumptionListInGrid(List<GroovyRowResult> consumptionList, int start) {
        List lstConsumptionList = [] as List
        int counter = start + 1
        GroovyRowResult singleRow
        GridEntity obj
        for (int i = 0; i < consumptionList.size(); i++) {
            singleRow = consumptionList[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.fixed_asset_name,
                    DateUtility.getLongDateForUI(singleRow.transaction_date),
                    singleRow.quantity + Tools.SINGLE_SPACE + singleRow.unit,
                    singleRow.inventory_name
            ]
            lstConsumptionList << obj
            counter++
        }
        return lstConsumptionList
    }
    /**
     * initialize pagination
     */
    private void generatePagination(GrailsParameterMap params) {

        if (!params.page || !params.rp) {
            params.page = 1
            params.rp = 15
            params.currentCount = 0
            params.sortname = DEFAULT_SORT_NAME
            params.sortorder = SORT_ORDER_ASCENDING
        }
        initPager(params)
    }
    /**
     * Get consumption details list
     * @param userProjectIds -project ids
     * @param fixedAssetDetailsIds - fixed asset details ids
     * @param itemId - item id
     * @param transactionTypeConId - transaction type = consumption
     * @param fromDate - start date
     * @param toDate - end date
     * @return - consumption details list
     */
    private Map getConsumptionList(List<Long> userProjectIds, List<Long> fixedAssetDetailsIds, long itemId, long transactionTypeConId, Date fromDate, Date toDate) {
        String lstUserProjectIds = Tools.buildCommaSeparatedStringOfIds(userProjectIds)
        String lstFixedAssetDetailsIds = Tools.buildCommaSeparatedStringOfIds(fixedAssetDetailsIds)

        String queryStr = """
            SELECT iitd.id, inv.name AS inventory_name, fad.name AS fixed_asset_name, iitd.transaction_date,
                   to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit
            FROM vw_inv_inventory_transaction_with_details iitd
            LEFT JOIN inv_inventory inv ON inv.id = iitd.inventory_id
            LEFT JOIN fxd_fixed_asset_details fad ON fad.id = iitd.fixed_asset_details_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE  iitd.transaction_type_id =:transactionTypeConId AND
                   iitd.approved_by > 0 AND
                   iitd.is_current = TRUE AND
                   iitd.item_id =:itemId AND
                   iitd.project_id IN(${lstUserProjectIds}) AND
                   iitd.fixed_asset_details_id IN(${lstFixedAssetDetailsIds}) AND
                   (iitd.transaction_date BETWEEN :fromDate AND :toDate)
            ORDER BY fad.name, iitd.transaction_date
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
                SELECT count(iitd.id)
                FROM vw_inv_inventory_transaction_with_details iitd
                WHERE iitd.transaction_type_id =:transactionTypeConId AND
                      iitd.approved_by > 0 AND
                      iitd.is_current = TRUE AND
                      iitd.item_id =:itemId AND
                      iitd.project_id IN(${lstUserProjectIds}) AND
                      iitd.fixed_asset_details_id IN(${lstFixedAssetDetailsIds}) AND
                     (iitd.transaction_date BETWEEN  :fromDate AND :toDate)
            
        """
        Map queryParams = [
                itemId: itemId,
                transactionTypeConId: transactionTypeConId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> consumptionList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [consumptionList: consumptionList, count: count]
    }


    private List<GroovyRowResult> getFixedAssetIdList(Date fromDate, Date toDate, List<Long> projectIdList, long itemId, long transactionTypeId) {
        String lstProjectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        String queryStr = """
            SELECT iitd.fixed_asset_details_id AS id
              FROM vw_inv_inventory_transaction_with_details iitd
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE  iitd.transaction_type_id =:transactionTypId AND
                   iitd.project_id IN(${lstProjectIds}) AND
                   iitd.approved_by > 0 AND
                   iitd.fixed_asset_details_id > 0 AND
                   iitd.is_current = TRUE AND
                   iitd.item_id =:itemId AND
                   (iitd.transaction_date BETWEEN :fromDate AND :toDate)
            GROUP BY iitd.fixed_asset_details_id
        """

        Map queryParams = [
                transactionTypId: transactionTypeId,
                itemId: itemId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate)
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(queryStr, queryParams)
        return fixedAssetList
    }
}

