package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntityType

/**
 *  Service class for basic SystemEntityType CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SystemEntityTypeService'
 */
/**
 * UPDATE THIS DOCUMENT EVERY TIME AFTER CREATING NEW SYSTEM ENTITY TYPE
 * REGISTER ALL INFORMATION FOR LASTLY CREATED SYSTEM ENTITY TYPE
 * LAST COUNTER - 724. so next applicable counter - 725.
 * TYPE - docContentType
 * PLUGIN - Document
 * LAST UPDATED BY - Rezaul(07/08/2014)
 */

class SystemEntityTypeService extends BaseService {
    // ALWAYS UPDATE THIS AFTER CREATING NEW ONE
    private static final long LAST_COUNTER_FOR_TYPE = 724L

    static transactional = false

    /**
     * @return -list of SystemEntityType
     */
    public List list() {
        return SystemEntityType.list(sort: "id", order: "asc", readOnly: true);
    }

    /**
     * Method to read systemEntityType by id
     * @param id - SystemEntityType.id
     * @return - SystemEntityType object
     */
    public SystemEntityType read(long id) {
        SystemEntityType systemEntityType = SystemEntityType.read(id)
        return systemEntityType
    }

    private static final String QUERY_CREATE = """INSERT INTO system_entity_type (id, version,description,name,plugin_id)
        VALUES(:id,:version,:description,:name,:pluginId);
    """

    private void create(SystemEntityType systemEntityType) {
        Map qParams = [id: systemEntityType.id, version: systemEntityType.version, description: systemEntityType.description, name: systemEntityType.name, pluginId: systemEntityType.pluginId]
        executeInsertSql(QUERY_CREATE, qParams)
    }

    /**
     * SQL to update SystemEntityType object in database
     * @param seType -SystemEntityType object
     * @return -int value(updateCount)
     */
    public int update(SystemEntityType seType) {
        String query = """
                    UPDATE system_entity_type SET
                          version=version+1,
                          name=:name,
                          description=:description

                      WHERE
                          id=:id AND
                          version=:version
                          """
        Map queryParams = [
                id: seType.id,
                version: seType.version,
                name: seType.name,
                description: seType.description
        ]

        int updateCount = executeUpdateSql(query, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating information')
        }
        seType.version = seType.version + 1
        return updateCount;
    }

    private static final String COUNT_QUERY = """
           SELECT COUNT(set.id)
           FROM system_entity_type set
    """

    public int countSystemEntityType() {
        List countResults = executeSelectSql(COUNT_QUERY)
        int count = countResults[0].count
        return count
    }

    public void createDefaultDataForBudget() {
        SystemEntityType budgTaskStatus = new SystemEntityType(version: 0, name: 'Budget Task Status', description: 'Defined, In Progress, Completed', pluginId: 3)
        budgTaskStatus.id = 3721L
        create(budgTaskStatus)
    }

    /**
     * insert accounting module default data into database when application starts with bootstrap
     */
    public void createDefaultDataForAcc() {

        SystemEntityType accSourceType = new SystemEntityType(version: 0, name: 'Accounting Source Type', description: 'None, Customer, Employee, Sub-Account, Supplier, Material, Fixed-Asset, Work', pluginId: 2)
        accSourceType.id = 51L
        create(accSourceType)

        SystemEntityType accVoucherType = new SystemEntityType(version: 0, name: 'Accounting Voucher Type', description: 'Payment-Voucher-Bank, Payment-Voucher-Cash, Received-Voucher-Bank, Received-Voucher-Cash, Journal', pluginId: 2)
        accVoucherType.id = 101L
        create(accVoucherType)

        SystemEntityType accInstrumentType = new SystemEntityType(version: 0, name: 'Accounting Instrument Type', description: 'Used in Voucher Create - IOU_ID, PO_ID', pluginId: 2)
        accInstrumentType.id = 601L
        create(accInstrumentType)
    }

    /**
     * insert application module default data into database when application starts with bootstrap
     */
    public void createDefaultDataForApp() {

        SystemEntityType unit = new SystemEntityType(version: 0, name: 'Unit', description: 'Foot, Ton, Bag, CFT, NOS, K.G., Meter, Liter etc.', pluginId: 1)
        unit.id = 251L
        create(unit)

        SystemEntityType invValuationType = new SystemEntityType(version: 0, name: 'Valuation Type', description: 'FIFO, LIFO, AVG', pluginId: 1)
        invValuationType.id = 451L
        create(invValuationType)

        SystemEntityType ownerType = new SystemEntityType(version: 0, name: 'Owner Type', description: 'Owner Type of a Fixed-Asset - Purchased, Rental', pluginId: 1)
        ownerType.id = 551L
        create(ownerType)

        SystemEntityType userMappingEntityType = new SystemEntityType(version: 0, name: 'App-User Mapping Entity Type', description: 'Customer, Bank-Branch, Project, PtProject, Inventory, Group, Pt Project', pluginId: 1)
        userMappingEntityType.id = 651L
        create(userMappingEntityType)

        SystemEntityType contentEntityType = new SystemEntityType(version: 0, name: 'Content Entity Type', description: 'Project, Company, App User, Bug etc', pluginId: 1)
        contentEntityType.id = 701L
        create(contentEntityType)

        SystemEntityType contentType = new SystemEntityType(version: 0, name: 'Content Type', description: 'Document, Image', pluginId: 1)
        contentType.id = 702L
        create(contentType)

        //703;"e.g. Task, Customer etc.";1800;FALSE;"Entity Type of Comment";1301
        SystemEntityType noteType = new SystemEntityType(version: 0, name: 'Entity Type of Note', description: 'e.g. Task, Customer etc.', pluginId: 1)
        noteType.id = 703L
        create(noteType)

        SystemEntityType supplierType = new SystemEntityType(version: 0, name: 'Supplier Type', description: 'Service provider, Material provider etc', pluginId: 1)
        supplierType.id = 704L
        create(supplierType)

        SystemEntityType itemCategory = new SystemEntityType(version: 0, name: 'Item Category', description: 'Inventory, Non-inventory, Fixed Asset etc.', pluginId: 1)
        itemCategory.id = 705L
        create(itemCategory)


        SystemEntityType gender = new SystemEntityType(version: 0, name: 'Gender', description: 'Male; Female', pluginId: 1)
        gender.id = 1717L
        create(gender)

    }

    /**
     * insert inventory module default data into database when application starts with bootstrap
     */
    public void createDefaultDataForInv() {

        SystemEntityType invProdItemType = new SystemEntityType(version: 0, name: 'Inventory Production Item Type', description: 'Raw-Material, Finished-Material', pluginId: 4)
        invProdItemType.id = 151L
        create(invProdItemType)

        SystemEntityType invTransactionType = new SystemEntityType(version: 0, name: 'Inventory Transaction Type', description: 'In, Out, Consumption, Production, Adjustment, Reverse-Adjustment', pluginId: 4)
        invTransactionType.id = 301L
        create(invTransactionType)

        SystemEntityType invTransactionEntityType = new SystemEntityType(version: 0, name: 'Inventory Transaction Entity Type', description: 'None, Inventory, Supplier, Customer', pluginId: 4)
        invTransactionEntityType.id = 351L
        create(invTransactionEntityType)

        SystemEntityType inventoryType = new SystemEntityType(version: 0, name: 'Inventory Type', description: 'Store, Site', pluginId: 4)
        inventoryType.id = 501L
        create(inventoryType)
    }

    /**
     * insert procurement module default data into database when application starts with bootstrap
     */
    public void createDefaultDataForProcurement() {
        SystemEntityType paymentMethodType = new SystemEntityType(version: 0, name: 'Payment Method Type', description: 'Used in Procurement for PO Details - Cash, Cheque, LC', pluginId: 5)
        paymentMethodType.id = 1L
        create(paymentMethodType)
    }

    /**
     * insert exchangeHouse module default data into database when application starts with bootstrap
     */
    public void createDefaultDataForExh() {

        SystemEntityType paidByType = new SystemEntityType(version: 0, name: 'Paid By Type', description: 'Cash, Online, Card', pluginId: 9)
        paidByType.id = 2001L
        create(paidByType)

        SystemEntityType paymentMethodType = new SystemEntityType(version: 0, name: 'Payment Method Type', description: 'Bank Deposit, Cash Collection, Mobile, Remittance Card', pluginId: 9)
        paymentMethodType.id = 2002L
        create(paymentMethodType)

        SystemEntityType taskStatusType = new SystemEntityType(version: 0, name: 'Task Status', description: 'Cancelled, New Task, Send to bank, Sent to other bank, Resolved by other bank', pluginId: 9)
        taskStatusType.id = 2003L
        create(taskStatusType)

        SystemEntityType taskType = new SystemEntityType(version: 0, name: 'Task Type', description: 'Type of Task; Task of Exh, Task of Agent, Task of Customer', pluginId: 9)
        taskType.id = 2004L
        create(taskType)
    }

    public void createDefaultDataForProjectTrack() {
        SystemEntityType backLogPriorityType = new SystemEntityType(version: 0, name: 'Backlog Priority Type', description: 'Backlog priority i.e. high,medium,low', pluginId: 10)
        backLogPriorityType.id = 10706L
        create(backLogPriorityType)

        SystemEntityType backlogStatusType = new SystemEntityType(version: 0, name: 'Backlog Status Type', description: 'Backlog status i.e. defined,in progress,completed,accepted', pluginId: 10)
        backlogStatusType.id = 10707L
        create(backlogStatusType)

        SystemEntityType acceptanceCriteriaStatusType = new SystemEntityType(version: 0, name: 'Acceptance criteria  status type', description: 'Acceptance criteria status i.e. defined,in progress,completed,blocked', pluginId: 10)
        acceptanceCriteriaStatusType.id = 10711L
        create(acceptanceCriteriaStatusType)

        SystemEntityType sprintStatusType = new SystemEntityType(version: 0, name: 'Sprint Status Type', description: 'defined,in progress,completed', pluginId: 10)
        sprintStatusType.id = 10708L
        create(sprintStatusType)

        SystemEntityType bugSeverity = new SystemEntityType(version: 0, name: 'Bug Severity Type', description: 'high,medium,low', pluginId: 10)
        bugSeverity.id = 10709L
        create(bugSeverity)

        SystemEntityType bugStatus = new SystemEntityType(version: 0, name: 'Bug Status Type', description: 'submitted,re-opened,fixed,closed', pluginId: 10)
        bugStatus.id = 10710L
        create(bugStatus)

        SystemEntityType bugType = new SystemEntityType(version: 0, name: 'Bug Type', description: 'functional,user interface,inconsistency,performance,suggestion', pluginId: 10)
        bugType.id = 10712L
        create(bugType)

        SystemEntityType acceptanceCriteriaType = new SystemEntityType(version: 0, name: 'Acceptance Criteria Type', description: 'pre-condition, business logic, post-condition', pluginId: 10)
        acceptanceCriteriaType.id = 10718L
        create(acceptanceCriteriaType)
    }

    public void createDefaultDataForArms() {
        SystemEntityType rmsProcessType = new SystemEntityType(version: 0, name: 'Process Type', description: 'Process i.e. Issue, Forward, Purchase', pluginId: 11)
        rmsProcessType.id = 11713
        create(rmsProcessType)

        SystemEntityType rmsInstrumentType = new SystemEntityType(version: 0, name: 'Instrument Type', description: 'Instrument i.e. PO, EFT, Online, Cash Collection', pluginId: 11)
        rmsInstrumentType.id = 11714
        create(rmsInstrumentType)

        SystemEntityType rmsPaymentMethod = new SystemEntityType(version: 0, name: 'Payment Method', description: 'Payment Method i.e. Bank deposit, Cash collection', pluginId: 11)
        rmsPaymentMethod.id = 11715
        create(rmsPaymentMethod)

        SystemEntityType rmsTaskStatus = new SystemEntityType(version: 0, name: 'Task Status', description: 'Task Status i.e. New task, Included in list, Decision taken etc.', pluginId: 11)
        rmsTaskStatus.id = 11716
        create(rmsTaskStatus)

    }

    public void createDefaultDataForDocument() {
        SystemEntityType dbVendor = new SystemEntityType(version: 0, name: 'Database Vendor', description: 'PostgreSQL, MySQL', pluginId: 13)
        dbVendor.id = 13722
        create(dbVendor)

        SystemEntityType docContentType = new SystemEntityType(version: 0, name: 'Content Type', description: 'All, File, Article', pluginId: 13)
        docContentType.id = 13724
        create(docContentType)
    }

    public void createDefaultDataForSarb() {
        SystemEntityType sarbTaskReviseStatus = new SystemEntityType(version: 0, name: 'Sarb task revise status', description: 'Moved for Cancel, Replace, Refund', pluginId: 12)
        sarbTaskReviseStatus.id = 13723
        create(sarbTaskReviseStatus)
    }

}
