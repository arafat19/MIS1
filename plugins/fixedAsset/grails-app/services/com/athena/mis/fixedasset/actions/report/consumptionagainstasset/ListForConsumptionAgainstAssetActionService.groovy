package com.athena.mis.fixedasset.actions.report.consumptionagainstasset

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
 * List of Consumption Against Asset.
 * For details go through Use-Case doc named 'ListForConsumptionAgainstAssetActionService'
 */
class ListForConsumptionAgainstAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate report."
    private static final String PROJECT_WISE_SUMMARY_LIST = "projectWiseSummaryList"
    private static final String CONSUMPTION_LIST_NOT_FOUND = "Consumption list not found."
    /**
     * 1. check input validation
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get wrapped consumption details for grid show
     * 1. initialize pagination if necessary
     * 2. set transaction type = consumption
     * 3. get project wise fixed asset consumption
     * 4. check consumption list existence
     * 5. wrap consumption details for grid
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing project wise summary and isError msg(True/False) and relevant msg(if any)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap serviceReturn

            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            long projectId = Long.parseLong(parameterMap.projectId.toString())
            long tranTypeCon = inventoryImplService.getInvTransactionTypeIdConsumption()
            serviceReturn = getProjectWiseFixedAssetConsump(projectId, tranTypeCon)

            List<GroovyRowResult> summaryList = (List<GroovyRowResult>) serviceReturn.summaryList
            int count = (int) serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, CONSUMPTION_LIST_NOT_FOUND)
                return result
            }
            List projectWiseSummaryList = wrapProjectWiseSummaryListInGrid(summaryList, start)
            result.put(PROJECT_WISE_SUMMARY_LIST, projectWiseSummaryList)
            result.put(Tools.COUNT, count)
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
     * Get wrapped grid output of project wise summary
     * @param obj - object receive from execute method
     * @return - wrapped grid output of project wise summary
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            List projectWiseSummaryListWrap = (List) executeResult.get(PROJECT_WISE_SUMMARY_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridOutput = [page: this.pageNumber, total: count, rows: projectWiseSummaryListWrap]

            result.put(PROJECT_WISE_SUMMARY_LIST, gridOutput)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
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
     * Wrapped consumption summary for grid show
     * @param summaryList - project wise consumption summary list
     * @param start - starting point of index
     * @return - Wrapped consumption summary
     */
    private List wrapProjectWiseSummaryListInGrid(List<GroovyRowResult> summaryList, int start) {
        List lstProjectWiseSummaryList = []
        int counter = start + 1
        GroovyRowResult projectWiseSummary
        GridEntity obj

        for (int i = 0; i < summaryList.size(); i++) {
            projectWiseSummary = summaryList[i]
            obj = new GridEntity()
            obj.id = projectWiseSummary.id
            obj.cell = [counter,
                    projectWiseSummary.name,
                    projectWiseSummary.total_consumed + ' ' + projectWiseSummary.unit
            ]
            lstProjectWiseSummaryList << obj
            counter++
        }
        return lstProjectWiseSummaryList
    }

    private static final String SELECT_QUERY = """
            SELECT item.id, item.name, item.unit,
                   COALESCE(SUM(actual_quantity),0) AS total_consumed
            FROM inv_inventory_transaction_details  iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN item ON item.id = iitd.item_id
            WHERE iit.project_id =:projectId AND
                  iit.transaction_type_id =:transactionTypeConsumption AND
                  iitd.approved_by > 0 AND
                  iitd.fixed_asset_details_id > 0 AND
                  iitd.fixed_asset_id > 0 AND
                  iitd.is_current = true
            GROUP BY item.id, item.name, item.unit
            ORDER BY item.name
            LIMIT :resultPerPage  OFFSET :start
        """

    private static final String SELECT_COUNT_QUERY = """
            SELECT COUNT(DISTINCT(item.id)) AS count
                 FROM inv_inventory_transaction_details  iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iit.project_id =:projectId AND
                  iit.transaction_type_id =:transactionTypeConsumption AND
                  iitd.approved_by > 0 AND
                  iitd.fixed_asset_details_id > 0 AND
                  iitd.fixed_asset_id > 0 AND
                  iitd.is_current = true
            """
    //@todo:model  change when inventory model implementation is done
    /**
     * Get project wise fixed asset consumption details
     * @param projectId - project id
     * @param transactionTypeConsumption - transaction type = consumption
     * @return - a map containing project wise fixed asset consumption details list
     */
    private Map getProjectWiseFixedAssetConsump(long projectId, long transactionTypeConsumption) {
        Map queryParams = [
                projectId: projectId,
                transactionTypeConsumption: transactionTypeConsumption,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> summaryList = executeSelectSql(SELECT_QUERY, queryParams)
        List<GroovyRowResult> summaryCount = executeSelectSql(SELECT_COUNT_QUERY, queryParams)

        Map result = [summaryList: summaryList, count: summaryCount[0].count]
        return result
    }
}
