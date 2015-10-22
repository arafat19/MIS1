package com.athena.mis.application.actions.costingdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingDetailsService
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ShowCostingDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load costing type page"
    private static final String COSTING_DETAILS_LIST = "costingDetailsList"
    private static final String COUNT = "count"
    private static final String GRID_OBJECT = "gridObject"

    CostingDetailsService costingDetailsService
    CostingTypeService costingTypeService

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
            /*if (!parameterMap.costingTypeId || !parameterMap.costingAmount) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }*/
            if (!parameterMap.rp) {
                parameterMap.rp = 20
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
            List costingDetailsList = (List) executeResult.get(COSTING_DETAILS_LIST)
            int count = (int) executeResult.get(COUNT)
            List wrappedCostingDetailsList = wrapListInGridEntityList(costingDetailsList, start)
            Map gridObject = [page: pageNumber, total: count, rows: wrappedCostingDetailsList]

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

    private List wrapListInGridEntityList(List<CostingDetails> costingDetailsList, int start) {
        List lstWrappedCostingDetails = [] as List
        try {
            int counter = start + 1
            for (int i = 0; i < costingDetailsList.size(); i++) {
                CostingDetails costingDetails = costingDetailsList[i]
                CostingType costingType = costingTypeService.read(costingDetails.costingTypeId)
                GridEntity obj = new GridEntity()
                obj.id = costingDetailsList[i].id
                obj.cell = [
                        counter,
                        costingType.name,
                        costingDetails.description,
                        costingDetails.costingAmount ? costingDetails.costingAmount : Tools.EMPTY_SPACE
                ]
                lstWrappedCostingDetails << obj
                counter++
            }
            return lstWrappedCostingDetails
        } catch (Exception ex) {
            log.error(ex.getMessage())
            lstWrappedCostingDetails = []
            return lstWrappedCostingDetails
        }
    }
}
