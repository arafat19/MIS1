package com.athena.mis.application.actions.costingdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingDetailsService
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateCostingDetailsActionService extends BaseService implements ActionIntf {

    CostingDetailsService costingDetailsService
    CostingTypeService costingTypeService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update costing details information"
    private static final String UPDATE_SUCCESS_MESSAGE = "Costing Details has been updated successfully"
    private static final String INVALID_INPUT_MSG = "Failed to update costing type due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected costing details has been changed by other user, Refresh the page again"
    private static final String COSTING_DETAILS = "costingDetails"
    private static final String NAME_EXISTS = "Same costing details already exists"


    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            //Check parameters
            if ((!params.id) || (!params.version) || (!params.costingTypeId) || (!params.costingAmount)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            CostingDetails oldCostingDetails = costingDetailsService.read(id)
            if ((!oldCostingDetails) || (oldCostingDetails.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }

            // Check existing of same costing type name
            String description = params.description.toString()
            if (costingDetailsService.countByDescriptionAndIdNotEqual(description, oldCostingDetails.id) > 0) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }


            CostingDetails costingDetailsInstance = buildCostingDetailsObject(params, oldCostingDetails)
            result.put(COSTING_DETAILS, costingDetailsInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            CostingDetails costingDetails = (CostingDetails) preResult.get(COSTING_DETAILS)
            costingDetailsService.update(costingDetails)
            result.put(COSTING_DETAILS, costingDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            CostingDetails costingDetails = (CostingDetails) executeResult.get(COSTING_DETAILS)
            CostingType costingTypeInstance = costingTypeService.read(costingDetails.costingTypeId)
            GridEntity object = new GridEntity()
            object.id = costingDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    costingTypeInstance.name,
                    costingDetails.description,
                    costingDetails.costingAmount ? costingDetails.costingAmount : Tools.EMPTY_SPACE
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    private CostingDetails buildCostingDetailsObject(GrailsParameterMap parameterMap, CostingDetails oldCostingDetails) {
        oldCostingDetails.description = parameterMap.description.toString()
        oldCostingDetails.costingAmount = Double.parseDouble(parameterMap.costingAmount.toString())
        oldCostingDetails.costingTypeId = Long.parseLong(parameterMap.costingTypeId.toString())
        oldCostingDetails.updatedBy = appSessionUtil.getAppUser().id
        oldCostingDetails.updatedOn = new Date()
        oldCostingDetails.costingDate = DateUtility.parseMaskedDate(parameterMap.costingDate.toString())
        return oldCostingDetails
    }
}
