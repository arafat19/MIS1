package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Approved beneficiary of a customer for cashier
 * For details go through Use-Case doc named 'ExhApproveBeneficiaryForCashierActionService'
 */
class ExhApproveBeneficiaryForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String APPROVED_SUCCESS = "Beneficiary successfully approved"
    private static final String APPROVED_FAILURE = "Beneficiary can not be approved"
    private static final String BENEFICIARY_NOT_FOUND_MSG = "Beneficiary not found. Refresh grid and try again"
    private static final String BENEFICIARY_ALREADY_APPROVED = "Beneficiary already approved"
    private static final String BENEFICIARY_OBJ = 'beneficiaryObj'

    ExhBeneficiaryService exhBeneficiaryService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Get parameters from UI and check pre condition
     * 1. pull beneficiary by id & check its exist or not
     * 2. check beneficiary already exist or not
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE) // set default value

            long beneficiaryId = Long.parseLong(params.id.toString())
            ExhBeneficiary beneficiary = exhBeneficiaryService.read(beneficiaryId)
            if (!beneficiary) {                               // check beneficiary exist or not
                result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_MSG)
                return result
            }
            if (beneficiary.approvedBy > 0) {
                result.put(Tools.MESSAGE, BENEFICIARY_ALREADY_APPROVED)
                return result
            }

            result.put(BENEFICIARY_OBJ, beneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
            return result
        }
    }

    /**
     * execute following activities
     * 1. Approved beneficiary by update its approvedBy property as appUser.id
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj        // cast map returned from previous method
            ExhBeneficiary beneficiary = (ExhBeneficiary) preResult.get(BENEFICIARY_OBJ)
            approvedBeneficiary(beneficiary)
            // update customer's tasks' status from STATUS_UN_APPROVED to STATUS_NEW_TASK

            result.put(BENEFICIARY_OBJ, beneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(APPROVED_FAILURE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
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
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) executeResult.get(BENEFICIARY_OBJ);
            String updatedOn = DateUtility.getLongDateForUI(beneficiary.updatedOn)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(beneficiary.updatedBy)
            GridEntity object = new GridEntity()       // build grid entity object
            object.id = beneficiary.id

            object.cell = [
                    Tools.LABEL_NEW,
                    beneficiary.id,
                    beneficiary.fullName,
                    beneficiary.approvedBy > 0 ? Tools.YES : Tools.NO,
                    beneficiary.bank,
                    beneficiary.accountNo,
                    beneficiary.photoIdType,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE,
                    updatedOn
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, beneficiary.version)
            result.put(Tools.MESSAGE, APPROVED_SUCCESS)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj                       // cast map returned from previous method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, APPROVED_FAILURE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
            return result
        }
    }

    /**
     * Approved beneficiary
     * @param beneficiary -an object of ExhBeneficiary
     * @return task by update count
     */
    private Integer approvedBeneficiary(ExhBeneficiary beneficiary) {
        String queryStr =
                """
                UPDATE exh_beneficiary
                SET version = version+1,
                    approved_by = :approvedBy,
                    approved_on = :approvedOn
                WHERE id = :beneficiaryId
            """
        Map queryParams = [
                beneficiaryId: beneficiary.id,
                approvedBy: exhSessionUtil.appSessionUtil.appUser.id,
                approvedOn: DateUtility.getSqlDateWithSeconds(new Date())
        ]

        int updateCount = executeUpdateSql(queryStr, queryParams);
        if (updateCount < 1) {
            throw new RuntimeException("Failed to update Beneficiary")
        }
        beneficiary.version = beneficiary.version + 1
        beneficiary.approvedBy = exhSessionUtil.appSessionUtil.appUser.id
        return (new Integer(updateCount));
    }
}
