package com.athena.mis.projecttrack.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBugSeverityCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * build html of bug list
 * for details go through use-case "GetBugListTagLibActionService"
 */
class GetBugListTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    PtBugService ptBugService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility

    private static final String MODULE_ID = "module_id"
    private static final String SEVERITY = "Severity: "

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
     * Build BUG html
     * @param parameters -N/A
     * @param obj -custom map returned from preCondition
     * @return -html for list of bug
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String bugHtml = buildBugHtml(preResult)
            return bugHtml
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
     * Build bug html
     * @return -html
     */
    private String buildBugHtml(Map attrs) {
        String divId = attrs.id
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List<Map> lstBug = buildBugList(attrs.module_id, companyId)
        String strAttributes = Tools.EMPTY_SPACE
        attrs.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String html = "<div ${strAttributes}></div>"
        String htmlPager = "<div name = 'pager' id = 'pager' class='k-pager-wrap' ></div>"

        String bugHtml = """
            <script type="text/x-kendo-template" id="templateBugs">
                        <div class="popover" id="#:id#">
                            <h3 class="popover-title">#:severity#
                                <button class="btn btn-default pull-right" onclick="addBugToSprint(#:id#);">
                                    <span class="fa fa-plus"></span>
                                </button>
                            </h3>
                            <div class="popover-content">
                                <p>#:title#</p>
                            </div>
                        </div>
                </script>

                <script type="text/javascript">
                    \$(document).ready(function () {
                          var dataSourceBug = new kendo.data.DataSource({
                            data:${lstBug as JSON},
                            pageSize: 5
                        });
                        \$("#pager").kendoPager({
                            dataSource: dataSourceBug
                        });
                        \$("#${divId}").kendoListView({
                            dataSource: dataSourceBug,
                            template: kendo.template(\$("#templateBugs").html()),
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

        return html + htmlPager + bugHtml + style
    }

    /**
     * build bug map for list view
     * @param moduleId -id of module
     * @param companyId -id of company
     * @return -list of map for list view
     */
    private List<Map> buildBugList(Long moduleId, long companyId) {
        List<Map> result = []
        if (moduleId <= 0) return result
        List<PtBug> lstBug = ptBugService.findAllByModuleIdAndSprintIdAndCompanyId(moduleId, 0, companyId)
        for (int i = 0; i < lstBug.size(); i++) {
            PtBug bug = lstBug[i]
            SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(bug.severity)
            Map obj = [
                    id: bug.id,
                    severity: SEVERITY + severity.key,
                    title: bug.title
            ]
            result << obj
        }
        return result
    }
}

