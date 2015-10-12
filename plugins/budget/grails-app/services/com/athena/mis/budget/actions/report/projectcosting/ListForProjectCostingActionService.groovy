package com.athena.mis.budget.actions.report.projectcosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/*
 * List of Project Costing
 * For details go through Use-Case doc named 'ListForProjectCostingActionService'
 */
class ListForProjectCostingActionService extends BaseService implements ActionIntf {

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate project costing report."
    private static final String PROJECT_COSTING_LIST = "projectCostingList"
    private static final String BUDGET_NOT_FOUND = "Budget not found with this Project."
    private static final String PROJECT_ID = "projectId"

    private Logger log = Logger.getLogger(getClass())

    /**
     *
     * @param parameters
     * @param obj
     * @return
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
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
     *
     * @param parameters
     * @param obj
     * @return
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
            this.initPager(parameters)

            long projectId = Long.parseLong(parameterMap.projectId.toString())
            LinkedHashMap serviceReturn = listProjectCosting(projectId)

            List<GroovyRowResult> projectCostingList = serviceReturn.projectCostingList
            int count = serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List lstProjectCosting = wrapProjectCostingListInGrid(projectCostingList, this.start)
            result.put(PROJECT_ID, projectId)
            result.put(PROJECT_COSTING_LIST, lstProjectCosting)
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
     *
     * @param obj
     * @return
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List lstProjectCosting = (List) executeResult.get(PROJECT_COSTING_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: lstProjectCosting]
            result.put(PROJECT_COSTING_LIST, gridOutput)
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
     *
     * @param obj
     * @return
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
     *
     * @param projectCostingList
     * @param start
     * @return
     */
    private List wrapProjectCostingListInGrid(List<GroovyRowResult> projectCostingList, int start) {
        List lstProjectCosting = []
        int counter = start + 1
        GroovyRowResult projectCosting

        for (int i = 0; i < projectCostingList.size(); i++) {
            GridEntity obj = new GridEntity()
            projectCosting = projectCostingList[i]
            obj.id = projectCosting.id
            obj.cell = [
                    counter,
                    projectCosting.name,
                    projectCosting.str_budget_quantity,
                    projectCosting.str_budget_amount
            ]
            lstProjectCosting << obj
            counter++
        }
        return lstProjectCosting
    }

    private static final String QUERY_COUNT = """
        SELECT count(distinct(item_id))
            FROM budg_budget_details bd
            LEFT JOIN item ON item.id = bd.item_id
            WHERE bd.project_id=:projectId
            AND item.is_finished_product = false
    """
    /**
     *
     * @param projectId
     * @return
     */
    private LinkedHashMap listProjectCosting(long projectId) {
        String queryStr = """
                  SELECT item.id AS id, item.name AS name,
                       TO_CHAR(COALESCE(SUM(budget.quantity),0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS str_budget_quantity,
                       TO_CHAR(COALESCE(SUM(budget.quantity* budget.rate),0),'${Tools.DB_CURRENCY_FORMAT}') AS str_budget_amount
                  FROM budg_budget_details budget
                  LEFT JOIN item ON item.id = budget.item_id
                  WHERE budget.project_id = :projectId
                  AND item.is_finished_product = false
                  GROUP BY item.id, item.name, item.unit
                  ORDER BY item.name LIMIT ${resultPerPage} OFFSET ${start}
              """


        Map queryParams = [projectId: projectId]
        List<GroovyRowResult> result = executeSelectSql(queryStr,queryParams)
        List<GroovyRowResult> projectCostingCount = executeSelectSql(QUERY_COUNT, queryParams)
        int total = projectCostingCount[0].count
        return [projectCostingList: result, count: total]
    }

}

