package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.customerbeneficiary.ExhCreateCustomerBeneficiaryMappingActionService
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import grails.converters.JSON


class ExhCustomerBeneficiaryController {

    static allowedMethods = [create: "POST"]

    ExhCreateCustomerBeneficiaryMappingActionService exhCreateCustomerBeneficiaryMappingActionService

    def create() {
        Map result
        Map preResult = (Map) exhCreateCustomerBeneficiaryMappingActionService.executePreCondition(params, null)
        boolean isError = ((Boolean) preResult.isError).booleanValue()
        if (isError) {
            result = (Map) exhCreateCustomerBeneficiaryMappingActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return;
        }

        ExhBeneficiary beneficiaryInstance = (ExhBeneficiary) exhCreateCustomerBeneficiaryMappingActionService.execute(params, preResult);
        if (beneficiaryInstance) {
            result = (Map) exhCreateCustomerBeneficiaryMappingActionService.buildSuccessResultForUI(beneficiaryInstance);
        } else {
            result = (Map) exhCreateCustomerBeneficiaryMappingActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }
}