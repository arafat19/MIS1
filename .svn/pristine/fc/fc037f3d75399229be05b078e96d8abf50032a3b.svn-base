package com.athena.mis.integration.procurement

import com.athena.mis.PluginConnector
import groovy.sql.GroovyRowResult

public abstract class ProcurementPluginConnector extends PluginConnector {

/*    // Return the PR Object by id
    Object readPR(long id)*/

    // Return the PO Object by id
    public abstract Object readPO(long id)

    // Return the PO Details Object by id
    public abstract Object readPODetails(long id)

    // Return the Indent Object by id
    public abstract Object readIndent(long id)

    // Return the IndentDetails Object by id
    public abstract Object readIndentDetails(long id)

    //return List of GroovyRowResult of PO by supplier and projects  (for Create in StoreIn from supplier)
    public abstract List<Object> listPOBySupplierIdAndProjectId(long supplierId, long projectId)

    //return List of GroovyRowResult of PO by supplier and projects   (for Edit in StoreIn from supplier)
    public abstract List<Object> listPOBySupplierIdAndProjectIdForEdit(long supplierId, long purchaseOrderId, long projectId)

    public abstract List<GroovyRowResult> listPOItemByPurchaseOrder(long purchaseOrderId)

    //update storeInQuantity in po details For Create StoreInDetails
    public abstract Object updateStoreInQuantityForPODetails(Object purchaseOrderDetailsInstance)

    //read PurchaseOrderDetails GroovyRowResult object by purchaseOrderId and materialId
    public abstract Object readPODetailsByPurchaseOrderAndItem(long purchaseOrderId, long itemId)

    //get po list of fixed asset
    public abstract List<GroovyRowResult> getPOListOfFixedAsset()

    //get fixed asset list of PO
    public abstract List<GroovyRowResult> getFixedAssetListByPOId(long poId)

    //Get List of Indent by projectId, IndentId
    public abstract List<GroovyRowResult> listIndentByProjectIdIntendId(long projectId, long indentId)

    //get Indent-List-By-ProjectId
    public abstract List<GroovyRowResult> getIntendListByProjectId(long projectId)

    public abstract void bootStrap(boolean isSchema, boolean isData)
}