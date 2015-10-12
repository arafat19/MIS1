package com.athena.mis.exchangehouse.actions.beneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomerBeneficiaryMapping
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerBeneficiaryMappingService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new beneficiary object and show in grid
 *  For details go through Use-Case doc named 'ExhCreateBeneficiaryForCustomerActionService'
 */
class ExhCreateBeneficiaryForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String BENEFICIARY_SAVE_SUCCESS_MESSAGE = "Beneficiary saved successfully"
    private static final String BENEFICIARY_SAVE_FAILURE_MESSAGE = "Failed to save beneficiary"
    private static final String BENEFICIARY_OBJ = 'beneficiaryObj'
    private static final String CODE_BD = "BD"

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
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            ExhBeneficiary beneficiary = buildBeneficiaryObject(paramsMap)        // build beneficiary obj

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
     * Save beneficiary and exhCustomerBeneficiaryMapping object in DB accordingly
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            ExhBeneficiary beneficiary = (ExhBeneficiary) preResult.get(BENEFICIARY_OBJ)
            ExhBeneficiary savedBeneficiary = exhBeneficiaryService.create(beneficiary)
            long customerId = Long.parseLong(params.customerId)
            ExhCustomerBeneficiaryMapping beneficiaryMapping = buildCustomerBeneficiaryMapping(savedBeneficiary.id, customerId)
            exhCustomerBeneficiaryMappingService.create(beneficiaryMapping)          // save customer beneficiary mapping

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
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhBeneficiary beneficiary = (ExhBeneficiary) executeResult.get(BENEFICIARY_OBJ)
            GridEntity object = new GridEntity()        // build grid object
            object.id = beneficiary.id
            object.cell = [
                    Tools.LABEL_NEW,
                    beneficiary.id,
                    beneficiary.fullName,
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj         // cast map returned from previous method
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
     * Build exhCustomerBeneficiaryMapping object
     * @param params -serialized parameters from UI
     * @return -new beneficiaryMapping object
     */
    private ExhCustomerBeneficiaryMapping buildCustomerBeneficiaryMapping(long beneficiaryId, long customerId) {
        ExhCustomerBeneficiaryMapping beneficiaryMapping = new ExhCustomerBeneficiaryMapping(
                beneficiaryId: beneficiaryId,
                customerId: customerId
        )
        return beneficiaryMapping
    }

    /**
     * Build exhBeneficiary object
     * @param params -serialized parameters from UI
     * @return -new beneficiaryInstance object
     */
    private ExhBeneficiary buildBeneficiaryObject(GrailsParameterMap params) {
        ExhBeneficiary beneficiary = new ExhBeneficiary(params)
        AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
        Country country =  countryCacheUtility.readByCode(CODE_BD)

        if (!params.middleName) {
            beneficiary.middleName = null
        }

        if (!params.lastName) {
            beneficiary.lastName = null
        }

        beneficiary.countryId = country.id     //@todo use dropDown for country
        beneficiary.approvedBy = 0L
        beneficiary.approvedOn = new Date()
        beneficiary.isSanctionException = Boolean.FALSE
        beneficiary.createdBy = user.id
        beneficiary.createdOn = new Date()
        beneficiary.updatedBy = 0L
        beneficiary.companyId = user.companyId
        return beneficiary
    }
}
