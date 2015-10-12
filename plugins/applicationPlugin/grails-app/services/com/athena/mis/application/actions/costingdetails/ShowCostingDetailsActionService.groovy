package com.athena.mis.application.actions.costingdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.CostingDetailsService
import com.athena.mis.application.utility.CostingDetailsCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowCostingDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load costing type page"
    private static final String COSTING_DETAILS_LIST = "costingDetailsList"
    private static final String COUNT = "count"
    private static final String GRID_OBJECT = "gridObject"

    CostingDetailsService costingDetailsService
    @Autowired
    CostingDetailsCacheUtility costingDetailsCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.sortname) || (parameterMap.sortname.toString().equals(ID))) {
                parameterMap.sortname = costingDetailsCacheUtility.SORT_ON_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }
            initPager(parameterMap)

            List costingDetailsList = costingDetailsService.list()
            int count = costingDetailsList.size()
            result.put(COSTING_DETAILS_LIST, costingDetailsList)
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
            List costingDetailsList = (List) executeResult.get(COSTING_TYPE_LIST)
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

    @Override
    Object buildFailureResultForUI(Object obj) {
        return null
    }
}
