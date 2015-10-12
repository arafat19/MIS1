package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accTypeCacheUtility')
class AccTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccTypeService accTypeService

    public final String SORT_ON_NAME = "name"

    public static final String ASSET = "ASSET"
    public static final String LIABILITIES = "LIABILITIES"
    public static final String INCOME = "INCOME"
    public static final String EXPENSE = "EXPENSE"

    public void init() {
        List list = accTypeService.list();
        super.setList(list)
    }

    public AccType readBySystemAccountType(String accountType) {
        List<AccType> list = super.list()
        for (int i = 0; i < list.size(); i++) {
            String systemAccountType = list[i].systemAccountType
            if (systemAccountType.equals(accountType))
                return list[i]
        }
        return null
    }
}
