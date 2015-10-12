package com.athena.mis.fixedasset.model

import com.athena.mis.utility.Tools

class FxdMaintenanceModel implements Serializable {

    public static final String SQL_FXD_MAINTENANCE_MODEL = """
            DROP TABLE IF EXISTS vw_fxd_maintenance;
            DROP VIEW IF EXISTS vw_fxd_maintenance;
            CREATE OR REPLACE view vw_fxd_maintenance AS
            SELECT fm.id AS maintenance_id, item.id AS item_id, item.name AS item_name, fm.description,
            fad.id AS fixed_asset_details_id, fad.name AS model_name,
            fm.maintenance_type_id, fmt.name AS maintenance_type_name,
            fm.amount, to_char(fm.amount, 'FM৳ 99,99,999,99,99,990.0099') AS str_amount,
            to_char(fm.maintenance_date, 'dd-Mon-YYYY') AS str_maintenance_date,
            created_by.username AS created_by_user_name,
            fm.company_id
            FROM fxd_maintenance fm
            LEFT JOIN item ON item.id = fm.item_id
            LEFT JOIN fxd_fixed_asset_details fad ON fad.id = fm.fixed_asset_details_id
            LEFT JOIN fxd_maintenance_type fmt ON fmt.id = fm.maintenance_type_id
            LEFT JOIN app_user created_by ON created_by.id = fm.created_by
   """


    long maintenanceId                  //FxdMaintenance.id
    long itemId                         //FxdMaintenance.id
    String itemName                     //Item.name
    long fixedAssetDetailsId            //FxdMaintenance.fixedAssetDetailsId
    String modelName                    //FixedAssetDetails.name
    long maintenanceTypeId              //FxdMaintenance.maintenanceTypeId
    String maintenanceTypeName          //FxdMaintenanceType.name
    double amount                       //FxdMaintenance.amount
    String strAmount                    //FxdMaintenance.amount
    String strMaintenanceDate           //FxdMaintenance.maintenanceDate
    String createdByUserName            //FxdMaintenance.createdBy
    long companyId                      //FxdMaintenance.companyId
    String description                  //FxdMaintenance.description


    static mapping = {
        table 'vw_fxd_maintenance'  //database view
        version false
        id composite: ['maintenanceId', 'itemId']
        cache usage: 'read-only'
    }

    static namedQueries = {
        search { String queryType, String query, long companyId ->
            eq('companyId', companyId)
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            setReadOnly(true)
        }

        searchWithFixedAssetDetailsId {long fixedAssetDetailsId, String queryType, String query, long companyId ->
            eq('fixedAssetDetailsId',fixedAssetDetailsId)
            eq('companyId', companyId)
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            setReadOnly(true)
        }
    }
}
