package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
// list of object(s) of custom group
class ListAccCustomGroupActionService extends BaseService implements ActionIntf {

    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Custom Group grid"

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
        try {
            Map executeResult = (Map) obj
            List<AccCustomGroup> accCustomGroupList = (List<AccCustomGroup>) executeResult.accCustomGroupList
            int count = (int) executeResult.count
            List customGroupList = wrapListInGridEntityList(accCustomGroupList, start)
            return [page: pageNumber, total: count, rows: customGroupList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<AccCustomGroup> accCustomGroupList, int start) {
        List materials = [] as List
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
            materials << obj
            counter++
        }
        return materials
    }
}
