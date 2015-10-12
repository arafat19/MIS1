package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.model.ProcPOForStoreInModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get ProcPOForStoreInModel object(s) list filtered by supplierId, projectId
 *  For details go through Use-Case doc named 'ReadBySupplierIdAndProjectIdsImplActionService'
 */
class ReadBySupplierIdAndProjectIdsImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * get PO list using ProcPOForStoreInModel filtered by supplierId, projectId
     * @param params1 - Supplier.id
     * @param params2 -Project.id
     * @return -List of ProcPOForStoreInModel object(s)
     */
    @Transactional(readOnly = true)
    public Object execute(Object params1, Object params2) {
        try {
            long supplierId = Long.parseLong(params1.toString())
            long projectId = Long.parseLong(params2.toString())
            return ProcPOForStoreInModel.listBySupplierAndProject(supplierId, projectId).list()
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
