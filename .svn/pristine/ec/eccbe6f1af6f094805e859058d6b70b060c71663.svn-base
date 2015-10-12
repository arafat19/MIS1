package com.athena.mis.fixedasset.model

// FixedAssetDetailsModel is the model for database view vw_fixed_asset_details,
// which is primarily used to list FxdFixedAssetDetails

class FixedAssetDetailsModel implements Serializable{

    public static final String SQL_FIXED_ASSET_DETAILS_MODEL = """
            DROP TABLE IF EXISTS vw_fixed_asset_details;
            DROP VIEW IF EXISTS vw_fixed_asset_details;
            CREATE OR REPLACE view vw_fixed_asset_details AS
            SELECT fad.id AS fixed_asset_details_id,
            to_char(fad.purchase_date, 'dd-Mon-yyyy') AS str_purchase_date,
            fad.name AS fixed_asset_details_name, fad.po_id AS po_id,
            inventory.name AS inventory_name, fad.item_id AS item_id,
            item.name AS item_name, fad.cost AS fixed_asset_details_cost,
            se.key AS inventory_type
            FROM fxd_fixed_asset_details fad
            LEFT JOIN item ON item.id = fad.item_id
            LEFT JOIN inv_inventory inventory ON inventory.id = fad.current_inventory_id
            LEFT JOIN system_entity se on se.id=inventory.type_id
   """

    long fixedAssetDetailsId
    String strPurchaseDate
    String fixedAssetDetailsName
    long poId
    String inventoryName
    long itemId
    String itemName
    double fixedAssetDetailsCost
    String inventoryType

    static mapping = {
        table 'vw_fixed_asset_details'  //database view
        version false
        id composite: ['fixedAssetDetailsId', 'poId']
        cache usage: 'read-only'
    }
}
