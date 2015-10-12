package com.athena.mis.qs.model

import com.athena.mis.utility.Tools

// QsMeasurementModel is the model for database view vw_qs_measurement_with_budget_inventory,
// which is primarily used to list QSMeasurement

class QsMeasurementModel implements Serializable {

    public static final String SQL_QS_MEASUREMENT_MODEL = """
            DROP TABLE IF EXISTS vw_qs_measurement_with_budget_inventory;
            DROP VIEW IF EXISTS vw_qs_measurement_with_budget_inventory;
            CREATE OR REPLACE view vw_qs_measurement_with_budget_inventory AS
            SELECT qsm.id AS qsm_id, qsm.project_id AS project_id,
            to_char(qsm.quantity,'FM99,99,999,99,99,990.0099') ||' '||unit.key AS qsm_quantity,
            to_char(qsm.created_on,'dd-Mon-yyyy') str_qsm_created_on,
            budget.budget_item, inv.name AS site_name,
            to_char(qsm.qs_measurement_date,'dd-Mon-yyyy') str_qsm_date,
            qsm.is_govt_qs,qsm.budget_id
            FROM qs_measurement qsm
            LEFT JOIN budg_budget budget ON budget.id = qsm.budget_id
            LEFT JOIN system_entity unit ON unit.id = budget.unit_id
            LEFT JOIN inv_inventory inv ON inv.id=qsm.site_id
   """

    long qsmId
    long projectId
    String qsmQuantity
    String strQsmCreatedOn
    String budgetItem
    String siteName
    String strQsmDate
    boolean isGovtQs
    long budgetId


    static mapping = {
        table 'vw_qs_measurement_with_budget_inventory'  //database view
        version false
        id composite: ['qsmId', 'budgetItem']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listByProjectAndIsGovt { long projectId, boolean isGovt ->
            eq('projectId', new Long(projectId))
            eq('isGovtQs', new Boolean(isGovt))
        }

        listByProjectIdsAndIsGovt { List<Long> lstProjectIds, boolean isGovt ->
            'in'('projectId', lstProjectIds)
            eq('isGovtQs', new Boolean(isGovt))
        }

        searchByProjectIdsAndIsGovtAndQueryIlike { List<Long> lstProjectIds, boolean isGovt, String queryType, String query ->
            'in'('projectId', lstProjectIds)
            eq('isGovtQs', new Boolean(isGovt))
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
        }
    }


}
