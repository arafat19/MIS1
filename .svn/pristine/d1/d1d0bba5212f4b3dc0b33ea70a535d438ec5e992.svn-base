package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * build html of entity notes
 * for details go through use-case "GetEntityNoteTagLibActionService"
 */
class GetEntityNoteTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    EntityNoteService entityNoteService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String ID = "id"
    private static final String ENTITY_ID = "entity_id"
    private static final String ENTITY_TYPE_ID = "entity_type_id"
    private static final String ORDER = "order"
    private static final String TEMPLATE = "template"
    private static final String RESULT_PER_PAGE = "result_per_page"
    private static final String DEFAULT_TEMPLATE = "<blockquote><span>#:note#</span><footer>By #:createdBy# On #:createdOn#</footer></blockquote>"
    private static final Integer DEFAULT_RP = 15
    private static final String DEFAULT_ORDER = "asc"
    private static final String URL = "url"

    /**
     * check if propertyName and propertyValue exists
     * @param parameters - attr of tagLib
     * @param obj - N/A
     * @return - Map containing attr
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map attrMap = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            String strId = attrs.get(ID)
            Long entityId = attrs.get(ENTITY_ID) ? Long.valueOf(attrs.get(ENTITY_ID).toString()) : null
            Long entityTypeId = attrs.get(ENTITY_TYPE_ID) ? Long.valueOf(attrs.get(ENTITY_TYPE_ID).toString()) : null
            String order = attrs.get(ORDER)
            String template = attrs.get(TEMPLATE)
            Integer resultPerPage = attrs.get(RESULT_PER_PAGE) ? Integer.valueOf(attrs.get(RESULT_PER_PAGE).toString()) : null
            String url = attrs.get(URL)
            if (!strId || !entityTypeId) {
                return attrMap
            }
            attrMap.put(ID, strId)
            attrMap.put(URL, url)
            attrMap.put(ENTITY_ID, entityId)
            attrMap.put(ENTITY_TYPE_ID, entityTypeId)
            order ? attrMap.put(ORDER, order) : attrMap.put(ORDER, DEFAULT_ORDER)
            template ? attrMap.put(TEMPLATE, template) : attrMap.put(TEMPLATE, DEFAULT_TEMPLATE)
            resultPerPage ? attrMap.put(RESULT_PER_PAGE, resultPerPage) : attrMap.put(RESULT_PER_PAGE, DEFAULT_RP)
            attrMap.put(Tools.IS_ERROR, Boolean.FALSE)
            return attrMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            return attrMap
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * build entityNote html
     * @param parameters
     * @param obj - preResult from preCondition
     * @return
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String entityNoteHtml = buildEntityNoteHtml(preResult)
            return entityNoteHtml
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * build entity note html
     * @return - html
     */
    private String buildEntityNoteHtml(Map attrs) {
        String strId = attrs.get(ID)
        long entityId = Tools.parseLongInput(attrs.get(ENTITY_ID).toString())
        long entityTypeId = Tools.parseLongInput(attrs.get(ENTITY_TYPE_ID).toString())
        String order = attrs.get(ORDER)
        String template = attrs.get(TEMPLATE)
        Integer resultPerPage = (Integer) attrs.get(RESULT_PER_PAGE)
        String url = attrs.get(URL)

        String strTemplate = template ? """
            template="${template}"
        """ : Tools.EMPTY_SPACE
        String strOrder = order ? "order = '${order}'" : Tools.EMPTY_SPACE
        String strRp = resultPerPage ? "result_per_page = '${resultPerPage}'" : Tools.EMPTY_SPACE

        List<Map> noteList = buildEntityNoteList(entityId.longValue(), entityTypeId.longValue(), order, resultPerPage.intValue())
        String dataSource = noteList as JSON

        String noteHtml = """
        <div>
            <div id="${strId}" entity_id="${entityId}" entity_type_id="${entityTypeId}" url="${url}" ${strTemplate} ${
            strOrder
        } ${strRp} style="border-width: 0"></div>
        <div>
        """

        String noteScript = """
        <script type='text/javascript'>
            \$(document).ready(function() {
                \$("#${strId}").kendoListView({
                    dataSource: ${dataSource},
                    template: "${template}"
                });
            });
        </script>
        """

        return noteHtml + noteScript
    }

    /**
     * build entity note map for list view
     * @param entityId
     * @return - list of map for list view
     */
    private List<Map> buildEntityNoteList(long entityId, long entityTypeId, String order, int resultPerPage) {
        List<Map> result = []
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity entityType = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(entityTypeId, companyId)
        List<EntityNote> lstEntityNote = entityNoteService.findAllByCompanyIdAndEntityTypeIdAndEntityIdOrdered(companyId, entityType.id, entityId, order)
        for (int i = 0; i < lstEntityNote.size(); i++) {
            AppUser appUser = (AppUser) appUserCacheUtility.read(lstEntityNote[i].createdBy)
            Map obj = [
                    createdBy: appUser.username,
                    createdOn: DateUtility.getDateTimeFormatAsString(lstEntityNote[i].createdOn),
                    note: lstEntityNote[i].note
            ]
            result << obj
        }
        return result
    }
}
