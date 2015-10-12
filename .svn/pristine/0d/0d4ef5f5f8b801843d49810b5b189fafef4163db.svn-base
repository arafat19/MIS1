package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.exchangehouse.service.ExhPhotoIdTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("exhPhotoIdTypeCacheUtility")
class ExhPhotoIdTypeCacheUtility extends ExtendedCacheUtility {

    // Following method will populate the list of ALL banks from DB
    public static final String DEFAULT_SORT_PROPERTY = 'name'
    @Autowired
    ExhPhotoIdTypeService exhPhotoIdTypeService

    public void init() {
        List list = exhPhotoIdTypeService.init()
        setList(list)
    }
}
