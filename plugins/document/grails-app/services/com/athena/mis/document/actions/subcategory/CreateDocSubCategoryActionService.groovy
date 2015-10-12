package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocCategoryUserMapping
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.entity.DocSubCategoryUserMapping
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.service.DocCategoryUserMappingService
import com.athena.mis.document.service.DocSubCategoryService
import com.athena.mis.document.service.DocSubCategoryUserMappingService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/*
* Create new sub category to DB
* Show new sub category to grid
* */

class CreateDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUB_CATEGORY = 'subCategory'
    private static final String CREATE_SUCCESS_MESSAGE = ' has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved '
    private static final String NAME_MUST_BE_UNIQUE = ' name must be unique'
    private static final String CATEGORY_MUST_BE_ACTIVE = ' must be active for this operation'
    private static final String NOT_FOUND_MESSAGE = ' not found'
    private static final String SUB_CATEGORY_LABEL = 'subCategoryLabel'
    private static final String SUB_CATEGORY_NAME = 'Sub Category'
    private static final String CATEGORY = 'category'
    private static final String DEFAULT_CATEGORY = 'Category'


    DocSubCategoryService docSubCategoryService
    DocCategoryService docCategoryService
    LinkGenerator grailsLinkGenerator
    DocCategoryUserMappingService docCategoryUserMappingService
    DocSubCategoryUserMappingService docSubCategoryUserMappingService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /*
    * @params parameters - serialize parameters form UI
    * @params obj - N/A
    * Build parameters to Sub Category object
    * Check duplicate sub category name
    * get category & sub category label from system configuration
    * @return - A map of containing sub category object or error message
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String subCategoryLabel = SUB_CATEGORY_NAME
        String categoryLabel = DEFAULT_CATEGORY
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                subCategoryLabel = sysConfiguration.value
            }

            SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration1) {
                categoryLabel = sysConfiguration1.value
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long categoryId = Long.parseLong(params.categoryId.toString())
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!category) {
                result.put(Tools.MESSAGE, categoryLabel + NOT_FOUND_MESSAGE)
                return result
            }

            if (!category.isActive.booleanValue()) {
                result.put(Tools.MESSAGE, categoryLabel + CATEGORY_MUST_BE_ACTIVE)
                return result
            }

            String subCategoryName = params.name.toString()
            int duplicateCount = docSubCategoryCacheUtility.countByNameIlikeAndCategoryId(subCategoryName, categoryId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, subCategoryLabel + NAME_MUST_BE_UNIQUE)
                return result
            }

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(CATEGORY, category)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + subCategoryLabel)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
    * Create sub Category object to DB
    * @params parameters - N/A
    * @params obj - sub category object from executePreCondition
    * @return - A map of saved sub category object or error messages
    * Ths method is in Transactional boundary so will rollback in case of any exception
    * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            DocCategory category = (DocCategory) preResult.get(CATEGORY)
            int incr = 1
            DocSubCategory subCategory = buildSubCategory(params, category)     //build sub category object
            docSubCategoryService.create(subCategory)
            docCategoryService.updateSubCategoryCount(category, incr)

            List<DocCategoryUserMapping> lstCategoryUserMapping = docCategoryUserMappingService.findAllByCompanyIdAndCategoryId(subCategory.companyId, subCategory.categoryId)
            for (int i = 0; i < lstCategoryUserMapping.size(); i++) {
                    DocSubCategoryUserMapping subCategoryUserMapping = buildAppUserSubCategoryObject(subCategory, lstCategoryUserMapping[i])
                    docSubCategoryUserMappingService.create(subCategoryUserMapping)
            }

            docSubCategoryCacheUtility.add(subCategory, docSubCategoryCacheUtility.DEFAULT_SORT_ORDER, docSubCategoryCacheUtility.SORT_ORDER_ASCENDING)
            docCategoryCacheUtility.update(category, docCategoryCacheUtility.DEFAULT_SORT_ORDER, docCategoryCacheUtility.SORT_ORDER_ASCENDING)

            result.put(SUB_CATEGORY_LABEL, subCategoryLabel)
            result.put(SUB_CATEGORY, subCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CREATE_FAILURE_MESSAGE + subCategoryLabel)

            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show newly created sub category to grid
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (Map) obj
        String subCategoryLabel = preResult.get(SUB_CATEGORY_LABEL)

        try {
            DocSubCategory subCategory = (DocSubCategory) preResult.get(SUB_CATEGORY)
            GridEntity object = new GridEntity()
            object.id = subCategory.id
            object.cell = [
                    Tools.LABEL_NEW,
                    subCategory.id,
                    subCategory.name,
                    subCategory.description ? subCategory.description : Tools.EMPTY_SPACE,
                    subCategory.isActive ? Tools.YES : Tools.NO,
                    subCategory.isEmailNotification ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, subCategoryLabel + CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
                    result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + subCategoryLabel)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE + subCategoryLabel)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build Sub Category for create
   * */

    private DocSubCategory buildSubCategory(GrailsParameterMap parameterMap, DocCategory category) {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        long subCategoryId = getSubCategoryId()
        String url = grailsLinkGenerator.link(controller: 'docSubCategory', action: 'showSubCategory', params: [category: category.id, sub: subCategoryId], absolute: true)
        DocSubCategory subCategory = new DocSubCategory(parameterMap)
        subCategory.id = subCategoryId
        subCategory.url = url
        subCategory.companyId = appUser.getCompanyId()
        subCategory.createdOn = new Date()
        subCategory.createdBy = appUser.id
        subCategory.updatedOn = null
        subCategory.updatedBy = 0L
        return subCategory
    }

    private static final String SELECT_NEXT_VAL_ID_SEQ = "SELECT NEXTVAL('doc_sub_category_id_seq') as id"

    /**
     * Get id from dedicated Sub Category id sequence
     * @return - a long variable containing the value of id
     */
    public long getSubCategoryId() {
        List results = executeSelectSql(SELECT_NEXT_VAL_ID_SEQ)
        long subCategoryId = results[0].id
        return subCategoryId
    }

    /**
     * Build appUserSubCategory Object
     * @param parameterMap - GrailsParameterMap from UI
     * @return subCategoryUserMapping - An object of appUserSubCategoryMapping
     */
    private DocSubCategoryUserMapping buildAppUserSubCategoryObject(DocSubCategory subCategory, DocCategoryUserMapping categoryUserMapping) {
        DocSubCategoryUserMapping docSubCategoryUserMapping = new DocSubCategoryUserMapping()
        docSubCategoryUserMapping.userId = categoryUserMapping.userId
        docSubCategoryUserMapping.categoryId = subCategory.categoryId
        docSubCategoryUserMapping.subCategoryId = subCategory.id
        docSubCategoryUserMapping.companyId = docSessionUtil.appSessionUtil.getCompanyId()
        docSubCategoryUserMapping.createdBy = docSessionUtil.appSessionUtil.getAppUser().id
        docSubCategoryUserMapping.createdOn = new Date()
        docSubCategoryUserMapping.updatedBy = 0
        docSubCategoryUserMapping.updatedOn = null
        return docSubCategoryUserMapping
    }
}

