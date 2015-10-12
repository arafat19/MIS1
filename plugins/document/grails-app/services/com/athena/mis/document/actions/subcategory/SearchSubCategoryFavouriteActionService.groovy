package com.athena.mis.document.actions.subcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SearchSubCategoryFavouriteActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_FAILURE_MSG_SHOW_BANK = "Failed to load favourite list information page"
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_SUB_CATEGORY_FAVOURITE = "lstSubCategoryFavourite"

    @Autowired
    DocSessionUtil docSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()

        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)                  // initialize params for flexGrid

            Map serviceReturn = subCategoryFavouriteList()
            List<GroovyRowResult> lstFavouriteList = serviceReturn.lstInvitedMember
            int count = serviceReturn.count

            result.put(LST_SUB_CATEGORY_FAVOURITE, lstFavouriteList)
            result.put(Tools.COUNT, Integer.valueOf(count))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return null
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstDistributionPoint = (List) executeResult.get(LST_SUB_CATEGORY_FAVOURITE)
            int count = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List<GroovyRowResult> wrappedDistributionPoint = wrapSubCategoryFavourite(lstDistributionPoint, start)
            Map gridOutput = [page: pageNumber, total: count, rows: wrappedDistributionPoint]
            result.put(GRID_OBJ, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_BANK)
            return result
        }
    }

    /**
     * Wrap list of bankBranch(s) in grid entity
     * @param lstDistributionPoint -list of bankBranch object(s)
     * @param start -starting index of the page
     * @return -list of wrapped distributionPoints
     */
    private List<GroovyRowResult> wrapSubCategoryFavourite(List<GroovyRowResult> lstDistributionPoint, int start) {
        List lstSubcategoryFavourite = []
        try {
            int counter = start + 1
            for (int i = 0; i < lstDistributionPoint.size(); i++) {
                GroovyRowResult favouritesRowResult = lstDistributionPoint[i]
                GridEntity obj = new GridEntity()
                obj.id = favouritesRowResult.id
                obj.cell = [
                        counter,
//                        "${favouritesRowResult.id}",
                        "${favouritesRowResult.category_id}",
                        "${favouritesRowResult.sub_category_id}",
                        "${favouritesRowResult.category_name}",
                        "${favouritesRowResult.sub_category_name}",
                ]
                lstSubcategoryFavourite.add(obj)
                counter = counter + 1
            };
            return lstSubcategoryFavourite;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return lstSubcategoryFavourite;
        }
    }

    private Map subCategoryFavouriteList() {
        AppUser appUser = docSessionUtil.appSessionUtil.appUser
        String QUERY_FOR_LIST = """
            SELECT DISTINCT(docscum.id), dc.id AS category_id, dsc.id AS sub_category_id, dc.name AS category_name, dsc.name AS sub_category_name
            FROM doc_sub_category_user_mapping docscum
            LEFT JOIN doc_category dc ON dc.id = docscum.category_id
            LEFT JOIN doc_sub_category dsc ON dsc.id = docscum.sub_category_id
            WHERE docscum.company_id =:companyId
            AND docscum.user_id = :userId
            AND docscum.is_favourite = TRUE
            AND ${queryType} ilike :query
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT ${resultPerPage} OFFSET ${start}

        """
        String QUERY_FOR_COUNT = """
            SELECT COUNT(docscum.id) count
            FROM doc_sub_category_user_mapping docscum
            LEFT JOIN doc_category dc ON dc.id = docscum.category_id
            LEFT JOIN doc_sub_category dsc ON dsc.id = docscum.sub_category_id
            WHERE docscum.company_id = :companyId
            AND docscum.user_id =:userId
            AND docscum.is_favourite = TRUE
            AND ${queryType} ilike :query
        """

        Map queryParams = [
                query        : Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                companyId: appUser.companyId, userId: appUser.id
        ]
        List<GroovyRowResult> lstInvitedMember = executeSelectSql(QUERY_FOR_LIST, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = (int) resultCount[0][0]
        return [lstInvitedMember: lstInvitedMember, count: count]
    }
}
