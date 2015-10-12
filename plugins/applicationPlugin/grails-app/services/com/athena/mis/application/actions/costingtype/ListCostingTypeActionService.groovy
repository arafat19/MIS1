package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ListCostingTypeActionService extends BaseService implements ActionIntf {

    CostingTypeService costingTypeService

    private final Logger log = Logger.getLogger(getClass())
    private static final String LIST_LOAD_FAILURE_MESSAGE = "Failed to load costing type list"
    private static final String COSTING_TYPE_LIST = "costingTypeList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null;
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
            initPager(parameterMap)                                     // initialize parameters for flexGrid

            List costingTypeList = costingTypeService.list()   // get sub list of designation
            int count = costingTypeList.size()
            result.put(COSTING_TYPE_LIST, costingTypeList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return null
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List costingTypeList = (List) executeResult.get(COSTING_TYPE_LIST)
            // cast map returned from execute method
            int count = (int) executeResult.get(COUNT)
            List wrappedCostingTypeList = wrapListInGridEntityList(costingTypeList, start)
            result = [page: pageNumber, total: count, rows: wrappedCostingTypeList]

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: LIST_LOAD_FAILURE_MESSAGE]
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
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, LIST_LOAD_FAILURE_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<CostingType> costingTypesList, int start) {
        List costingTypes = [] as List
        int counter = start + 1
        for (int i = 0; i < costingTypesList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = costingTypesList[i].id
            obj.cell = [
                    counter,
                    costingTypesList[i].name,
                    costingTypesList[i].description
            ]
            costingTypes << obj
            counter++
        }
        return costingTypes
    }
}
