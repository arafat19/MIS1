package com.athena.mis.inventory.actions.report.itemwisebudgetsummary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  get list of Item-Wise-Budget-Summary for grid
 *  For details go through Use-Case doc named 'ListForItemWiseBudgetSummaryActionService'
 */
class ListForItemWiseBudgetSummaryActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String FAILURE_MSG = "Fail to load item wise budget summary report"
    private static final String ITEM_WISE_SUMMARY_LIST = "itemWiseSummaryList"
    private static final String PROJECT_ID = "projectId"
    private static final String COUNT = "count"

    /**
     * Checking required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.projectId) {
                result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * method to get list of item-wise-budget-summary and wrap for grid
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing wrapped itemWiseBudgetSummaryList for grid and isError(True/False) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            long projectId = Long.parseLong(parameterMap.projectId.toString())

            //map contains ItemWiseBudgetSummaryList and count
            Map serviceReturn = listItemWiseBudgetSummaryByProjectId(projectId)

            List<GroovyRowResult> itemWiseSummaryList = (List<GroovyRowResult>) serviceReturn.itemWiseSummaryList
            int count = (int) serviceReturn.count

            //Wrap ItemWiseBudgetSummaryList for grid
            List wrapItemWiseSummaryList = wrapItemWiseBudgetSummaryInGrid(itemWiseSummaryList, start)

            result.put(PROJECT_ID, projectId)
            result.put(ITEM_WISE_SUMMARY_LIST, wrapItemWiseSummaryList)
            result.put(COUNT, count)
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
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped ItemWiseBudgetSummaryList for grid & isError(True/False) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            List itemWiseSummaryListWrap = (List) executeResult.get(ITEM_WISE_SUMMARY_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: itemWiseSummaryListWrap]

            result.put(ITEM_WISE_SUMMARY_LIST, gridOutput)
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
     * @param obj -map returned from previous method, can be null
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
     * Wrapped-ItemWiseBudgetSummaryList for grid
     * @param itemWiseSummaryList -List of GroovyRowResult
     * @param start -start index
     * @return -WrappedItemWiseBudgetSummaryList
     */
    private List wrapItemWiseBudgetSummaryInGrid(List<GroovyRowResult> itemWiseSummaryList, int start) {
        List lstItemWiseSummary = [] as List
        int counter = start + 1
        GroovyRowResult eachRow
        GridEntity obj

        int summaryCount = itemWiseSummaryList.size()
        if (summaryCount > 0) {
            for (int i = 0; i < summaryCount; i++) {
                eachRow = itemWiseSummaryList[i]
                obj = new GridEntity()
                obj.id = counter
                obj.cell = [counter,
                        eachRow.name,
                        eachRow.budget_amount,
                        eachRow.po_amount,
                        eachRow.po_remaining_amount,
                        eachRow.store_in_amount,
                        eachRow.consumption_amount
                ]
                lstItemWiseSummary << obj
                counter++
            }
        }
        return lstItemWiseSummary
    }

    private static final String COUNT_QUERY = """
                SELECT COUNT(DISTINCT(item_id)) count FROM  budg_budget_details  WHERE project_id =:projectId
             """

    /**
     * Get list of ItemWiseBudgetSummary list
     * @param projectId -Project.id
     * @return -a map contains GroovyRawResult(ItemWiseBudgetSummary list) and count
     */
    public Map listItemWiseBudgetSummaryByProjectId(long projectId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        String queryStr = """
                   SELECT item.name,
                       to_char(COALESCE(SUM(bd.quantity * bd.rate),0),'${Tools.DB_QUANTITY_FORMAT}') AS budget_amount,
                      (SELECT to_char(COALESCE(SUM(pod.quantity * pod.rate),0),'${Tools.DB_QUANTITY_FORMAT}') FROM proc_purchase_order_details pod WHERE pod.item_id = bd.item_id AND pod.project_id =:projectId) AS po_amount,
                       to_char(COALESCE(SUM(bd.quantity * bd.rate),0) - (SELECT COALESCE(SUM(pod.quantity * pod.rate),0) FROM proc_purchase_order_details pod WHERE pod.item_id = bd.item_id AND pod.project_id = ${projectId}),'${Tools.DB_QUANTITY_FORMAT}') AS po_remaining_amount,
                       (SELECT to_char(COALESCE(SUM(iitd.actual_quantity * iitd.rate),0),'${Tools.DB_QUANTITY_FORMAT}')
                            FROM inv_inventory_transaction_details iitd
                            LEFT JOIN inv_inventory_transaction iit ON iit.id=iitd.inventory_transactiON_id
                            WHERE iitd.item_id = bd.item_id AND iitd.approved_by > 0 AND iit.transaction_type_id =:transactionTypeIdIn
                            AND iit.transaction_entity_type_id =:transactionEntityTypeId
                            AND iit.project_id =:projectId AND iitd.is_current = true) AS store_in_amount,
                        (SELECT to_char(COALESCE(SUM(iitd.actual_quantity * iitd.rate),0),'${Tools.DB_QUANTITY_FORMAT}')
                            FROM inv_inventory_transaction_details iitd
                            LEFT JOIN inv_inventory_transaction iit ON iit.id=iitd.inventory_transactiON_id
                            WHERE iitd.item_id = bd.item_id AND iitd.approved_by > 0 AND iit.transaction_type_id =:transactionTypeIdConsump
                            AND iit.project_id =:projectId AND iitd.is_current = true) AS consumption_amount
                    FROM budg_budget_details bd
                    LEFT JOIN item ON item.id = bd.item_id
                    WHERE bd.project_id =:projectId
                    GROUP BY bd.item_id,item.name
                    ORDER BY item.name
                    LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                projectId: projectId,
                transactionEntityTypeId: transactionEntitySupplier.id,
                transactionTypeIdConsump: transactionTypeConsumption.id,
                transactionTypeIdIn: transactionTypeIn.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstSummary = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> budgetCount = executeSelectSql(COUNT_QUERY, queryParams)

        Map result = [itemWiseSummaryList: lstSummary, count: budgetCount[0].count]
        return result
    }
}
