package com.athena.mis.procurement.model
// PurchaseRequestDetailsModel is the model for database view vw_proc_purchase_request_details,
// which is primarily used to list ProcPurchaseRequestDetails

class ProcPRDetailsModel implements Serializable {

    public static final String SQL_PROC_PR_DETAILS_MODEL = """
            DROP TABLE IF EXISTS vw_proc_purchase_request_details;
            DROP VIEW IF EXISTS vw_proc_purchase_request_details;
            CREATE OR REPLACE view vw_proc_purchase_request_details AS
            SELECT purchase_request_details.id AS purchase_request_details_id, purchase_request_id,
            item.name item_name, item.code item_code, quantity||' '||item.unit as quantity_with_unit,
            to_char(rate,'FM৳ 99,99,999,99,99,990.0099') AS str_rate,
            (to_char(rate*quantity,'FM৳ 99,99,999,99,99,990.0099')) AS str_total_cost,
            rate,(rate*quantity) AS total_cost,
            system_entity.key item_type
            FROM proc_purchase_request_details purchase_request_details
            LEFT JOIN item ON item.id= purchase_request_details.item_id
            LEFT JOIN system_entity system_entity ON system_entity.id= item.item_type_id
            ORDER BY system_entity.id, item.name
   """

    long purchaseRequestDetailsId   //ProcPurchaseRequestDetails.id
    long purchaseRequestId          //ProcPurchaseRequestDetails.PurchaseRequestId
    String itemName                 //Item.name
    String itemCode                 //Item.code
    String quantityWithUnit         //ProcPurchaseRequestDetails.quantity+unit
    String strRate                 //ProcPurchaseRequestDetails.rate (formatted)
    String strTotalCost             //ProcPurchaseRequestDetails.rate*quantity (formatted)
    double rate                    //ProcPurchaseRequestDetails.rate
    double totalCost                //ProcPurchaseRequestDetails.rate*quantity
    String itemType                 //SystemEntity.key



    static mapping = {
        table 'vw_proc_purchase_request_details'  //database view
        version false
        id composite: ['purchaseRequestDetailsId', 'purchaseRequestId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listItemByPurchaseRequest { long purchaseRequestId ->
            eq('purchaseRequestId',purchaseRequestId)

        }
    }
    
}
