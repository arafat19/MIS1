package com.athena.mis.document.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.document.service.DocDbInstanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Rezaul on 6/16/14.
 */
@Component('docDbInstanceCacheUtility')
class DocDbInstanceCacheUtility extends ExtendedCacheUtility {
    public static final String DEFAULT_SORT_NAME = 'instanceName'

    @Autowired
    DocDbInstanceService docDbInstanceService

    @Transactional(readOnly = true)
    public void init() {
        List lstDbInstance = docDbInstanceService.list()
        super.setList(lstDbInstance)
    }

    /*
    * Check Duplicate Instance Name for Create
    * @params instanceName - DB instance name
    * @return count - count of duplicate object
    * */

    public int countByInstanceNameIlike(String instanceName) {
        int count = 0
        List lstAll = list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].instanceName.equalsIgnoreCase(instanceName)) {
                count++
            }
        }
        return count
    }

    /*
    * Check Duplicate Instance Name for Update
    * @params instanceName - DB instance name
    * @params id - Updated Object Id
    * @return count - count of duplicate object
    * */

    public int countByInstanceNameIlikeAndIdNotEqual(String instanceName, long id) {
        int count = 0
        List lstAll = list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].instanceName.equalsIgnoreCase(instanceName) && lstAll[i].id != id) {
                count++
            }
        }
        return count
    }
}
