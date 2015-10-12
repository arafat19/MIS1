package com.athena.mis.fixedasset.model

// FixedAssetTraceModel is the model for database view vw_fixed_asset_trace,
// which is primarily used to list FxdFixedAssetTrace

class FixedAssetTraceModel implements  Serializable{

    public static final String SQL_FIXED_ASSET_TRACE_MODEL = """
            DROP TABLE IF EXISTS vw_fixed_asset_trace;
            DROP VIEW IF EXISTS vw_fixed_asset_trace;
            CREATE OR REPLACE view vw_fixed_asset_trace AS
            SELECT fat.id AS fixed_asset_trace_id, item.name AS item_name,
            fad.name AS model_name, fad.id fixed_asset_details_id,
            to_char(fat.transaction_date, 'dd-Mon-YYYY') AS str_transaction_date,
            inventory.name AS inventory_name, fat.is_current AS is_current
            FROM fxd_fixed_asset_trace fat
            LEFT JOIN item ON item.id = fat.item_id
            LEFT JOIN fxd_fixed_asset_details fad ON fad.id = fat.fixed_asset_details_id
            LEFT JOIN inv_inventory inventory ON inventory.id = fat.inventory_id
            ORDER BY fat.id desc
   """


    long fixedAssetTraceId                   //FxdFixedAssetTrace.id
    long fixedAssetDetailsId                 //FxdFixedAssetDetails.id
    String itemName                          //Item.name
    String modelName                         //FxdFixedAssetDetails.name
    String strTransactionDate                //FxdFixedAssetTrace.transactionDate
    String inventoryName                     //Inventory.name
    boolean isCurrent                        //FxdFixedAssetTrace.isCurrent



    static mapping = {
        table 'vw_fixed_asset_trace'  //database view
        version false
        id composite: ['fixedAssetTraceId', 'fixedAssetDetailsId']
        cache usage: 'read-only'
    }
}
