package com.athena.mis.fixedasset.actions.report.consumptionagainstassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Show Consumption Against Asset Details in the grid.
 * For details go through Use-Case doc named 'ShowForConsumptionAgainstAssetDetailsActionService'
 */
class ShowForConsumptionAgainstAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Fail to load fixed asset details report page"
    private static final String ITEM_LIST = "itemList"
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get item(consumed against fixed asset) list to populate item drop-down
     * @param parameters - N/A
     * @param obj - N/A
     * @return - item(consumed against fixed asset) list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            Map serviceReturn = getIsConsumedAgainstItemList()
            List itemList = serviceReturn.itemList

            result.put(ITEM_LIST, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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

    private static final String SELECT_QUERY = """
            SELECT item.id, item.name, item.unit
            FROM budg_budget_details bbd
            LEFT JOIN item ON item.id = bbd.item_id
            WHERE  bbd.is_consumed_against_fixed_asset = TRUE
            GROUP BY item.id, item.name, item.unit
            ORDER BY item.name
        """

    //Get items which : isConsumedAgainstFixedAsset = TRUE(On Budget details creation)
    private Map getIsConsumedAgainstItemList() {
        List<GroovyRowResult> itemList = executeSelectSql(SELECT_QUERY)
        return [itemList: itemList]
    }
}