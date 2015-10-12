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

class ExhShowRemittancePurposeActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String LST_REMITTANCE_PURPOSE = "remittancePurposeList"
    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * List remittancePurpose has no pre-condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map outputMap = new HashMap()
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(HAS_ACCESS, new Boolean(true))
            } else {
                outputMap.put(HAS_ACCESS, new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            outputMap.put(HAS_ACCESS, new Boolean(false))
            return outputMap
        }
    }

    /**
     * Retrieving a list of remittancePurposes, it may sort and pagingate the resulting
     * remittancePurpose list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of remittancePurposes with count and page number for pagination
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            if (!parameterMap.rp) {
                parameterMap.rp = 15
            }
            initPager(parameterMap)
            sortColumn = 'name'
            sortOrder = ASCENDING_SORT_ORDER

            List<ExhRemittancePurpose> remittancePurposeList = exhRemittancePurposeCacheUtility.list(this)
            int count=exhRemittancePurposeCacheUtility.count()
            result.put(LST_REMITTANCE_PURPOSE,remittancePurposeList)
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
    private def wrapRemittancePurposeListInGridEntityList(List<ExhRemittancePurpose> remittancePurposeList, int start) {
        List remittancePurposes = []
        try {
            int counter = start + 1
            remittancePurposeList.each { ExhRemittancePurpose remittancePurpose ->
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
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return remittancePurposes
        }
    }

    /**
     * List remittancePurpose has no post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Wrapping remittancePurpose list into FlexiGrid equivalent row representation
     * with page number and total
     *
     * @param obj remittancePurpose list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object remittancePurposeResult) {
        Map output = null
        try {
            Map executeResult= (Map) remittancePurposeResult
            List<ExhRemittancePurpose> remittancePurposeList = (List<ExhRemittancePurpose>) executeResult.get(LST_REMITTANCE_PURPOSE)
            int count = (int) executeResult.get(Tools.COUNT)

            List remittancePurposes = (List)wrapRemittancePurposeListInGridEntityList(remittancePurposeList, start)
            output = [page: pageNumber, total: count, rows: remittancePurposes]

            return [remittancePurposeListJSON: output]
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
        // do nothing
        return null
    }
}
