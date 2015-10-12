package com.athena.mis.exchangehouse.actions.remittancepurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhSearchRemittancePurposeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String LST_REMITTANCE_PURPOSE="remittancePurposeList"

    public Object executePreCondition(Object parameters, Object obj) {
        Map outputMap = new HashMap()
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            outputMap.put("hasAccess", new Boolean(false))
            return outputMap
        }
    }

    Object execute(Object params, Object obj = null) {
        LinkedHashMap result=new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            if ((!parameterMap.sortname) || (parameterMap.sortname.toString().equals('id'))) {
                // if no sort name then sort by name/asc
                parameterMap.sortname = 'name'
                parameterMap.sortorder = 'asc'
            }
            initSearch(parameterMap)
            Map searchResult= exhRemittancePurposeCacheUtility.search(queryType,query,this)
            List<ExhRemittancePurpose> lstExhRemittancePurpose= searchResult.list
            int count= searchResult.count
            result.put(LST_REMITTANCE_PURPOSE,lstExhRemittancePurpose)
            result.put(Tools.COUNT,count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [remittancePurposeList: null, count: 0]
            return result
        }
    }

    /**
     * Wrapping each RemittancePurpose entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param remittancePurposeList List of all RemittancePurposes
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List<ExhRemittancePurpose> wrapRemittancePurposeListInGridEntityList(List<ExhRemittancePurpose> remittancePurposeList, int start) {

        List<ExhRemittancePurpose> remittancePurposes = []
        int counter = start + 1
        remittancePurposeList.each { remittancePurpose ->
            GridEntity obj = new GridEntity()
            obj.id = remittancePurpose.id
            obj.cell = [
                    counter,
                    remittancePurpose.id,
                    remittancePurpose.name,
                    remittancePurpose.code? remittancePurpose.code : Tools.EMPTY_SPACE
            ]
            remittancePurposes << obj
            counter++
        }
        return remittancePurposes
    }

    /**
     * List remittancePurpose has not post-condition
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Wrapping remittancePurpose list retrieved from search into FlexiGrid equivalent
     * row representation with page number and total
     *
     * @param obj remittancePurpose list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap output
        try {
            Map executeResult=(Map) obj
            List<ExhRemittancePurpose> remittancePurposeList= (List<ExhRemittancePurpose>)executeResult.get(LST_REMITTANCE_PURPOSE)
            int count= (int)executeResult.get(Tools.COUNT)
//            List<ExhRemittancePurpose> remittancePurposeList = (List<ExhRemittancePurpose>) remittancePurposeResult.remittancePurposeList
         //   int count = (int) remittancePurposeResult.count
            List<ExhRemittancePurpose> remittancePurposes = wrapRemittancePurposeListInGridEntityList(remittancePurposeList, start)
            output = [page: pageNumber, total: count, rows: remittancePurposes]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}