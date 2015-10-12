package com.athena.mis.application.actions.company

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete company object from DB as well as from cache
 *          also delete companyLogo from entityContent domain
 *  For details go through Use-Case doc named 'DeleteCompanyActionService'
 */
class DeleteCompanyActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    CompanyService companyService
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_COMPANY_SUCCESS_MESSAGE = "Company has been deleted successfully"
    private static final String INVALID_INPUT_MESSAGE = "Could not delete company due to invalid input"
    private static final String DELETE_COMPANY_FAILURE_MESSAGE = "Company could not be deleted, please refresh the company list"
    private static final String USER_FOUND = " associated user(s) found of this company"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete company object
     *      1) Check access permission to delete company
     *      2) Check existence of required parameter
     *      3) Check existence of companyUser
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)

            //Only developmentUser can delete company object
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            //check if any companyUser exists or not
            long companyId = Long.parseLong(parameterMap.id.toString())
            List<AppUser> userList = appUserCacheUtility.list()
            if (userList.size() > 0) {
                result.put(Tools.MESSAGE, userList.size() + USER_FOUND)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Delete company object from DB & cache. Also delete other contents from entityContent domain
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long companyId = Long.parseLong(parameterMap.id.toString())
            // pull system entity type(Company) object
            SystemEntity contentEntityTypeCompany = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_COMPANY, companyId)

            //delete company object from DB
            Boolean result = (Boolean) companyService.delete(companyId)
            //delete company object from cache
            companyCacheUtility.delete(companyId)

            //delete all images(e.g : Photo, signature, logo etc) of this Company
            entityContentService.delete(companyId, contentEntityTypeCompany.id)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete company')
            return Boolean.FALSE
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_COMPANY_SUCCESS_MESSAGE]
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_COMPANY_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_COMPANY_FAILURE_MESSAGE)
            return result
        }
    }
}
