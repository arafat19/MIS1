package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
 // show list of object(s) of custom group
class ShowAccCustomGroupActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load custom group page"
    private static final String ACC_CUSTOM_GROUP_LIST = "accCustomGroupList"

    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    public Object execute(Object params, Object obj) {
        try {
            initPager(params)
            int count = accCustomGroupCacheUtility.count()
            List accCustomGroupList = accCustomGroupCacheUtility.list(this)
            return [accCustomGroupList: accCustomGroupList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<AccCustomGroup> accCustomGroupList = (List<AccCustomGroup>) executeResult.accCustomGroupList
            int count = (int) executeResult.count
            List resultList = wrapListInGridEntityList(accCustomGroupList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            result.put(ACC_CUSTOM_GROUP_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<AccCustomGroup> accCustomGroupList, int start) {
        List accCustomGroups = [] as List
        int counter = start + 1
        for (int i = 0; i < accCustomGroupList.size(); i++) {
            AccCustomGroup accCustomGroup = accCustomGroupList[i]
            GridEntity obj = new GridEntity()
            obj.id = accCustomGroup.id
            obj.cell = [
                    counter,
                    accCustomGroup.id,
                    accCustomGroup.name ? accCustomGroup.name : Tools.EMPTY_SPACE,
                    accCustomGroup.description ? accCustomGroup.description : Tools.EMPTY_SPACE,
                    accCustomGroup.isActive ? Tools.YES : Tools.NO
            ]
            accCustomGroups << obj
            counter++
        }
        return accCustomGroups
    }
}
