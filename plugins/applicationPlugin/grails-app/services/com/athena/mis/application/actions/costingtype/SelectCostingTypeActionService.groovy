package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SelectCostingTypeActionService extends BaseService implements ActionIntf {

    CostingTypeService costingTypeService

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected costing type not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select costing type"
    private static final String INVALID_INPUT_MASSAGE = "Failed to select costing type due to invalid input"

    private final Logger log = Logger.getLogger(getClass())
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

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MASSAGE)
                return result
            }

            long costingTypeId = Long.parseLong(parameterMap.id.toString())
            CostingType costingType = (CostingType) costingTypeService.read(costingTypeId)
            if (!costingType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            }
            result.put(Tools.ENTITY, costingType)
            result.put(Tools.VERSION, costingType.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
