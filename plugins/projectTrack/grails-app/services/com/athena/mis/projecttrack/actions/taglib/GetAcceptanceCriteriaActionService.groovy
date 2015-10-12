package com.athena.mis.projecttrack.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * build html of acceptance criteria
 * for details go through use-case "GetAcceptanceCriteriaActionService"
 */
class GetAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility

    private static final String ID = "id"
    private static final String BACKLOG_ID = "backlog_id"
    private static final String TYPE_ID = "type_id"
    private static final String TEMPLATE = "template"
    private static final String DEFAULT_TEMPLATE = "<tr>" +
            "<td width='5%'>#:sl#</td>" +
            "<td width='85%'>#:acceptanceCriteria#</td>" +
            "<td width='10%'>#:status#</td>" +
            "</tr>"

    /**
     * Check required parameters
     * @param parameters -attr of tag
     * @param obj -N/A
     * @return -Map containing attr of tag
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map attrMap = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            String strId = attrs.get(ID)
            Long backlogId = attrs.get(BACKLOG_ID) ? Long.valueOf(attrs.get(BACKLOG_ID).toString()) : null
            Long typeId = attrs.get(TYPE_ID) ? Long.valueOf(attrs.get(TYPE_ID).toString()) : null
            String template = attrs.get(TEMPLATE)
            if (!strId || !typeId || !backlogId) {
                return attrMap
            }
            attrMap.put(ID, strId)
            attrMap.put(BACKLOG_ID, backlogId)
            attrMap.put(TYPE_ID, typeId)
            template ? attrMap.put(TEMPLATE, template) : attrMap.put(TEMPLATE, DEFAULT_TEMPLATE)
            attrMap.put(Tools.IS_ERROR, Boolean.FALSE)
            return attrMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            return attrMap
        }
    }

    /**
     * Build acceptance criteria html
     * @param parameters -N/A
     * @param obj -custom map returned from preCondition
     * @return -html for acceptance criteria
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String acceptanceCriteriaHtml = buildAcceptanceCriteriaHtml(preResult)
            return acceptanceCriteriaHtml
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
     * Build acceptance criteria html
     * @return -html
     */
    private String buildAcceptanceCriteriaHtml(Map attrs) {
        String strId = attrs.get(ID)
        long backlogId = Tools.parseLongInput(attrs.get(BACKLOG_ID).toString())
        long typeId = Tools.parseLongInput(attrs.get(TYPE_ID).toString())
        String template = attrs.get(TEMPLATE)

        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity entityType = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(typeId, companyId)

        List<Map> lstAcceptanceCriteria = buildAcceptanceCriteriaList(backlogId, entityType.id, companyId)
        if (lstAcceptanceCriteria.size() <= 0) {
            return Tools.EMPTY_SPACE
        }
        String dataSource = lstAcceptanceCriteria as JSON

        String acceptanceCriteriaHtml = """
            <div class="panel panel-default">
                <div class="panel-heading">${entityType.key}</div>

                <div class="panel-body">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>SL.</th>
                                <th>Criteria</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="${strId}">
                        </tbody>
                    </table>
                </div>
            </div>
        """

        String acceptanceCriteriaScript = """
        <script type='text/javascript'>
            \$(document).ready(function() {
                \$("#${strId}").kendoListView({
                    dataSource: ${dataSource},
                    template: "${template}"
                });
            });
        </script>
        """

        return acceptanceCriteriaHtml + acceptanceCriteriaScript
    }

    /**
     * build acceptance criteria map for list view
     * @param backlogId -id of backlog
     * @param typeId -id of acceptance criteria type
     * @param companyId -id of company
     * @return -list of map for list view
     */
    private List<Map> buildAcceptanceCriteriaList(long backlogId, long typeId, long companyId) {
        List<Map> result = []
        List<PtAcceptanceCriteria> lstAcceptanceCriteria = ptAcceptanceCriteriaService.findAllByCompanyIdAndBacklogIdAndType(companyId, backlogId, typeId)
        for (int i = 0; i < lstAcceptanceCriteria.size(); i++) {
            SystemEntity status = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.read(lstAcceptanceCriteria[i].statusId)
            Map obj = [
                    sl: i + 1,
                    acceptanceCriteria: lstAcceptanceCriteria[i].criteria,
                    status: status.key
            ]
            result << obj
        }
        return result
    }
}
