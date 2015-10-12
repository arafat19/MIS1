package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.exchangehouse.service.ExhRegularFeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhRegularFeeCacheUtility')
class ExhRegularFeeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ExhRegularFeeService exhRegularFeeService

    public void init() {
        List lstItems = exhRegularFeeService.list();
        setList(lstItems)
    }
}
