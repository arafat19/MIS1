package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.entity.DocSubCategoryUserMapping
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.service.DocSubCategoryUserMappingService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AddOrRemoveSubCategoryFavouriteActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = 'Failed to add or remove favourite list '
    private static final String CATEGORY_MUST_BE_ACTIVE = ' must be active for this operation'
    private static final String NOT_FOUND_MESSAGE = ' not found'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String DOC_SUB_CATEGORY_USER_MAPPING = 'docSubCategoryUserMapping'
    private static final String DEFAULT_CATEGORY = 'Category'
    private static final String SUCCESS_MSG_ADD_TO_FAVOURITE_LIST = " successfully add to favourite list"
    private static final String SUCCESS_MSG_REMOVE_TO_FAVOURITE_LIST = " successfully remove from favourite list"
    private static final String SUB_CATEGORY_NOT_ACCESS_MSG = 'You are not allowed with this '

    DocSubCategoryService docSubCategoryService
    DocSubCategoryUserMappingService docSubCategoryUserMappingService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryLabel = SUB_CATEGORY_LABEL
        String categoryLabel = DEFAULT_CATEGORY
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long categoryId = Long.parseLong(parameterMap.categoryId.toString())

            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                result.put(Tools.MESSAGE, categoryLabel + NOT_FOUND_MESSAGE)
                return result
            }

            if (!category.isActive.booleanValue()) {
                result.put(Tools.MESSAGE, categoryLabel + CATEGORY_MUST_BE_ACTIVE)
                return result
            }

            long subCategoryId = Long.parseLong(parameterMap.subCategoryId.toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
            if (!subCategory) {
                result.put(Tools.MESSAGE, subCategoryLabel + NOT_FOUND_MESSAGE)
                return result
            }

            if (!subCategory.isActive.booleanValue()) {
                result.put(Tools.MESSAGE, subCategory.name + CATEGORY_MUST_BE_ACTIVE)
                return result
            }
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            long appUserId = docSessionUtil.appSessionUtil.getAppUser().id
            DocSubCategoryUserMapping docSubCategoryUserMapping = docSubCategoryUserMappingService.findByCompanyIdAndUserIdAndCategoryIdAndSubCategoryId(companyId, appUserId, category.id, subCategory.id)

            if (!docSubCategoryUserMapping) {
                result.put(Tools.MESSAGE, subCategory.name + SUB_CATEGORY_NOT_ACCESS_MSG)
                return result
            }

            docSubCategoryUserMapping = buildDocSubCategoryUserMapping(parameterMap, docSubCategoryUserMapping)
            //build sub category object

            result.put(DOC_SUB_CATEGORY_USER_MAPPING, docSubCategoryUserMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE )
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do not nothing post condition
        return null
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String favouriteMsg = Tools.EMPTY_SPACE
            Map preResult = (LinkedHashMap) obj
            DocSubCategoryUserMapping docSubCategoryUserMapping = (DocSubCategoryUserMapping) preResult.get(DOC_SUB_CATEGORY_USER_MAPPING)
            updateIsFavourite(docSubCategoryUserMapping)
            if (docSubCategoryUserMapping.isFavourite) {
                favouriteMsg = SUCCESS_MSG_ADD_TO_FAVOURITE_LIST
                result.put(Tools.MESSAGE, favouriteMsg)
                return result
            }
            favouriteMsg = SUCCESS_MSG_REMOVE_TO_FAVOURITE_LIST
            result.put(Tools.MESSAGE, favouriteMsg)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Contains delete success message to show on UI
     * @param obj -A map from execute method
     * @return -a map contains boolean value(true/false) and delete success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, Tools.MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /*
* Build Failure result in case of any error
* @params obj - A map from execute method
* @return - A map containing all necessary message for show
* */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    private DocSubCategoryUserMapping buildDocSubCategoryUserMapping(GrailsParameterMap parameterMap, DocSubCategoryUserMapping docSubCategoryUserMapping) {
        docSubCategoryUserMapping.isFavourite = Boolean.parseBoolean(parameterMap.isLike.toString())
        return docSubCategoryUserMapping

    }

    /**
     * SQL to update DocMemberJoinRequest object in database
     * @param joinRequest -DocMemberJoinRequest object
     * @return -int value(updateCount)
     */
    public boolean updateIsFavourite(DocSubCategoryUserMapping docSubCategoryUserMapping) {
        String query = """ UPDATE doc_sub_category_user_mapping SET
                            is_favourite =:isFavourite
                            WHERE company_id =:companyId
                            AND id = :id
                       """

        Map queryParams = [
                isFavourite: docSubCategoryUserMapping.isFavourite,
                companyId  : docSubCategoryUserMapping.companyId,
                id         : docSubCategoryUserMapping.id
        ]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at DocMemberJoinRequest update")
        }
        return true
    }
}
