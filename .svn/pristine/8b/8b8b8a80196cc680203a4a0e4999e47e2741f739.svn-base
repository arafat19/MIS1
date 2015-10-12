package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.*
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get total information of a bug
 * For details go through Use-Case doc named 'ShowPtBugDetailsActionService'
 */
class ShowPtBugDetailsActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    LinkGenerator grailsLinkGenerator
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility


    private final Logger log = Logger.getLogger(getClass())

    private static final String BUG_DETAILS = "bugMap"
    private static final String NOT_FOUND_MSG = "Bug not found"
    private static final String PT_BUG = 'ptBug'
    private static final String DOWNLOAD_BUG_CONTENT = 'downloadBugContent'
    private static final String CONTENT_MAP_LIST = "contentMapList"
    private static final String DEFAULT_FAILURE_MSG = "Failed to load bug details page"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get Bug object by bug id for html grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                return result
            }
            initPager(parameterMap)                // initialize parameters
            long bugId = Long.parseLong(parameterMap.id.toString())
            PtBug bugObj = ptBugService.read(bugId)
            if(!bugObj){
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            Map bugDetails = buildBugDetailsMap(bugObj)

            if (bugObj.hasAttachment) {
                List<GroovyRowResult> contentList = getContentList(bugId) // get content list
                List lstContent = []
                for (int i = 0; i < contentList.size(); i++) {
                    GroovyRowResult eachRow = contentList[i]
                    String link = grailsLinkGenerator.link(controller: PT_BUG, action: DOWNLOAD_BUG_CONTENT, absolute: true, params: [entityId: bugId])
                    Map contentMap = [
                            sl: i + 1,
                            fileName: eachRow.file_name,
                            link: link
                    ]
                    lstContent << contentMap
                }
                result.put(CONTENT_MAP_LIST, lstContent as JSON)
            }
            result.put(BUG_DETAILS, bugDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }
    /**
     * Map of bug details
     * @param bug - bug object
     * @return - a map containing all fields to display bug details
     */
    private Map buildBugDetailsMap(PtBug bug) {
        PtProject project = (PtProject) ptProjectCacheUtility.read(bug.projectId)
        PtModule module = (PtModule) ptModuleCacheUtility.read(bug.moduleId)
        PtSprint sprint = ptSprintService.read(bug.sprintId)
        PtBacklog backlog = ptBacklogService.read(bug.backlogId)
        SystemEntity type = (SystemEntity) ptBugTypeCacheUtility.read(bug.type)
        SystemEntity status = (SystemEntity) ptBugStatusCacheUtility.read(bug.status)
        SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(bug.severity)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(bug.createdBy)
        AppUser updatedBy = (AppUser) appUserCacheUtility.read(bug.updatedBy)
        AppUser fixedBy = (AppUser) appUserCacheUtility.read(bug.fixedBy)
        AppUser closedBy = (AppUser) appUserCacheUtility.read(bug.closedBy)
        AppUser owner = (AppUser) appUserCacheUtility.read(bug.ownerId)
        String loggedUser = ptSessionUtil.appSessionUtil.getAppUser().username
        String actor = backlog ? backlog.actor : Tools.EMPTY_SPACE

        Map bugDetails = [
                id: bug.id,
                project: project ? project.name : Tools.EMPTY_SPACE,
                module: module.name,
                sprint: sprint ? sprint.name : Tools.EMPTY_SPACE,
                actor: actor,
                backlogId: bug.backlogId,
                useCaseId: backlog ? backlog.useCaseId : Tools.EMPTY_SPACE,
                owner: owner ? owner.username : Tools.EMPTY_SPACE,
                title: bug.title,
                type: type.key,
                status: status.key,
                severity: severity.key,
                stepToReproduce: bug.stepToReproduce,
                createdOn: DateUtility.getDateTimeFormatAsString(bug.createdOn),
                note: bug.note,
                createdBy: createdBy.username,
                updatedBy: updatedBy ? updatedBy.username : Tools.EMPTY_SPACE,
                loggedUser: loggedUser,
                fixedOn: DateUtility.getDateTimeFormatAsString(bug.fixedOn),
                fixedBy: fixedBy ? fixedBy.username:Tools.EMPTY_SPACE,
                closedOn: DateUtility.getDateTimeFormatAsString(bug.closedOn),
                closedBy: closedBy ? closedBy.username:Tools.EMPTY_SPACE,
                sprintId: bug.sprintId
        ]
        return bugDetails
    }
    private static final String ENTITY_CONTENT_SELECT_QUERY = """
            SELECT id, caption, file_name FROM entity_content
            WHERE entity_type_id =:entityTypeId AND
                  entity_id =:bugId
            ORDER BY caption
    """
    /**
     * Get bug Content List
     * 1. Get bug content list by executeSelectSql method by sending raw query string(ENTITY_CONTENT_SELECT_QUERY) and queryParams
     * @param bugId - bug id from execute method
     * @return - a list of budget content
     */
    private List<GroovyRowResult> getContentList(long bugId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(Bug) object
        SystemEntity contentEntityTypeBug = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PT_BUG, companyId)

        Map queryParam = [
                entityTypeId: contentEntityTypeBug.id,
                bugId: bugId
        ]
        List<GroovyRowResult> bugContentList = executeSelectSql(ENTITY_CONTENT_SELECT_QUERY, queryParam)
        return bugContentList
    }
}
