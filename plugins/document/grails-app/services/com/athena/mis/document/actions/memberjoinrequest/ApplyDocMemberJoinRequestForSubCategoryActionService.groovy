package com.athena.mis.document.actions.memberjoinrequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocAllCategoryUserMapping
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocMemberJoinRequest
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocAllCategoryUserMappingService
import com.athena.mis.document.service.DocMemberJoinRequestService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.plugin.jcaptcha.JcaptchaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

class ApplyDocMemberJoinRequestForSubCategoryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DUPLICATE_JOIN_REQUEST = 'You already applied for membership'
    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load invitation page, please refresh'
    private static final String CATEGORY_NOT_FOUND_MESSAGE = 'Category not found'
    private static final String SUB_CATEGORY_NOT_FOUND_MESSAGE = 'Sub Category not found'
    private static final String CATEGORY_ALREADY_MAPPED = 'You are already a member of this '
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String CATEGORY_NAME = 'Category'
    private static final String SUB_CATEGORY_NAME = 'SubCategory'
    private static final String SUB_CATEGORY = 'subCategory'
    private static final String CATEGORY = 'category'
    private static final String HTTP_REQUEST_OBJ = 'httpRequestObj'
    private static final String SUCCESS_MESSAGE = 'Your membership request sent successfully'
    private static final String CAPTCHA_ERR_MESSAGE = "Security ID wasn't matched."


    DocMemberJoinRequestService docMemberJoinRequestService
    DocAllCategoryUserMappingService docAllCategoryUserMappingService
    def jcaptchaService

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    /**
     * 1. Get parameters from UI
     * 2. category and sub category label from system configuration
     * 3. check category existence
     * 4. check already sent request existance
     * 5. check appuser, sub category mapping
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all object necessary for execute
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = CATEGORY_NAME
        String subCategoryLabel = SUB_CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            HttpServletRequest request = (HttpServletRequest) obj

            boolean matched = jcaptchaService.validateResponse("image", request.session.id, params.captcha);
            if (!matched) {
                result.put(Tools.MESSAGE, CAPTCHA_ERR_MESSAGE)
                return result
            }

            long companyId = getCompanyId(request)
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)

            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }

            sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryLabel = sysConfiguration.value
            }

            // check required parameter
            if ((!params.categoryId) || (!params.subCategoryId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long subCategoryId = Long.parseLong(params.subCategoryId.toString())
            long categoryId = Long.parseLong(params.categoryId.toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.readByIdAndCompanyId(subCategoryId, companyId)
            DocCategory category = (DocCategory) docCategoryCacheUtility.readByIdAndCompanyId(subCategory.categoryId, request)

            if (!subCategory) {
                result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
                result.put(SUB_CATEGORY, subCategory)
                result.put(CATEGORY_LABEL, categoryLabel)
                result.put(CATEGORY, category)
                result.put(Tools.MESSAGE, SUB_CATEGORY_NOT_FOUND_MESSAGE)
                return result
            }

            if (!category) {
                result.put(CATEGORY_LABEL, categoryLabel)
                result.put(CATEGORY, category)
                result.put(Tools.MESSAGE, CATEGORY_NOT_FOUND_MESSAGE)
                return result
            }
            // check required parameter
            if ((!params.email) || (!params.userName)) {
                result.put(CATEGORY_LABEL, categoryLabel)
                result.put(CATEGORY, category)
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            String email = params.email.toString()
            AppUser appUser = appUserCacheUtility.readByLoginId(email, companyId)
            if (appUser) {
                DocAllCategoryUserMapping categoryUserMapping = docAllCategoryUserMappingService.findByCategoryIdAndUserIdAndSubCategoryId(categoryId, appUser.id, subCategoryId)
                if (categoryUserMapping) {
                    result.put(CATEGORY_LABEL, categoryLabel)
                    result.put(CATEGORY, category)
                    result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
                    result.put(SUB_CATEGORY, subCategory)
                    result.put(Tools.MESSAGE, CATEGORY_ALREADY_MAPPED + subCategoryLabel)
                    return result
                }
            }
            DocMemberJoinRequest docJoinRequest = docMemberJoinRequestService.findByCategoryIdAndSubCategoryIdAndEmailAndCompanyId(categoryId, subCategoryId, email, companyId)
            if (docJoinRequest) {
                result.put(CATEGORY_LABEL, categoryLabel)
                result.put(CATEGORY, category)
                result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
                result.put(SUB_CATEGORY, subCategory)
                result.put(Tools.MESSAGE, DUPLICATE_JOIN_REQUEST)
                return result
            }
            result.put(HTTP_REQUEST_OBJ, request)
            result.put(CATEGORY, category)
            result.put(SUB_CATEGORY, subCategory)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post condition
        return null
    }

    /* DocMemberJoinRequest save to DB
    * @param parameters - serialized parameters from UI
    * @param obj - A map from precondition
    * @return - A map containing all objects of category, sub category, categoryLabel, subCategoryLabel for buildSuccessResultForUI
    * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryName = preResult.get(CATEGORY_LABEL)
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)
            HttpServletRequest request = (HttpServletRequest) preResult.get(HTTP_REQUEST_OBJ)
            DocMemberJoinRequest docJoinRequest = buildDocJoinRequest(parameterMap, request)
            docMemberJoinRequestService.create(docJoinRequest)

            result.put(CATEGORY, category)
            result.put(SUB_CATEGORY, subCategory)
            result.put(CATEGORY_LABEL, categoryName)
            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /*
    * Build Success Results for UI
    * @params obj - Map return from execute method
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)

            result.put(CATEGORY, category)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(SUB_CATEGORY, subCategory)
            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build Failure result for UI
    * @params obj - A map from execute method
    * @return a Map containing IsError and default error message/relevant error message to display
    * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(CATEGORY_LABEL, categoryLabel)
                    result.put(CATEGORY, category)
                    result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
                    result.put(SUB_CATEGORY, subCategory)
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(CATEGORY, category)
            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(SUB_CATEGORY, subCategory)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * retrieve companyId from request
     * @param request - HttpServletRequest
     * @return company ID
     */

    private long getCompanyId(HttpServletRequest request) {
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl) // compare with www
        if (company) {
            return company.id
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)     // compare without www
        if (company) {
            return company.id
        } else {
            return 0L
        }
    }

    /*
    * Build DocMemberJoinRequest object
    * @param parameterMap - serialize parameters from UI
    * @param request - HttpServletRequest
    * @return docJoinRequest - DocMemberJoinRequest object
    * */

    private DocMemberJoinRequest buildDocJoinRequest(GrailsParameterMap parameterMap, HttpServletRequest request) {
        String expiredOn = ''
        long companyId = getCompanyId(request)
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_EXPIRATION_INVITED_MEMBER, companyId)
        if (sysConfiguration) {
            expiredOn = sysConfiguration.value
        }
        DocMemberJoinRequest docJoinRequest = new DocMemberJoinRequest(parameterMap)
        docJoinRequest.createdOn = new Date()
        docJoinRequest.expiredOn = new Date() + Integer.parseInt(expiredOn.toString())
        docJoinRequest.approvedBy = 0L
        docJoinRequest.approvedOn = null
        docJoinRequest.companyId = companyId
        return docJoinRequest
    }
}
