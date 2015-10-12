package com.athena.mis.exchangehouse.actions.remittancepurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.service.ExhRemittancePurposeService
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhCreateRemittancePurposeActionService extends BaseService implements ActionIntf {

    ExhRemittancePurposeService exhRemittancePurposeService
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static String REMITTANCE_PURPOSE_CREATE_SUCCESS_MSG = "Remittance Purpose has been successfully saved"
    private static String REMITTANCE_PURPOSE_CREATE_FAILURE_MSG = "Remittance Purpose has not been saved"

    private final Logger log = Logger.getLogger(getClass())

    //implement the precondition method of action class
    /*
    Get all pre condition to save remittancePurpose info
     */

    public Object executePreCondition(Object parameters, Object obj) {
        //set a map object to send all information to caller
        Map output = new HashMap()
        try {
            //now check session log
            //only admin can create new remittancePurpose
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                output.put('hasAccess', new Boolean(true))
            } else {
                output.put('hasAccess', new Boolean(false))
                return null
            }

            //create a remittancePurpose instance
            ExhRemittancePurpose remittancePurposeInstance = (ExhRemittancePurpose) obj
            remittancePurposeInstance.companyId = exhSessionUtil.appSessionUtil.getCompanyId()

            //check remittancePurpose remittancePurpose input validation
            remittancePurposeInstance.validate()

            if (remittancePurposeInstance.hasErrors()) {
                output.put("isValid", new Boolean(false))
            } else {
                output.put("isValid", new Boolean(true))
            }

            return output

        } catch (Exception e) {
            log.error(e.getMessage())
            output.put("isValid", new Boolean(false))
            return output
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save remittancePurpose info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            ExhRemittancePurpose objRemittancePurpose = (ExhRemittancePurpose) obj
            ExhRemittancePurpose remittancePurposeServiceReturn = exhRemittancePurposeService.create(objRemittancePurpose)
            exhRemittancePurposeCacheUtility.add(remittancePurposeServiceReturn, exhRemittancePurposeCacheUtility.DEFAULT_SORT_PROPERTY, exhRemittancePurposeCacheUtility.SORT_ORDER_ASCENDING)
            return remittancePurposeServiceReturn
        }
        catch (Exception e) {
            log.error(e.message)
            //@todo:rollback
            throw new RuntimeException(REMITTANCE_PURPOSE_CREATE_FAILURE_MSG)
            return null
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
        LinkedHashMap result

        try {

            ExhRemittancePurpose objRemittancePurpose = (ExhRemittancePurpose) obj
            GridEntity objGrid = new GridEntity()
            objGrid.id = objRemittancePurpose.id
            objGrid.cell = [Tools.LABEL_NEW,
                    objRemittancePurpose.id,
                    objRemittancePurpose.name,
                    objRemittancePurpose.code ? objRemittancePurpose.code : Tools.EMPTY_SPACE
            ]
            result = [isError: false, entity: objGrid, version: objRemittancePurpose.version, message: REMITTANCE_PURPOSE_CREATE_SUCCESS_MSG];
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result = [isError: true, entity: obj, version: 0, message: REMITTANCE_PURPOSE_CREATE_FAILURE_MSG]
            return result
        }
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if remittancePurpose build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        // add try catch and add log entry
        LinkedHashMap result = [isError: true, entity: obj, message: REMITTANCE_PURPOSE_CREATE_FAILURE_MSG]
        return result
    }
}
