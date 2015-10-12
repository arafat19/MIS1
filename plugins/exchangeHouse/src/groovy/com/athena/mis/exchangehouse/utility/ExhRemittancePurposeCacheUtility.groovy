package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.exchangehouse.service.ExhRemittancePurposeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("exhRemittancePurposeCacheUtility")
class ExhRemittancePurposeCacheUtility extends ExtendedCacheUtility {

    // Following method will populate the list of ALL remittancePurpose from DB
    public static final String DEFAULT_SORT_PROPERTY = 'name'
    @Autowired
    ExhRemittancePurposeService exhRemittancePurposeService

    public void init() {
        List lst = exhRemittancePurposeService.init()
        setList(lst)
    }

}
