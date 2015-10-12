package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhCustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhUserCustomerCacheUtility')
class ExhUserCustomerCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    ExhCustomerService exhCustomerService

    public final String SORT_ON_ID = "id"

    public void init() {
        List list = appUserEntityService.listByType(AppUserEntityTypeCacheUtility.CUSTOMER)
        super.setList(list)
    }

    public AppUserEntity readByCustomerId(long customerId) {
        List<AppUserEntity> lst = (List<AppUserEntity>) list()
        for (int i = 0; i < lst.size(); i++) {
            if (lst[i].entityId == customerId) {
                return lst[i]
            }
        }
        return null
    }

    public List<ExhCustomer> listUserCustomers(long userId) {
        List<AppUserEntity> lstUserCustomer = (List<AppUserEntity>) super.list()
        if(!lstUserCustomer) return []
        List<ExhCustomer> lstCustomers = []
        for (int i = 0; i < lstUserCustomer.size(); i++) {
            if (lstUserCustomer[i].appUserId == userId) {
                lstCustomers << exhCustomerService.read(lstUserCustomer[i].entityId)
            }
        }
        return lstCustomers
    }
}
