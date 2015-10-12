package com.athena.mis.projecttrack.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/**
 * build html of backlog idea
 * for details go through use-case "GetBacklogIdeaTagLibActionService"
 */
class GetBacklogIdeaTagLibActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService

    private Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_ID = 'backlogId'
    private static final String ID = 'id'
    private static final String SPAN_START = '<span '
    private static final String SPAN_END = '</span>'
    private static final String AS_A = 'As a : '
    private static final String I_WANT_TO = 'I want to : '
    private static final String SO_THAT = 'So that : '
    private static final String BREAK = '<br>'
    private static final String EMPTY_SPACE = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'

    /** Build a map containing properties of backlog
     *  Set values of properties
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map backlogAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            backlogAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value
            String strBacklogId = attrs.get(BACKLOG_ID)
            String strId = attrs.get(ID)
            if (!strBacklogId || !strId) {
                return backlogAttributes
            }
            long backlogId = Long.parseLong(strBacklogId)
            backlogAttributes.put(BACKLOG_ID, new Long(backlogId))  // set backlogId
            backlogAttributes.put(ID, strId)
            backlogAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return backlogAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            backlogAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return backlogAttributes
        }
    }

    /** Build the html for backlog idea
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition
     * @return - html string for backlog idea
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map backlogAttributes = (Map) obj
            Long backlogId = (Long) backlogAttributes.get(BACKLOG_ID)
            String strId = backlogAttributes.get(ID)
            String html = buildHtmlForBacklogIdea(backlogId, strId)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /** Generate the html for backlog idea
     * @param backlogId - id of backlog
     * @return - html string for backlog idea
     */
    private String buildHtmlForBacklogIdea(long backlogId, String strId) {
        String html = Tools.EMPTY_SPACE
        if (backlogId <= 0) {
            return html
        }
        PtBacklog backlog = ptBacklogService.read(backlogId)
        html = SPAN_START + "id='${strId}'>" +
                AS_A + backlog.actor + BREAK +
                I_WANT_TO + BREAK +
                EMPTY_SPACE + backlog.purpose + BREAK +
                SO_THAT + BREAK +
                EMPTY_SPACE + backlog.benefit +
                SPAN_END
        return html
    }
}
