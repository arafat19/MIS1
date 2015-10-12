package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCustomerBeneficiaryMapping

class ExhCustomerBeneficiaryMappingService extends BaseService{

    /**
     * Save customerBeneficiaryMapping object into DB
     * @param customerBeneficiaryMapping -ExhCustomerBeneficiaryMapping object
     * @return -saved customerBeneficiaryMapping object
     */
    public ExhCustomerBeneficiaryMapping create(ExhCustomerBeneficiaryMapping customerBeneficiaryMapping) {
        return customerBeneficiaryMapping.save()
    }

}
