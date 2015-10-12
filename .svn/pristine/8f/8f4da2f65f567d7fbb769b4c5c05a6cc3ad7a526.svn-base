package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.entity.RmsTaskTrace
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetRmsTaskHistoryTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = "id"
    private static final String PROPERTY_NAME = "propertyName"
    private static final String PROPERTY_VALUE = "propertyValue"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static String FORWARD = "glyphicon glyphicon-arrow-down"
    private static String BACKWARD = "glyphicon glyphicon-arrow-up"
    private static String RED = "color:red"
    private static String GREEN = "color:green"

    RmsTaskService rmsTaskService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility

    /**
     * check if propertyName and propertyValue exists
     * @param parameters - attr of tagLib
     * @param obj - N/A
     * @return - Map containing attr
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map attrMap = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            String strId = attrs.get(ID)
            String propertyName = attrs.get(PROPERTY_NAME)
            def propertyValue = attrs.get(PROPERTY_VALUE)
            String fromDate = attrs.get(FROM_DATE)
            String toDate = attrs.get(TO_DATE)
            if (!propertyName || !propertyValue) {
                return attrMap
            }
            attrMap.put(ID, strId)
            attrMap.put(PROPERTY_NAME, propertyName)
            attrMap.put(PROPERTY_VALUE, propertyValue)
            attrMap.put(FROM_DATE, fromDate)
            attrMap.put(TO_DATE, toDate)
            attrMap.put(Tools.IS_ERROR, Boolean.FALSE)
            return attrMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            attrMap.put(Tools.IS_ERROR, Boolean.TRUE)
            return attrMap
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get the rmsTask and build taskDetails html
     * @param parameters
     * @param obj - preResult from preCondition
     * @return
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String strFromDate = preResult.get(FROM_DATE)
            String strToDate = preResult.get(TO_DATE)
            Date fromDate = DateUtility.parseMaskedFromDate(strFromDate)
            Date toDate = DateUtility.parseMaskedToDate(strToDate)
            String propertyName = preResult.get(PROPERTY_NAME)
            def propertyValue = preResult.get(PROPERTY_VALUE)
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
            RmsTask rmsTask = getRmsTask(fromDate, toDate, propertyName, propertyValue, isRestrictExchangeHouse, isRestrictBranch)
            if (!rmsTask) {
                String notFound = buildNoTaskFoundHtml()
                return notFound
            }
            List<RmsTaskTrace> lsRmsTaskTrace = rmsTaskTraceService.findAllByTaskId(rmsTask.id)
            String taskHistory = buildRmsTaskHistory(lsRmsTaskTrace, preResult)
            return taskHistory
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private String buildRmsTaskHistory(List<RmsTaskTrace> lsRmsTaskTrace, Map preResult) {
        String strId = preResult.get(ID) ? ("id='${preResult.get(ID)}'") : Tools.EMPTY_SPACE
        List<Map> listViewObj = buildListViewObj(lsRmsTaskTrace)
        String html = """
        <table class="table table-bordered">
            <thead>
                <tr class="active">
                    <th width="5%">&nbsp</th>
                    <th>Task Status</th>
                    <th>Date</th>
                    <th>User</th>
                </tr>
            </thead>
            <tbody id="listView">
            </tbody>
        </table>
        """

        String script = """
        <script type="text/javascript">
            \$(document).ready(function() {
                var tmplHistory = "<tr style='#:strRevised#'><td><span class='#:strClass#' style='#:strColor#'></span></td><td>#:toStatus#</td><td>#:date#</td><td>#:user#</td></tr>";
                \$("#listView").kendoListView({
                    dataSource: ${listViewObj as JSON},
                    template: tmplHistory
                });
                \$("#listView").removeAttr("class");
            });
        </script>
        """
        return html + script
    }

    private String buildNoTaskFoundHtml() {
        String script = """
        <script type="text/javascript">
            \$(document).ready(function() {
                showError("Task not found");
            });
        </script>
        """
        return script
    }

    /**
     * find the rmsTask
     * @return - RmsTask obj
     */
    private RmsTask getRmsTask(Date fromDate, Date toDate, String propertyName,
                               def propertyValue, boolean isRestrictExchangeHouse, boolean isRestrictBranch) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstValidStatus = rmsTaskStatusCacheUtility.listAllValidTaskStatusIds()

        List<RmsTask> lstRmsTask = RmsTask.withCriteria {
            if (propertyName.equals(ID)) {
                eq(propertyName, Long.valueOf(propertyValue.toString()))
            } else {
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
            return lstRmsTask[0]
        }
        return null
    }

    private List<Map> buildListViewObj(List<RmsTaskTrace> lstRmsTaskTrace) {
        List<Map> lstHistory = []
        for (int i = 0; i < lstRmsTaskTrace.size(); i++) {
            RmsTaskTrace rmsTaskTrace = lstRmsTaskTrace[i]
            SystemEntity fromStatus = (SystemEntity) rmsTaskStatusCacheUtility.read(rmsTaskTrace.previousStatus)
            SystemEntity toStatus = (SystemEntity) rmsTaskStatusCacheUtility.read(rmsTaskTrace.currentStatus)
            AppUser appUser = (AppUser) appUserCacheUtility.read(rmsTaskTrace.createdBy)
            String strClass = rmsTaskTrace.isRevised ? BACKWARD : FORWARD
            String strColor = rmsTaskTrace.isRevised ? RED : GREEN
            String strRevised = rmsTaskTrace.isRevised ? RED : Tools.EMPTY_SPACE
            Map obj = [
                    fromStatus: (fromStatus ? fromStatus.key : Tools.NOT_APPLICABLE),
                    toStatus: (toStatus ? toStatus.key : Tools.NOT_APPLICABLE),
                    user: (appUser ? appUser.username : Tools.EMPTY_SPACE),
                    date: DateUtility.getDateTimeFormatAsString(rmsTaskTrace.createdOn),
                    strClass: strClass,
                    strColor: strColor,
                    strRevised: strRevised
            ]
            lstHistory << obj
        }
        return lstHistory
    }
}
