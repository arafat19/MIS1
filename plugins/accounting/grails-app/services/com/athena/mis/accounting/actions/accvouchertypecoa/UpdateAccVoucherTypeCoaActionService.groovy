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
 *  Class to update AccVoucherTypeCoa object and show on grid list
 *  For details go through Use-Case doc named 'UpdateAccVoucherTypeCoaActionService'
 */
class UpdateAccVoucherTypeCoaActionService extends BaseService implements ActionIntf {

    AccVoucherTypeCoaService accVoucherTypeCoaService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String INVALID_INPUT_MESSAGE = "Could not update Voucher-Type mapping due to invalid input"
    private static final String FAILURE_MESSAGE = "Voucher Type could not be updated"
    private static final String SUCCESS_MESSAGE = "Voucher Type has been updated successfully"
    private static final String ACC_VOUCHER_TYPE_COA = "accVoucherTypeCoa"
    private static final String ERROR_CHART_OF_ACCOUNT = "Chart of account code is invalid"
    private static final String MAPPING_EXISTS = "The mapping already exists"
    private static final String OBJ_NOT_FOUND = "Selected Voucher-Type mapping not exists"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected voucher type map"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for updating accVoucherTypeCoa object
     *      1) Check existence of old accVoucherTypeCoa object
     *      2) If Chart of Account Code is changed then :
     *              -Check existence of given AccChartOfAccount object
     *      3) Check existence of duplicate accVoucherTypeCoa object
     *      4) if Voucher Type OR COA code is changed then : Check association with AccVoucherDetails
     *      5) Validate accVoucherTypeCoa object
     * @param params -parameters send from UI
     * @param obj -accVoucherTypeCoa object send from controller
     * @return -a map containing accVoucherTypeCoa object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {  //check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accVoucherTypeCoaId = Long.parseLong(parameterMap.id.toString())
            AccVoucherTypeCoa oldVoucherTypeCoa = (AccVoucherTypeCoa) accVoucherTypeCoaCacheUtility.read(accVoucherTypeCoaId)
            if (!oldVoucherTypeCoa) {  //check existence of oldVoucherTypeCoa object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            //build accVoucherTypeCoa object to update
            AccVoucherTypeCoa newAccVoucherTypeCoa = buildAccVoucherTypeCoa(parameterMap, oldVoucherTypeCoa)

            AccChartOfAccount oldChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(oldVoucherTypeCoa.coaId)

            if (oldChartOfAccount.code.equals(parameterMap.code)) { //If Chart of Account Code don't change
                newAccVoucherTypeCoa.coaId = oldChartOfAccount.id
            } else {  //If Chart of Account Code is changed
                AccChartOfAccount accChartOfAccount = accChartOfAccountCacheUtility.readByCode(parameterMap.code.toString())
                if (!accChartOfAccount) { //Check existence of given AccChartOfAccount object
                    result.put(Tools.MESSAGE, ERROR_CHART_OF_ACCOUNT)
                    return result
                }
                newAccVoucherTypeCoa.coaId = accChartOfAccount.id
            }

            AccVoucherTypeCoa existingVoucherTypeCoa = accVoucherTypeCoaCacheUtility.readByVoucherTypeIdAndCoaIdForUpdate(newAccVoucherTypeCoa.id, newAccVoucherTypeCoa.accVoucherTypeId, newAccVoucherTypeCoa.coaId)
            if (existingVoucherTypeCoa) {//Check existence of duplicate accVoucherTypeCoa object
                result.put(Tools.MESSAGE, MAPPING_EXISTS)
                return result
            }

            //if Voucher Type OR COA code is changed then : Check association with AccVoucher
            if ((newAccVoucherTypeCoa.accVoucherTypeId != oldVoucherTypeCoa.accVoucherTypeId)
                    || (newAccVoucherTypeCoa.coaId != oldVoucherTypeCoa.coaId)) {
                Map hasAssociationMap = hasAssociation(oldVoucherTypeCoa)
                Boolean hasAssociation = (Boolean) hasAssociationMap.get(Tools.HAS_ASSOCIATION)
                if (hasAssociation.booleanValue()) {
                    result.put(Tools.MESSAGE, hasAssociationMap.get(Tools.MESSAGE))
                    return result
                }
            }

            //Validate AccChartOfAccount object
            newAccVoucherTypeCoa.validate()
            if (newAccVoucherTypeCoa.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            result.put(ACC_VOUCHER_TYPE_COA, newAccVoucherTypeCoa)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_CHART_OF_ACCOUNT)
            return result
        }
    }

    /**
     * Update AccVoucherTypeCoa object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -AccVoucherTypeCoa send from execute method
     * @return -updated AccVoucherTypeCoa object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            AccVoucherTypeCoa accVoucherTypeCoa = (AccVoucherTypeCoa) preResult.get(ACC_VOUCHER_TYPE_COA)
            //update in DB
            AccVoucherTypeCoa newAccVoucherTypeCoa = accVoucherTypeCoaService.update(accVoucherTypeCoa)
            //update in cache
            accVoucherTypeCoaCacheUtility.update(newAccVoucherTypeCoa, accVoucherTypeCoaCacheUtility.SORT_BY_VOUCHER_ID, accVoucherTypeCoaCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_VOUCHER_TYPE_COA, newAccVoucherTypeCoa)
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
     * Wrap updated accChartOfAccount object to show on grid
     * @param obj -updated accChartOfAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            GridEntity object = new GridEntity()
            AccVoucherTypeCoa voucherTypeCoa = (AccVoucherTypeCoa) preResult.get(ACC_VOUCHER_TYPE_COA)
            AccChartOfAccount chartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(voucherTypeCoa.coaId)
            SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(voucherTypeCoa.accVoucherTypeId)
            object.id = voucherTypeCoa.id
            object.cell = [
                    Tools.LABEL_NEW,
                    voucherTypeCoa.id,
                    accVoucherType.key,
                    chartOfAccount.code
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

    /**
     * Check association with accVoucher
     * @param accSubAccount -AccSubAccount
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(AccVoucherTypeCoa accVoucherTypeCoa) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int count = countVoucher(accVoucherTypeCoa)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_SELECT = """
            SELECT COUNT(avd.id)
            FROM acc_voucher_details  avd
            LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
            WHERE av.voucher_type_id = :accVoucherTypeId
              AND avd.coa_id = :coaId
            """
    /**
     * count number of accVoucherDetails associated with given AccVoucherTypeCoa
     * @param accVoucherTypeCoa -AccVoucherTypeCoa
     * @return int value
     */
    private int countVoucher(AccVoucherTypeCoa accVoucherTypeCoa) {
        Map queryParams = [
                accVoucherTypeId: accVoucherTypeCoa.accVoucherTypeId,
                coaId: accVoucherTypeCoa.coaId
        ]
        List results = executeSelectSql(QUERY_SELECT, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * build accVoucherTypeCoa mapping object to update
     * @param params -GrailsParameterMap
     * @param oldVoucherTypeCoa -AccVoucherTypeCoa
     * @return -AccVoucherTypeCoa
     */
    private AccVoucherTypeCoa buildAccVoucherTypeCoa(GrailsParameterMap params, AccVoucherTypeCoa oldVoucherTypeCoa) {
        AccVoucherTypeCoa voucherTypeCoa = new AccVoucherTypeCoa(params)
        voucherTypeCoa.updatedBy = accSessionUtil.appSessionUtil.getAppUser().id
        voucherTypeCoa.updatedOn = new Date()
        voucherTypeCoa.createdOn = oldVoucherTypeCoa.createdOn
        voucherTypeCoa.createdBy = oldVoucherTypeCoa.createdBy
        voucherTypeCoa.id = oldVoucherTypeCoa.id
        voucherTypeCoa.version = oldVoucherTypeCoa.version
        voucherTypeCoa.companyId = oldVoucherTypeCoa.companyId
        return voucherTypeCoa
    }
}
