package com.athena.mis.procurement.actions.proctermsandcondition

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Procurement Terms and condition
 * For details go through Use-Case doc named 'ListProcTermsAndConditionActionService'
 */
class ListProcTermsAndConditionActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load Terms and condition list"
    private static final String TERMS_AND_CONDITION_LIST = "termsAndConditionList"
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get terms & conditions list for grid
     * 1. pull terms & conditions by purchase order.
     * 2. count total terms & conditions
     * 3. wrap terms & conditions for grid entity
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            List<ProcTermsAndCondition> procTermsAndConditionList = []
            int total = 0

            if (purchaseOrderId > 0) {
                procTermsAndConditionList = ProcTermsAndCondition.findAllByPurchaseOrderId(purchaseOrderId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = ProcTermsAndCondition.countByPurchaseOrderId(purchaseOrderId)
            }

            List termsAndConditionListWrap = wrapTermsAndConditionListInGrid(procTermsAndConditionList, start)
            result.put(TERMS_AND_CONDITION_LIST, termsAndConditionListWrap)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
     /**
     * Wrap list of terms & conditions in grid entity
     * @param procTermsAndConditionList -list of terms & conditions object(s)
     * @param start -starting index of the page
     * @return -list of wrapped terms & conditions
     */
    private List wrapTermsAndConditionListInGrid(List<ProcTermsAndCondition> procTermsAndConditionList, int start) {
        List procTermsAndConditions = [] as List
        int counter = start + 1
        ProcTermsAndCondition procTermsAndCondition
        GridEntity object
        AppUser user
        for (int i = 0; i < procTermsAndConditionList.size(); i++) {
            procTermsAndCondition = procTermsAndConditionList[i]
            object = new GridEntity();
            object.id = procTermsAndCondition.id;
            user = (AppUser) appUserCacheUtility.read(procTermsAndCondition.createdBy)
            object.cell = [counter,
                    procTermsAndCondition.details,
                    user.username
            ]
            procTermsAndConditions << object
            counter++
        }
        return procTermsAndConditions;
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Wrap terms & conditions list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        LinkedHashMap receiveResult = (LinkedHashMap) obj
        try {
            int count = (int) receiveResult.get(Tools.COUNT)
            List procTermsAndConditionList = (List) receiveResult.get(TERMS_AND_CONDITION_LIST)
            result = [page: pageNumber, total: count, rows: procTermsAndConditionList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage());
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return failureResult
        }
    }
}