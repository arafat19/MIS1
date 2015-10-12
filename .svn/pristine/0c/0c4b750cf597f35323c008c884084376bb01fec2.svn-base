package com.athena.mis.integration.procurement

import com.athena.mis.integration.procurement.actions.*
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.*
import groovy.sql.GroovyRowResult

class ProcurementImplService extends ProcurementPluginConnector {

    static transactional = false
    static lazyInit = false
    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(PROCUREMENT, this);
        return true
    }

    @Override
    public String getName() {
        return PROCUREMENT;
    }

    @Override
    public int getId() {
        return PROCUREMENT_ID;
    }


    PurchaseRequestService purchaseRequestService
    PurchaseOrderService purchaseOrderService
    PurchaseOrderDetailsService purchaseOrderDetailsService
    IndentService indentService
    IndentDetailsService indentDetailsService
    ReadBySupplierIdAndProjectIdsImplActionService readBySupplierIdAndProjectIdsImplActionService
    ReadBySupplierIdAndProjectIdsForEditImplActionService readBySupplierIdAndProjectIdsForEditImplActionService
    ListItemByPurchaseOrderImplActionService listItemByPurchaseOrderImplActionService
    UpdateStoreInQuantityPODImplActionService updateStoreInQuantityPODImplActionService
    ReadByPurchaseOrderAndItemImplActionService readByPurchaseOrderAndItemImplActionService
    GetPOListOfFixedAssetImplActionService getPOListOfFixedAssetImplActionService
    GetFixedAssetListByPOIdImplActionService getFixedAssetListByPOIdImplActionService
    ListIndentByProjectIdIntendIdImplActionService listIndentByProjectIdIntendIdImplActionService
    GetIntendListByProjectIdImplActionService getIntendListByProjectIdImplActionService

    ProcDefaultDataBootStrapService procDefaultDataBootStrapService
    ProcSchemaUpdateBootStrapService procSchemaUpdateBootStrapService
    ProcurementBootStrapService procurementBootStrapService

    // Return the PR Object by id
    Object readPR(long id) {
        ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(id)
        return purchaseRequest
    }

    // Return the PO Object by id
    @Override
    Object readPO(long id) {
        ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(id)
        return purchaseOrder
    }

    // Return the ProcIndent Object by id
    @Override
    Object readIndent(long id) {
        ProcIndent indent = indentService.read(id)
        return indent
    }

    // Return the ProcIndentDetails Object by id
    @Override
    Object readIndentDetails(long id) {
        return indentDetailsService.read(id)
    }

    //return List of GroovyRowResult of PO by supplier and project
    @Override
    List<Object> listPOBySupplierIdAndProjectId(long supplierId, long projectId) {
        return (List<Object>) readBySupplierIdAndProjectIdsImplActionService.execute(supplierId, projectId)
    }

    //return List of GroovyRowResult of PO by supplier and project (for Edit in StoreIn from supplier)
    @Override
    List<Object> listPOBySupplierIdAndProjectIdForEdit(long supplierId, long purchaseOrderId, long projectId) {
        LinkedHashMap parameterMap = [supplierId: supplierId, purchaseOrderId: purchaseOrderId, projectId: projectId]

        return (List<Object>) readBySupplierIdAndProjectIdsForEditImplActionService.execute(parameterMap, null)
    }

    //return List of GroovyRowResult of PO Material by purchaseOrderId for Show StoreInDetails
    @Override
    List<GroovyRowResult> listPOItemByPurchaseOrder(long purchaseOrderId) {
        return (List<GroovyRowResult>) listItemByPurchaseOrderImplActionService.execute(purchaseOrderId, null)
    }

    //update storeInQuantity in po details For Create StoreInDetails
    @Override
    Object updateStoreInQuantityForPODetails(Object purchaseOrderDetailsInstance) {
        return updateStoreInQuantityPODImplActionService.execute(null, purchaseOrderDetailsInstance)
    }

    // Return the PO Details Object by id
    @Override
    Object readPODetails(long id) {
        return purchaseOrderDetailsService.read(id)
    }

    //read ProcPurchaseOrderDetails GroovyRowResult object by purchaseOrderId and materialId
    @Override
    Object readPODetailsByPurchaseOrderAndItem(long purchaseOrderId, long itemId) {
        return readByPurchaseOrderAndItemImplActionService.execute(purchaseOrderId, itemId)
    }

    //get po list of fixed asset
    List<GroovyRowResult> getPOListOfFixedAsset() {
        return (List<GroovyRowResult>) getPOListOfFixedAssetImplActionService.execute(null, null)
    }

    //get fixed asset list of PO
    List<GroovyRowResult> getFixedAssetListByPOId(long poId) {
        return (List<GroovyRowResult>) getFixedAssetListByPOIdImplActionService.execute(poId, null)
    }

    //Get List of ProcIndent by projectId, IndentId
    List<GroovyRowResult> listIndentByProjectIdIntendId(long projectId, long indentId) {
        return (List<GroovyRowResult>) listIndentByProjectIdIntendIdImplActionService.execute(projectId, indentId)
    }

    //get ProcIndent-List-By-ProjectId
    List<GroovyRowResult> getIntendListByProjectId(long projectId) {
        return (List<GroovyRowResult>) getIntendListByProjectIdImplActionService.execute(projectId, null)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) procDefaultDataBootStrapService.init()
        if (isSchema) procSchemaUpdateBootStrapService.init()
        procurementBootStrapService.init()
    }
}
