package com.athena.mis.arms.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ListTaskByRefOrPinTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility1
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility1

    RmsTaskListService rmsTaskListService

    private static final String ID = "id"
    private static final String PROPERTY_NAME = "property_name"
    private static final String PROPERTY_VALUE = "property_value"
    private static final String TASK_LIST = "task_list"
    private static final String FROM_DATE = "from_date"
    private static final String TO_DATE = "to_date"
    private static final String HAS_EXH_ROLE = "hasExhRole"
    private static final String HAS_BRANCH_ROLE = "hasBranchRole"
    private static final String HAS_OTHER_ROLE = "hasOtherRole"

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
            String propertyName = attrs.get(PROPERTY_NAME)
            def propertyValue = attrs.get(PROPERTY_VALUE)
            String fromDate = attrs.get(FROM_DATE)
            String toDate = attrs.get(TO_DATE)
            List<RmsTask> lstRmsTask = (List<RmsTask>) attrs.get(TASK_LIST)
            if (!lstRmsTask || (lstRmsTask.size() == 0)) {
                if (!propertyName || !propertyValue) {
                    return attrMap
                }
            }
            attrMap.put(TASK_LIST, lstRmsTask)
            attrMap.put(PROPERTY_NAME, propertyName)
            attrMap.put(PROPERTY_VALUE, propertyValue)
            attrMap.put(FROM_DATE, fromDate)
            attrMap.put(TO_DATE, toDate)
            attrMap.put(Tools.IS_ERROR, Boolean.FALSE)
            boolean hasExhRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_EXCHANGE_HOUSE_USER)
            boolean hasBranchRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_BRANCH_USER)
            boolean hasOtherBankRole = rmsSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_ARMS_OTHER_BANK_USER)
            attrMap.put(HAS_EXH_ROLE, hasExhRole)
            attrMap.put(HAS_BRANCH_ROLE, hasBranchRole)
            attrMap.put(HAS_OTHER_ROLE, hasOtherBankRole)
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
            Date fromDate = DateUtility.parseMaskedFromDate(strFromDate)
            Date toDate = DateUtility.parseMaskedToDate(strToDate)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(TASK_LIST)
            String propertyName = preResult.get(PROPERTY_NAME)
            def propertyValue = preResult.get(PROPERTY_VALUE)
            List<RmsTask> taskList = (lstRmsTask.size() > 0) ? lstRmsTask : getRmsTaskList(fromDate, toDate, propertyName, propertyValue, isRestrictExchangeHouse, isRestrictBranch)
            String taskDetails = buildTaskDetails(taskList, preResult)
            return taskDetails
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

    /**
     * build taskDetails html
     * @param rmsTask - RmsTaskObj
     * @param attrMap
     * @return - html
     */
    private String buildTaskDetails(List<RmsTask> lstRmsTask, Map attrMap) {
        if (!lstRmsTask || (lstRmsTask.size() == 0)) return Tools.EMPTY_SPACE
        List lstWrappedTask = wrapTask(lstRmsTask, start)
        Map gridMap = [page: pageNumber, total: lstRmsTask.size(), rows: lstWrappedTask]
        String dataSource = gridMap as JSON
        String html = """
        <table id="flex" style="display:none"></table>
        <script>
            \$(document).ready(function(){
                \$("#flex").flexigrid
                (
                        {
                            url: false,
                            dataType: 'json',
                            colModel: [
                                {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                                {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                                {display: "Ref No", name: "ref_no", width: 120, sortable: true, align: "left"},
                                {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                                {display: "Value Date", name: "value_date", width: 80, sortable: false, align: "left"},
                                {display: "Beneficiary Name", name: "beneficiary_name", width: 100, sortable: false, align: "left"},
                                {display: "Outlet", name: "outlet", width: 200, sortable: false, align: "left"},
                                {display: "Payment Method", name: "payment_method", width: 110, sortable: false, align: "left"},
                                {display: "Created On", name: "created_on", width: 80, sortable: false, align: "left"},
                                {display: "Exchange House", name: "exchange_house", width: 170, sortable: false, align: "left"}
                            ],
                            buttons: [
                                {name:'View Details', bclass: 'view', onpress: viewTaskDetails}
                            ],
                            usepager: false,
                            singleSelect: true,
                            title: 'All Task',
                            useRp: false,
                            showTableToggleBtn: false,
                            height: getGridHeight()
                        }
                );
                \$('#flex').flexAddData(${dataSource});
            });
            function viewTaskDetails() {
                if (executeCommonPreConditionForSelect(\$('#flex'), 'task',true) == false) {
                    return;
                }
                showLoadingSpinner(true);
                var id = getSelectedIdFromGrid(\$('#flex'));
                var propertyValue=id;
                var propertyName="id";
                var params="?property_value="+propertyValue+"&property_name="+propertyName;
                var loc = "rmsTask/showTaskDetailsWithNote" + params;
                \$.history.load(formatLink(loc));
            }
        </script>
        """
        return html
    }

    private List wrapTask(List<RmsTask> lstTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTasks.size(); i++) {
            RmsTask task = lstTasks[i]
            SystemEntity paymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.read(task.paymentMethod)
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(task.exchangeHouseId)
            GridEntity obj = new GridEntity()
            obj.id = task.id
            obj.cell = [
                    counter,
                    task.id,
                    task.refNo,
                    task.amount,
                    DateUtility.getLongDateForUI(task.valueDate),
                    task.beneficiaryName,
                    task.getFullOutletName(),
                    paymentMethod? paymentMethod.key : Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(task.createdOn),
                    exchangeHouse? exchangeHouse.name : Tools.EMPTY_SPACE
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }

    /**
     * find the rmsTask
     * @return - RmsTask obj
     */
    private List<RmsTask> getRmsTaskList(Date fromDate, Date toDate, String propertyName,
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
