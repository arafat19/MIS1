package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update beneficiary object and grid data
 *  For details go through Use-Case doc named 'ExhUpdateBeneficiaryActionService'
 */
class ExhUpdateBeneficiaryActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    // constants
    private static String BENEFICIARY_UPDATE_FAILURE_MESSAGE = "Beneficiary could not be updated"
    private static String BENEFICIARY_UPDATE_SUCCESS_MESSAGE = "Beneficiary has been updated successfully"
    private static final String BENEFICIARY_NOT_FOUND_ERROR_MESSAGE = "No beneficiary found or might have been deleted by someone!";
    private static final String BENEFICIARY_OBJ = 'beneficiaryObj'
    private static final String BENEFICIARY_PREV_NAME = 'beneficiaryPrevName'
    private static final String ON = "on"
    private static final String CUSTOMER_IS_BLOCKED = "Customer is blocked"

    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
	@Autowired
	ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    /**
     * Get parameters from UI and build beneficiary object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)              // set default value
            GrailsParameterMap paramsMap = (GrailsParameterMap) parameters

            if (!paramsMap.id) {                         // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!paramsMap.customerId) {                         // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long customerId = Long.parseLong(paramsMap.customerId.toString())
            ExhCustomer exhCustomer=exhCustomerService.read(customerId)
            String errMsg=checkBlockedCustomer(exhCustomer)
            if(errMsg){
                result.put(Tools.MESSAGE,errMsg)
                return result
            }
            long beneficiaryId = Long.parseLong(paramsMap.id)
            ExhBeneficiary oldBeneficiaryObj = exhBeneficiaryService.read(beneficiaryId)      // get beneficiary object


            if (!oldBeneficiaryObj) {                    // check whether selected beneficiary object exists or not
                result.put(Tools.MESSAGE, BENEFICIARY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
			String beneficiaryPrevName = oldBeneficiaryObj.fullName
            ExhBeneficiary beneficiaryObject = buildBeneficiaryObject(oldBeneficiaryObj, paramsMap)
            // // build beneficiary object for update

            result.put(BENEFICIARY_OBJ, beneficiaryObject)
            result.put(BENEFICIARY_PREV_NAME, beneficiaryPrevName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update beneficiary object in DB & update cache utility accordingly
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) preResult.get(BENEFICIARY_OBJ);
            String beneficiaryPrevName = (String) preResult.get(BENEFICIARY_PREV_NAME);
            exhBeneficiaryService.update(beneficiary)    // update beneficiary object in DB
			//change in Beneficiary Name
			if(!beneficiaryPrevName.equals(beneficiary.fullName)) {
				updateBeneficiaryNameInTaskAndTaskTrace(beneficiary.fullName, beneficiary.id)
			}
            result.put(BENEFICIARY_OBJ, beneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(BENEFICIARY_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_FAILURE_MESSAGE)
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
     * Show updated beneficiary object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) executeResult.get(BENEFICIARY_OBJ);
            GridEntity object = new GridEntity()       // build grid entity object
            String updatedOn = DateUtility.getLongDateForUI(beneficiary.updatedOn)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(beneficiary.updatedBy)
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
            result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build beneficiary object for update
     * @param params -serialized parameters from UI
     * @param oldBeneficiaryObj -old beneficiary object
     * @return -updated beneficiary object
     */
    private ExhBeneficiary buildBeneficiaryObject(ExhBeneficiary oldBeneficiaryObj, GrailsParameterMap params) {
        ExhBeneficiary beneficiaryInstance = new ExhBeneficiary(params)
        oldBeneficiaryObj.firstName = beneficiaryInstance.firstName
        oldBeneficiaryObj.middleName = beneficiaryInstance.middleName
        oldBeneficiaryObj.lastName = beneficiaryInstance.lastName
        oldBeneficiaryObj.accountNo = beneficiaryInstance.accountNo
        oldBeneficiaryObj.bank = beneficiaryInstance.bank
        oldBeneficiaryObj.bankBranch = beneficiaryInstance.bankBranch
        oldBeneficiaryObj.address = beneficiaryInstance.address
        oldBeneficiaryObj.district = beneficiaryInstance.district
        oldBeneficiaryObj.email = beneficiaryInstance.email
        oldBeneficiaryObj.phone = beneficiaryInstance.phone
        oldBeneficiaryObj.photoIdType = beneficiaryInstance.photoIdType
        oldBeneficiaryObj.photoIdNo = beneficiaryInstance.photoIdNo
        oldBeneficiaryObj.thana = beneficiaryInstance.thana
        oldBeneficiaryObj.relation = beneficiaryInstance.relation
        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        oldBeneficiaryObj.companyId = user.companyId
        if (params.isSanctionException.toString().equals(ON)) {
            oldBeneficiaryObj.isSanctionException = true
        } else {
            oldBeneficiaryObj.isSanctionException = false
        }
        oldBeneficiaryObj.updatedBy = user.id
        oldBeneficiaryObj.updatedOn = new Date()
        return oldBeneficiaryObj
    }

	private void updateBeneficiaryNameInTaskAndTaskTrace(String newName, long beneficiaryId) {
		long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
		SystemEntity newTaskObj = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
		SystemEntity unApprovedTaskObj = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)
		SystemEntity pendingTaskObj = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)
		String strStatus = newTaskObj.id.toString()+ Tools.COMA + unApprovedTaskObj.id.toString() + Tools.COMA + pendingTaskObj.id.toString()
		String taskUpdateQuery= """
		UPDATE exh_task set beneficiary_name=:newName
		WHERE current_status in (${strStatus}) AND beneficiary_id=:beneficiaryId;
		"""
		String taskTraceUpdateQuery= """
		UPDATE exh_task set beneficiary_name=:newName
		WHERE current_status in (${strStatus}) AND beneficiary_id=:beneficiaryId;
		"""
		Map queryParam = [newName: newName, beneficiaryId: beneficiaryId]
		executeUpdateSql(taskUpdateQuery,queryParam)
		executeUpdateSql(taskTraceUpdateQuery,queryParam)
	}
    private String checkBlockedCustomer(ExhCustomer exhCustomer){
        boolean isBlocked= exhCustomer.isBlocked
        if(isBlocked){
            return CUSTOMER_IS_BLOCKED
        }
        return null
    }
}
