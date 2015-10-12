package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.exchangehouse.service.ExhAgentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhAgentCacheUtility')
class ExhAgentCacheUtility extends ExtendedCacheUtility {

    public static final String SORT_ON_NAME = "name"
    @Autowired
    ExhAgentService exhAgentService

    public void init() {
        List lstAgent = exhAgentService.list();
        setList(lstAgent)
    }
}
