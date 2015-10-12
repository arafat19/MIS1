package com.athena.mis.application.actions.costingdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.CostingDetailsService
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateCostingDetailsActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Costing details has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save costing details"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String COSTING_TYPE_NOT_FOUND = "Costing type not found"
    private static final String COSTING_DETAILS_OBJ = "costingDetails"

    private Logger log = Logger.getLogger(getClass())

    CostingTypeService costingTypeService
    CostingDetailsService costingDetailsService
    @Autowired
    AppSessionUtil appSessionUtil

    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if ((!parameterMap.costingTypeId || !parameterMap.costingAmount)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long costingTypeId = Long.parseLong(parameterMap.costingTypeId.toString())
            CostingType costingType = costingTypeService.read(costingTypeId)
            if (!costingType) {
                result.put(Tools.MESSAGE, COSTING_TYPE_NOT_FOUND)
                return result
            }

            CostingDetails costingDetails = buildCostingDetails(parameterMap, costingType)



            result.put(COSTING_DETAILS_OBJ, costingDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned from executePreCondition method
            CostingDetails costingType = (CostingDetails) preResult.get(COSTING_DETAILS_OBJ)
            CostingDetails costingDetailsInstance = costingDetailsService.create(costingType)  // save new budget in DB
            result.put(COSTING_DETAILS_OBJ, costingDetailsInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj     // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            CostingDetails costingDetailsInstance = (CostingDetails) receiveResult.get(COSTING_DETAILS_OBJ)
            GridEntity object = new GridEntity()    // build new grid object
            object.id = costingDetailsInstance.id
            CostingType costingTypeInstance = costingTypeService.read(costingDetailsInstance.costingTypeId)
            object.cell = [
                    Tools.LABEL_NEW,
                    costingTypeInstance.name,
                    costingDetailsInstance.description,
                    costingDetailsInstance.costingAmount ? costingDetailsInstance.costingAmount : Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj      // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private CostingDetails buildCostingDetails(GrailsParameterMap parameterMap, CostingType costingType) {
        CostingDetails costingDetails = new CostingDetails(parameterMap)
        AppUser systemUser = appSessionUtil.getAppUser()

        costingDetails.costingTypeId = costingType.id
        costingDetails.createdOn = new Date()
        costingDetails.createdBy = systemUser.id

        // ensure null on updateBy and additional fields
        costingDetails.updatedOn = null
        costingDetails.updatedBy = 0
        costingDetails.companyId = systemUser.companyId
        return costingDetails
    }

}
