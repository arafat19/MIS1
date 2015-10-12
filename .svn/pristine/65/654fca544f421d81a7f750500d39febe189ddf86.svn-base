package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get budget list by list of project ids to build budget list for right panel
 * For details go through Use-Case doc named 'ListBudgetByProjectIdsImplActionService'
 */
class ListBudgetByProjectIdsImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget list by list of project ids
     * @param baseServiceObj -object of baseService
     * @param projectIds -list of project ids
     * @return -a map containing the list and count of budget by project ids
     */
    @Transactional(readOnly = true)
    public Object execute(Object baseServiceObj, Object projectIds) {
        try {
            BaseService baseService = (BaseService) baseServiceObj
            List<Long> lstProjects = (List<Long>) projectIds
            return listBudgetByProjectIdList(baseService, lstProjects)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return []
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build success failure for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get budget list by list of project ids
     * @param baseService -object of baseService
     * @param projectIdList -list of project ids
     * @return -a map containing the list and count of budget by project ids
     */
    private LinkedHashMap listBudgetByProjectIdList(BaseService baseService, List<Long> projectIdList) {

        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        //@todo:model adjust using BudgetProjectModel.listBudgetByProject(x)
        String queryStr = """
                SELECT budget.id, budget.budget_item, budget.details, budget.project_id, project.name project_name, budget.budget_quantity, system_entity.key AS unit_name
                FROM budg_budget budget
                LEFT JOIN system_entity ON system_entity.id = budget.unit_id
                LEFT JOIN project ON project.id = budget.project_id
                WHERE budget.project_id in (${projectIds})
                ORDER BY ${baseService.sortColumn} ${baseService.sortOrder}  LIMIT ${baseService.resultPerPage}  OFFSET ${baseService.start}
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)
        int count = BudgBudget.countByProjectIdInList(projectIdList)
        return [budgetList: result, count: count]
    }
}