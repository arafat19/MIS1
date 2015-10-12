package com.athena.mis.budget.model

class BudgetProjectItemModel implements Serializable {

    public static final String SQL_BUDGET_PROJECT_ITEM_MODEL = """
        DROP TABLE IF EXISTS vw_budget_project_item;
        DROP VIEW IF EXISTS vw_budget_project_item;
        CREATE OR REPLACE view vw_budget_project_item AS
        SELECT project.id AS project_id, project.code AS project_code, project.name AS project_name,
        item.id AS item_id, item.item_type_id AS item_type_id, item.name AS item_name, item.unit AS item_unit,

        SUM(quantity) AS total_budget_quantity,

        COALESCE(
        (SELECT SUM(pr_details.quantity)
        FROM proc_purchase_request_details pr_details
        WHERE pr_details.project_id = project.id
        AND pr_details.item_id = item.id),0) AS total_pr_quantity,

        SUM(quantity) -
        COALESCE(
        (SELECT SUM(pr_details.quantity)
        FROM proc_purchase_request_details pr_details
        WHERE pr_details.project_id = project.id
        AND pr_details.item_id = item.id),0) AS remaining_quantity

        FROM budg_budget_details budget_details
        LEFT JOIN project ON project.id = budget_details.project_id
        LEFT JOIN item ON item.id = budget_details.item_id
        GROUP BY project.id, project.code, project.name, item.id, item.item_type_id, item.name, item.unit
        ORDER BY project.code;
    """

    long projectId
    String projectCode
    String projectName
    long itemId
    long itemTypeId
    String itemName
    String itemUnit
    double totalBudgetQuantity
    double totalPrQuantity
    double remainingQuantity

    static mapping = {
        table 'vw_budget_project_item'  //database view
        version false
        id composite: ['projectId', 'itemId']
        cache usage: 'read-only'
    }
}
