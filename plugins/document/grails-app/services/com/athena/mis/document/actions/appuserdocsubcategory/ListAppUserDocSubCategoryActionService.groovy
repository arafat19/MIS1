package com.athena.mis.document.actions.appuserdocsubcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListAppUserDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load list of member"
    private static final String LST_APP_USER_SUB_CATEGORY = "lstAppUserSubCategory"
    private static final String OBJ_NOT_FOUND = "Object not found"
    private static final String GRID_OBJ = "gridObj"

    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get the list of user for related Sub Category
     *  1.Get the list of user & count for Sub category
     *  2.check if subCategoryId is valid/invalid
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return result - A map of containing all object necessary for buildSuccessResultForUI
     * This method is in transactional boundary and will roll back in case of any exception
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameters = (GrailsParameterMap) params
            initPager(parameters)
            // check required parameters
            if (!parameters.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long subCategoryId = Long.parseLong(parameters.id.toString())
            DocSubCategory subCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
            if (!subCategory) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            Map appUserSubCategory = listOfAppUserSubCategory(subCategory)
            List<GroovyRowResult> lstAppUserSubCategory = appUserSubCategory.lstAppUserSubCategory
            int total = (int) appUserSubCategory.count

            result.put(LST_APP_USER_SUB_CATEGORY, lstAppUserSubCategory)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap list of user for grid
     * @param obj - a map returned from execute method
     * @return result - a map containing necessary information for show page
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<GroovyRowResult> lstAppUserSubCategory = (List) executeResult.get(LST_APP_USER_SUB_CATEGORY)
            int total = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> LstWrapAppUserSubCategory = wrapAppUserSubCategoryList(lstAppUserSubCategory, start)
            Map gridObject = [page: pageNumber, total: total, rows: LstWrapAppUserSubCategory]
            result.put(GRID_OBJ, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list in grid entity list
     * @param lstAppUserSubCategory - list of user for Sub category
     * @param start - starting index of the page
     * @return lstWrapAppUserSubCategory - list of wrap userSubCategory
     */
    private List wrapAppUserSubCategoryList(List<GroovyRowResult> lstAppUserSubCategory, int start) {
        List LstWrapAppUserSubCategory = []
        int counter = start + 1
        for (int i = 0; i < lstAppUserSubCategory.size(); i++) {
            GroovyRowResult appUserSubCategory = lstAppUserSubCategory[i]
            GridEntity obj = new GridEntity()
            obj.id = appUserSubCategory.id
            obj.cell = [
                    counter,
                    appUserSubCategory.id,
                    appUserSubCategory.username,
                    appUserSubCategory.is_sub_category_manager ? Tools.YES : Tools.NO
            ]
            LstWrapAppUserSubCategory << obj
            counter++
        }
        return LstWrapAppUserSubCategory
    }

    private static final String LST_QUERY = """
         SELECT cum.id,au.username,cum.is_sub_category_manager
            FROM app_user au
            LEFT JOIN doc_sub_category_user_mapping cum ON cum.user_id=au.id
            WHERE cum.company_id = :companyId
            AND cum.category_id = :categoryId
            AND cum.sub_category_id = :subCategoryId
         ORDER BY au.username
         LIMIT :resultPerPage OFFSET :start
    """

    private static final String COUNT_QUERY = """
        SELECT COUNT(au.id) count
            FROM app_user au
            LEFT JOIN doc_sub_category_user_mapping cum ON cum.user_id=au.id
            WHERE cum.company_id = :companyId
            AND cum.category_id = :categoryId
            AND cum.sub_category_id = :subCategoryId
    """

    /**
     * Get the list of appUser
     * @param subCategory - DocSubCategory object
     * @return - a map containing list of appUserSubCategory and count
     */
    private Map listOfAppUserSubCategory(DocSubCategory subCategory) {
        long companyId = docSessionUtil.appSessionUtil.getCompanyId()

        Map queryParams = [
                categoryId   : subCategory.categoryId,
                subCategoryId: subCategory.id,
                companyId    : companyId,
                resultPerPage: resultPerPage,
                start        : start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(LST_QUERY, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [lstAppUserSubCategory: lstResult, count: total]
    }
}
