package com.athena.mis.sarb.service

import com.athena.mis.BaseService
import com.athena.mis.sarb.model.SarbCustomerModel

class SarbCustomerModelService extends BaseService {

    public SarbCustomerModel read(long id) {
        SarbCustomerModel customerModel = SarbCustomerModel.read(id)
        return customerModel
    }


}
