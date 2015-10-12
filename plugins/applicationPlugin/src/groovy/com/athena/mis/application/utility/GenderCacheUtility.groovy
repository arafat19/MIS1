package com.athena.mis.application.utility


import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component("genderCacheUtility")
class GenderCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    private static final String SORT_BY_ID = 'id';

    // Sys entity type
    private static final long ENTITY_TYPE = 1717;

    // Entity id get from reserved system entity
    public static final long GENDER_MALE = 176
    public static final long GENDER_FEMALE = 177

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE,SORT_BY_ID)
        setList(list)
    }

}
