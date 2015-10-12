package com.athena.mis.budget.actions.report.consumptiondeviation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/*
 * List of Consumption Deviation for Grid
 * For details go through Use-Case doc named 'ListForConsumptionDeviationActionService'
 */

class ListForConsumptionDeviationActionService extends BaseService implements ActionIntf {

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String COUNT = "count"
    private static final String PROJECT_ID = "projectId"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String CONSUMPTION_DEVIATION_LIST = "consumptionDeviationList"
    private static final String FAILURE_MSG = "Fail to generate consumption deviation report."
    private static final String BUDGET_CONSUMPTION_NOT_FOUND = "Budget consumption not found "

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * 1. Checking the mapping between user and project by isAccessible method of projectCacheUtility
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false)
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

            long projectId = Long.parseLong(params.projectId.toString())
            boolean isAccessible = projectCacheUtility.isAccessible(projectId)
            if (!isAccessible) {
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
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get consumption deviation list
     * 1. Get consumption deviation list by project id
     * 2. Wrap consumption deviation list in grid entity by wrapConsumptionDeviationListInGridEntityList method
     * 3. Check the existence of consumption deviation by when count<= 0
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            this.initPager(parameterMap)

            long projectId = Long.parseLong(parameterMap.projectId.toString())

            LinkedHashMap serviceReturn = getConsumptionDeviationList(projectId)


            List<GroovyRowResult> consumptionDeviationList = serviceReturn.consumptionDeviationList
            int count = serviceReturn.count

            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_CONSUMPTION_NOT_FOUND)
                return result
            }

            List consumptionDeviationListWrap = wrapConsumptionDeviationListInGridEntityList(consumptionDeviationList, this.start)

            result.put(CONSUMPTION_DEVIATION_LIST, consumptionDeviationListWrap)
            result.put(COUNT, count)
            result.put(PROJECT_ID, projectId)
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
     * Give consumption deviation list as grid output in UI
     * 1. Get consumption deviation list & count from execute method
     * 2. Make a map name gridOutput as CONSUMPTION_DEVIATION_LIST
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List supplierPayableListWrap = (List) executeResult.get(CONSUMPTION_DEVIATION_LIST)
            int count = (int) executeResult.get(COUNT)

            Map gridOutput = [page: this.pageNumber, total: count, rows: supplierPayableListWrap]
            result.put(CONSUMPTION_DEVIATION_LIST, gridOutput)
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
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
     * Wrap consumption deviation in grid entity
     * @param consumptionDeviationList -  consumption deviation list from execute method
     * @param start - starting index of the page
     * @return - list of wrapped consumption deviation
     */
    private
    static List wrapConsumptionDeviationListInGridEntityList(List<GroovyRowResult> consumptionDeviationList, int start) {
        List lstConsumptionDeviation = [] as List

        int counter = start + 1
        GroovyRowResult consumptionDeviation
        GridEntity obj

        for (int i = 0; i < consumptionDeviationList.size(); i++) {
            consumptionDeviation = consumptionDeviationList[i]
            obj = new GridEntity()
            obj.id = consumptionDeviation.item_id
            obj.cell = [
                    counter,
                    consumptionDeviation.item_name,
                    consumptionDeviation.str_budget_quantity,
                    consumptionDeviation.str_budget_amount,
                    consumptionDeviation.str_cosume_quantity,
                    consumptionDeviation.str_consume_amount,
                    consumptionDeviation.str_deviation_amount
            ]
            lstConsumptionDeviation << obj
            counter++
        }
        return lstConsumptionDeviation
    }

    /**
     * Get consumption deviation list and it's count
     * @param projectId - get project id from params
     * @return - a map of consumptionDeviationList and count
     */
    private LinkedHashMap getConsumptionDeviationList(long projectId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()

        String queryStr = """
                SELECT item.id AS item_id, item.name AS item_name,
                TO_CHAR(COALESCE(budget_quantity,0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS str_budget_quantity,
                TO_CHAR(COALESCE(budget_amount,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_budget_amount,
                TO_CHAR(COALESCE(cosume_quantity,0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS str_cosume_quantity,
                TO_CHAR(COALESCE(consume_amount,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_consume_amount,
                TO_CHAR((COALESCE(budget_amount,0) - COALESCE(consume_amount,0)),'${Tools.DB_CURRENCY_FORMAT}') AS str_deviation_amount
                FROM item
                FULL OUTER JOIN
                    (
                           SELECT  item_id, SUM(quantity) AS budget_quantity,
                            SUM(quantity*rate) AS budget_amount
                        FROM budg_budget_details
                        WHERE project_id = ${projectId}
                        GROUP BY item_id
                    ) bbd
                ON bbd.item_id = item.id

                FULL OUTER JOIN
                    (
                           SELECT  item_id, SUM(inv_inventory_transaction_details.actual_quantity) AS cosume_quantity,
                            SUM(inv_inventory_transaction_details.actual_quantity*inv_inventory_transaction_details.rate) AS consume_amount
                        FROM inv_inventory_transaction_details
                        LEFT JOIN inv_inventory_transaction ON inv_inventory_transaction.id = inv_inventory_transaction_details.inventory_transaction_id
                        WHERE inv_inventory_transaction.project_id = ${projectId}
                        AND inv_inventory_transaction.transaction_type_id = ${
            inventoryImplService.getInvTransactionTypeIdConsumption()
        }
                        AND inv_inventory_transaction.transaction_entity_type_id = ${
            inventoryImplService.transactionEntityTypeIdNone
        }
                        AND inv_inventory_transaction_details.is_current = true
                        AND inv_inventory_transaction_details.approved_by > 0
                        AND inv_inventory_transaction.inv_production_line_item_id = 0
                        AND inv_inventory_transaction.budget_id > 0
                        GROUP BY item_id
                    ) iitd
                ON iitd.item_id = item.id
                WHERE item.company_id = ${companyId}
                GROUP BY item.id, item.name, item.unit,budget_quantity,budget_amount,
                cosume_quantity,consume_amount
                HAVING budget_quantity>0
                ORDER BY item.name
                LIMIT ${resultPerPage}  OFFSET ${start}
        """
        List<GroovyRowResult> consumptionDeviationList = executeSelectSql(queryStr)
        // execute raw sql for getting consumption deviation list

        String queryCount = """
        SELECT COUNT(1) count FROM
        (
            SELECT item.id 
                FROM item
                FULL OUTER JOIN
                    (
                           SELECT  item_id, SUM(quantity) AS budget_quantity,
                            SUM(quantity*rate) AS budget_amount
                        FROM budg_budget_details
                        WHERE project_id = ${projectId}
                        GROUP BY item_id
                    ) bbd
                ON bbd.item_id = item.id

                FULL OUTER JOIN
                    (
                           SELECT  item_id, SUM(inv_inventory_transaction_details.actual_quantity) AS cosume_quantity,
                            SUM(inv_inventory_transaction_details.actual_quantity*inv_inventory_transaction_details.rate) AS consume_amount
                        FROM inv_inventory_transaction_details
                        LEFT JOIN inv_inventory_transaction ON inv_inventory_transaction.id = inv_inventory_transaction_details.inventory_transaction_id
                        WHERE inv_inventory_transaction.project_id = ${projectId}
                        AND inv_inventory_transaction.transaction_type_id = ${
            inventoryImplService.getInvTransactionTypeIdIn()
        }
                        AND inv_inventory_transaction.transaction_entity_type_id = ${
            inventoryImplService.transactionEntityTypeIdNone
        }
                        AND inv_inventory_transaction_details.is_current = true
                        AND inv_inventory_transaction_details.approved_by > 0
                        AND inv_inventory_transaction.inv_production_line_item_id = 0
                        AND inv_inventory_transaction.budget_id > 0
                        GROUP BY item_id
                    ) iitd
                ON iitd.item_id = item.id
                WHERE item.company_id = ${companyId}
                GROUP BY item.id, item.name, item.unit,budget_quantity,budget_amount,
                cosume_quantity,consume_amount
                HAVING budget_quantity>0
        ) tmp
        """
        List countResults = executeSelectSql(queryCount) // get count result for consumption deviation
        int count = countResults[0].count
        return [consumptionDeviationList: consumptionDeviationList, count: count]
    }
}