package com.athena.mis.application.actions.bankbranch

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class SelectBankBranchActionService extends BaseService implements ActionIntf {

    // auto-wiring required services

    private static final String HAS_ACCESS = "hasAccess"
    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            outputMap.put(HAS_ACCESS, hasAccess)
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Select the bankBranch by id (primary key) and returns
     *
     * @param params request parameters
     * @param obj additional parameters, not required for this action
     * @return return BankBranch instance and version info in a map
     */
    public Object execute(Object parameters, Object obj) {
        try {
            BankBranch bankBranchInstance = (BankBranch) bankBranchCacheUtility.read(Long.parseLong(parameters.id))
            return bankBranchInstance;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Select bankBranch has not post-condition
     *
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }


    public Object buildSuccessResultForUI(Object bankBranchInstance) {
        Map result = [] as LinkedHashMap
        try {
            result = [entity: bankBranchInstance, version: bankBranchInstance.version]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result

        }
    }

}
