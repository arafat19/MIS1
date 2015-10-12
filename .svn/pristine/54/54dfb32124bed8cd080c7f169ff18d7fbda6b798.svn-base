package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search company user(appUser) and show specific list of company user for grid
 *  For details go through Use-Case doc named 'SearchAppCompanyUserActionService'
 */
class SearchAppCompanyUserActionService extends BaseService implements ActionIntf {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String FAILURE_MESSAGE = 'Failed to search company user'

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check if user has access to search company user or not
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            // only development role type user can search company user
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get company user(appUser) list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            initSearch(params)  // initialize parameters for flexGrid
            // get list and count of company user(appUser) by search keyword
            LinkedHashMap resultMap = searchAppUserByNameAndIsCompany()
            List<GroovyRowResult> lstAppUser = resultMap.lstAppUser
            int count = (int) resultMap.count
            return [appUserList: lstAppUser, count: count]
        } catch (Exception e) {
            log.error(e.getMessage())
            result = [appUserList: null, count: 0]
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap company user(appUser) list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            // wrap company user list in grid entity
            List lstWrappedAppUser = wrapAppUserList(executeResult.appUserList, start)
            result.put(Tools.PAGE, pageNumber)
            result.put(Tools.TOTAL, executeResult.count)
            result.put(Tools.ROWS, lstWrappedAppUser)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.PAGE, pageNumber)
            result.put(Tools.TOTAL, 0)
            result.put(Tools.ROWS, null)
            return result
        }
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of company user(appUser) in grid entity
     * @param lstAppUser -list of company user(appUser)
     * @param start -starting index of the page
     * @return -list of wrapped company user(appUser)
     */
    private List wrapAppUserList(List<GroovyRowResult> lstAppUser, int start) {
        List lstWrappedAppUser = []
        int counter = start + 1
        for (int i = 0; i < lstAppUser.size(); i++) {
            GroovyRowResult eachRow = lstAppUser[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.username,
                    eachRow.login_id,
                    eachRow.enabled ? Tools.YES : Tools.NO,
                    eachRow.account_locked ? Tools.YES : Tools.NO,
                    eachRow.account_expired ? Tools.YES : Tools.NO,
                    eachRow.company_name
            ]
            lstWrappedAppUser << obj
            counter++
        }
        return lstWrappedAppUser
    }

    /**
     * Get specific list and count of company user(appUser) by search keyword
     * @return -a map containing list and count of company user(appUser)
     */
    private LinkedHashMap searchAppUserByNameAndIsCompany() {
        // query for list
        String strQuery = """
            SELECT au.id, au.username, au.login_id, au.account_locked, au.account_expired, au.enabled, company.name company_name
            FROM app_user au
            LEFT JOIN company ON au.company_id = company.id
            WHERE ${queryType} ilike :query  AND is_company_user = true
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]
        // query for count
        String queryCount = """
            SELECT COUNT(au.id) count
            FROM app_user  au
            LEFT JOIN company ON au.company_id = company.id
            WHERE ${queryType} ilike :query AND is_company_user = true
        """

        List<GroovyRowResult> lstResult = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [lstAppUser: lstResult, count: total]
    }
}
