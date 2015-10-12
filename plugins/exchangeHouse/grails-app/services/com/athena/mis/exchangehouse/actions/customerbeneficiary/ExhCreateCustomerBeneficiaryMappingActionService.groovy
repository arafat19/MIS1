package com.athena.mis.exchangehouse.actions.customerbeneficiary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomerBeneficiaryMapping
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerBeneficiaryMappingService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhCreateCustomerBeneficiaryMappingActionService extends BaseService implements ActionIntf {

    // constants
    private static String DEFAULT_FAILURE_MESSAGE = "Failed to create customer beneficiary mapping "
    private static String BENEFICIARY_MAPPING_EXISTS = "Beneficiary already mapped with this customer "
    private static String BENEFICIARY_MAPPING_SUCCESS_MESSAGE = "Beneficiary has been mapped successfully"
    private static final String MAP_OBJ = 'mapObj'
    private static final String EXISTING_BENEFICIARY_ID = 'existingBeneficiaryId'

    private final Logger log = Logger.getLogger(getClass());

    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerBeneficiaryMappingService exhCustomerBeneficiaryMappingService
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameters = (GrailsParameterMap) params
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long newBeneficiaryId = Long.parseLong(parameters.beneficiaryId)
            long customerId = Long.parseLong(parameters.customerId)
            long existingBeneficiaryId = Long.parseLong(parameters.existingBeneficiaryId)
            ExhCustomerBeneficiaryMapping mapping = ExhCustomerBeneficiaryMapping.findByBeneficiaryIdAndCustomerId(newBeneficiaryId, customerId, [readOnly: true])
            if (mapping) {
                result.put(Tools.MESSAGE, BENEFICIARY_MAPPING_EXISTS)
                return result
            }
            result.put(EXISTING_BENEFICIARY_ID, new Long(existingBeneficiaryId))
            ExhCustomerBeneficiaryMapping newMapping = buildCustomerBeneficiaryMapping(newBeneficiaryId, customerId)
            result.put(MAP_OBJ, newMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            Long existingBeneficiaryId = (Long) preResult.get(EXISTING_BENEFICIARY_ID)
            ExhCustomerBeneficiaryMapping beneficiaryMapping = (ExhCustomerBeneficiaryMapping) preResult.get(MAP_OBJ)
            ExhBeneficiary newBeneficiary = ExhBeneficiary.read(beneficiaryMapping.beneficiaryId)
            if (existingBeneficiaryId.longValue() > 0) {
                ExhBeneficiary existingBeneficiary = ExhBeneficiary.read(existingBeneficiaryId)
                updateExistingBeneficiary(existingBeneficiary, newBeneficiary)
            } else {
                exhCustomerBeneficiaryMappingService.create(beneficiaryMapping)
            }
            return newBeneficiary
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_FAILURE_MESSAGE)
            return null
        }
    }

    private updateExistingBeneficiary(ExhBeneficiary existingBeneficiary, ExhBeneficiary newBeneficiary) {
        String sqlSelectSameCustomer = """
                SELECT DISTINCT(map.customer_id) FROM exh_customer_beneficiary_mapping map
                WHERE map.beneficiary_id =:existingBeneficiaryId OR map.beneficiary_id =:newBeneficiaryId
                GROUP BY map.customer_id
                HAVING COUNT(map.customer_id)>1;
        """

        String sqlUpdateMapping = """
                UPDATE exh_customer_beneficiary_mapping
                SET beneficiary_id = :newBeneficiaryId
                WHERE beneficiary_id = :existingBeneficiaryId;
        """
        String sqlUpdateTask = """
                UPDATE exh_task
                SET beneficiary_id = :newBeneficiaryId, beneficiary_name= :newBeneficiaryName
                WHERE beneficiary_id = :existingBeneficiaryId;
        """
        String sqlUpdateTaskTrace = """
                UPDATE exh_task_trace
                SET beneficiary_id = :newBeneficiaryId
                WHERE beneficiary_id = :existingBeneficiaryId;
        """
        String sqlDeleteBeneficiary = """
                DELETE FROM exh_beneficiary
                WHERE id = :existingBeneficiaryId;
        """

        Map queryParams = [newBeneficiaryId: newBeneficiary.id,
                newBeneficiaryName: newBeneficiary.fullName, existingBeneficiaryId: existingBeneficiary.id]

        // first check if same customer is mapped with both beneficiary, in that case only delete mapping of existingBeneficiary

        List<GroovyRowResult> lstSameCustomer = executeSelectSql(sqlSelectSameCustomer, queryParams)

        if (lstSameCustomer.size() > 0) {
            String strCustomerIds = Tools.EMPTY_SPACE
            for (int i = 0; i < lstSameCustomer.size(); i++) {
                strCustomerIds = strCustomerIds + lstSameCustomer[i].customer_id
                if ((i + 1) < lstSameCustomer.size()) strCustomerIds = strCustomerIds + Tools.COMA
            }
            String sqlDeleteMapping = """
                    DELETE FROM exh_customer_beneficiary_mapping
                    WHERE customer_id IN(${strCustomerIds})
                    AND beneficiary_id = ${existingBeneficiary.id}
                    """
            executeUpdateSql(sqlDeleteMapping)
        }

        executeUpdateSql(sqlUpdateMapping, queryParams)
        executeUpdateSql(sqlUpdateTask, queryParams)
        executeUpdateSql(sqlUpdateTaskTrace, queryParams)
        executeUpdateSql(sqlDeleteBeneficiary, queryParams)
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result
        try {
            ExhBeneficiary beneficiary = (ExhBeneficiary) obj;
            GridEntity object = new GridEntity()
            object.id = beneficiary.id

            String updatedOn = DateUtility.getLongDateForUI(beneficiary.updatedOn)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(beneficiary.updatedBy)
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
            result = [isError: false, entity: object, version: beneficiary.version, message: BENEFICIARY_MAPPING_SUCCESS_MESSAGE]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [isError: true, entity: null, version: 0, message: BENEFICIARY_MAPPING_SUCCESS_MESSAGE]
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

    private ExhCustomerBeneficiaryMapping buildCustomerBeneficiaryMapping(long beneficiaryId, long customerId) {
        ExhCustomerBeneficiaryMapping beneficiaryMapping = new ExhCustomerBeneficiaryMapping(beneficiaryId: beneficiaryId, customerId: customerId)
        return beneficiaryMapping
    }
}
