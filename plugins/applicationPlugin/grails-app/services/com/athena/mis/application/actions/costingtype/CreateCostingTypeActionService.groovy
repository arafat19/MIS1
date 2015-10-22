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

class CreateCostingTypeActionService extends BaseService implements ActionIntf {

    private static final String COSTING_TYPE_SAVE_SUCCESS_MESSAGE = "Costing type has been saved successfully"
    private static final String COSTING_TYPE_SAVE_FAILURE_MESSAGE = "Costing type could not be saved"
    private static final String COSTING_TYPE_ALREADY_EXIST = "Same Costing type name already exist"
    private static final String INVALID_INPUT_MSG = "Failed to create Costing type due to invalid input"
    private static final String COSTING_TYPE = "costingType"

    CostingTypeService costingTypeService
    @Autowired
    AppSessionUtil appSessionUtil


    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            //Check parameters
            if ((!params.name)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            int duplicateCount = costingTypeService.countByNameIlike(params.name.toString())
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, COSTING_TYPE_ALREADY_EXIST)
                return result
            }

            CostingType costingType = buildObject(params)

            result.put(COSTING_TYPE, costingType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
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
            CostingType costingType = (CostingType) preResult.get(COSTING_TYPE)
            CostingType newCostingType = costingTypeService.create(costingType)
            result.put(COSTING_TYPE, newCostingType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(COSTING_TYPE_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COSTING_TYPE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            CostingType costingTypeInstance = (CostingType) receiveResult.get(COSTING_TYPE)
            GridEntity object = new GridEntity()
            object.id = costingTypeInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    costingTypeInstance.name,
                    costingTypeInstance.description ? costingTypeInstance.description : Tools.EMPTY_SPACE
            ]

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, COSTING_TYPE_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COSTING_TYPE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, COSTING_TYPE_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COSTING_TYPE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private CostingType buildObject(GrailsParameterMap parameterMap) {
        CostingType costingType = new CostingType(parameterMap)
        costingType.companyId = appSessionUtil.getCompanyId()
        costingType.createdOn = new Date()
        costingType.createdBy = appSessionUtil.getAppUser().id
        costingType.updatedOn = null
        costingType.updatedBy = 0
        return costingType
    }

}
