package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.service.CostingTypeService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class DeleteCostingTypeActionService extends BaseService implements ActionIntf {

    CostingTypeService costingTypeService

    private static final String DELETE_SUCCESS_MSG = "Costing Type has been successfully deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Costing Type"
    private static final String INVALID_INPUT_MSG = "Failed to delete Costing Type due to invalid input"
    private static final String OBJ_NOT_FOUND_MSG = "Selected Costing Type not found, Refresh the page"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long costingTypeId = Long.parseLong(params.id.toString())
            //Check existing of object
            CostingType costingType = (CostingType) costingTypeService.read(costingTypeId)
            if (!costingType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MSG)
                return result
            }

            Map associationResult = isAssociated(costingType)    // check association of bank with relevant domains
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long costingTypeId = Long.parseLong(parameterMap.id.toString())
            int deleteCount = costingTypeService.delete(costingTypeId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete costing type')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
        return result
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private Map isAssociated(CostingType costingType) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long costingTypeId = costingType.id
        String costingTypeName = costingType.name
        int count=0


        count = countCostingType(costingTypeId)                                      // hasBranch
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, Tools.getMessageOfAssociation(costingTypeName, count, Tools.COSTING_DETAILS))
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    //count number of costing type in costing details table
    private static final String QUERY_COUNT_COSTING_TYPE = """
            SELECT COUNT(id) as count
            FROM costing_details
            WHERE costing_type_id = :costingTypeId
        """

    private int countCostingType(long costingTypeId) {
        List costingTypeCount = executeSelectSql(QUERY_COUNT_COSTING_TYPE, [costingTypeId: costingTypeId]);
        int count = costingTypeCount[0].count;
        return count;
    }
}
