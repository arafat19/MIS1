package com.athena.mis.budget.model

class BudgetStatusModel implements Serializable {

    public static final String SQL_BUDG_BUDGET_STATUS_MODEL = """
            DROP TABLE IF EXISTS vw_budg_budget_status;
            DROP VIEW IF EXISTS vw_budg_budget_status;
            CREATE OR REPLACE view vw_budg_budget_status AS
            SELECT budget.project_id AS project_id, project.code AS project_code, COUNT(DISTINCT budget.id) AS budget_count,
            TO_CHAR(SUM(budget.contract_rate*budget.budget_quantity),'FM৳ 99,99,999,99,99,990.0099') AS contract_value,
            TO_CHAR(COALESCE(details.total_cost,0),'FM৳ 99,99,999,99,99,990.0099') AS total_budget,
            TO_CHAR(COALESCE((SUM(budget.contract_rate*budget.budget_quantity) - details.total_cost),0),'FM৳ 99,99,999,99,99,990.0099') AS revenue_margin
            FROM budg_budget AS budget
            full outer join
            (
                SELECT SUM(budget_details.quantity*budget_details.rate) AS total_cost, project_id
                FROM budg_budget_details AS budget_details
                LEFT JOIN item ON item.id = budget_details.item_id
                WHERE item.is_finished_product = 'false'
                GROUP BY project_id
            ) AS details
            ON details.project_id = budget.project_id
            LEFT JOIN project ON project.id = budget.project_id
            GROUP BY budget.project_id, details.total_cost, project.code
            ORDER BY project.code;
    """

    long projectId                      // project.id
    String projectCode                  // project.code
    int budgetCount                     // budget count
    String totalBudget                  // SUM(budget_details.quantity*budget_details.estimated_cost)
    String contractValue                // SUM(budget.budget_quantity * budget.contract_rate)
    String revenueMargin                // contract_value - total_budget

    static mapping = {
        table 'vw_budg_budget_status'  //database view
        version false
        id composite: ['projectId', 'budgetCount']
        cache usage: 'read-only'
    }
}
