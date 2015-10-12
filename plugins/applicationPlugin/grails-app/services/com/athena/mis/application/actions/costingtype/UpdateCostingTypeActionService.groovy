package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateCostingTypeActionService extends BaseService implements ActionIntf {

    CostingTypeService costingTypeService

    @Autowired
    AppSessionUtil appSessionUtil

    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update costing type information"
    private static final String UPDATE_SUCCESS_MESSAGE = "Costing type has been updated successfully"
    private static final String INVALID_INPUT_MSG = "Failed to update costing type due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected costing type has been changed by other user, Refresh the page again"
    private static final String COSTING_TYPE = "costingType"
    private static final String NAME_EXISTS = "Same costing type name already exists"


    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            CostingType oldCostingType = costingTypeService.read(id)
            if ((!oldCostingType) || (oldCostingType.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }

            // Check existing of same costing type name
            String name = params.name.toString()
            if (costingTypeService.countByNameAndIdNotEqual(name, oldCostingType.id) > 0) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }


            CostingType costingType = buildCostingTypeObject(params, oldCostingType)
            result.put(COSTING_TYPE, costingType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            CostingType costingType = (CostingType) preResult.get(COSTING_TYPE)
            costingTypeService.update(costingType)
            result.put(COSTING_TYPE, costingType)
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
            CostingType costingType = (CostingType) executeResult.get(COSTING_TYPE)
            GridEntity object = new GridEntity()
            object.id = costingType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    costingType.name,
                    costingType.description
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

    private CostingType buildCostingTypeObject(GrailsParameterMap parameterMap, CostingType oldCostingType) {
        oldCostingType.name = parameterMap.name.toString()
        oldCostingType.description = parameterMap.description.toString()
        oldCostingType.updatedBy = appSessionUtil.getAppUser().id
        oldCostingType.updatedOn = new Date()
        return oldCostingType
    }
}
