package com.athena.mis.document.actions.invitedmembers

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/*
* Search outstanding invitation for grid
* For details go through Use-Case doc named 'SearchOutstandingInvitationsDocInvitedActionService'
* */
class SearchOutstandingInvitationsDocInvitedActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load outstanding invitation page'
    private static final String GRID_OBJ = 'gridObj'
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String LST_INVITED_MEMBER = 'lstInvitedMember'
    private static final String STR_EXPIRED_LABEL = "<span style='color:red'>Expired</span>"
    private static final String STR_DAYS_LABEL = " day(s)"
    private static final String PENDING = 'Pending'
    private static final String QUERY_STR_ACCEPTED_INVITATION = "AND dim.invitation_accepted_on IS NOT NULL"
    private static final String QUERY_STR_NOT_ACCEPTED_INVITATION = "AND dim.invitation_accepted_on IS NULL"
    private static final String STR_NOT_APPLICALBE_LABEL = "<span>N/A</span>"


    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre - operation
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /*
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * Get category list for grid through specific search
     * get category label value from system configuration
     * @return - A map containing all objects of lstInvitedMember,count of lstInvitedMember for buildSuccessResultForUI
     * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initSearch(params)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            boolean acceptedInvitation = Boolean.parseBoolean(params.acceptedInvitation.toString())
            Map serviceReturn = searchOutstandingInvitations(fromDate, toDate, acceptedInvitation)
            List<GroovyRowResult> lstInvitedMember = serviceReturn.lstInvitedMember
            int count = serviceReturn.count
            result.put(LST_INVITED_MEMBER, lstInvitedMember)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /*
    * Build Success Results for grid in UI
    * @params obj - Map return from execute method
    * Wrap lstInvitedMember list for grid
    * @return a map of containing all object necessary for show page
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List lstInvitedMember = (List) preResult.get(LST_INVITED_MEMBER)
            int count = (int) preResult.get(Tools.COUNT)
            List lstOutstandingInvitedMember = wrapOutstandingInvitations(lstInvitedMember, start)
            Map gridObject = [page: pageNumber, total: count, rows: lstOutstandingInvitedMember]
            result.put(GRID_OBJ, gridObject)
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
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE + categoryLabel)
            return result
        }
    }

    /*
    * Wrap Outstanding Invitation List for Grid
    * @params listOutstandingInvitation - List of Outstanding Invitation List
    * @params start - starting index of the page
    * @return List of wrapped lstOutstandingInvitedMember
    * */

    private List wrapOutstandingInvitations(List<GroovyRowResult> listOutstandingInvitation, int start) {
        List lstOutstandingInvitedMember = []
        int counter = start + 1
        try {
            for (int i = 0; i < listOutstandingInvitation.size(); i++) {
                GroovyRowResult rowResult = listOutstandingInvitation[i]
                Date expiredOnStart = DateUtility.getSqlDate(rowResult.expired_on)
                String invitationSentDate = DateUtility.getDateForSMS(rowResult.invitation_sent_on)
                String strAcceptDt = DateUtility.getDateForSMS(rowResult.invitation_accepted_on)
                GridEntity obj = new GridEntity()
                obj.id = rowResult.id
                obj.cell = [
                        counter,
                        rowResult.id,
                        rowResult.email,
                        rowResult.category_name ? rowResult.category_name : Tools.EMPTY_SPACE,
                        rowResult.sub_category_name ? rowResult.sub_category_name : Tools.EMPTY_SPACE,
                        invitationSentDate,
                        rowResult.invitation_accepted_on ? strAcceptDt : PENDING,
                        strAcceptDt ? STR_NOT_APPLICALBE_LABEL : expiredOnStart.before(new Date()) ? STR_EXPIRED_LABEL : expiredOnStart - new Date() + STR_DAYS_LABEL
                ]
                lstOutstandingInvitedMember << obj
                counter++
            }
            return lstOutstandingInvitedMember

        } catch (Exception e) {
            log.error(e.getMessage())
            return lstOutstandingInvitedMember
        }
    }


    private Map searchOutstandingInvitations(Date fromDate, Date toDate, boolean acceptedInvitation) {
        String queryStrInvitationAccepted = Tools.EMPTY_SPACE
        if(acceptedInvitation)queryStrInvitationAccepted = QUERY_STR_ACCEPTED_INVITATION
        else queryStrInvitationAccepted = QUERY_STR_NOT_ACCEPTED_INVITATION
        String QUERY_FOR_LIST = """
            SELECT DISTINCT(dim.id) AS id, dim.email AS email,dim.invitation_accepted_on, dim.expired_on AS expired_on,
                dim.invitation_sent_on, dc.name AS category_name,
                ARRAY_TO_STRING(ARRAY(SELECT dsc.name FROM doc_sub_category dsc WHERE dsc.id IN(SELECT DISTINCT (sub_category_id) FROM doc_invited_members_category dimc WHERE dim.id = dimc.invited_member_id)), ', ') AS sub_category_name
            FROM doc_invited_members dim
            LEFT JOIN doc_invited_members_category dimc ON dim.id = dimc.invited_member_id
            LEFT JOIN doc_category dc ON dc.id = dimc.category_id
            LEFT JOIN doc_sub_category dsc ON dsc.id = dimc.sub_category_id
            WHERE dim.invitation_sent_on BETWEEN '${fromDate}' AND'${toDate}'
            AND ${queryType} ilike :query
            ${queryStrInvitationAccepted}
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """
        String QUERY_FOR_COUNT = """
            SELECT COUNT(DISTINCT(dim.id)) count
            FROM doc_invited_members dim
            LEFT JOIN doc_invited_members_category dimc ON dim.id = dimc.invited_member_id
            LEFT JOIN doc_category dc ON dc.id = dimc.category_id
            LEFT JOIN doc_sub_category dsc ON dsc.id = dimc.sub_category_id
            WHERE dim.invitation_sent_on BETWEEN '${fromDate}' AND'${toDate}'
            AND ${queryType} ilike :query
        """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage, start: start
        ]
        List<GroovyRowResult> lstInvitedMember = executeSelectSql(QUERY_FOR_LIST, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = (int) resultCount[0][0]
        return [lstInvitedMember: lstInvitedMember, count: count]
    }
}
