package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.service.DesignationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('designationCacheUtility')
class DesignationCacheUtility extends ExtendedCacheUtility {

    @Autowired
    DesignationService designationService

    public final String SORT_ON_NAME = "name";

    public void init() {
        List list = designationService.list();
        super.setList(list)
    }

    /**
     * Get Designation object by name and companyId
     * @param name -name of Designation
     * @return -object of Designation
     */
    public Designation findByName(String name) {
        List<Designation> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            if (lstTemp[i].name.equalsIgnoreCase(name)) {
                return lstTemp[i]
            }
        }
        return null
    }

    /**
     * Get Designation object by name and id not equal
     * @param name -name of Designation
     * @param id -id of Designation
     * @return -object of Designation
     */
    public Designation findByNameAndIdNotEqual(String name, long id) {
        List<Designation> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            if ((lstTemp[i].name.equalsIgnoreCase(name)) && (lstTemp[i].id != id)) {
                return lstTemp[i]
            }
        }
        return null
    }

    /**
     * Get Designation object by shortName and companyId
     * @param shortName -short name of Designation
     * @return -object of Designation
     */
    public Designation findByShortName(String shortName) {
        List<Designation> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            if (lstTemp[i].shortName.equalsIgnoreCase(shortName)) {
                return lstTemp[i]
            }
        }
        return null
    }

    /**
     * Get Designation object by shortName and id not equal
     * @param shortName -short name of Designation
     * @param id -id of Designation
     * @return -object of Designation
     */
    public Designation findByShortNameAndIdNotEqual(String shortName, long id) {
        List<Designation> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            if ((lstTemp[i].shortName.equalsIgnoreCase(shortName)) && (lstTemp[i].id != id)) {
                return lstTemp[i]
            }
        }
        return null
    }
}
