package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.utility.DateUtility

class AccVoucherDetailsService extends BaseService {

    static transactional = false

    public AccVoucherDetails create(AccVoucherDetails accVoucherDetails) {
        accVoucherDetails.save(false)
        return accVoucherDetails
    }
}
