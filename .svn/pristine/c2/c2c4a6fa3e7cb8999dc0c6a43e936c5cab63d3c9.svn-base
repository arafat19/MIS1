package com.athena.mis.budget.model

import com.athena.mis.utility.Tools

// BudgetProjectModel is the model for database view vw_budg_budget_with_project,
// which is primarily used to list BudgBudget


class BudgetProjectModel implements Serializable {

    public static final String SQL_BUDG_BUDGET_PROJECT_MODEL = """
            DROP TABLE IF EXISTS vw_budg_budget_with_project;
            DROP VIEW IF EXISTS vw_budg_budget_with_project;
            CREATE OR REPLACE view vw_budg_budget_with_project AS
            SELECT budget.id AS budget_id, budget.budget_item, budget.details AS budget_details,
            budget.project_id, project.name AS project_name, project.code AS project_code, budget.budget_quantity,
            system_entity.key AS unit_name, budget_scope.name AS budget_scope_name,
            budget.billable, budget.company_id, budget.item_count, budget.content_count, budget.task_count, budget.is_production
            FROM budg_budget budget
            LEFT JOIN system_entity ON system_entity.id = budget.unit_id
            LEFT JOIN budg_budget_scope budget_scope ON budget_scope.id = budget.budget_scope_id
            LEFT JOIN project ON project.id = budget.project_id;
   """

    long budgetId                       //BudgBudget.id
    String budgetItem                   //BudgBudget.budgetItem
    String budgetDetails                //BudgBudget.details
    long projectId                      //BudgBudget.projectId
    long companyId                      //BudgBudget.companyId
    String projectName                  //Project.name
    String projectCode                  //Project.code
    double budgetQuantity               //BudgBudget.budgetQuantity
    String unitName                     //SystemEntity.value
    String budgetScopeName              //BudgBudgetScope.name
    boolean billable                    //BudgBudget.billable
    int itemCount                       //BudgBudget.itemCount
    int contentCount                    //BudgBudget.contentCount
    int taskCount                       //BudgBudget.taskCount
    boolean isProduction                //BudgBudget.billable

    static mapping = {
        table 'vw_budg_budget_with_project'  //database view
        version false
        id composite: ['budgetId', 'projectId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listBudgetByProject { List<Long> projectIds ->
            'in'('projectId', projectIds)
        }

        listBudgetByDetailsAndItem { List<Long> projectIds, String query ->
            or {
                ilike('budgetDetails', Tools.PERCENTAGE + query + Tools.PERCENTAGE)
                ilike('budgetItem', Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            }
            'in'('projectId', projectIds)
            setReadOnly(true)
        }

        listBudgetByDetailsAndItemAndBillable { List<Long> projectIds, String query, boolean billable ->
            or {
                ilike('budgetDetails', Tools.PERCENTAGE + query + Tools.PERCENTAGE)
                ilike('budgetItem', Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            }
            'in'('projectId', projectIds)
            eq('billable', new Boolean(billable))
            setReadOnly(true)
        }

        searchByProjectIdsAndIsProductionAndQuery { List<Long> projectIds, boolean isProduction, String queryType, String query ->
            'in'('projectId', projectIds)
            eq('isProduction', new Boolean(isProduction))
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            setReadOnly(true)
        }

        searchByProjectIdsAndBudgetIdsAndIsProduction { List<Long> projectIds, List<Long> budgetIds, boolean isProduction ->
            'in'('projectId', projectIds)
            'in'('budgetId', budgetIds)
            eq('isProduction', new Boolean(isProduction))
            setReadOnly(true)
        }

        // following method is used in budget grid search by material name
        searchByProjectIdsAndIsProductionAndMaterial { List<Long> projectIds, boolean isProduction, String material ->
            List<BudgetDetailsItemModel> lstBudgetItemModel = BudgetDetailsItemModel.findAllByItemNameIlike(Tools.PERCENTAGE + material + Tools.PERCENTAGE,[readOnly:true])
            List<Long> lstBudget = lstBudgetItemModel.collect { it.budgetId }
            if (lstBudget.size() == 0) {
                lstBudget << 0L
            }
            searchByProjectIdsAndBudgetIdsAndIsProduction(projectIds, lstBudget, isProduction)
        }
    }
}
