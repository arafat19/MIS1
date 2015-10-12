package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("ptProjectModuleCacheUtility")
class PtProjectModuleCacheUtility extends ExtendedCacheUtility{

    @Autowired
    PtProjectModuleService ptProjectModuleService

    public static final String SORT_ON_PROJECT_ID = "projectId";

    public void init(){
        List list = ptProjectModuleService.list();
        super.setList(list)
    }

    /**
     * Get count of PtProjectModule by projectId
     * @param projectId
     * @return -int count
     */
    public int countByProjectId(long projectId) {
        int count = 0;
        List<PtProjectModule> lstPtProjectModule = (List<PtProjectModule>) list()
        for (int i = 0; i < lstPtProjectModule.size(); i++) {
            if (lstPtProjectModule[i].projectId.equals(projectId.toLong()))
                count++
        }
        return count
    }
    /**
     * Get count of PtProjectModule by moduleId
     * @param moduleId
     * @return -int count
     */
    public int countByModuleId(long moduleId) {
        int count = 0;
        List<PtProjectModule> lstPtProjectModule = (List<PtProjectModule>) list()
        for (int i = 0; i < lstPtProjectModule.size(); i++) {
            if (lstPtProjectModule[i].moduleId.equals(moduleId.toLong()))
                count++
        }
        return count
    }
}
