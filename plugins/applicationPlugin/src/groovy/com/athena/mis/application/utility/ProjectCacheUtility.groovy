package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("projectCacheUtility")
class ProjectCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ProjectService projectService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    static final String SORT_ON_NAME = "name";

    public void init() {
        List list = projectService.list();
        super.setList(list)
    }

    public boolean isAccessible(Long projectId) {
        List<Long> userProjectIds = appSessionUtil.getUserProjectIds()
        if (userProjectIds.size() <= 0) {
            return false;
        }
        if (projectId <= 0) {
            return false;
        }
        for (int i = 0; i < userProjectIds.size(); i++) {
            if (projectId == userProjectIds[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get count of Project by name
     * @param name -name of Project
     * @return -an integer containing the value of count
     */
    public int countByName(String name) {
        List<Project> lstTemp = super.list()
        List<Project> lstProject = []
        for (int i = 0; i < lstTemp.size(); i++) {
            if (lstTemp[i].name.equalsIgnoreCase(name)) {
                lstProject << lstTemp[i]
            }
        }
        return lstProject.size()
    }

    /**
     * Get count of Project by code
     * @param code -code of Project
     * @return -an integer containing the value of count
     */
    public int countByCode(String code) {
        List<Project> lstTemp = super.list()
        List<Project> lstProject = []
        for (int i = 0; i < lstTemp.size(); i++) {
            if (lstTemp[i].code.equalsIgnoreCase(code)) {
                lstProject << lstTemp[i]
            }
        }
        return lstProject.size()
    }

    /**
     * Get count of Project by name and id not equal
     * @param name -name of Project
     * @param id -id of Project
     * @return -an integer containing the value of count
     */
    public int countByNameAndIdNotEqual(String name, long id) {
        List<Project> lstTemp = super.list()
        List<Project> lstProject = []
        for (int i = 0; i < lstTemp.size(); i++) {
            if ((lstTemp[i].name.equalsIgnoreCase(name)) && (lstTemp[i].id != id)) {
                lstProject << lstTemp[i]
            }
        }
        return lstProject.size()
    }

    /**
     * Get count of Project by code and id not equal
     * @param code -code of Project
     * @param id -id of Project
     * @return -an integer containing the value of count
     */
    public int countByCodeAndIdNotEqual(String code, long id) {
        List<Project> lstTemp = super.list()
        List<Project> lstProject = []
        for (int i = 0; i < lstTemp.size(); i++) {
            if ((lstTemp[i].code.equalsIgnoreCase(code)) && (lstTemp[i].id != id)) {
                lstProject << lstTemp[i]
            }
        }
        return lstProject.size()
    }
}
