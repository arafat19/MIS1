package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of AcceptanceCriteria for grid
 *  For details go through Use-Case doc named 'ListPtAcceptanceCriteriaForMyBacklogActionService'
 */
class ListPtAcceptanceCriteriaForMyBacklogActionService extends BaseService implements ActionIntf {

    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_ACCEPTANCE_CRITERIA_FAILURE_MESSAGE = "Failed to load acceptance criteria page"
    private static final String LST_ACCEPTANCE_CRITERIA = "lstAcceptanceCriteria"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get AcceptanceCriteria list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())

            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            initPager(parameterMap) // initialize parameters for flexGrid
            // get AcceptanceCriteria List from map
            List<PtAcceptanceCriteria> lstAcceptanceCriteria = ptAcceptanceCriteriaService.findAllByCompanyIdAndBacklogId(this, companyId, backlogId)
            int count = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(companyId, backlogId)
            // get total count from map
            result.put(LST_ACCEPTANCE_CRITERIA, lstAcceptanceCriteria)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_ACCEPTANCE_CRITERIA_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap AcceptanceCriteria list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<PtAcceptanceCriteria> lstAcceptanceCriteria = (List<PtAcceptanceCriteria>) executeResult.get(LST_ACCEPTANCE_CRITERIA)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedAcceptanceCriteria = wrapAcceptanceCriteria(lstAcceptanceCriteria, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedAcceptanceCriteria]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_ACCEPTANCE_CRITERIA_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_ACCEPTANCE_CRITERIA_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_ACCEPTANCE_CRITERIA_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of AcceptanceCriteria in grid entity
     * @param lstAcceptanceCriteria -list of AcceptanceCriteria object(s)
     * @param start -starting index of the page
     * @return -list of wrapped AcceptanceCriteria
     */
    private List wrapAcceptanceCriteria(List<PtAcceptanceCriteria> lstAcceptanceCriteria, int start) {
        List lstWrappedAcceptanceCriteria = []
        int counter = start + 1
        for (int i = 0; i < lstAcceptanceCriteria.size(); i++) {
            PtAcceptanceCriteria acceptanceCriteria = lstAcceptanceCriteria[i]
            SystemEntity accCriteriaStatus = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.read(acceptanceCriteria.statusId)
            SystemEntity accCriteriaType = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.read(acceptanceCriteria.type)
            GridEntity obj = new GridEntity()
            obj.id = acceptanceCriteria.id
            obj.cell = [
                    counter,
                    accCriteriaType.key,
                    acceptanceCriteria.criteria,
                    accCriteriaStatus.key,
                    DateUtility.getDateTimeFormatAsString(acceptanceCriteria.completedOn)
            ]
            lstWrappedAcceptanceCriteria << obj
            counter++
        }
        return lstWrappedAcceptanceCriteria
    }
}
