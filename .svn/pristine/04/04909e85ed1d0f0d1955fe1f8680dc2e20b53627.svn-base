package com.athena.mis.inventory.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.inventory.service.InvProductionLineItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('invProductionLineItemCacheUtility')
class InvProductionLineItemCacheUtility extends ExtendedCacheUtility{
    @Autowired
    InvProductionLineItemService invProductionLineItemService

    static final String SORT_ON_NAME = "name";

    public void init() {
        List list = invProductionLineItemService.list();
        super.setList(list)
    }

}
