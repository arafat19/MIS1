package com.athena.mis.application.actions.costingdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.service.CostingDetailsService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SelectCostingDetailsActionService extends BaseService implements ActionIntf {

    CostingDetailsService costingDetailsService

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected costing details not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select costing details"
    private static final String INVALID_INPUT_MASSAGE = "Failed to select costing details due to invalid input"
    private static final String COSTING_DATE = "costingDate"

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

            long costingDetailsId = Long.parseLong(parameterMap.id.toString())
            CostingDetails costingDetails = (CostingDetails) costingDetailsService.read(costingDetailsId)
            if (!costingDetails) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            }
            String costingDate = DateUtility.getDateForUI(costingDetails.costingDate)
            result.put(Tools.ENTITY, costingDetails)
            result.put(COSTING_DATE, costingDate)
            result.put(Tools.VERSION, costingDetails.version)
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
