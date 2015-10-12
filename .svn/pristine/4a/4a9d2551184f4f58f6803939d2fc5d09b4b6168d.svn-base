package com.athena.mis.procurement.model

// ProcPOForStoreInModel is the model for database view of vw_proc_po_for_store_in 

class ProcPOForStoreInModel implements Serializable {

    public static final String SQL_PROC_PR_DETAILS_MODEL = """
            DROP TABLE IF EXISTS vw_proc_po_for_store_in;
            DROP VIEW IF EXISTS vw_proc_po_for_store_in;
            CREATE OR REPLACE view vw_proc_po_for_store_in AS
            SELECT  DISTINCT po.id po_id, 'PO No '|| po.id as str_po_id,
                    po.supplier_id AS supplier_id, po.project_id AS project_id
            FROM proc_purchase_order po
            INNER JOIN proc_purchase_order_details pod ON pod.purchase_order_id = po.id
            INNER JOIN item ON item.id = pod.item_id
            INNER JOIN system_entity se ON se.id = item.category_id
            WHERE (pod.quantity - pod.store_in_quantity) > 0
            AND po.approved_by_director_id > 0
            AND po.approved_by_project_director_id > 0
            AND se.reserved_id IN(150,152)
            AND item.is_individual_entity = FALSE
            ORDER BY po.id desc;
   """

    long poId
    String strPoId
    long supplierId
    long projectId


    static mapping = {
        table 'vw_proc_po_for_store_in'  //database view
        version false
        id composite: ['poId', 'supplierId']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listBySupplierAndProject { long supplierId, long projectId ->
            eq('supplierId', supplierId)
            eq('projectId', projectId)
        }

        listBySupplierAndProjectForEdit { long supplierId, long projectId, long poId ->
            eq('supplierId', supplierId)
            eq('projectId', projectId)
            ne('poId', poId)
        }

    }

}
