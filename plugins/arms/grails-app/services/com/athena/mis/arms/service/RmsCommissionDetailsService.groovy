package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsCommissionDetails

class RmsCommissionDetailsService extends BaseService{

    public RmsCommissionDetails create(RmsCommissionDetails rmsCommissionDetails) {
        RmsCommissionDetails savedRmsCommissionDetails = rmsCommissionDetails.save()
        return savedRmsCommissionDetails
    }

}
