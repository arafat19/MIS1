package com.athena.mis.procurement.model

class ProcPOStatusModel implements Serializable {

    public static final String SQL_PROC_PO_STATUS_MODEL = """
            DROP TABLE IF EXISTS vw_proc_po_status;
            DROP VIEW IF EXISTS vw_proc_po_status;
            CREATE OR REPLACE VIEW vw_proc_po_status AS
            SELECT project.id AS project_id, project.code AS project_code,
            TO_CHAR(COALESCE(
            (SELECT SUM(budget_details.quantity*budget_details.rate)
		    FROM budg_budget_details budget_details WHERE budget_details.project_id = project.id),0),'FM৳ 99,99,999,99,99,990.0099') AS total_budget,
            COUNT(po.id) AS po_count,
            TO_CHAR(COALESCE(SUM(po.total_price),0),'FM৳ 99,99,999,99,99,990.0099') AS total_po
            FROM project
            INNER JOIN proc_purchase_order po ON po.project_id = project.id
            GROUP BY project.id, project.code
            ORDER BY project.code;
    """

    long projectId                     //project.id
    String projectCode                 //project.code
    String totalBudget                 //SUM(budget_details.quantity*budget_details.rate)
    int poCount                        //po count
    String totalPo                     //SUM(pod.quantity*pod.rate)


    static mapping = {
        table 'vw_proc_po_status'  //database view
        version false
        id composite: ['projectId', 'poCount']
        cache usage: 'read-only'
    }

    static namedQueries = {
        listByProjectIds { List<Long> projectIds ->
            'in'('projectId', projectIds)
        }
    }
}
