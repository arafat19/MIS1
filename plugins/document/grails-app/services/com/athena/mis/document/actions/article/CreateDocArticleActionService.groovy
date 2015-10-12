package com.athena.mis.document.actions.article

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocArticle
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocArticleService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateDocArticleActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DOC_ARTICLE = 'docArticle'
    private static final String CREATE_SUCCESS_MESSAGE = 'Article has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved article'
    private static final String DETAILS_LENGTH_ERROR = "Your Article is too large to saved."
    private static final String NOT_FOUND = "Please Select "
    private static final String DEFAULT_CATEGORY = "Category"
    private static final String CATEGORY_LABEL = "categoryLabel"
    private static final String DEFAULT_SUB_CATEGORY = "Sub Category"
    private static final String SUB_CATEGORY_LABEL = "subCategoryLabel"

    DocArticleService docArticleService

    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /**
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * Check Article length
     * @return - A map of containing isError message
     * */

    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = DEFAULT_CATEGORY
        String subCategoryLabel = DEFAULT_SUB_CATEGORY
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()
            String categoryId = params.categoryId.toString()
            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }
            if (categoryId == null || categoryId.equalsIgnoreCase('')) {
                result.put(Tools.MESSAGE, NOT_FOUND + categoryLabel)
                return result
            }
            String subCategoryId = params.subCategoryId.toString()
            SysConfiguration sysConfiguration1 = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, companyId)
            if (sysConfiguration1) {
                subCategoryLabel = sysConfiguration1.value
            }
            if (subCategoryId == null || subCategoryId.equalsIgnoreCase('')) {
                result.put(Tools.MESSAGE, NOT_FOUND + subCategoryLabel)
                return result
            }
            String details = params.details.toString()
            if (details.length() > 15000) {
                result.put(Tools.MESSAGE, DETAILS_LENGTH_ERROR)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Create Article object to DB
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * @return - A map of saved article object or error messages
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            DocArticle article = buildArticle(parameterMap)     //build Article object

            docArticleService.create(article)
            result.put(DOC_ARTICLE, article)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CREATE_FAILURE_MESSAGE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * @params obj - map from execute method
     * Show newly created article to grid
     * Show success message
     * @return - A map containing all necessary object for show
     * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()

        try {
            Map preResult = (Map) obj
            DocArticle article = (DocArticle) preResult.get(DOC_ARTICLE)
            GridEntity object = new GridEntity()
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(article.categoryId)
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(article.subCategoryId)
            object.id = article.id
            object.cell = [
                    Tools.LABEL_NEW,
                    article.id,
                    Tools.makeDetailsShort(article.title, 60),
                    category.name,
                    subCategory.name
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Build Failure result in case of any error
     * @params obj - A map from execute method
     * @return - A map containing all necessary message for show
     * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build Article object for create
   * */

    private DocArticle buildArticle(GrailsParameterMap parameterMap) {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        DocArticle article = new DocArticle(parameterMap)
        article.companyId = appUser.getCompanyId()
        article.createdBy = appUser.id
        article.createdOn = new Date()
        article.updatedBy = 0L
        article.updatedOn = null
        return article
    }
}
