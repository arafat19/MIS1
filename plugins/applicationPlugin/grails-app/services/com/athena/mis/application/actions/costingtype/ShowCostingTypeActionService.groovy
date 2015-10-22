package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.application.utility.CostingTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowCostingTypeActionService extends BaseService implements ActionIntf {

    CostingTypeService costingTypeService
    @Autowired
    CostingTypeCacheUtility costingTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load costing type page"
    private static final String COSTING_TYPE_LIST = "costingTypeList"
    private static final String COUNT = "count"
    private static final String GRID_OBJECT = "gridObject"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            /*if ((!parameterMap.sortname) || (parameterMap.sortname.toString().equals(ID))) {
                parameterMap.sortname = costingTypeCacheUtility.SORT_ON_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }*/

            if (!parameterMap.rp) {
                parameterMap.rp = 20
            }
            initPager(parameterMap)
//            int count = costingTypeCacheUtility.count()
//            List costingTypeList = costingTypeCacheUtility.list(this)


            List costingTypeList = costingTypeService.list()
            int count = costingTypeList.size()
            result.put(COSTING_TYPE_LIST, costingTypeList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return null
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List costingTypeList = (List) executeResult.get(COSTING_TYPE_LIST)
            int count = (int) executeResult.get(COUNT)
            List wrappedCostingTypeList = wrapListInGridEntityList(costingTypeList, start)
            Map gridObject = [page: pageNumber, total: count, rows: wrappedCostingTypeList]

            result.put(GRID_OBJECT, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<CostingType> costingTypeList, int start) {
        List costingTypes = [] as List
        try {
            int counter = start + 1
            for (int i = 0; i < costingTypeList.size(); i++) {
                GridEntity obj = new GridEntity()
                obj.id = costingTypeList[i].id
                obj.cell = [
                        counter,
                        costingTypeList[i].name,
                        costingTypeList[i].description
                ]
                costingTypes << obj
                counter++
            }
            return costingTypes
        } catch (Exception ex) {
            log.error(ex.getMessage())
            costingTypes = []
            return costingTypes
        }
    }
}
