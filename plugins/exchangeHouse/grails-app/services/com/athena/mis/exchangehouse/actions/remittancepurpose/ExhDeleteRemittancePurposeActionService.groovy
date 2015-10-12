package com.athena.mis.exchangehouse.actions.remittancepurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.service.ExhRemittancePurposeService
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhDeleteRemittancePurposeActionService extends BaseService implements ActionIntf {
    //call remittancePurpose service
    ExhRemittancePurposeService exhRemittancePurposeService
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    //assign remittancePurpose information removing status information
    private static String REMITTANCE_PURPOSE_DELETE_SUCCESS_MSG = "Remittance Purpose has been successfully deleted!"
    private static String REMITTANCE_PURPOSE_DELETE_FAILURE_MSG = "Sorry! Remittance Purpose has not been deleted."

    //define logger to set all log message
    private final Logger log = Logger.getLogger(getClass())

    //implement the pre-condition method of action class
    /*
    Get all pre-condition to save remittancePurpose info
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {

        try {
            Map outputMap = new HashMap()
            GrailsParameterMap parameters=(GrailsParameterMap) params
            ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose) exhRemittancePurposeCacheUtility.read(parameters.id.toLong())
            String associationMessage = null
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
                associationMessage = isAssociated(remittancePurpose)
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            if (associationMessage != null) {
                outputMap.put("hasAssociation", associationMessage)
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save remittancePurpose info using
    execute method
     */

    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameters = (GrailsParameterMap) params
            Long remittancePurposeId = parameters.id.toLong()
            Boolean result = (Boolean) exhRemittancePurposeService.delete(remittancePurposeId)
            if (result.booleanValue()) {
                ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose) exhRemittancePurposeCacheUtility.read(remittancePurposeId)
                exhRemittancePurposeCacheUtility.delete(remittancePurpose.id)
            }
            return result
        } catch (Exception ex) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Remittance Purpose')
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
    if remittancePurpose build successfully then initiate success message
     */

    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: REMITTANCE_PURPOSE_DELETE_SUCCESS_MSG]
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if remittancePurpose build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        // add try catch and add log entry
        LinkedHashMap result
        try {
            if (!obj) {
                return [deleted: Boolean.FALSE.booleanValue(), message: REMITTANCE_PURPOSE_DELETE_FAILURE_MSG]
            }
            return [deleted: Boolean.FALSE.booleanValue(), message: (String) obj]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [deleted: Boolean.FALSE.booleanValue(), message: REMITTANCE_PURPOSE_DELETE_FAILURE_MSG]
        }
    }

    // checking all association with Remittance Purpose
    private String isAssociated(ExhRemittancePurpose remittancePurpose) {
        Long remittancePurposeId = remittancePurpose.id
        String remittancePurposeName = remittancePurpose.name
        Integer count = 0

        // has Task
        count = countTask(remittancePurposeId)
        if (count.intValue() > 0) return Tools.getMessageOfAssociation(remittancePurposeName, count, Tools.DOMAIN_TASK)

        return null
    }

    //count number of row in task table by remittance purpose id
    private int countTask(Long remittancePurposeId) {
        String query = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE remittance_purpose = ${remittancePurposeId}
        """
        List remittancePurposeCount = executeSelectSql(query)
        int count = remittancePurposeCount[0].count
        return count
    }
}
