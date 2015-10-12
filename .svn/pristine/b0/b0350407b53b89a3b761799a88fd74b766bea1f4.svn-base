package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtModuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('ptModuleCacheUtility')
class PtModuleCacheUtility extends ExtendedCacheUtility {
    @Autowired
    PtModuleService ptModuleService

    public final String SORT_ON_NAME = "name";

    public void init() {
        List list = ptModuleService.list();
        super.setList(list)
    }

    // return number of same module code in a specific company
    public int countByCode(String code) {
        int count = 0;
        List<PtModule> lstPtModule = (List<PtModule>) list()
        for (int i = 0; i < lstPtModule.size(); i++) {
            if (lstPtModule[i].code.equalsIgnoreCase(code))
                count++
        }
        return count
    }

    // return number of same project code and id is not equal in a specific company
    public int countByCodeAndIdNotEqual(String code, long id) {
        int count = 0;
        List<PtModule> lstPtModule = (List<PtModule>) list()
        for (int i = 0; i < lstPtModule.size(); i++) {
            if (lstPtModule[i].code.equalsIgnoreCase(code) && lstPtModule[i].id != id)
                count++
        }
        return count
    }

}
