package com.athena.mis.accounting.actions.acccustomgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
 // grid search custom group
class SearchAccCustomGroupActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search custom group grid"

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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object execute(Object params, Object obj = null) {
        try {
            initSearch(params)
            Map searchResult = accCustomGroupCacheUtility.search(queryType, query, this)
            List<AccCustomGroup> accCustomGroupList = searchResult.list
            int count = searchResult.count
            return [accCustomGroupList: accCustomGroupList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<AccCustomGroup> accCustomGroupList = (List<AccCustomGroup>) executeResult.accCustomGroupList
            int count = (int) executeResult.count
            List inventory = wrapListInGridEntityList(accCustomGroupList, start)
            return [page: pageNumber, total: count, rows: inventory]
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
        List AccCustomGroups = [] as List
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
            AccCustomGroups << obj
            counter++
        }
        return AccCustomGroups
    }
}
