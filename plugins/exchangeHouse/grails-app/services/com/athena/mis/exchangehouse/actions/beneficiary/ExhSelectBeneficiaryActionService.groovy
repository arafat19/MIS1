package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select beneficiary object and show in UI for editing
 *  For details go through Use-Case doc named 'ExhSelectBeneficiaryActionService'
 */
class ExhSelectBeneficiaryActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    public static final String BENEFICIARY_NOT_FOUND_ERROR = "Selected beneficiary is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select beneficiary"
    private static final String USER_NAME = "userName"
    private static final String FULL_NAME = "fullName"

    ExhBeneficiaryService exhBeneficiaryService

    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get beneficiary object by id
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {                      // check required params
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(parameterMap.id)

            ExhBeneficiary beneficiary = exhBeneficiaryService.read(id)   // get beneficiary object from DB

            if (!beneficiary) {
                result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_ERROR)
                return result
            }

            result.put(Tools.ENTITY, beneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_ERROR)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with beneficiary object to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) executeResult.get(Tools.ENTITY)
            Long verifierUserId = beneficiary.createdBy
            String verifierUserName = ((AppUser) appUserCacheUtility.read(verifierUserId)).username

            result.put(Tools.ENTITY, beneficiary)
            result.put(Tools.VERSION, beneficiary.version)
            result.put(USER_NAME, verifierUserName)
            result.put(FULL_NAME, beneficiary.fullName)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj               // cast map returned from previous method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result
        }
    }

}
