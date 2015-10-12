package com.athena.mis.accounting.actions.accvouchertypecoa

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.service.AccVoucherTypeCoaService
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCoaCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create AccVoucherTypeCoa object and show on grid list
 *  For details go through Use-Case doc named 'CreateAccVoucherTypeCoaActionService'
 */
class CreateAccVoucherTypeCoaActionService extends BaseService implements ActionIntf {

    AccVoucherTypeCoaService accVoucherTypeCoaService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String FAILURE_MESSAGE = 'Voucher type could not be saved'
    private static final String SUCCESS_MESSAGE = 'Voucher type has been saved successfully'
    private static final String ACC_VOUCHER_TYPE_COA = "accVoucherTypeCoa"
    private static final String ERROR_CHART_OF_ACCOUNT = "Chart of account code is invalid"
    private static final String MAPPING_EXISTS = "The mapping already exists"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new accVoucherTypeCoa
     *      1) Check existence of given AccChartOfAccount object
     *      2) Check existence of same accVoucherTypeCoa object
     *      3) Validate accVoucherTypeCoa object
     * @param params -parameters send from UI
     * @param obj -AccVoucherTypeCoa object send from controller
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            AccVoucherTypeCoa accVoucherTypeCoa = (AccVoucherTypeCoa) obj
            AccChartOfAccount accChartOfAccount = accChartOfAccountCacheUtility.readByCode(parameters.code.toString())
            if (!accChartOfAccount) {//Check existence of given AccChartOfAccount object
                result.put(Tools.MESSAGE, ERROR_CHART_OF_ACCOUNT)
                return result
            }
            accVoucherTypeCoa.coaId = accChartOfAccount.id

            //Validate accVoucherTypeCoa object
            accVoucherTypeCoa.validate()
            if (accVoucherTypeCoa.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            AccVoucherTypeCoa existingVoucherTypeCoa = accVoucherTypeCoaCacheUtility.readByVoucherTypeIdAndCoaId(accVoucherTypeCoa.accVoucherTypeId, accVoucherTypeCoa.coaId)
            if (existingVoucherTypeCoa) {//Check existence of same accVoucherTypeCoa object
                result.put(Tools.MESSAGE, MAPPING_EXISTS)
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
     * Save AccVoucherTypeCoa object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -AccVoucherTypeCoa Object send from controller
     * @return -newly created AccVoucherTypeCoa object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) obj
            accVoucherTypeCoaService.create(voucherTypeCoa)
            accVoucherTypeCoaCacheUtility.add(voucherTypeCoa, accVoucherTypeCoaCacheUtility.SORT_BY_VOUCHER_ID, accVoucherTypeCoaCacheUtility.SORT_ORDER_ASCENDING)

            result.put(ACC_VOUCHER_TYPE_COA, voucherTypeCoa)
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
     * Wrap accChartOfAccount object to show on grid
     * @param obj -newly created accChartOfAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            SystemEntity accVoucherType
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) obj
            GridEntity object = new GridEntity()
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(voucherTypeCoa.coaId)
            accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(voucherTypeCoa.accVoucherTypeId)
            object.id = voucherTypeCoa.id
            object.cell = [
                    Tools.LABEL_NEW,
                    voucherTypeCoa.id,
                    accVoucherType.key,
                    accChartOfAccount.code,
                    voucherTypeCoa.createdBy
            ]
            Map resultMap = [entity: object, version: voucherTypeCoa.version]
            result.put(ACC_VOUCHER_TYPE_COA, resultMap)
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
