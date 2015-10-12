package com.athena.mis.budget.actions.report.projectbudget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/*
 * Search project wise list of items by specific key word
 * For details go through Use-Case doc named 'SearchForProjectBudgetActionService'
 */
class SearchForProjectBudgetActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to generate project costing report"
    private static final String LST_PROJECT_ITEM = "lstProjectItem"
    private static final String PROJECT_ID = "projectId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check required parameter
     * @param parameters -parameter from UI
     * @param obj -N/A
     * @return -a map containing isError (true/false) and related message
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.projectId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
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
     * Get list of item by project and search key word
     * @param parameters -parameter from UI
     * @param obj -N/A
     * @return -a map containing all necessary information for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
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
            initSearch(parameters)
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            String itemName = Tools.PERCENTAGE + query + Tools.PERCENTAGE
            List<BudgetProjectItemModel> lstProjectItem = BudgetProjectItemModel.findAllByProjectIdAndItemNameIlike(projectId, itemName, [offset: start, max: resultPerPage, sort: 'itemName', order: 'asc'])
            int count = BudgetProjectItemModel.countByProjectIdAndItemNameIlike(projectId, itemName)
            result.put(PROJECT_ID, projectId)
            result.put(LST_PROJECT_ITEM, lstProjectItem)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap item list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all necessary information for grid
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<BudgetProjectItemModel> lstProjectItem = (List<BudgetProjectItemModel>) executeResult.get(LST_PROJECT_ITEM)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedProjectItem = wrapProjectItemList(lstProjectItem, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedProjectItem]
            result.put(LST_PROJECT_ITEM, gridOutput)
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
            if (executeResult.get(Tools.MESSAGE)) {
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
     * wrap item list for grid
     * @param lstProjectItem -list of item
     * @param start -starting index
     * @return -wrapped list for grid
     */
    private List wrapProjectItemList(List<BudgetProjectItemModel> lstProjectItem, int start) {
        List lstWrappedProjectItem = []
        int counter = start + 1

        for (int i = 0; i < lstProjectItem.size(); i++) {
            BudgetProjectItemModel projectItem = lstProjectItem[i]
            GridEntity obj = new GridEntity()
            obj.id = projectItem.id
            obj.cell = [
                    counter,
                    projectItem.itemName,
                    projectItem.totalBudgetQuantity + Tools.SINGLE_SPACE + projectItem.itemUnit,
                    projectItem.totalPrQuantity + Tools.SINGLE_SPACE + projectItem.itemUnit,
                    projectItem.remainingQuantity + Tools.SINGLE_SPACE + projectItem.itemUnit
            ]
            lstWrappedProjectItem << obj
            counter++
        }
        return lstWrappedProjectItem
    }
}
