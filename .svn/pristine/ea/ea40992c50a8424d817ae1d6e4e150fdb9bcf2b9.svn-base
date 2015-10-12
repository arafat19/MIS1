package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.service.AccCustomGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("accCustomGroupCacheUtility")
class AccCustomGroupCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccCustomGroupService accCustomGroupService

    static final String SORT_BY_NAME = "name";

    public void init() {
        List list = accCustomGroupService.list();
        super.setList(list)
    }

    public AccCustomGroup readByName(String accCustomGroupName) {
        accCustomGroupName = accCustomGroupName.trim()
        String existingAccCustomGroupName
        List<AccCustomGroup> accCustomGroupList = super.list()
        for (int i = 0; i < accCustomGroupList.size(); i++) {
            existingAccCustomGroupName = accCustomGroupList[i].name
            if (existingAccCustomGroupName.equalsIgnoreCase(accCustomGroupName)) {
                return accCustomGroupList[i]
            }
        }
        return null
    }

    public AccCustomGroup readByNameForUpdate(String accCustomGroupName, long accCustomGroupId) {
        accCustomGroupName = accCustomGroupName.trim()
        String existingAccCustomGroupName
        List<AccCustomGroup> accCustomGroupList = super.list()
        for (int i = 0; i < accCustomGroupList.size(); i++) {
            existingAccCustomGroupName = accCustomGroupList[i].name
            if (existingAccCustomGroupName.equalsIgnoreCase(accCustomGroupName)
                    && accCustomGroupList[i].id != accCustomGroupId) {
                return accCustomGroupList[i]
            }
        }
        return null
    }
}