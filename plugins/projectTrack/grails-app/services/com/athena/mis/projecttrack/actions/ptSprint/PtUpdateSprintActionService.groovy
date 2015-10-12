package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.ExecutorService

/**
 *  Update ptSprint object and grid data
 *  For details go through Use-Case doc named 'PtUpdateSprintActionService'
 */
class PtUpdateSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    PtBugService ptBugService
    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_UPDATE_SUCCESS_MSG = "Sprint has been successfully updated"
    private static final String BACKLOG_FAILURE_MESSAGE_COMPLETED = " task is not completed"
    private static final String BACKLOG_FAILURE_MESSAGE = " task is not accepted"
    private static final String BUG_FAILURE_MESSAGE_FIXED = " bug is not fixed"
    private static final String BUG_FAILURE_MESSAGE = " bug is not closed"
    private static final String SPRINT_UPDATE_FAILURE_MSG = "Sprint has not been updated"
    private static final String DATE_EXIST_MESSAGE = "This date-range over-laps another sprint"
    private static final String HAS_ACTIVE_MSG = "The selected project has another active sprint"
    private static final String ACTIVE_ERROR_MSG = "Only closed sprint can be inactive"
    private static final String TRANSACTION_CODE = "PtUpdateSprintActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail template not found"
    private static final String MAIL_SUBJECT_POSTFIX = "Sprint : "
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String SPRINT = "ptSprint"
    private static final String SPRINT_STATUS_COMPLETED = "statusCompletedId"
    private static final String OBJ_NOT_FOUND = "Selected ptSprint not found"
    private static final String DATE_FORMAT = "dd-MMM-yy"
    private static final String TO = " To "
    private static final String FROM = " From "

    /**
     * Get parameters from UI and build ptSprint object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id || !parameterMap.statusId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(parameterMap.id.toString())
            long sprintStatusId = Long.parseLong(parameterMap.statusId.toString())
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity ptSprintStatusCompleted = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.COMPLETED_RESERVED_ID, companyId)
            SystemEntity ptSprintStatusClosed = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.CLOSED_RESERVED_ID, companyId)

            PtSprint oldSprint = ptSprintService.read(sprintId)
            // check whether selected  object exists or not
            if (!oldSprint) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            boolean isActive = parameterMap.isActive
            if (oldSprint.isActive && !isActive) {
                if (sprintStatusId != ptSprintStatusClosed.id) {
                    result.put(Tools.MESSAGE, ACTIVE_ERROR_MSG)
                    return result
                }
            }
            PtSprint sprint = buildSprintObject(parameterMap, oldSprint)
            if (sprint.isActive) {
                int count = ptSprintService.countByProjectIdAndIsActiveAndIdNotEqual(sprint.projectId, true, sprint.id)
                if (count > 0) {
                    result.put(Tools.MESSAGE, HAS_ACTIVE_MSG)
                    return result
                }
            }
            //check duplicate sprint date range
            int countOverLap = ptSprintService.countDateRangeOverLapForEdit(sprint.id, sprint.startDate, sprint.endDate, sprint.projectId)
            if (countOverLap > 0) {
                result.put(Tools.MESSAGE, DATE_EXIST_MESSAGE)
                return result
            }

            /* Sprint Completed
             * Pre condition -
               * i) Task(s) - Completed/Accepted
               * ii) Bug(s) - Fixed/Closed
             * Post Condition -
               * send mail to SQA & ADMIN for further process
            */
            if (sprintStatusId == ptSprintStatusCompleted.id) {
                Map returnResult = checkStatusOnSprintCompleted(sprintId, companyId)
                Boolean isError = (Boolean) returnResult.isError
                if (isError.booleanValue()) {
                    result.put(Tools.MESSAGE, returnResult.message)
                    return result
                }
            }

            /* Sprint Closed - Pre condition
              * i) Task(s) - Accepted
              * ii) Bug(s) - Closed
            */
            if (sprintStatusId == ptSprintStatusClosed.id) {
                Map returnResult = checkStatusOnSprintClosed(sprintId, companyId)
                Boolean isError = (Boolean) returnResult.isError
                if (isError.booleanValue()) {
                    result.put(Tools.MESSAGE, returnResult.message)
                    return result
                }
                sprint.isActive = false
            }

            result.put(SPRINT, sprint)
            result.put(SPRINT_STATUS_COMPLETED, ptSprintStatusCompleted.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)

            return result
        }
    }

    /**
     * Update ptSprint object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            PtSprint sprint = (PtSprint) preResult.get(SPRINT)
            long statusCompletedId = (long) preResult.get(SPRINT_STATUS_COMPLETED)
            ptSprintService.update(sprint)
            // mail send only when sprint status is Completed
            if (sprint.statusId == statusCompletedId) {
                sendMail(sprint)    // send mail
            }
            result.put(SPRINT, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
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
     * Show updated ptSprint object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            PtSprint sprint = (PtSprint) executeResult.get(SPRINT)
            SystemEntity status = (SystemEntity) ptSprintStatusCacheUtility.read(sprint.statusId)
            int backlogCount = ptBacklogService.countBySprintIdAndCompanyId(sprint.id, sprint.companyId)
            int bugCount = ptBugService.countBySprintIdAndCompanyId(sprint.id, sprint.companyId)
            GridEntity object = new GridEntity()
            object.id = sprint.id
            object.cell = [
                    Tools.LABEL_NEW,
                    sprint.id,
                    sprint.name,
                    status.key,
                    sprint.isActive ? Tools.YES : Tools.NO,
                    backlogCount,
                    bugCount
            ]
            result.put(Tools.MESSAGE, SPRINT_UPDATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        }
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build ptSprint object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldSprint -old ptSprint object
     * @return -updated ptSprint object
     */
    private PtSprint buildSprintObject(GrailsParameterMap parameterMap, PtSprint oldSprint) {
        PtSprint sprint = new PtSprint(parameterMap)
        oldSprint.projectId = sprint.projectId
        oldSprint.statusId = sprint.statusId
        oldSprint.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        oldSprint.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        PtProject ptProject = (PtProject) ptProjectCacheUtility.read(sprint.projectId)
        oldSprint.name = ptProject.name + FROM + oldSprint.startDate.format(DATE_FORMAT) + TO + oldSprint.endDate.format(DATE_FORMAT)
        oldSprint.isActive = sprint.isActive
        return oldSprint
    }

    /**
     * @param sprintId - sprint id
     * @param companyId - company id
     * @return - a map containing isError true/false & if false then relative message
     */
    private Map checkStatusOnSprintCompleted(long sprintId, long companyId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        //checking how many tasks are remaining to be completed
        SystemEntity statusCompleted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.COMPLETED_RESERVED_ID, companyId)
        SystemEntity statusAccepted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, companyId)

        String count_backlog = """
                SELECT COUNT(id) FROM pt_backlog
                 WHERE sprint_id =:sprintId
                 AND status_id NOT IN (${statusCompleted.id}, ${statusAccepted.id});
            """
        Map queryParams = [
                sprintId: sprintId
        ]
        List countResults = executeSelectSql(count_backlog, queryParams)
        int queryCounts = countResults[0].count
        if (queryCounts > 0) {
            result.put(Tools.MESSAGE, queryCounts + BACKLOG_FAILURE_MESSAGE_COMPLETED)
            return result
        }

        //checking how many bugs are remaining to be fixed
        SystemEntity bugStatusFixed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.FIXED_RESERVED_ID, companyId)
        SystemEntity bugStatusClosed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID, companyId)

        String count_bug = """
                SELECT COUNT(id) FROM pt_bug
                 WHERE sprint_id =:sprintId
                 AND status NOT IN (${bugStatusFixed.id}, ${bugStatusClosed.id});
            """

        Map queryParam = [
                sprintId: sprintId
        ]
        List countResultForBug = executeSelectSql(count_bug, queryParam)
        int queryCountForBug = countResultForBug[0].count
        if (queryCountForBug > 0) {
            result.put(Tools.MESSAGE, queryCountForBug + BUG_FAILURE_MESSAGE_FIXED)
            return result
        }
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    private static final String COUNT_BACKLOG_ON_SPRINT_ACCEPTED = """
        SELECT COUNT(id) FROM pt_backlog WHERE sprint_id =:sprintId AND status_id <>:backlogStatusId
	"""
    private static final String COUNT_BUG_ON_SPRINT_ACCEPTED = """
        SELECT COUNT(id) FROM pt_bug WHERE sprint_id =:sprintId AND status <>:bugStatusId
	"""
    /**
     * @param sprintId
     * @param companyId
     * @return - a map containing isError true/false & if false then relative message
     */
    private Map checkStatusOnSprintClosed(long sprintId, long companyId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        //checking how many tasks are remaining to be accepted
        SystemEntity ptBacklogStatus = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, companyId)
        Map queryParams = [
                sprintId: sprintId,
                backlogStatusId: ptBacklogStatus.id
        ]
        List countResults = executeSelectSql(COUNT_BACKLOG_ON_SPRINT_ACCEPTED, queryParams)
        int queryCounts = countResults[0].count
        if (queryCounts > 0) {
            result.put(Tools.MESSAGE, queryCounts + BACKLOG_FAILURE_MESSAGE)
            return result
        }
        //checking how many bugs are remaining to be closed
        SystemEntity ptBugStatus = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID, companyId)
        Map queryParam = [
                sprintId: sprintId,
                bugStatusId: ptBugStatus.id
        ]
        List countResultForBug = executeSelectSql(COUNT_BUG_ON_SPRINT_ACCEPTED, queryParam)
        int queryCountForBug = countResultForBug[0].count
        if (queryCountForBug > 0) {
            result.put(Tools.MESSAGE, queryCountForBug + BUG_FAILURE_MESSAGE)
            return result
        }
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result

    }

    /**
     * 1. get mail template by transaction code
     * 2. get user email addresses
     * 3. build mail body template and subject
     * 4. get purchase order report as attachment
     * 5. send mail
     * @param sprint -object of ProcPurchaseOrder
     * @return -a string containing message or null value depending on method success
     */
    private String sendMail(PtSprint sprint) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, sprint.companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        LinkedHashMap chkMailMap = checkMail(appMail, sprint.projectId)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return chkMailMap.get(Tools.MESSAGE)
        }
        String strCreatedOn = DateUtility.getDateTimeFormatAsString(sprint.startDate)
        String completedOn = DateUtility.getDateTimeFormatAsString(new Date())
        int tasks = ptBacklogService.countBySprintIdAndCompanyId(sprint.id, sprint.companyId)
        int bugs = ptBugService.countBySprintIdAndCompanyId(sprint.id, sprint.companyId)
        Map paramsBody = [
                id: sprint.id,
                name: sprint.name,
                startDate: strCreatedOn,
                completedOn: completedOn,
                totalTask: tasks,
                totalBug: bugs
        ]
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable templateBody = engine.createTemplate(appMail.body).make(paramsBody)
        appMail.body = templateBody.toString()
        appMail.subject = appMail.subject + Tools.PARENTHESIS_START + MAIL_SUBJECT_POSTFIX + sprint.name + Tools.PARENTHESIS_END
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)

        executeMail(appMail, userMailAddress)
        return null
    }

    /**
     * 1. check role ids of user
     * 2. get user mail addresses
     * @param appMail -object of AppMail
     * @param projectId -id of Project
     * @return -a map containing user mail addresses, isError(true/false) and relevant message
     */
    private LinkedHashMap checkMail(AppMail appMail, long projectId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)
        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }
        List<String> mailAddress = getUserMailAddress(roleIds, projectId)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }
        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * list of user mail addresses
     * @param roleIds -a string containing the role ids of user
     * @param projectId -id of Project
     * @return -a list containing the list of user mail addresses
     */
    public List<String> getUserMailAddress(String roleIds, long projectId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PT_PROJECT, companyId)

        String query = """
            SELECT au.login_id FROM app_user_entity app
            LEFT JOIN app_user au ON au.id= app.app_user_id
                WHERE app.entity_id = :projectId AND
                      app.entity_type_id = :project AND
                      app.app_user_id IN
                          (SELECT ur.user_id FROM user_role ur
                                WHERE ur.role_id IN
                                    (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                        AND company_id = :companyId))
        """

        Map queryParams = [
                projectId: projectId,
                project: appUserSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)

        List<String> userMailAddress = []
        for (int i = 0; i < result.size(); i++) {
            userMailAddress << result[i][0]
        }
        return userMailAddress
    }

    /**
     * Send mail
     * @param appMail -object of AppMail
     * @param userMailAddress -list of user mail addresses
     * @param PtSprint - object of PtSprint
     */
    private void executeMail(AppMail appMail, List<String> userMailAddress) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            mailService.sendMail {
                to userMailAddress
                subject appMail.subject
                html appMail.body
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }
}
