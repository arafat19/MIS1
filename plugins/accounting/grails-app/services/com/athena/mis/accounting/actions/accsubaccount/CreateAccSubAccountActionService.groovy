package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.service.AccSubAccountService
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create AccSubAccount object and show on grid list
 *  For details go through Use-Case doc named 'CreateAccSubAccountActionService'
 */
class CreateAccSubAccountActionService extends BaseService implements ActionIntf {

    AccSubAccountService accSubAccountService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String FAILURE_MESSAGE = 'Sub account could not be saved'
    private static final String SUCCESS_MESSAGE = 'Sub account has been saved successfully'
    private static final String ACC_SUB_ACCOUNT = "accSubAccount"
    private static final String ERROR_CHART_OF_ACCOUNT = "Chart of account code is invalid"
    private static final String ALREADY_EXISTS = "Same sub account already exists"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new accSubAccount
     *      1) Check existence of given AccChartOfAccount object
     *      2) Check existence of duplicate accSubAccount object
     *      3) Validate accSubAccount object
     * @param params -parameters send from UI
     * @param obj -AccSubAccount object send from controller
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            AccSubAccount accSubAccount = (AccSubAccount) obj
            String code = parameters.code
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()

            AccChartOfAccount accChartOfAccount = AccChartOfAccount.findByCodeAndCompanyId(code, companyId, [readOnly: true])
            if (!accChartOfAccount) {//Check existence of given AccChartOfAccount object
                result.put(Tools.MESSAGE, ERROR_CHART_OF_ACCOUNT)
                return result
            }

            accSubAccount.coaId = accChartOfAccount.id
            AccSubAccount existingObj = accSubAccountCacheUtility.readByDescriptionAndCoaId(accSubAccount.description, accSubAccount.coaId)
            if (existingObj) {//Check existence of duplicate accSubAccount object
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }

            //Validate accSubAccount object
            accSubAccount.validate()
            if (accSubAccount.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save accSubAccount object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accSubAccountObject send from controller
     * @return -newly created accSubAccount object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccSubAccount accSubAccountInstance = (AccSubAccount) obj
            //save in DB
            AccSubAccount savedSubAccount = accSubAccountService.create(accSubAccountInstance)
            //save in cache and keep the data sorted
            accSubAccountCacheUtility.add(savedSubAccount, accSubAccountCacheUtility.SORT_ON_DESCRIPTION, accSubAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_SUB_ACCOUNT, savedSubAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created accSubAccount to show on grid
     * @param obj -newly created accSubAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccSubAccount subAccount = (AccSubAccount) obj
            GridEntity object = new GridEntity()
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(subAccount.coaId)
            object.id = subAccount.id
            object.cell = [
                    Tools.LABEL_NEW,
                    subAccount.id,
                    subAccount.description,
                    accChartOfAccount.code,
                    subAccount.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: subAccount.version]
            result.put(ACC_SUB_ACCOUNT, resultMap)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
