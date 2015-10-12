package com.athena.mis.procurement.model
// ProcSupplierWisePOModel is the model for database view vw_proc_supplier_wise_po

class ProcSupplierWisePOModel implements Serializable {

    public static final String SQL_PROC_PR_MODEL = """
            DROP TABLE IF EXISTS vw_proc_supplier_wise_po;
            DROP VIEW IF EXISTS vw_proc_supplier_wise_po;
            CREATE OR REPLACE view vw_proc_supplier_wise_po AS
            SELECT po.id AS po_id, po.supplier_id AS supplier_id, po.project_id AS project_id,
            item.id AS item_id, item.name AS item_name, item.unit AS item_unit,

            pod.rate,
            to_char(pod.rate,'FM৳ 99,99,999,99,99,990.0099') AS str_rate,

            item.item_type_id, pod.created_on AS created_on,

            pod.quantity,
            to_char(pod.quantity,'FM99,99,999,99,99,990.0099') AS str_quantity,

            (pod.quantity*pod.rate) AS po_amount,
            to_char(pod.quantity*pod.rate,'FM৳ 99,99,999,99,99,990.0099') AS str_po_amount,

            COALESCE(SUM(viitd.actual_quantity),0) AS store_in_quantity,
            to_char(COALESCE(SUM(viitd.actual_quantity),0),'FM99,99,999,99,99,990.0099') AS str_store_in_quantity,

            COALESCE(SUM(viitd.actual_quantity*viitd.rate),0) AS store_in_amount,
            to_char(COALESCE(SUM(viitd.actual_quantity*viitd.rate),0),'FM99,99,999,99,99,990.0099') AS str_store_in_amount,

            COALESCE(pod.fixed_asset_details_count*pod.rate,0) AS fixed_asset_amount,
            to_char(COALESCE(pod.fixed_asset_details_count*pod.rate,0),'FM৳ 99,99,999,99,99,990.0099') AS str_fixed_asset_amount,

            pod.fixed_asset_details_count AS fixed_asset_quantity,
            to_char(pod.fixed_asset_details_count,'FM99,99,999,99,99,990.0099') AS str_fixed_asset_quantity

            FROM proc_purchase_order_details pod
            LEFT JOIN proc_purchase_order po ON pod.purchase_order_id = po.id
            LEFT JOIN item ON item.id = pod.item_id
            LEFT JOIN vw_inv_inventory_transaction_with_details viitd
                                ON viitd.transaction_details_id = pod.id
                            AND viitd.transaction_type_id = 301
                            AND viitd.transaction_entity_type_id = 353
                            AND viitd.is_current = true
                            AND viitd.approved_by > 0
	GROUP BY po.id, po.supplier_id, po.project_id, item.id, item.name, item.unit, pod.rate, item.item_type_id,
                     pod.created_on, pod.quantity, pod.fixed_asset_details_count;
   """

    long poId                           //PO.id
    long supplierId                     //PO.supplierId
    long projectId                      //PO.projectId
    long itemId                         //Item.id
    String itemName                     //Item.name
    String itemUnit                     //Item.unit
    double rate                         // pod.rate
    String strRate                      //formatted pod.rate
    long itemTypeId                    //Item.itemTypeId
    Date createdOn                      //PODetails.createdOn
    double quantity                     //PODetails.quantity
    String strQuantity                  // formatted quantity
    double poAmount                     //PODetails.quantity * rate
    String strPoAmount                  // formatted PO Amount
    double storeInQuantity              //PODetails.storeInQuantity
    String strStoreInQuantity           //formatted quantity
    double storeInAmount                //pod.store_in_quantity*pod.rate
    String strStoreInAmount             // formatted pod.store_in_quantity*pod.rate
    double fixedAssetAmount             //SUM(COALESCE(fad.cost,0))
    String strFixedAssetAmount          //formatted SUM(COALESCE(fad.cost,0))
    int fixedAssetQuantity             // COUNT(COALESCE(fad.item_id,0))
    String strFixedAssetQuantity       // formatted COUNT(COALESCE(fad.item_id,0))

    static mapping = {
        table 'vw_proc_supplier_wise_po'  //database view
        version false
        id composite: ['poId', 'itemId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listSupplierWisePO { long supplierId, List<Long> projectIds, List<Long> itemTypeIds, Date fromDate, Date toDate ->
            eq('supplierId', new Long(supplierId))
            'in'('projectId', projectIds)
            'in'('itemTypeId', itemTypeIds)
            between('createdOn', fromDate, toDate)
        }

        searchSupplierWisePO { long supplierId, long poId, List<Long> projectIds, List<Long> itemTypeIds, Date fromDate, Date toDate ->
            eq('supplierId', new Long(supplierId))
            eq('poId', new Long(poId))
            'in'('projectId', projectIds)
            'in'('itemTypeId', itemTypeIds)
            between('createdOn', fromDate, toDate)
        }
    }

}
