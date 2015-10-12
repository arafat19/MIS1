package com.athena.mis.application.actions.district

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.District
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteDistrictActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String HAS_ASSOCIATION = "hasAssociation"
    private static String DISTRICT_DELETE_SUCCESS_MSG = "District has been successfully deleted!"
    private static String DISTRICT_DELETE_FAILURE_MSG = "Sorry! District has not been deleted."
    private final Logger log = Logger.getLogger(getClass())

    DistrictService districtService
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    //implement the pre-condition method of action class
    /*
    Get all pre-condition to save district info
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap()
            District district = (District) districtCacheUtility.read(parameters.id.toLong())
            String associationMessage = null
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(HAS_ACCESS, new Boolean(true))
                associationMessage = isAssociated(district)
            } else {
                outputMap.put(HAS_ACCESS, new Boolean(false))
            }
            if (associationMessage != null) {
                outputMap.put(HAS_ASSOCIATION, associationMessage)
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null;
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save district info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Long districtId = parameters.id.toLong()
            Boolean result = (Boolean) districtService.delete(districtId)
            if (result.booleanValue()) {
                District district = (District) districtCacheUtility.read(districtId)
                districtCacheUtility.delete(district.id)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DISTRICT_DELETE_FAILURE_MSG)
            return Boolean.FALSE
        }
    }

    //implement the executePostCondition method of action class
    /*
    if execute is ok. then executePostCondition method will be checked
     */

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null

    }

    //implement the buildSuccessResultForUI method of action class
    /*
    if district build successfully then initiate success message
     */

    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DISTRICT_DELETE_SUCCESS_MSG]
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if district build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        // add try catch and add log entry
        LinkedHashMap result
        try {
            if (!obj) {
                return [deleted: Boolean.FALSE.booleanValue(), message: DISTRICT_DELETE_FAILURE_MSG]
            }
            return [deleted: Boolean.FALSE.booleanValue(), message: (String) obj]
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return [deleted: Boolean.FALSE.booleanValue(), message: DISTRICT_DELETE_FAILURE_MSG]
        }
    }

    // checking all association
    private String isAssociated(District district) {
        Long districtId = district.id;
        String districtName = district.name;
        Integer count = 0;

        // has Bank Branch
        count = countBankBranch(districtId);
        if (count.intValue() > 0) {
            return Tools.getMessageOfAssociation(districtName, count, Tools.DOMAIN_BANK_BRANCH)
        }

        //has Task
        if(exhImplService){
            count = countTask(districtId)
            if (count.intValue() > 0) {
                return Tools.getMessageOfAssociation(districtName, count, Tools.DOMAIN_TASK)
            }
        }
        if(armsImplService){
            count = countRmsTask(districtId)
            if (count.intValue() > 0) {
                return Tools.getMessageOfAssociation(districtName, count, Tools.DOMAIN_TASK)
            }
            count = countRmsPurchaseInstrumentMapping(districtId)
            if (count.intValue() > 0) {
                return Tools.getMessageOfAssociation(districtName, count, Tools.DOMAIN_TASK)
            }

        }

        return null;
    }

    //count number of row in branch table by bank id
    private int countBankBranch(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM bank_branch
            WHERE district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count;
    }

    //count number of rows in task table by bank district id
    private int countTask(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count;
    }
    private int countRmsTask(Long districtId) {
        String query = """
            SELECT COUNT(id) as count
            FROM rms_task
            WHERE mapping_district_id = ${districtId}
        """
        List districtCount = executeSelectSql(query)
        int count = districtCount[0].count
        return count;
    }
    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING="""
        SELECT COUNT(id) as count
        FROM rms_purchase_instrument_mapping
        WHERE district_id = :districtId
    """
    private int countRmsPurchaseInstrumentMapping(long districtId){
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING, [districtId: districtId])
        int count = taskCount[0].count
        return count
    }
}


