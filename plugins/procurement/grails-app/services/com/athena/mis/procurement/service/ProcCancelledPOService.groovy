package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcCancelledPO
import com.athena.mis.procurement.utility.ProcSessionUtil
import org.springframework.beans.factory.annotation.Autowired

class ProcCancelledPOService extends BaseService {
    @Autowired
    ProcSessionUtil procSessionUtil

    public ProcCancelledPO create(ProcCancelledPO cancelledPO) {
        ProcCancelledPO newCancelledPO = cancelledPO.save()
        return newCancelledPO
    }

    /**
     * Method to read ProcCancelledPO object by id
     * @param id - ProcCancelledPO.id
     * @return - object of ProcCancelledPO
     */
    public ProcCancelledPO read(long id) {
        return (ProcCancelledPO) ProcCancelledPO.read(id);
    }

    /**
     * Get approved & cancelled purchase order object by id
     * @param id - ProcCancelledPO.id
     * @return - object of ProcCancelledPO
     */
    public ProcCancelledPO readCancelledApprovedPurchaseOrder(long id) {
        ProcCancelledPO purchaseOrder = ProcCancelledPO.findByIdAndApprovedByDirectorIdNotEqualAndApprovedByProjectDirectorIdNotEqualAndCompanyId(id, 0L, 0L, procSessionUtil.appSessionUtil.getCompanyId(), [readOnly: true])
        return purchaseOrder
    }
}
