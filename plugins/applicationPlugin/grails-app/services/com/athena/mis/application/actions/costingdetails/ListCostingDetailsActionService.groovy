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

class ListCostingDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String LIST_LOAD_FAILURE_MESSAGE = "Failed to load costing details list"
    private static final String COSTING_DETAILS_LIST = "costingDetailsList"
    private static final String COUNT = "count"

    CostingDetailsService costingDetailsService
    CostingTypeService costingTypeService

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

            List costingDetailsList = costingDetailsService.list()   // get sub list of designation
            int count = costingDetailsList.size()
            result.put(COSTING_DETAILS_LIST, costingDetailsList)
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
            List costingDetailsList = (List) executeResult.get(COSTING_DETAILS_LIST)
            // cast map returned from execute method
            int count = (int) executeResult.get(COUNT)
            List wrappedCostingTypeList = wrapListInGridEntityList(costingDetailsList, start)
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

    private List wrapListInGridEntityList(List<CostingDetails> costingDetailsList, int start) {
        List lstCostingDetails = [] as List
        int counter = start + 1
        for (int i = 0; i < costingDetailsList.size(); i++) {
            GridEntity obj = new GridEntity()
            CostingDetails costingDetails = costingDetailsList[i]
            CostingType costingType = costingTypeService.read(costingDetails.costingTypeId)
            obj.id = costingDetails.id
            obj.cell = [
                    counter,
                    costingType.name,
                    costingDetails.description,
                    costingDetails.costingAmount ? costingDetails.costingAmount : Tools.EMPTY_SPACE
            ]
            lstCostingDetails << obj
            counter++
        }
        return lstCostingDetails
    }
}
