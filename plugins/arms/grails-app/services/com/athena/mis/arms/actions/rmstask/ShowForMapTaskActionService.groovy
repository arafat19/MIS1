package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * Show page for map task
 * for details go through use-case named "ShowForMapTaskActionService"
 */
class ShowForMapTaskActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Failed to load map task page"
    private static final String PB_BANK_ID = "pbBankId"
    private static final String PB_BRANCH_ID = "pbBranchId"
    private static final String PB_DISTRICT_ID = "pbDistrictId"
    private static final String ISSUE_ID = "issueId"
    private static final String FORWARD_ID = "forwardId"
    private static final String PURCHASE_ID = "purchaseId"

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * read principle bank, branch, district id & some process type for ui
     * @param parameters - n/a
     * @param obj - n/a
     * @return
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try{
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            Bank bank = (Bank) bankCacheUtility.getSystemBank();
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.getPrincipleBankBranch(bank.id)
            SystemEntity issue = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.ISSUE, companyId)
            SystemEntity forward = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.FORWARD, companyId)
            SystemEntity purchase = (SystemEntity) rmsProcessTypeCacheUtility.readByReservedAndCompany(RmsProcessTypeCacheUtility.PURCHASE, companyId)

            result.put(ISSUE_ID, issue.id)
            result.put(FORWARD_ID, forward.id)
            result.put(PURCHASE_ID, purchase.id)
            result.put(PB_BANK_ID, bankBranch.bankId)
            result.put(PB_BRANCH_ID, bankBranch.id)
            result.put(PB_DISTRICT_ID, bankBranch.districtId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch(Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
