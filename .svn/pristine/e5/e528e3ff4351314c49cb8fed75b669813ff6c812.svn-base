package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.model.ProcPOForStoreInModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get ProcPOForStoreInModel object(s) list filtered by supplierId, projectId and PO-id
 *  For details go through Use-Case doc named 'ReadBySupplierIdAndProjectIdsForEditImplActionService'
 */
class ReadBySupplierIdAndProjectIdsForEditImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * get PO list using ProcPOForStoreInModel filtered by supplierId, projectId and PO-id
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -list of ProcPOForStoreInModel object
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            LinkedHashMap parameterMap = (LinkedHashMap) params
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            return ProcPOForStoreInModel.listBySupplierAndProjectForEdit(supplierId, projectId, purchaseOrderId).list()
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return false
    }

    /**
     * Do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}