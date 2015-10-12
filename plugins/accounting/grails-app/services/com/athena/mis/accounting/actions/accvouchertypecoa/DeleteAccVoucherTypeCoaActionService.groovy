package com.athena.mis.accounting.actions.accvouchertypecoa

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.service.AccVoucherTypeCoaService
import com.athena.mis.accounting.utility.AccVoucherTypeCoaCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete accVoucherTypeCoa object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteAccVoucherTypeCoaActionService'
 */
class DeleteAccVoucherTypeCoaActionService extends BaseService implements ActionIntf {

    AccVoucherTypeCoaService accVoucherTypeCoaService
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility

    private static final String INVALID_INPUT_MESSAGE = "Could not delete Voucher-Type mapping due to invalid input"
    private static final String DELETE_SUCCESS_MESSAGE = "Voucher Type mapping has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Voucher Type mapping could not be deleted, please refresh the voucher type list"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected voucher type map"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete voucher type mapping"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete accVoucherTypeCoa object
     *      1) Check existence of required parameter
     *      2) Check existence of accVoucherTypeCoa object
     *      3) check association with accVoucherDetails
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //Check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            long accVoucherTypeId = Long.parseLong(params.id.toString())
            AccVoucherTypeCoa accVoucherTypeCoa = (AccVoucherTypeCoa) accVoucherTypeCoaCacheUtility.read(accVoucherTypeId)
            if (!accVoucherTypeCoa) { //Check existence of accVoucherTypeCoa object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //check association with accVoucherDetails
            Map hasAssociationMap = hasAssociation(accVoucherTypeCoa)
            Boolean hasAssociation = (Boolean) hasAssociationMap.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, hasAssociationMap.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete accVoucherTypeCoa object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long accVoucherTypeCoaId = Long.parseLong(parameterMap.id.toString())
            //delete from DB
            accVoucherTypeCoaService.delete(accVoucherTypeCoaId)
            //delete from cache
            accVoucherTypeCoaCacheUtility.delete(accVoucherTypeCoaId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains isError(false) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Check association on accVoucherTypeCoa with accVoucher
     * @param accVoucherTypeCoa -AccVoucherTypeCoa
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
     * count number of acc_voucher_details
     * @param accVoucherTypeCoa -AccVoucherTypeCoa object
     * @return -int value
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

}
