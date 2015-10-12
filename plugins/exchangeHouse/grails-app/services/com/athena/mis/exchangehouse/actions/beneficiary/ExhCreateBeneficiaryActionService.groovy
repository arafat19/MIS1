package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhCustomerBeneficiaryMapping
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerBeneficiaryMappingService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new beneficiary and customer beneficiary mapping object and show in grid
 *  For details go through Use-Case doc named 'ExhCreateBeneficiaryActionService'
 */
class ExhCreateBeneficiaryActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String BENEFICIARY_SAVE_SUCCESS_MESSAGE = "Beneficiary saved successfully"
    private static final String BENEFICIARY_SAVE_FAILURE_MESSAGE = "Failed to save beneficiary"
    private static final String BENEFICIARY_OBJ = 'beneficiaryObj'
    private static final String CUSTOMER_IS_BLOCKED = "Customer is blocked"
    private static final String CODE_BD = "BD"
    private static final String CUSTOMER_ID = "customerId"

    ExhCustomerService exhCustomerService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerBeneficiaryMappingService exhCustomerBeneficiaryMappingService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility

    /**
     * Get params from UI and build beneficiary object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            if(!paramsMap.customerId){
                result.put(Tools.MESSAGE,BENEFICIARY_SAVE_FAILURE_MESSAGE)
                return result
            }
            long customerId= Long.parseLong(paramsMap.customerId)
            ExhCustomer exhCustomer=exhCustomerService.read(customerId)
            String errMsg=checkBlockCustomer(exhCustomer)
            if(errMsg){
                result.put(Tools.MESSAGE,errMsg)
                return result
            }
            ExhBeneficiary beneficiary = buildBeneficiaryObject(paramsMap)       // build beneficiary object

            beneficiary.validate()   // beneficiary obj validate in DB

            if (beneficiary.hasErrors()) {
                return result
            }

            result.put(CUSTOMER_ID, customerId)
            result.put(BENEFICIARY_OBJ, beneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save beneficiary and customerBeneficiaryMapping object in DB accordingly
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj  // cast map returned from executePreCondition method
            ExhBeneficiary beneficiary = (ExhBeneficiary) preResult.get(BENEFICIARY_OBJ)
            ExhBeneficiary savedBeneficiary = exhBeneficiaryService.create(beneficiary)     // save new beneficiary object in DB
            long customerId = (long)preResult.get(CUSTOMER_ID)
            ExhCustomerBeneficiaryMapping beneficiaryMapping = buildCustomerBeneficiaryMapping(savedBeneficiary.id, customerId)  // build customerBeneficiaryMapping object
            exhCustomerBeneficiaryMappingService.create(beneficiaryMapping)   // save new customerBeneficiaryMapping object in DB

            result.put(BENEFICIARY_OBJ, savedBeneficiary)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(BENEFICIARY_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Show newly created beneficiary object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) executeResult.get(BENEFICIARY_OBJ)

            GridEntity object = new GridEntity()    //build grid object
            object.id = beneficiary.id
            object.cell = [
                    Tools.LABEL_NEW,
                    beneficiary.id,
                    beneficiary.fullName,
                    beneficiary.approvedBy > 0 ? Tools.YES : Tools.NO,        // beneficiary approved status yes or no
                    beneficiary.bank,
                    beneficiary.accountNo,
                    beneficiary.photoIdType,
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, beneficiary.version)
            result.put(Tools.MESSAGE, BENEFICIARY_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_SAVE_FAILURE_MESSAGE)
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
                Map preResult = (Map) obj       // cast map returned from previous method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, BENEFICIARY_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BENEFICIARY_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build customerBeneficiary object
     * @param beneficiaryId -from execute method
     * @param customerId -from execute method
     * @return -new beneficiaryMapping object
     */
    private ExhCustomerBeneficiaryMapping buildCustomerBeneficiaryMapping(long beneficiaryId, long customerId) {
        ExhCustomerBeneficiaryMapping beneficiaryMapping = new ExhCustomerBeneficiaryMapping(
                beneficiaryId: beneficiaryId,
                customerId: customerId)
        return beneficiaryMapping
    }

    /**
     * Build beneficiary object
     * @param params -serialized parameters from UI
     * @return -new beneficiary object
     */
    private ExhBeneficiary buildBeneficiaryObject(GrailsParameterMap params) {
        ExhBeneficiary beneficiary = new ExhBeneficiary(params)
        Country country =  countryCacheUtility.readByCode(CODE_BD)

        if (!params.middleName) {
            beneficiary.middleName = null
        }
        if (!params.lastName) {
            beneficiary.lastName = null
        }

        if (params.isSanctionException) {
            beneficiary.isSanctionException = Boolean.TRUE
        }
        beneficiary.countryId = country.id     //@todo use dropDown for country
        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        beneficiary.companyId = user.companyId
        beneficiary.approvedBy = user.id
        beneficiary.approvedOn = new Date()
        beneficiary.createdBy = user.id
        beneficiary.createdOn = new Date()
        beneficiary.updatedBy = 0L
        return beneficiary
    }
    private String checkBlockCustomer(ExhCustomer exhCustomer){
        boolean isBlocked= exhCustomer.isBlocked
        if(isBlocked){
            return CUSTOMER_IS_BLOCKED
        }
        return null
    }
}
