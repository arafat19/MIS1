package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SearchTaskDetailsWithNoteActionService extends BaseService implements ActionIntf {

    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_OBJECT = "task_object"
    private static final String TASK_LIST = "task_list"
    private static final String TASK_ID = "task_id"

    @Autowired
    RmsSessionUtil rmsSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            String propertyName = params.property_name
            String propertyValue = params.property_value
            Date fromDate = DateUtility.parseMaskedFromDate(params.from_date)
            Date toDate = DateUtility.parseMaskedToDate(params.to_date)
            boolean isRestrictExchangeHouse = false
            boolean isRestrictBranch = false
            if (
            (!rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_REMITTANCE_USER)) &&
                    (!rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_ADMIN))
            ) {
                if (rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_EXCHANGE_HOUSE_USER)) {
                    isRestrictExchangeHouse = true
                }
                if (rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER) ||
                        rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)) {
                    isRestrictBranch = true
                }
            }
            List<RmsTask> lstRmsTask = getRmsTask(fromDate, toDate, propertyName, propertyValue, isRestrictExchangeHouse, isRestrictBranch)
            if (lstRmsTask && (lstRmsTask.size() == 1)) {
                result.put(TASK_OBJECT, lstRmsTask[0])
                result.put(TASK_ID, lstRmsTask[0].id)
                return result
            } else if (lstRmsTask && (lstRmsTask.size() > 0)) {
                result.put(TASK_LIST, lstRmsTask)
                return result
            }
            result.put(TASK_OBJECT, null)
            result.put(TASK_ID, 0)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(TASK_OBJECT, null)
            result.put(TASK_ID, 0)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private List<RmsTask> getRmsTask(Date fromDate, Date toDate, String propertyName,
                               def propertyValue, boolean isRestrictExchangeHouse, boolean isRestrictBranch) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstValidStatus = rmsTaskStatusCacheUtility.listAllValidTaskStatusIds()

        List<RmsTask> lstRmsTask = RmsTask.withCriteria {
            if (propertyName.equals(ID)) {
                eq(propertyName, Long.valueOf(propertyValue))
            } else {
                propertyValue = Tools.PERCENTAGE + propertyValue
                ilike(propertyName, propertyValue)
            }
            if (fromDate && toDate) {
                between('createdOn', fromDate, toDate)
            }
            if (isRestrictExchangeHouse) {
                long exhHouseId = rmsSessionUtil.getUserExchangeHouseId()
                eq('exchangeHouseId', exhHouseId)
            }
            if (isRestrictBranch) {
                long bankBranchId = rmsSessionUtil.appSessionUtil.getUserBankBranchId()
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(bankBranchId)
                eq('mappingBankId', bankBranch.bankId)
            }
            'in'('currentStatus', lstValidStatus)
            eq('companyId', companyId)
            setReadOnly(true)
        }
        if (lstRmsTask.size() > 0) {
            return lstRmsTask
        }
        return null
    }
}
