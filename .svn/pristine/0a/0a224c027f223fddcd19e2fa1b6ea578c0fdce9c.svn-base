package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.service.SmsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('smsCacheUtility')
class SmsCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SmsService smsService

    public final String SORT_ON_ID = "id";

    public void init() {
        List list = smsService.list();
        super.setList(list)
    }

/**
 * Return a new list from cache based on given parameters
 * @param actionName -transaction code (name of action service e.g. SendSmsActionService)
 * @param companyId -id of company
 * @param isActive -boolean value (true/false)
 * @return -list of matching sms objects
 */
    public List<Sms> listByTransactionCodeAndCompanyIdAndIsActive(String actionName, long companyId, boolean isActive) {
        List<Sms> lstMain = (List<Sms>) list()
        List<Sms> lstSms = lstMain.findAll {
            it.transactionCode.equals(actionName) && it.companyId == companyId && it.isActive == isActive
        }
        return lstSms
    }
}
