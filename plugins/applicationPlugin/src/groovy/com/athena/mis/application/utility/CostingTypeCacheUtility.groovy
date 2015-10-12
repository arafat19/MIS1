package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("costingTypeCacheUtility")
class CostingTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    CostingTypeService costingTypeService

    static final String SORT_ON_NAME = "name"

    public void init() {
        List list = costingTypeService.list()
        super.setList(list)
    }

    public CostingType findByNameAndIdNotEqual(String name, long id) {
        List<CostingType> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            if ((lstTemp[i].name.equalsIgnoreCase(name)) && (lstTemp[i].id != id)) {
                return lstTemp[i]
            }
        }
        return null
    }
}