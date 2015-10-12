package com.athena.mis.budget.model
// BudgetDetailsItemModel is the model for database view vw_budg_budget_details_with_item,
// which is primarily used to list BudgBudgetDetails

class BudgetDetailsItemModel implements Serializable {

    public static final String SQL_BUDG_BUDGET_DETAILS_ITEM_MODEL = """
            DROP TABLE IF EXISTS vw_budg_budget_details_with_item;
            DROP VIEW IF EXISTS vw_budg_budget_details_with_item;
            CREATE OR REPLACE view vw_budg_budget_details_with_item AS
            SELECT budget_details.id AS budget_details_id,budget_details.budget_id,
            item_type.name AS item_type_name,
            item.name AS item_name,item.code AS item_code, quantity,
            to_char(quantity, 'FM99,99,999,99,99,990.0099') ||' '|| item.unit as str_quantity,
            rate, to_char(rate, 'FM৳ 99,99,999,99,99,990.0099') AS str_rate,
            (rate*quantity) AS total_cost, to_char((rate*quantity), 'FM৳ 99,99,999,99,99,990.0099') AS str_total_cost
            FROM budg_budget_details budget_details
            LEFT JOIN item ON item.id= budget_details.item_id
            LEFT JOIN item_type ON item_type.id = item.item_type_id
            ORDER BY item.name, item_type.name
   """

    long budgetDetailsId                //BudgetDetail.id
    long budgetId                       //BudgBudget.id
    String itemTypeName                 //SystemEntity.value
    String itemName                     //Item.name
    String itemCode                     //Item.name
    double quantity                     //BudgetDetail.quantity
    String strQuantity                  //BudgetDetail.quantity + unit
    double rate                       //BudgetDetail.rate
    String strRate                    //Formatted BudgetDetail.rate
    double totalCost                  //BudgetDetail.rate * rate
    String strTotalCost               // Formatted BudgetDetail.rate * rate

    static mapping = {
        table 'vw_budg_budget_details_with_item'  //database view
        version false
        id composite: ['budgetDetailsId', 'quantity']
        cache usage: 'read-only'
    }
}
