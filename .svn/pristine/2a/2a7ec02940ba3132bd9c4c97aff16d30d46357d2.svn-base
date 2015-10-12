package com.athena.mis.projecttrack.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * build html of backlog list by module id
 * for details go through use-case "GetBacklogListTagLibActionService"
 */
class GetBacklogListTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    PtBacklogService ptBacklogService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility

    private static final String MODULE_ID = "module_id"
    private static final String PRIORITY = "Priority: "
    private static final String SO_THAT = " so that "

    /**
     * Check required parameters
     * @param parameters -attr of tag
     * @param obj -N/A
     * @return -Map containing attr of tag
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map attrs = (Map) parameters
        try {
            attrs.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!attrs.get(MODULE_ID)) {
                attrs.put(MODULE_ID, new Long(0L))
            } else {
                attrs.put(MODULE_ID, Long.valueOf(attrs.get(MODULE_ID).toString())) // convert string to Long
            }
            attrs.put(Tools.IS_ERROR, Boolean.FALSE)
            return attrs
        } catch (Exception ex) {
            log.error(ex.getMessage())
            attrs.put(Tools.IS_ERROR, Boolean.TRUE)
            return attrs
        }
    }

    /**
     * Build backlog html
     * @param parameters -N/A
     * @param obj -custom map returned from preCondition
     * @return -html for list of backlog
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String backlogHtml = buildBacklogHtml(preResult)
            return backlogHtml
        } catch (Exception ex) {
            log.error(ex.getMessage())
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

    /**
     * Build backlog html
     * @return -html
     */
    private String buildBacklogHtml(Map attrs) {
        String divId = attrs.id
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List<Map> lstBacklog = buildBacklogList(attrs.module_id, companyId)
        String strAttributes = Tools.EMPTY_SPACE
        attrs.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String html = "<div ${strAttributes}></div>"
        String htmlPager = "<div name = 'pager' id = 'pager' class='k-pager-wrap' ></div>"

        String backlogHtml = """
            <script type="text/x-kendo-template" id="templateBacklogs">
                        <div class="popover" id="#:id#">
                            <h3 class="popover-title">#:title#
                                <button class="btn btn-default pull-right" onclick="addBacklogToSprint(#:id#);">
                                    <span class="fa fa-plus"></span>
                                </button>
                            </h3>
                            <div class="popover-content">
                                <p>#:description#</p>
                            </div>
                        </div>
                </script>

                <script type="text/javascript">
                    \$(document).ready(function () {
                          var dataSourceBacklog = new kendo.data.DataSource({
                            data:${lstBacklog as JSON},
                            pageSize: 5
                        });
                        \$("#pager").kendoPager({
                            dataSource: dataSourceBacklog
                        });
                        \$("#${divId}").kendoListView({
                            dataSource: dataSourceBacklog,
                            template: kendo.template(\$("#templateBacklogs").html()),
                        });
                    });
            </script>
        """

        String style = """
                <style>
                    #${divId}{
                        overflow-y:auto;
                    }
                    #${divId} .popover {
                        display:block;
                        position: relative;
                        margin:3px;
                        max-width:none;
                    }
                    #${divId} .popover > h3 > button {
                        cursor:pointer;
                        padding:0px 7px;
                    }
                </style>
             """

        return html + htmlPager + backlogHtml + style
    }

    /**
     * build backlog map for list view
     * @param moduleId -id of module
     * @param companyId -id of company
     * @return -list of map for list view
     */
    private List<Map> buildBacklogList(Long moduleId, long companyId) {
        List<Map> result = []
        if (moduleId <= 0) return result
        List<PtBacklog> lstBacklog = ptBacklogService.findAllByModuleIdAndSprintIdAndCompanyId(moduleId, 0, companyId)
        for (int i = 0; i < lstBacklog.size(); i++) {
            PtBacklog backlog = lstBacklog[i]
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            Map obj = [
                    id: backlog.id,
                    title: PRIORITY + priority.key,
                    description: backlog.purpose + SO_THAT + backlog.benefit
            ]
            result << obj
        }
        return result
    }
}
