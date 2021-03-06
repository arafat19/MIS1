package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.ReservedSystemEntity

/**
 *  Service class for basic ReservedSystemEntity CRUD
 *  For details go through Use-Case doc named 'ReservedSystemEntityService'
 */
/**
 * UPDATE THIS DOCUMENT EVERY TIME AFTER CREATING NEW RESERVED SYSTEM ENTITY
 * REGISTER ALL INFORMATION FOR LASTLY CREATED RESERVED SYSTEM ENTITY
 * LAST COUNTER - 103. so next applicable counter - 104.
 * RESERVED SYSTEM ENTITY - Content Type
 * PLUGIN - Document
 * LAST UPDATED BY - Rezaul(07/08/2014)
 */

class ReservedSystemEntityService extends BaseService {
    // ALWAYS UPDATE THIS AFTER CREATING NEW ONE
    private static final long LAST_COUNTER_FOR_RESERVED = 103L

    private static final String QUERY_CREATE = """INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES(:id, :key, :type, :value, :pluginId);
    """

    private void create(ReservedSystemEntity reservedSystemEntity) {
        Map qParams = [id: reservedSystemEntity.id, key: reservedSystemEntity.key, type: reservedSystemEntity.type, value: reservedSystemEntity.value, pluginId: reservedSystemEntity.pluginId]
        executeInsertSql(QUERY_CREATE, qParams)
    }

    public List<ReservedSystemEntity> findAllByPluginId(int pluginConnector) {
        List<ReservedSystemEntity> lstReservedSystemEntity = ReservedSystemEntity.findAllByPluginId(pluginConnector, [readOnly: true])
        return lstReservedSystemEntity
    }

    public void createDefaultDataForBudget() {
        ReservedSystemEntity budgTaskStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 3721, pluginId: 3)
        budgTaskStatusDefined.id = 389L
        create(budgTaskStatusDefined)
        ReservedSystemEntity budgTaskStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 3721, pluginId: 3)
        budgTaskStatusInProgress.id = 390L
        create(budgTaskStatusInProgress)
        ReservedSystemEntity budgTaskStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 3721, pluginId: 3)
        budgTaskStatusCompleted.id = 391L
        create(budgTaskStatusCompleted)
    }

    public void createDefaultDataForAccounting() {
        ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 51, pluginId: 2)
        none.id = 21
        create(none)
        ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 51, pluginId: 2)
        customer.id = 22
        create(customer)
        ReservedSystemEntity employee = new ReservedSystemEntity(key: 'Employee', value: 'Employee', type: 51, pluginId: 2)
        employee.id = 23
        create(employee)
        ReservedSystemEntity subAccount = new ReservedSystemEntity(key: 'Sub-Account', value: 'Sub-Account', type: 51, pluginId: 2)
        subAccount.id = 24
        create(subAccount)
        ReservedSystemEntity supplier = new ReservedSystemEntity(key: 'Supplier', value: 'Supplier', type: 51, pluginId: 2)
        supplier.id = 25
        create(supplier)
        ReservedSystemEntity item = new ReservedSystemEntity(key: 'Item', value: 'Item', type: 51, pluginId: 2)
        item.id = 26
        create(item)
        ReservedSystemEntity lc = new ReservedSystemEntity(key: 'LC', value: 'LC', type: 51, pluginId: 2)
        lc.id = 27
        create(lc)
        ReservedSystemEntity ipc = new ReservedSystemEntity(key: 'IPC', value: 'IPC', type: 51, pluginId: 2)
        ipc.id = 28
        create(ipc)
        ReservedSystemEntity leaseAccount = new ReservedSystemEntity(key: 'Lease Account', value: 'Lease Account', type: 51, pluginId: 2)
        leaseAccount.id = 29
        create(leaseAccount)

        ReservedSystemEntity pb = new ReservedSystemEntity(key: 'Payment Voucher-Bank', value: 'PB', type: 101, pluginId: 2)
        pb.id = 210
        create(pb)
        ReservedSystemEntity pc = new ReservedSystemEntity(key: 'Payment Voucher-Cash', value: 'PC', type: 101, pluginId: 2)
        pc.id = 211
        create(pc)
        ReservedSystemEntity rb = new ReservedSystemEntity(key: 'Received Voucher-Bank', value: 'RB', type: 101, pluginId: 2)
        rb.id = 212
        create(rb)
        ReservedSystemEntity rc = new ReservedSystemEntity(key: 'Received Voucher-Cash', value: 'RC', type: 101, pluginId: 2)
        rc.id = 213
        create(rc)
        ReservedSystemEntity jr = new ReservedSystemEntity(key: 'Journal', value: 'JR', type: 101, pluginId: 2)
        jr.id = 214
        create(jr)

        ReservedSystemEntity iouTrace = new ReservedSystemEntity(key: 'IOU Trace', value: 'IOU Trace', type: 601, pluginId: 2)
        iouTrace.id = 234
        create(iouTrace)
        ReservedSystemEntity poTrace = new ReservedSystemEntity(key: 'PO Trace', value: 'PO Trace', type: 601, pluginId: 2)
        poTrace.id = 235
        create(poTrace)
    }

    public void createDefaultDataForInventory() {
        ReservedSystemEntity rawMaterial = new ReservedSystemEntity(key: 'Raw Material', value: 'Raw Material', type: 151, pluginId: 4)
        rawMaterial.id = 415
        create(rawMaterial)
        ReservedSystemEntity finishedProduct = new ReservedSystemEntity(key: 'Finished Product', value: 'Finished Product', type: 151, pluginId: 4)
        finishedProduct.id = 416
        create(finishedProduct)

        ReservedSystemEntity invIn = new ReservedSystemEntity(key: 'IN', value: 'IN', type: 301, pluginId: 4)
        invIn.id = 417
        create(invIn)
        ReservedSystemEntity invOut = new ReservedSystemEntity(key: 'OUT', value: 'OUT', type: 301, pluginId: 4)
        invOut.id = 418
        create(invOut)
        ReservedSystemEntity consumption = new ReservedSystemEntity(key: 'Consumption', value: 'Consumption', type: 301, pluginId: 4)
        consumption.id = 419
        create(consumption)
        ReservedSystemEntity production = new ReservedSystemEntity(key: 'Production', value: 'Production', type: 301, pluginId: 4)
        production.id = 420
        create(production)
        ReservedSystemEntity adjustment = new ReservedSystemEntity(key: 'Adjustment', value: 'Adjustment', type: 301, pluginId: 4)
        adjustment.id = 421
        create(adjustment)
        ReservedSystemEntity revAdjustment = new ReservedSystemEntity(key: 'Reverse Adjustment', value: 'Reverse Adjustment', type: 301, pluginId: 4)
        revAdjustment.id = 422
        create(revAdjustment)

        ReservedSystemEntity inventory = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 351, pluginId: 4)
        inventory.id = 423
        create(inventory)
        ReservedSystemEntity supplier = new ReservedSystemEntity(key: 'Supplier', value: 'Supplier', type: 351, pluginId: 4)
        supplier.id = 424
        create(supplier)
        ReservedSystemEntity none = new ReservedSystemEntity(key: 'None', value: 'None', type: 351, pluginId: 4)
        none.id = 425
        create(none)
        ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 351, pluginId: 4)
        customer.id = 426
        create(customer)

        ReservedSystemEntity store = new ReservedSystemEntity(key: 'STORE', value: 'STORE', type: 501, pluginId: 4)
        store.id = 430
        create(store)
        ReservedSystemEntity site = new ReservedSystemEntity(key: 'SITE', value: 'SITE', type: 501, pluginId: 4)
        site.id = 431
        create(site)
    }

    public void createDefaultDataForApplication() {
        ReservedSystemEntity fifo = new ReservedSystemEntity(key: 'FIFO', value: 'FIFO', type: 451, pluginId: 1)
        fifo.id = 127
        create(fifo)
        ReservedSystemEntity lifo = new ReservedSystemEntity(key: 'LIFO', value: 'LIFO', type: 451, pluginId: 1)
        lifo.id = 128
        create(lifo)
        ReservedSystemEntity avg = new ReservedSystemEntity(key: 'AVG', value: 'AVG', type: 451, pluginId: 1)
        avg.id = 129
        create(avg)

        ReservedSystemEntity purchased = new ReservedSystemEntity(key: 'Purchased', value: 'Purchased', type: 551, pluginId: 1)
        purchased.id = 132
        create(purchased)
        ReservedSystemEntity rental = new ReservedSystemEntity(key: 'Rental', value: 'Rental', type: 551, pluginId: 1)
        rental.id = 133
        create(rental)

        ReservedSystemEntity customer = new ReservedSystemEntity(key: 'Customer', value: 'Customer', type: 651, pluginId: 1)
        customer.id = 136
        create(customer)
        ReservedSystemEntity bankBranch = new ReservedSystemEntity(key: 'Bank Branch', value: 'Bank Branch', type: 651, pluginId: 1)
        bankBranch.id = 137
        create(bankBranch)
        ReservedSystemEntity project = new ReservedSystemEntity(key: 'Project', value: 'Project', type: 651, pluginId: 1)
        project.id = 138
        create(project)
        ReservedSystemEntity inventory = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 651, pluginId: 1)
        inventory.id = 139
        create(inventory)
        ReservedSystemEntity group = new ReservedSystemEntity(key: 'Group', value: 'Group', type: 651, pluginId: 1)
        group.id = 140
        create(group)
        ReservedSystemEntity agent = new ReservedSystemEntity(key: 'Agent', value: 'Agent', type: 651, pluginId: 1)
        agent.id = 141
        create(agent)

        ReservedSystemEntity user = new ReservedSystemEntity(key: 'AppUser', value: 'AppUser', type: 701, pluginId: 1)
        user.id = 142
        create(user)
        ReservedSystemEntity company = new ReservedSystemEntity(key: 'Company', value: 'Company', type: 701, pluginId: 1)
        company.id = 143
        create(company)
        ReservedSystemEntity customerExh = new ReservedSystemEntity(key: 'Customer (Exh)', value: 'Customer (Exh)', type: 701, pluginId: 1)
        customerExh.id = 144
        create(customerExh)
        ReservedSystemEntity entityProject = new ReservedSystemEntity(key: 'Project', value: 'Project', type: 701, pluginId: 1)
        entityProject.id = 145
        create(entityProject)
        ReservedSystemEntity entityBudget = new ReservedSystemEntity(key: 'BOQ Line Item', value: 'BOQ Line Item', type: 701, pluginId: 1)
        entityBudget.id = 184
        create(entityBudget)
        ReservedSystemEntity entityBudgSprint = new ReservedSystemEntity(key: 'Sprint', value: 'Sprint', type: 701, pluginId: 1)
        entityBudgSprint.id = 192
        create(entityBudgSprint)
        ReservedSystemEntity entityFinancialYear = new ReservedSystemEntity(key: 'Financial Year', value: 'Financial Year', type: 701, pluginId: 1)
        entityFinancialYear.id = 185
        create(entityFinancialYear)

        ReservedSystemEntity document = new ReservedSystemEntity(key: 'Document', value: 'Document', type: 702, pluginId: 1)
        document.id = 146
        create(document)
        ReservedSystemEntity image = new ReservedSystemEntity(key: 'Image', value: 'Image', type: 702, pluginId: 1)
        image.id = 147
        create(image)

        ReservedSystemEntity task = new ReservedSystemEntity(key: 'TASK', value: 'Note Entity Type Task', type: 703, pluginId: 1)
        task.id = 148
        create(task)
        ReservedSystemEntity customerNote = new ReservedSystemEntity(key: 'CUSTOMER', value: 'Note Entity Type Customer', type: 703, pluginId: 1)
        customerNote.id = 149
        create(customerNote)

        ReservedSystemEntity inventoryItem = new ReservedSystemEntity(key: 'Inventory', value: 'Inventory', type: 705, pluginId: 1)
        inventoryItem.id = 150
        create(inventoryItem)
        ReservedSystemEntity nonInventory = new ReservedSystemEntity(key: 'Non Inventory', value: 'Non Inventory', type: 705, pluginId: 1)
        nonInventory.id = 151
        create(nonInventory)
        ReservedSystemEntity fixedAsset = new ReservedSystemEntity(key: 'Fixed Asset', value: 'Fixed Asset', type: 705, pluginId: 1)
        fixedAsset.id = 152
        create(fixedAsset)

        ReservedSystemEntity male = new ReservedSystemEntity(key: 'Male', value: 'M', type: 1717, pluginId: 1)
        male.id = 176
        create(male)

        ReservedSystemEntity female = new ReservedSystemEntity(key: 'Female', value: 'F', type: 1717, pluginId: 1)
        female.id = 177
        create(female)

    }

    public void createDefaultDataForPT() {
        ReservedSystemEntity project = new ReservedSystemEntity(key: 'Pt Project', value: 'Pt Project', type: 651, pluginId: 10)
        project.id = 1059

        ReservedSystemEntity backlogPriorityHigh = new ReservedSystemEntity(key: 'High', value: 'High', type: 10706, pluginId: 10)
        backlogPriorityHigh.id = 1032L
        create(backlogPriorityHigh)
        ReservedSystemEntity backlogPriorityMedium = new ReservedSystemEntity(key: 'Medium', value: 'Medium', type: 10706, pluginId: 10)
        backlogPriorityMedium.id = 1033L
        create(backlogPriorityMedium)
        ReservedSystemEntity backlogPriorityLow = new ReservedSystemEntity(key: 'Low', value: 'Low', type: 10706, pluginId: 10)
        backlogPriorityLow.id = 1034L
        create(backlogPriorityLow)

        ReservedSystemEntity backlogStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10707, pluginId: 10)
        backlogStatusDefined.id = 1035L
        create(backlogStatusDefined)
        ReservedSystemEntity backlogStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10707, pluginId: 10)
        backlogStatusInProgress.id = 1036L
        create(backlogStatusInProgress)
        ReservedSystemEntity backlogStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10707, pluginId: 10)
        backlogStatusCompleted.id = 1037L
        create(backlogStatusCompleted)
        ReservedSystemEntity backlogStatusAccepted = new ReservedSystemEntity(key: 'Accepted', value: 'Accepted', type: 10707, pluginId: 10)
        backlogStatusAccepted.id = 1038L
        create(backlogStatusAccepted)

        ReservedSystemEntity sprintStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10708, pluginId: 10)
        sprintStatusDefined.id = 1039L
        create(sprintStatusDefined)
        ReservedSystemEntity sprintStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10708, pluginId: 10)
        sprintStatusInProgress.id = 1040L
        create(sprintStatusInProgress)
        ReservedSystemEntity sprintStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10708, pluginId: 10)
        sprintStatusCompleted.id = 1041L
        create(sprintStatusCompleted)
        ReservedSystemEntity sprintStatusClosed = new ReservedSystemEntity(key: 'Closed', value: 'Closed', type: 10708, pluginId: 10)
        sprintStatusClosed.id = 1095L
        create(sprintStatusClosed)

        ReservedSystemEntity acceptanceStatusDefined = new ReservedSystemEntity(key: 'Defined', value: 'Defined', type: 10711, pluginId: 10)
        acceptanceStatusDefined.id = 1042L
        create(acceptanceStatusDefined)
        ReservedSystemEntity acceptanceStatusInProgress = new ReservedSystemEntity(key: 'In Progress', value: 'In Progress', type: 10711, pluginId: 10)
        acceptanceStatusInProgress.id = 1043L
        create(acceptanceStatusInProgress)
        ReservedSystemEntity acceptanceStatusCompleted = new ReservedSystemEntity(key: 'Completed', value: 'Completed', type: 10711, pluginId: 10)
        acceptanceStatusCompleted.id = 1044L
        create(acceptanceStatusCompleted)
        ReservedSystemEntity acceptanceStatusBlocked = new ReservedSystemEntity(key: 'Blocked', value: 'Blocked', type: 10711, pluginId: 10)
        acceptanceStatusBlocked.id = 1045L
        create(acceptanceStatusBlocked)

        ReservedSystemEntity bugSeverityHigh = new ReservedSystemEntity(key: 'High', value: 'High', type: 10709, pluginId: 10)
        bugSeverityHigh.id = 1046L
        create(bugSeverityHigh)
        ReservedSystemEntity bugSeverityMedium = new ReservedSystemEntity(key: 'Medium', value: 'Medium', type: 10709, pluginId: 10)
        bugSeverityMedium.id = 1047L
        create(bugSeverityMedium)
        ReservedSystemEntity bugSeverityLow = new ReservedSystemEntity(key: 'Low', value: 'Low', type: 10709, pluginId: 10)
        bugSeverityLow.id = 1048L
        create(bugSeverityLow)

        ReservedSystemEntity bugStatusSubmitted = new ReservedSystemEntity(key: 'Submitted', value: 'Submitted', type: 10710, pluginId: 10)
        bugStatusSubmitted.id = 1049L
        create(bugStatusSubmitted)
        ReservedSystemEntity bugStatusOpen = new ReservedSystemEntity(key: 'Re-opened', value: 'Re-opened', type: 10710, pluginId: 10)
        bugStatusOpen.id = 1050L
        create(bugStatusOpen)
        ReservedSystemEntity bugStatusFixed = new ReservedSystemEntity(key: 'Fixed', value: 'Fixed', type: 10710, pluginId: 10)
        bugStatusFixed.id = 1051L
        create(bugStatusFixed)
        ReservedSystemEntity bugStatusClosed = new ReservedSystemEntity(key: 'Closed', value: 'Closed', type: 10710, pluginId: 10)
        bugStatusClosed.id = 1052L
        create(bugStatusClosed)

        ReservedSystemEntity bugTypeFunctional = new ReservedSystemEntity(key: 'Functional', value: 'Functional', type: 10712, pluginId: 10)
        bugTypeFunctional.id = 1053L
        create(bugTypeFunctional)
        ReservedSystemEntity bugTypeUserInterface = new ReservedSystemEntity(key: 'User Interface', value: 'User Interface', type: 10712, pluginId: 10)
        bugTypeUserInterface.id = 1054L
        create(bugTypeUserInterface)
        ReservedSystemEntity bugTypeInconsistency = new ReservedSystemEntity(key: 'Inconsistency', value: 'Inconsistency', type: 10712, pluginId: 10)
        bugTypeInconsistency.id = 1055L
        create(bugTypeInconsistency)
        ReservedSystemEntity bugTypePerformance = new ReservedSystemEntity(key: 'Performance', value: 'Performance', type: 10712, pluginId: 10)
        bugTypePerformance.id = 1056L
        create(bugTypePerformance)
        ReservedSystemEntity bugTypeSuggestion = new ReservedSystemEntity(key: 'Suggestion', value: 'Suggestion', type: 10712, pluginId: 10)
        bugTypeSuggestion.id = 1057L
        create(bugTypeSuggestion)

        ReservedSystemEntity acceptanceCriteriaTypePreCondition = new ReservedSystemEntity(key: 'Pre-condition', value: 'Pre-condition', type: 10718, pluginId: 10)
        acceptanceCriteriaTypePreCondition.id = 1078L
        create(acceptanceCriteriaTypePreCondition)
        ReservedSystemEntity acceptanceCriteriaTypeBusinessLogic = new ReservedSystemEntity(key: 'Business Logic', value: 'Business Logic', type: 10718, pluginId: 10)
        acceptanceCriteriaTypeBusinessLogic.id = 1079L
        create(acceptanceCriteriaTypeBusinessLogic)
        ReservedSystemEntity acceptanceCriteriaTypePostCondition = new ReservedSystemEntity(key: 'Post-condition', value: 'Post-condition', type: 10718, pluginId: 10)
        acceptanceCriteriaTypePostCondition.id = 1080L
        create(acceptanceCriteriaTypePostCondition)

        ReservedSystemEntity acceptanceCriteriaTypeOthers = new ReservedSystemEntity(key: 'Others', value: 'Others', type: 10718, pluginId: 10)
        acceptanceCriteriaTypeOthers.id = 1096L
        create(acceptanceCriteriaTypeOthers)

        ReservedSystemEntity bug = new ReservedSystemEntity(key: 'Bug', value: 'Bug', type: 701, pluginId: 10)
        bug.id = 1058L
        create(bug)

        ReservedSystemEntity ptTask = new ReservedSystemEntity(key: 'Task', value: 'Note Entity Type Pt Task', type: 703, pluginId: 10)
        ptTask.id = 1094
        create(ptTask)
    }

    public void createDefaultDataForArms() {
        ReservedSystemEntity processIssue = new ReservedSystemEntity(key: 'Issue', value: 'Issue', type: 11713, pluginId: 11)
        processIssue.id = 1150L
        create(processIssue)
        ReservedSystemEntity processForward = new ReservedSystemEntity(key: 'Forward', value: 'Forward', type: 11713, pluginId: 11)
        processForward.id = 1151L
        create(processForward)
        ReservedSystemEntity processPurchase = new ReservedSystemEntity(key: 'Purchase', value: 'Purchase', type: 11713, pluginId: 11)
        processPurchase.id = 1152L
        create(processPurchase)

        ReservedSystemEntity instrumentPo = new ReservedSystemEntity(key: 'PO', value: 'PO', type: 11714, pluginId: 11)
        instrumentPo.id = 1153L
        create(instrumentPo)
        ReservedSystemEntity instrumentEft = new ReservedSystemEntity(key: 'EFT', value: 'EFT', type: 11714, pluginId: 11)
        instrumentEft.id = 1154L
        create(instrumentEft)
        ReservedSystemEntity instrumentOnline = new ReservedSystemEntity(key: 'Online', value: 'Online', type: 11714, pluginId: 11)
        instrumentOnline.id = 1155L
        create(instrumentOnline)
        ReservedSystemEntity instrumentCashCollection = new ReservedSystemEntity(key: 'Cash collection', value: 'Cash collection', type: 11714, pluginId: 11)
        instrumentCashCollection.id = 1156L
        create(instrumentCashCollection)
        ReservedSystemEntity instrumentTt = new ReservedSystemEntity(key: 'TT', value: 'TT', type: 11714, pluginId: 11)
        instrumentTt.id = 1157L
        create(instrumentTt)
        ReservedSystemEntity instrumentMt = new ReservedSystemEntity(key: 'MT', value: 'MT', type: 11714, pluginId: 11)
        instrumentMt.id = 1158L
        create(instrumentMt)

        ReservedSystemEntity payMethodBankDeposit = new ReservedSystemEntity(key: 'Bank deposit', value: 'Bank deposit', type: 11715, pluginId: 11)
        payMethodBankDeposit.id = 1160L
        create(payMethodBankDeposit)
        ReservedSystemEntity payMethodCashCollection = new ReservedSystemEntity(key: 'Cash collection', value: 'Cash collection', type: 11715, pluginId: 11)
        payMethodCashCollection.id = 1161L
        create(payMethodCashCollection)

        ReservedSystemEntity taskStatusPendingTask = new ReservedSystemEntity(key: 'Pending task', value: 'Pending  task', type: 11716, pluginId: 11)
        taskStatusPendingTask.id = 1162L
        create(taskStatusPendingTask)
        ReservedSystemEntity taskStatusNewTask = new ReservedSystemEntity(key: 'New task', value: 'New  task', type: 11716, pluginId: 11)
        taskStatusNewTask.id = 1163L
        create(taskStatusNewTask)
        ReservedSystemEntity taskStatusIncludeInLst = new ReservedSystemEntity(key: 'Included in list', value: 'Included in list', type: 11716, pluginId: 11)
        taskStatusIncludeInLst.id = 1164L
        create(taskStatusIncludeInLst)
        ReservedSystemEntity taskStatusDecisionTaken = new ReservedSystemEntity(key: 'Decision taken', value: 'Decision taken', type: 11716, pluginId: 11)
        taskStatusDecisionTaken.id = 1165L
        create(taskStatusDecisionTaken)
        ReservedSystemEntity taskStatusDecisionApproved = new ReservedSystemEntity(key: 'Decision approved', value: 'Decision approved', type: 11716, pluginId: 11)
        taskStatusDecisionApproved.id = 1166L
        create(taskStatusDecisionApproved)
        ReservedSystemEntity taskStatusDisbursed = new ReservedSystemEntity(key: 'Disbursed', value: 'Disbursed', type: 11716, pluginId: 11)
        taskStatusDisbursed.id = 1167L
        create(taskStatusDisbursed)
        ReservedSystemEntity taskStatusCanceled = new ReservedSystemEntity(key: 'Canceled', value: 'Canceled', type: 11716, pluginId: 11)
        taskStatusCanceled.id = 1168L
        create(taskStatusCanceled)
        ReservedSystemEntity rmsTaskNote = new ReservedSystemEntity(key: 'RmsTask', value: 'Note Entity Type RmsTask', type: 703, pluginId: 11)
        rmsTaskNote.id = 1181L
        create(rmsTaskNote)
        ReservedSystemEntity rmsExhUser = new ReservedSystemEntity(key: 'Exchange House', value: 'Exchange House', type: 651, pluginId: 11)
        rmsExhUser.id = 1186L
        create(rmsExhUser)
    }

    public void createDefaultDataForExh() {
        ReservedSystemEntity cash = new ReservedSystemEntity(key: 'Cash', value: 'Cash', type: 2001, pluginId: 9)
        cash.id = 950L
        create(cash)
        ReservedSystemEntity onlineTransfer = new ReservedSystemEntity(key: 'Online Transfer', value: 'Online Transfer', type: 2001, pluginId: 9)
        onlineTransfer.id = 951L
        create(onlineTransfer)
        ReservedSystemEntity payPoint = new ReservedSystemEntity(key: 'Pay Point', value: 'Pay Point', type: 2001, pluginId: 9)
        payPoint.id = 952L
        create(payPoint)

        ReservedSystemEntity bankDeposit = new ReservedSystemEntity(key: 'Bank Deposit', value: '1', type: 2002, pluginId: 9)
        bankDeposit.id = 953L
        create(bankDeposit)
        ReservedSystemEntity cashCollection = new ReservedSystemEntity(key: 'Cash Collection', value: '2', type: 2002, pluginId: 9)
        cashCollection.id = 954L
        create(cashCollection)

        ReservedSystemEntity cancelled = new ReservedSystemEntity(key: 'Cancelled Task', value: 'Cancelled Task', type: 2003, pluginId: 9)
        cancelled.id = 956L
        create(cancelled)
        ReservedSystemEntity pendingTask = new ReservedSystemEntity(key: 'Pending Task', value: 'Pending Task', type: 2003, pluginId: 9)
        pendingTask.id = 957L
        create(pendingTask)
        ReservedSystemEntity newTask = new ReservedSystemEntity(key: 'New Task', value: 'New Task', type: 2003, pluginId: 9)
        newTask.id = 958L
        create(newTask)
        ReservedSystemEntity sendToBank = new ReservedSystemEntity(key: 'Send To Bank', value: 'Send To Bank', type: 2003, pluginId: 9)
        sendToBank.id = 959L
        create(sendToBank)
        ReservedSystemEntity sentToOtherBank = new ReservedSystemEntity(key: 'Sent To Other Bank', value: 'Sent To Other Bank', type: 2003, pluginId: 9)
        sentToOtherBank.id = 960L
        create(sentToOtherBank)
        ReservedSystemEntity resolvedByOtherBank = new ReservedSystemEntity(key: 'Resolved By Other Bank', value: 'Resolved By Other Bank', type: 2003, pluginId: 9)
        resolvedByOtherBank.id = 961L
        create(resolvedByOtherBank)
        ReservedSystemEntity unapprovedTask = new ReservedSystemEntity(key: 'Unapproved Task', value: 'Unapproved Task', type: 2003, pluginId: 9)
        unapprovedTask.id = 962L
        create(unapprovedTask)

        ReservedSystemEntity refundTask = new ReservedSystemEntity(key: 'Refund Task', value: 'Refund Task', type: 2003, pluginId: 9)
        refundTask.id = 997L
        create(refundTask)

        ReservedSystemEntity exhTask = new ReservedSystemEntity(key: 'Exh Task', value: 'Exh Task', type: 2004, pluginId: 9)
        exhTask.id = 963L
        create(exhTask)
        ReservedSystemEntity agentTask = new ReservedSystemEntity(key: 'Agent Task', value: 'Agent Task', type: 2004, pluginId: 9)
        agentTask.id = 964L
        create(agentTask)
        ReservedSystemEntity customerTask = new ReservedSystemEntity(key: 'Customer Task', value: 'Customer Task', type: 2004, pluginId: 9)
        customerTask.id = 965L
        create(customerTask)

    }

    public void createDefaultDataForDocument() {
        ReservedSystemEntity vendorPostgre = new ReservedSystemEntity(key: 'PostgreSQL', value: 'org.postgresql.Driver', type: 13722, pluginId: 13)
        vendorPostgre.id = 1393L
        create(vendorPostgre)
        ReservedSystemEntity vendorMysql = new ReservedSystemEntity(key: 'MySQL', value: 'com.mysql.jdbc.Driver', type: 13722, pluginId: 13)
        vendorMysql.id = 1394L
        create(vendorMysql)

        ReservedSystemEntity all = new ReservedSystemEntity(key: 'All', value: 'All', type: 13724, pluginId: 13)
        all.id = 13101L
        create(all)
        ReservedSystemEntity file = new ReservedSystemEntity(key: 'File', value: 'File', type: 13724, pluginId: 13)
        file.id = 13102L
        create(file)
        ReservedSystemEntity article = new ReservedSystemEntity(key: 'Article', value: 'Article', type: 13724, pluginId: 13)
        article.id = 13103L
        create(article)
    }
    
    public void createDefaultDataForSarb() {
        ReservedSystemEntity movedForCancel = new ReservedSystemEntity(key: 'Moved for cancel', value: 'Moved for cancel', type: 12723, pluginId: 12)
        movedForCancel.id = 1298L
        create(movedForCancel)

        ReservedSystemEntity movedForReplace = new ReservedSystemEntity(key: 'Moved for replace', value: 'Moved for replace', type: 12723, pluginId: 12)
        movedForReplace.id = 1299L
        create(movedForReplace)

        ReservedSystemEntity movedForRefund = new ReservedSystemEntity(key: 'Moved for refund', value: 'Moved for refund', type: 12723, pluginId: 12)
        movedForRefund.id = 12100L
        create(movedForRefund)
    }

}
