package com.athena.mis.exchangehouse.actions.regularfee

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhRegularFee
import com.athena.mis.exchangehouse.service.ExhRegularFeeService
import com.athena.mis.exchangehouse.utility.ExhRegularFeeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateExhRegularFeeActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_FAILURE_MESSAGE = "Regular Fee could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Regular Fee logic has been updated successfully"
    private static final String EXH_REGULAR_FEE = "exhRegularFee"
    private static final String LOGIC_NOT_FOUND_MSG = "Regular Fee logic not found"
    private static final String LOGIC_REQUIRED = "Regular Fee logic required"
    private static final String SORT_ON_LOGIC = "logic"

    @Autowired
    ExhRegularFeeCacheUtility exhRegularFeeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil


    ExhRegularFeeService exhRegularFeeService

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long id = Long.parseLong(parameterMap.id.toString())
            ExhRegularFee oldExhRegularFee = exhRegularFeeService.read(id)
            if(!oldExhRegularFee){
                result.put(Tools.MESSAGE, LOGIC_NOT_FOUND_MSG)
                return result
            }
            if (parameterMap.logic.toString().isEmpty()){
                result.put(Tools.MESSAGE, LOGIC_REQUIRED)
                return result
            }
            ExhRegularFee exhRegularFee = buildExhRegularFeeObject(parameterMap, oldExhRegularFee)
            result.put(EXH_REGULAR_FEE, exhRegularFee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhRegularFee exhRegularFee = (ExhRegularFee) preResult.get(EXH_REGULAR_FEE)
            exhRegularFeeService.update(exhRegularFee)
            exhRegularFeeCacheUtility.update(exhRegularFee, SORT_ON_LOGIC, ASCENDING_SORT_ORDER)
            result.put(EXH_REGULAR_FEE, exhRegularFee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhRegularFee exhRegularFee = (ExhRegularFee) executeResult.get(EXH_REGULAR_FEE)
            result.put(EXH_REGULAR_FEE, exhRegularFee)
            result.put(Tools.VERSION, exhRegularFee.version)
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
                if (preResult.get(Tools.MESSAGE)) {
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

    private ExhRegularFee buildExhRegularFeeObject(GrailsParameterMap parameterMap, ExhRegularFee oldExhRegularFee) {
        ExhRegularFee exhRegularFee = new ExhRegularFee(parameterMap)
        exhRegularFee.id = oldExhRegularFee.id
        exhRegularFee.version = oldExhRegularFee.version
        exhRegularFee.companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        exhRegularFee.updatedBy = exhSessionUtil.appSessionUtil.getAppUser().id
        exhRegularFee.updatedOn = new Date()
        return exhRegularFee
    }
}
