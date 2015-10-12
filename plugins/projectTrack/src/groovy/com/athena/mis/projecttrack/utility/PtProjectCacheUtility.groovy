package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.service.PtProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component("ptProjectCacheUtility")
class PtProjectCacheUtility extends ExtendedCacheUtility {

    @Autowired
    PtProjectService ptProjectService

    public static final String SORT_ON_NAME = "name";

    public void init(){
        List list = ptProjectService.list();
        super.setList(list)
    }

    // return number of same project code in a specific company
    public int countByCode(String code) {
        int count = 0;
        List<PtProject> lstPtProject = (List<PtProject>) list()
        for (int i = 0; i < lstPtProject.size(); i++) {
            if (lstPtProject[i].code.equalsIgnoreCase(code))
                count++
        }
        return count
    }

    // return number of same project code and id is not equal in a specific company
    public int countByCodeAndIdNotEqual(String code, long id) {
        int count = 0;
        List<PtProject> lstPtProject = (List<PtProject>) list()
        for (int i = 0; i < lstPtProject.size(); i++) {
            if (lstPtProject[i].code.equalsIgnoreCase(code) && lstPtProject[i].id != id)
                count++
        }
        return count
    }

}
