package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * SystemEntityService is used to handle only CRUD related object manipulation
 * (e.g. read, create, list, update etc.)
 */
class SystemEntityService extends BaseService {

    static transactional = false

    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String SORT_BY_KEY = 'key'
    private static final String SORT_BY_TYPE = 'type'

    /**
     * Method to read SystemEntity by id
     * @param id - SystemEntity.id
     * @return - SystemEntity object
     */
    public SystemEntity read(long id) {
        SystemEntity systemEntity = SystemEntity.read(id)
        systemEntity.discard()  // This domain checking dirty with .read(), discard() resolves it
        return systemEntity
    }

    /**
     * Method to get list system entity by type
     * @param type - system entity type id
     */
    @Transactional(readOnly = true)
    public List listByType(long type) {
        return SystemEntity.findAllByType(type, [readOnly: true, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER])
    }

    @Transactional(readOnly = true)
    public List listByType(long type, String sortColumn) {
        return SystemEntity.findAllByType(type, [readOnly: true, sort: sortColumn, order: ASCENDING_SORT_ORDER])
    }

    private static final String QUERY_FOR_LIST = """
        SELECT se.id, se.key, se.value, se.is_active, se.reserved_id
            FROM system_entity se
            WHERE se.type = :systemEntityTypeId
            AND se.company_id = :companyId
            ORDER BY se.type, se.id asc
            LIMIT :resultPerPage OFFSET :start
    """

    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(se.id)
            FROM system_entity se
            WHERE se.type = :systemEntityTypeId
            AND se.company_id = :companyId
    """

    /**
     * Get list of system entity
     * @param systemEntityTypeId - SystemEntityType.id
     * @return - a map containing system entity list and it's count
     */
    public LinkedHashMap list(long systemEntityTypeId, BaseService baseService) {
        Map queryParams = [
                systemEntityTypeId: systemEntityTypeId,
                companyId: appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> systemEntityList = executeSelectSql(QUERY_FOR_LIST, queryParams)
        List countResults = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = countResults[0].count
        return [systemEntityList: systemEntityList, count: count]
    }

    private static final String INSERT_QUERY = """
            INSERT INTO system_entity(id, version, key, value, type, is_active, company_id, reserved_id, plugin_id)
            VALUES (:id, :version, :key, :value, :type, :isActive, :companyId, :reservedId, :pluginId);
    """

    /**
     * Method to save system entity
     * @param sysEntity - SystemEntity object
     */
    public SystemEntity create(SystemEntity sysEntity) {
        Map queryParams = [
                id: sysEntity.id,
                version: sysEntity.version,
                key: sysEntity.key,
                value: sysEntity.value,
                type: sysEntity.type,
                isActive: sysEntity.isActive,
                companyId: sysEntity.companyId,
                reservedId: sysEntity.reservedId,
                pluginId: sysEntity.pluginId
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while inserting information')
        }
        int sysEntityId = (int) result[0][0]
        sysEntity.id = sysEntityId
        return sysEntity
    }

    private static final String UPDATE_QUERY = """
                    UPDATE system_entity SET
                          version=version+1,
                          value=:value,
                          key=:key,
                          is_active=:isActive
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Method to update system entity
     * @param sysEntity - object of SystemEntity
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction otherwise return update count
     */
    public int update(SystemEntity sysEntity) {
        Map queryParams = [
                id: sysEntity.id,
                version: sysEntity.version,
                value: sysEntity.value,
                key: sysEntity.key,
                isActive: sysEntity.isActive
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating information')
        }
        sysEntity.version = sysEntity.version + 1
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM system_entity
                      WHERE
                          id=:id
    """

    /**
     * Method to delete system entity
     * @param id - SystemEntity.id
     * @return - if deleteCount <= 0 then throw exception to rollback transaction otherwise boolean true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred at systemEntity.delete")
        }
        return Boolean.TRUE
    }

    private static final String SELECT_NEXT_VAL_ID_SEQ = "SELECT NEXTVAL('system_entity_id_seq') as id"

    /**
     * get customized id as : companyId + pluginId + latest system_entity_id from system_entity_id_sequence
     * e.g : 123(1 = companyId, 2 = pluginId of Accounting module, 3 = latest id of system_entity_id_sequence)
     * @param companyId -id of company
     * @param pluginId -id of plugin
     * @return -long value of id
     */
    public long getSystemEntityId(long companyId, long pluginId) {
        List results = executeSelectSql(SELECT_NEXT_VAL_ID_SEQ)
        long sequenceId = results[0].id
        long systemEntityId = Long.parseLong(companyId + "" + pluginId + "" + sequenceId)
        return systemEntityId
    }

    /**
     * Get list of system entity and count by search keyword
     * @param queryParameter - query keyword from UI
     * @param systemEntityTypeId - SystemEntityType.id
     * @return - a map containing list and count of system entity
     */
    public LinkedHashMap searchResult(BaseService baseService, String queryParameter, long systemEntityTypeId) {
        String queryStr = """
            SELECT se.id, se.key, se.value, se.is_active, se.reserved_id
            FROM system_entity se
            WHERE ${baseService.queryType} ilike :queryParameter
            AND se.type = :systemEntityTypeId
            AND se.company_id = :companyId
            ORDER BY se.type, se.id asc
            LIMIT :resultPerPage  OFFSET :start;
        """

        Map queryParams = [
                queryParameter: queryParameter,
                systemEntityTypeId: systemEntityTypeId,
                companyId: appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> results = executeSelectSql(queryStr, queryParams)

        String countQuery = """
            SELECT COUNT(se.id)
            FROM system_entity se
            WHERE ${baseService.queryType} ilike :queryParameter
            AND se.type = :systemEntityTypeId
            AND se.company_id = :companyId;
        """

        List countResults = executeSelectSql(countQuery, queryParams)
        int count = countResults[0].count
        return [systemEntityList: results, count: count]
    }

    public int countByTypeAndKeyIlikeAndCompanyId(long type, String key, long companyId) {
        int count = SystemEntity.countByTypeAndKeyIlikeAndCompanyId(type, key, companyId)
        return count
    }

    public int countByTypeAndKeyIlikeAndIdNotEqualAndCompanyId(long type, String key, long id, long companyId) {
        int count = SystemEntity.countByTypeAndKeyIlikeAndIdNotEqualAndCompanyId(type, key, id, companyId)
        return count
    }

    public int countByType(long type) {
        int count = SystemEntity.countByType(type)
        return count
    }

    public int countByTypeAndCompanyId(long type, long companyId) {
        int count = SystemEntity.countByTypeAndCompanyId(type, companyId)
        return count
    }

    public SystemEntity findByReservedIdAndCompanyId(long userEntityType, long companyId) {
        SystemEntity systemEntity = SystemEntity.findByReservedIdAndCompanyId(userEntityType, companyId)
        return systemEntity
    }


    /**
     * Method to create default budget task status
     */
    public void createDefaultBudgetTaskStatus(long companyId) {
        long pluginId = PluginConnector.BUDGET_ID
        SystemEntity budgTaskStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 389L)
        budgTaskStatusDefined.id = getSystemEntityId(companyId, pluginId)
        create(budgTaskStatusDefined)
        SystemEntity budgTaskStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 390L)
        budgTaskStatusInProgress.id = getSystemEntityId(companyId, pluginId)
        create(budgTaskStatusInProgress)
        SystemEntity budgTaskStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: systemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 390L)
        budgTaskStatusCompleted.id = getSystemEntityId(companyId, pluginId)
        create(budgTaskStatusCompleted)
    }

    /**
     * Method to create default account source
     */
    public void createDefaultAccSource(long companyId) {
        long pluginId = PluginConnector.ACCOUNTING_ID
        SystemEntity systemEntityNone = new SystemEntity(version: 0, key: 'None', value: 'None', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 21L)
        systemEntityNone.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityNone)
        SystemEntity systemEntityCustomer = new SystemEntity(version: 0, key: 'Customer', value: 'Customer', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 22L)
        systemEntityCustomer.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityCustomer)
        SystemEntity systemEntityEmployee = new SystemEntity(version: 0, key: 'Employee', value: 'Employee', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 23L)
        systemEntityEmployee.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityEmployee)
        SystemEntity systemEntityCashSubAccount = new SystemEntity(version: 0, key: 'Sub-Account', value: 'Sub-Account', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 24L)
        systemEntityCashSubAccount.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityCashSubAccount)
        SystemEntity systemEntitySupplier = new SystemEntity(version: 0, key: 'Supplier', value: 'Supplier', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 25L)
        systemEntitySupplier.id = getSystemEntityId(companyId, pluginId)
        create(systemEntitySupplier)
        SystemEntity systemEntityItem = new SystemEntity(version: 0, key: 'Item', value: 'Item', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 26L)
        systemEntityItem.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityItem)
        SystemEntity systemEntityLc = new SystemEntity(version: 0, key: 'LC', value: 'LC', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 27L)
        systemEntityLc.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityLc)
        SystemEntity systemEntityIpc = new SystemEntity(version: 0, key: 'IPC', value: 'IPC', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 28L)
        systemEntityIpc.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityIpc)
        SystemEntity systemEntityLeaseAcc = new SystemEntity(version: 0, key: 'Lease Account', value: 'Lease Account', type: systemEntityTypeCacheUtility.TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 29L)
        systemEntityLeaseAcc.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityLeaseAcc)
    }

    /**
     * Method to create default Account Instrument type
     */
    public void createDefaultAccInstrumentType(long companyId) {
        long pluginId = PluginConnector.ACCOUNTING_ID
        SystemEntity systemEntityIouTrace = new SystemEntity(version: 0, key: 'IOU Trace', value: null, type: systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 234L)
        systemEntityIouTrace.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityIouTrace)
        SystemEntity systemEntityPoTrace = new SystemEntity(version: 0, key: 'PO Trace', value: null, type: systemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 235L)
        systemEntityPoTrace.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityPoTrace)
    }

    /**
     * Method to create default Account Voucher type
     */
    public void createDefaultAccVoucherType(long companyId) {
        long pluginId = PluginConnector.ACCOUNTING_ID
        SystemEntity systemEntityPaymentVoucherBank = new SystemEntity(version: 0, key: 'Payment Voucher-Bank', value: 'PB', type: systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 210L)
        systemEntityPaymentVoucherBank.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityPaymentVoucherBank)
        SystemEntity systemEntityPaymentVoucherCash = new SystemEntity(version: 0, key: 'Payment Voucher-Cash', value: 'PC', type: systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 211L)
        systemEntityPaymentVoucherCash.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityPaymentVoucherCash)
        SystemEntity systemEntityReceivedVoucherBank = new SystemEntity(version: 0, key: 'Received Voucher-Bank', value: 'RB', type: systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 212L)
        systemEntityReceivedVoucherBank.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityReceivedVoucherBank)
        SystemEntity systemEntityReceivedVoucherCash = new SystemEntity(version: 0, key: 'Received Voucher-Cash', value: 'RC', type: systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 213L)
        systemEntityReceivedVoucherCash.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityReceivedVoucherCash)
        SystemEntity systemEntityJournal = new SystemEntity(version: 0, key: 'Journal', value: 'JR', type: systemEntityTypeCacheUtility.TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 214L)
        systemEntityJournal.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityJournal)
    }

    /**
     * Method to create default inventory production item type
     */
    public void createDefaultInvProductionItemType(long companyId) {

        long pluginId = PluginConnector.INVENTORY_ID
        SystemEntity invProductionItemTypeRawMaterial = new SystemEntity(version: 0, key: 'Raw material', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 415L)
        invProductionItemTypeRawMaterial.id = getSystemEntityId(companyId, pluginId)
        create(invProductionItemTypeRawMaterial)
        SystemEntity invProductionItemTypeFinishedProduct = new SystemEntity(version: 0, key: 'Finished Product', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 416L)
        invProductionItemTypeFinishedProduct.id = getSystemEntityId(companyId, pluginId)
        create(invProductionItemTypeFinishedProduct)
    }

    /**
     * Method to create default transaction type
     */
    public void createDefaultTransactionType(long companyId) {

        long pluginId = PluginConnector.INVENTORY_ID
        SystemEntity transactionTypeIn = new SystemEntity(version: 0, key: 'In', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 417L)
        transactionTypeIn.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeIn)
        SystemEntity transactionTypeOut = new SystemEntity(version: 0, key: 'Out', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 418L)
        transactionTypeOut.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeOut)
        SystemEntity transactionTypeConsumption = new SystemEntity(version: 0, key: 'Consumption', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 419L)
        transactionTypeConsumption.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeConsumption)
        SystemEntity transactionTypeProduction = new SystemEntity(version: 0, key: 'Production', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 420L)
        transactionTypeProduction.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeProduction)
        SystemEntity transactionTypeAdjustment = new SystemEntity(version: 0, key: 'Adjustment', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 421L)
        transactionTypeAdjustment.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeAdjustment)
        SystemEntity transactionTypeReverseAdjustment = new SystemEntity(version: 0, key: 'Reverse Adjustment', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 422L)
        transactionTypeReverseAdjustment.id = getSystemEntityId(companyId, pluginId)
        create(transactionTypeReverseAdjustment)
    }

    /**
     * Method to create default transaction entity type
     */
    public void createDefaultTransactionEntityType(long companyId) {

        long pluginId = PluginConnector.INVENTORY_ID
        SystemEntity transactionEntityTypeStore = new SystemEntity(version: 0, key: 'Inventory', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 423L)
        transactionEntityTypeStore.id = getSystemEntityId(companyId, pluginId)
        create(transactionEntityTypeStore)
        SystemEntity transactionEntityTypeSupplier = new SystemEntity(version: 0, key: 'Supplier', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 424L)
        transactionEntityTypeSupplier.id = getSystemEntityId(companyId, pluginId)
        create(transactionEntityTypeSupplier)
        SystemEntity transactionEntityTypeNone = new SystemEntity(version: 0, key: 'None', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 425L)
        transactionEntityTypeNone.id = getSystemEntityId(companyId, pluginId)
        create(transactionEntityTypeNone)
        SystemEntity transactionEntityTypeCustomer = new SystemEntity(version: 0, key: 'Customer', value: null, type: systemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 426L)
        transactionEntityTypeCustomer.id = getSystemEntityId(companyId, pluginId)
        create(transactionEntityTypeCustomer)
    }

    /**
     * Method to create default valuation type for application
     */
    public void createDefaultValuationTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity valuationTypeFIFO = new SystemEntity(version: 0, key: 'FIFO', value: null, type: systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 127L)
        valuationTypeFIFO.id = getSystemEntityId(companyId, pluginId)
        create(valuationTypeFIFO)
        SystemEntity valuationTypeLIFO = new SystemEntity(version: 0, key: 'LIFO', value: null, type: systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 128L)
        valuationTypeLIFO.id = getSystemEntityId(companyId, pluginId)
        create(valuationTypeLIFO)
        SystemEntity valuationTypeAVG = new SystemEntity(version: 0, key: 'AVG', value: null, type: systemEntityTypeCacheUtility.TYPE_VALUATION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 129L)
        valuationTypeAVG.id = getSystemEntityId(companyId, pluginId)
        create(valuationTypeAVG)
    }

    /**
     * Method to create default payment method
     */
    public void createDefaultPaymentMethod(long companyId) {

        long pluginId = PluginConnector.PROCUREMENT_ID
        SystemEntity systemEntityCash = new SystemEntity(version: 0, key: 'Cash', value: 'Cash', type: systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        systemEntityCash.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityCash)
        SystemEntity systemEntityCheque = new SystemEntity(version: 0, key: 'Cheque', value: 'Cheque', type: systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        systemEntityCheque.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityCheque)
        SystemEntity systemEntityLc = new SystemEntity(version: 0, key: 'LC', value: 'LC', type: systemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        systemEntityLc.id = getSystemEntityId(companyId, pluginId)
        create(systemEntityLc)
    }

    /**
     * Method to create default unit for application
     */
    public void createDefaultUnitForApp(long companyId) {

        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity unit1 = new SystemEntity(version: 0, key: 'cu.cm.', value: 'cu.cm.', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit1.id = getSystemEntityId(companyId, pluginId)
        create(unit1)
        SystemEntity unit2 = new SystemEntity(version: 0, key: 'Foot', value: 'FT', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit2.id = getSystemEntityId(companyId, pluginId)
        create(unit2)
        SystemEntity unit3 = new SystemEntity(version: 0, key: 'Bags', value: 'Bags', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit3.id = getSystemEntityId(companyId, pluginId)
        create(unit3)
        SystemEntity unit4 = new SystemEntity(version: 0, key: 'NOS', value: 'NOS', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit4.id = getSystemEntityId(companyId, pluginId)
        create(unit4)
        SystemEntity unit5 = new SystemEntity(version: 0, key: 'Liters', value: 'liters', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit5.id = getSystemEntityId(companyId, pluginId)
        create(unit5)
        SystemEntity unit6 = new SystemEntity(version: 0, key: 'METER', value: 'METER', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit6.id = getSystemEntityId(companyId, pluginId)
        create(unit6)
        SystemEntity unit7 = new SystemEntity(version: 0, key: 'Months', value: 'months', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit7.id = getSystemEntityId(companyId, pluginId)
        create(unit7)
        SystemEntity unit8 = new SystemEntity(version: 0, key: 'P.S', value: 'P.S', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit8.id = getSystemEntityId(companyId, pluginId)
        create(unit8)
        SystemEntity unit9 = new SystemEntity(version: 0, key: 'KG', value: 'kg', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit9.id = getSystemEntityId(companyId, pluginId)
        create(unit9)
        SystemEntity unit10 = new SystemEntity(version: 0, key: 'CU. M.', value: 'CU. M.', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit10.id = getSystemEntityId(companyId, pluginId)
        create(unit10)
        SystemEntity unit11 = new SystemEntity(version: 0, key: 'L.S.', value: 'L.S.', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit11.id = getSystemEntityId(companyId, pluginId)
        create(unit11)
        SystemEntity unit12 = new SystemEntity(version: 0, key: 'SQM', value: 'SQM', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit12.id = getSystemEntityId(companyId, pluginId)
        create(unit12)
        SystemEntity unit13 = new SystemEntity(version: 0, key: 'Set', value: 'Set', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit13.id = getSystemEntityId(companyId, pluginId)
        create(unit13)
        SystemEntity unit14 = new SystemEntity(version: 0, key: 'Tonnes', value: 'Tonnes', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit14.id = getSystemEntityId(companyId, pluginId)
        create(unit14)
        SystemEntity unit15 = new SystemEntity(version: 0, key: 'CFT', value: 'CFT', type: systemEntityTypeCacheUtility.TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        unit15.id = getSystemEntityId(companyId, pluginId)
        create(unit15)
    }

    /**
     * Method to create default owner type for application
     */
    public void createDefaultOwnerTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity ownerType1 = new SystemEntity(version: 0, key: 'Purchased', value: null, type: systemEntityTypeCacheUtility.TYPE_OWNER_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 132L)
        ownerType1.id = getSystemEntityId(companyId, pluginId)
        create(ownerType1)
        SystemEntity ownerType2 = new SystemEntity(version: 0, key: 'Rental', value: null, type: systemEntityTypeCacheUtility.TYPE_OWNER_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 133L)
        ownerType2.id = getSystemEntityId(companyId, pluginId)
        create(ownerType2)
    }

    /**
     * Method to create default content entity type for application
     */
    public void createDefaultContentEntityTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity entityTypeAppUser = new SystemEntity(version: 0, key: 'APP USER', value: 'APP USER', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 142L)
        entityTypeAppUser.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeAppUser)
        SystemEntity entityTypeCompany = new SystemEntity(version: 0, key: 'COMPANY', value: 'COMPANY', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 143L)
        entityTypeCompany.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeCompany)
        SystemEntity entityTypeExhCustomer = new SystemEntity(version: 0, key: 'EXH CUSTOMER', value: 'EXH CUSTOMER', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 144L)
        entityTypeExhCustomer.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeExhCustomer)
        SystemEntity entityTypeProject = new SystemEntity(version: 0, key: 'PROJECT', value: 'PROJECT', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 145L)
        entityTypeProject.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeProject)
        SystemEntity entityTypePtBug = new SystemEntity(version: 0, key: 'PT BUG', value: 'PT BUG', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1058L)
        entityTypePtBug.id = getSystemEntityId(companyId, pluginId)
        create(entityTypePtBug)
        SystemEntity entityTypeBudget = new SystemEntity(version: 0, key: 'BOQ Line Item', value: 'BOQ Line Item', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 184L)
        entityTypeBudget.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeBudget)
        SystemEntity entityTypeBudgetSprint = new SystemEntity(version: 0, key: 'Sprint', value: 'Sprint', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 192L)
        entityTypeBudgetSprint.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeBudgetSprint)
        SystemEntity entityTypeFinancialYear = new SystemEntity(version: 0, key: 'Financial Year', value: 'Financial Year', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 185L)
        entityTypeFinancialYear.id = getSystemEntityId(companyId, pluginId)
        create(entityTypeFinancialYear)
    }

    /**
     * Method to create default note entity type for application
     */
    public void createDefaultNoteEntityTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity noteTask = new SystemEntity(version: 0, key: 'TASK', value: 'Note Entity Type Task', type: systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 148L)
        noteTask.id = getSystemEntityId(companyId, pluginId)
        create(noteTask)
        SystemEntity noteCustomer = new SystemEntity(version: 0, key: 'EXH CUSTOMER', value: 'Note Entity Type Customer', type: systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 149L)
        noteCustomer.id = getSystemEntityId(companyId, pluginId)
        create(noteCustomer)
    }

    /**
     * Method to create default supplier entity type for application
     */
    public void createDefaultSupplierEntityTypeForApp(long companyId) {

        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity serviceProvider = new SystemEntity(version: 0, key: 'Material Provider', value: 'Material Provider', type: systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        serviceProvider.id = getSystemEntityId(companyId, pluginId)
        create(serviceProvider)

        SystemEntity materialProvider = new SystemEntity(version: 0, key: 'Service Provider', value: 'Service Provider', type: systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        materialProvider.id = getSystemEntityId(companyId, pluginId)
        create(materialProvider)

        SystemEntity materialAndServiceProvider = new SystemEntity(version: 0, key: 'Material & Service Provider', value: 'Material & Service Provider', type: systemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L)
        materialAndServiceProvider.id = getSystemEntityId(companyId, pluginId)
        create(materialAndServiceProvider)
    }

    /**
     * Method to create default item category for application
     */
    public void createDefaultItemCategoryForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity inventory = new SystemEntity(version: 0, key: 'Inventory', value: 'Inventory', type: systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150L)
        inventory.id = getSystemEntityId(companyId, pluginId)
        create(inventory)

        SystemEntity nonInventory = new SystemEntity(version: 0, key: 'Non Inventory', value: 'Non-Inventory', type: systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 151L)
        nonInventory.id = getSystemEntityId(companyId, pluginId)
        create(nonInventory)

        SystemEntity fixedAsset = new SystemEntity(version: 0, key: 'Fixed Asset', value: 'Fixed Asset', type: systemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 152L)
        fixedAsset.id = getSystemEntityId(companyId, pluginId)
        create(fixedAsset)
    }

    /**
     * Method to create default content type for application
     */
    public void createDefaultContentTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity contentTypeDocument = new SystemEntity(version: 0, key: 'Document', value: 'Document', type: systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 146L)
        contentTypeDocument.id = getSystemEntityId(companyId, pluginId)
        create(contentTypeDocument)

        SystemEntity contentTypeImage = new SystemEntity(version: 0, key: 'Image', value: 'Image', type: systemEntityTypeCacheUtility.TYPE_CONTENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 147L)
        contentTypeImage.id = getSystemEntityId(companyId, pluginId)
        create(contentTypeImage)
    }

    /**
     * Method to create default inventory type
     */
    public void createDefaultInventoryType(long companyId) {

        long pluginId = PluginConnector.INVENTORY_ID
        SystemEntity invTypeStore = new SystemEntity(version: 0, key: 'STORE', value: null, type: systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 430L)
        invTypeStore.id = getSystemEntityId(companyId, pluginId)
        create(invTypeStore)
        SystemEntity invTypeSite = new SystemEntity(version: 0, key: 'SITE', value: null, type: systemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 431L)
        invTypeSite.id = getSystemEntityId(companyId, pluginId)
        create(invTypeSite)
    }

    /**
     * Method to create default data paid by for exchange house
     */
    public void createDefaultDataPaidByForExh(long companyId) {
        long pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        SystemEntity paidByCash = new SystemEntity(version: 0, key: 'Cash', value: 'Cash', type: systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, pluginId: 9, reservedId: 950)
        paidByCash.id = getSystemEntityId(companyId, pluginId)
        create(paidByCash)

        SystemEntity paidByOnline = new SystemEntity(version: 0, key: 'Online Transfer', value: 'Online Transfer', type: systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, pluginId: 9, reservedId: 951)
        paidByOnline.id = getSystemEntityId(companyId, pluginId)
        create(paidByOnline)

        SystemEntity paidByCard = new SystemEntity(version: 0, key: 'Pay Point', value: 'Pay Point', type: systemEntityTypeCacheUtility.TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, pluginId: 9, reservedId: 952)
        paidByCard.id = getSystemEntityId(companyId, pluginId)
        create(paidByCard)
    }

    /**
     * Method to create default data payment method for exchange house
     */
    public void createDefaultDataPaymentMethodForExh(long companyId) {
        long pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        SystemEntity paymentMethodCash = new SystemEntity(version: 0, key: 'Bank Deposit', value: '1', type: systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: 9, reservedId: 953)
        paymentMethodCash.id = getSystemEntityId(companyId, pluginId)
        create(paymentMethodCash)

        SystemEntity paymentMethodOnline = new SystemEntity(version: 0, key: 'Cash Collection', value: '2', type: systemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: 9, reservedId: 954)
        paymentMethodOnline.id = getSystemEntityId(companyId, pluginId)
        create(paymentMethodOnline)
    }

    /**
     * Method to create default data task status for exchange house
     */
    public void createDefaultDataTaskStatusForExh(long companyId) {
        long pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        SystemEntity taskStatus1 = new SystemEntity(version: 0, key: 'Cancelled', value: 'Cancelled', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 956)
        taskStatus1.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus1)

        SystemEntity taskStatusPending = new SystemEntity(version: 0, key: 'Pending Task', value: 'Pending Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 957)
        taskStatusPending.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusPending)

        SystemEntity taskStatus2 = new SystemEntity(version: 0, key: 'New Task', value: 'New Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 958)
        taskStatus2.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus2)

        SystemEntity taskStatus3 = new SystemEntity(version: 0, key: 'Send to bank', value: 'Send to bank', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 959)
        taskStatus3.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus3)

        SystemEntity taskStatus4 = new SystemEntity(version: 0, key: 'Sent to other bank', value: 'Sent to other bank', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 960)
        taskStatus4.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus4)

        SystemEntity taskStatus5 = new SystemEntity(version: 0, key: 'Resolved by other bank', value: 'Resolved by other bank', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 961)
        taskStatus5.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus5)

        SystemEntity taskStatus6 = new SystemEntity(version: 0, key: 'Unapproved Task', value: 'Unapproved Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 962)
        taskStatus6.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus6)

        SystemEntity taskStatus7 = new SystemEntity(version: 0, key: 'Refund Task', value: 'Refund Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, pluginId: 9, reservedId: 997)
        taskStatus7.id = getSystemEntityId(companyId, pluginId)
        create(taskStatus7)
    }

    /**
     * Method to create default data task type for exchange house
     */
    public void createDefaultDataTaskTypeForExh(long companyId) {

        long pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        SystemEntity taskTypeExh = new SystemEntity(version: 0, key: 'Exh Task', value: 'Exh Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, pluginId: 9, reservedId: 963)
        taskTypeExh.id = getSystemEntityId(companyId, pluginId)
        create(taskTypeExh)

        SystemEntity taskTypeAgent = new SystemEntity(version: 0, key: 'Agent Task', value: 'Agent Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, pluginId: 9, reservedId: 964)
        taskTypeAgent.id = getSystemEntityId(companyId, pluginId)
        create(taskTypeAgent)

        SystemEntity taskTypeCustomer = new SystemEntity(version: 0, key: 'Customer Task', value: 'Customer Task', type: systemEntityTypeCacheUtility.TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, pluginId: 9, reservedId: 965)
        taskTypeCustomer.id = getSystemEntityId(companyId, pluginId)
        create(taskTypeCustomer)
    }

    /**
     * Method to create default data user entity type for application
     */
    public void createDefaultDataUserEntityTypeForApp(long companyId) {
        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity userEntityTypeCust = new SystemEntity(version: 0, key: 'Customer', value: 'User Mapping Customer', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 136L)
        userEntityTypeCust.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeCust)

        SystemEntity userEntityTypeBankBr = new SystemEntity(version: 0, key: 'Bank Branch', value: 'User Mapping Bank Branch', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 137L)
        userEntityTypeBankBr.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeBankBr)

        SystemEntity userEntityTypeProject = new SystemEntity(version: 0, key: 'Project', value: 'User Mapping Project', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 138L)
        userEntityTypeProject.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeProject)

        SystemEntity userEntityTypePtProject = new SystemEntity(version: 0, key: 'Project', value: 'User Mapping PtProject', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1059L)
        userEntityTypePtProject.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypePtProject)

        SystemEntity userEntityTypeInventory = new SystemEntity(version: 0, key: 'Inventory', value: 'User Mapping Inventory', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 139L)
        userEntityTypeInventory.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeInventory)

        SystemEntity userEntityTypeGroup = new SystemEntity(version: 0, key: 'Group', value: 'User Mapping Group', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140L)
        userEntityTypeGroup.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeGroup)

        SystemEntity userEntityTypeAgent = new SystemEntity(version: 0, key: 'Agent', value: 'User Mapping Agent', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 141L)
        userEntityTypeAgent.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypeAgent)
    }
    /**
     * Method to create default data user entity type for application
     */
    public void createDefaultDataGenderForApp(long companyId) {

        long pluginId = PluginConnector.APPLICATION_ID
        SystemEntity male = new SystemEntity(version: 0, key: 'Male', value: 'M', type: systemEntityTypeCacheUtility.TYPE_GENDER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 176L)
        male.id = getSystemEntityId(companyId, pluginId)
        create(male)

        SystemEntity female = new SystemEntity(version: 0, key: 'Female', value: 'F', type: systemEntityTypeCacheUtility.TYPE_GENDER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 177L)
        female.id = getSystemEntityId(companyId, pluginId)
        create(female)
    }

    /**
     * Method to create default content entity type for Project Track
     */
    public void createDefaultContentEntityTypeForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity userEntityTypePtProject = new SystemEntity(version: 0, key: 'User Mapping PtProject', value: 'User Mapping PtProject', type: systemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1059L)
        userEntityTypePtProject.id = getSystemEntityId(companyId, pluginId)
        create(userEntityTypePtProject)

        SystemEntity entityTypePtBug = new SystemEntity(version: 0, key: 'PT BUG', value: 'PT BUG', type: systemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1058L)
        entityTypePtBug.id = getSystemEntityId(companyId, pluginId)
        create(entityTypePtBug)
    }

    /**
     * Method to create default backlog priority type for ProjectTrack
     */
    public void createDefaultDataBacklogPriorityForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity backlogPriorityHigh = new SystemEntity(version: 0, key: 'High', value: 'High', type: systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1032L)
        backlogPriorityHigh.id = getSystemEntityId(companyId, pluginId)
        create(backlogPriorityHigh)

        SystemEntity backlogPriorityMedium = new SystemEntity(version: 0, key: 'Medium', value: 'Medium', type: systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1033L)
        backlogPriorityMedium.id = getSystemEntityId(companyId, pluginId)
        create(backlogPriorityMedium)

        SystemEntity backlogPriorityLow = new SystemEntity(version: 0, key: 'Low', value: 'Low', type: systemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1034L)
        backlogPriorityLow.id = getSystemEntityId(companyId, pluginId)
        create(backlogPriorityLow)
    }

    public void createDefaultDataBacklogStatusForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity backlogStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: systemEntityTypeCacheUtility.PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1035L)
        backlogStatusDefined.id = getSystemEntityId(companyId, pluginId)
        create(backlogStatusDefined)

        SystemEntity backlogStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: systemEntityTypeCacheUtility.PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1036L)
        backlogStatusInProgress.id = getSystemEntityId(companyId, pluginId)
        create(backlogStatusInProgress)

        SystemEntity backlogStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: systemEntityTypeCacheUtility.PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1037L)
        backlogStatusCompleted.id = getSystemEntityId(companyId, pluginId)
        create(backlogStatusCompleted)

        SystemEntity backlogStatusAccepted = new SystemEntity(version: 0, key: 'Accepted', value: 'Accepted', type: systemEntityTypeCacheUtility.PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1038L)
        backlogStatusAccepted.id = getSystemEntityId(companyId, pluginId)
        create(backlogStatusAccepted)
    }

    public void createDefaultDataSprintStatusForPt(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity sprintStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: systemEntityTypeCacheUtility.PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1039L)
        sprintStatusDefined.id = getSystemEntityId(companyId, pluginId)
        create(sprintStatusDefined)

        SystemEntity sprintStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: systemEntityTypeCacheUtility.PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1040L)
        sprintStatusInProgress.id = getSystemEntityId(companyId, pluginId)
        create(sprintStatusInProgress)

        SystemEntity sprintStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: systemEntityTypeCacheUtility.PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1041L)
        sprintStatusCompleted.id = getSystemEntityId(companyId, pluginId)
        create(sprintStatusCompleted)

        SystemEntity sprintStatusClosed = new SystemEntity(version: 0, key: 'Closed', value: 'Closed', type: systemEntityTypeCacheUtility.PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1095L)
        sprintStatusCompleted.id = getSystemEntityId(companyId, pluginId)
        create(sprintStatusClosed)
    }

    public void createDefaultDataAcceptanceCriteriaStatusForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity acceptanceStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1042L)
        acceptanceStatusDefined.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceStatusDefined)

        SystemEntity acceptanceStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1043L)
        acceptanceStatusInProgress.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceStatusInProgress)

        SystemEntity acceptanceStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1044L)
        acceptanceStatusCompleted.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceStatusCompleted)

        SystemEntity acceptanceStatusBlocked = new SystemEntity(version: 0, key: 'Blocked', value: 'Blocked', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1045L)
        acceptanceStatusBlocked.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceStatusBlocked)
    }

    public void createDefaultDataBugSeverityForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity bugSeverityHigh = new SystemEntity(version: 0, key: 'High', value: 'High', type: systemEntityTypeCacheUtility.PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1046L)
        bugSeverityHigh.id = getSystemEntityId(companyId, pluginId)
        create(bugSeverityHigh)

        SystemEntity bugSeverityMedium = new SystemEntity(version: 0, key: 'Medium', value: 'Medium', type: systemEntityTypeCacheUtility.PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1047L)
        bugSeverityMedium.id = getSystemEntityId(companyId, pluginId)
        create(bugSeverityMedium)

        SystemEntity bugSeverityLow = new SystemEntity(version: 0, key: 'Low', value: 'Low', type: systemEntityTypeCacheUtility.PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1048L)
        bugSeverityLow.id = getSystemEntityId(companyId, pluginId)
        create(bugSeverityLow)
    }

    public void createDefaultDataBugStatusForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity bugStatusSubmitted = new SystemEntity(version: 0, key: 'Submitted', value: 'Submitted', type: systemEntityTypeCacheUtility.PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1049L)
        bugStatusSubmitted.id = getSystemEntityId(companyId, pluginId)
        create(bugStatusSubmitted)

        SystemEntity bugStatusOpen = new SystemEntity(version: 0, key: 'Re-opened', value: 'Re-opened', type: systemEntityTypeCacheUtility.PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1050L)
        bugStatusOpen.id = getSystemEntityId(companyId, pluginId)
        create(bugStatusOpen)

        SystemEntity bugStatusFixed = new SystemEntity(version: 0, key: 'Fixed', value: 'Fixed', type: systemEntityTypeCacheUtility.PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1051L)
        bugStatusFixed.id = getSystemEntityId(companyId, pluginId)
        create(bugStatusFixed)

        SystemEntity bugStatusClosed = new SystemEntity(version: 0, key: 'Closed', value: 'Closed', type: systemEntityTypeCacheUtility.PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1052L)
        bugStatusClosed.id = getSystemEntityId(companyId, pluginId)
        create(bugStatusClosed)
    }

    public void createDefaultDataBugTypeForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity bugTypeFunctional = new SystemEntity(version: 0, key: 'Functional', value: 'Functional', type: systemEntityTypeCacheUtility.PT_BUG_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1053L)
        bugTypeFunctional.id = getSystemEntityId(companyId, pluginId)
        create(bugTypeFunctional)

        SystemEntity bugTypeUserInterface = new SystemEntity(version: 0, key: 'User Interface', value: 'User Interface', type: systemEntityTypeCacheUtility.PT_BUG_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1054L)
        bugTypeUserInterface.id = getSystemEntityId(companyId, pluginId)
        create(bugTypeUserInterface)

        SystemEntity bugTypeInconsistency = new SystemEntity(version: 0, key: 'Inconsistency', value: 'Inconsistency', type: systemEntityTypeCacheUtility.PT_BUG_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1055L)
        bugTypeInconsistency.id = getSystemEntityId(companyId, pluginId)
        create(bugTypeInconsistency)

        SystemEntity bugTypePerformance = new SystemEntity(version: 0, key: 'Performance', value: 'Performance', type: systemEntityTypeCacheUtility.PT_BUG_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1056L)
        bugTypePerformance.id = getSystemEntityId(companyId, pluginId)
        create(bugTypePerformance)

        SystemEntity bugTypeSuggestion = new SystemEntity(version: 0, key: 'Suggestion', value: 'Suggestion', type: systemEntityTypeCacheUtility.PT_BUG_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1057L)
        bugTypeSuggestion.id = getSystemEntityId(companyId, pluginId)
        create(bugTypeSuggestion)
    }

    public void createDefaultDataAcceptanceCriteriaTypeForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity acceptanceCriteriaTypePreCondition = new SystemEntity(version: 0, key: 'Pre-condition', value: 'Pre-condition', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1078L)
        acceptanceCriteriaTypePreCondition.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceCriteriaTypePreCondition)

        SystemEntity acceptanceCriteriaTypeBusinessLogic = new SystemEntity(version: 0, key: 'Business Logic', value: 'Business Logic', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1079L)
        acceptanceCriteriaTypeBusinessLogic.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceCriteriaTypeBusinessLogic)

        SystemEntity acceptanceCriteriaTypePostCondition = new SystemEntity(version: 0, key: 'Post-condition', value: 'Post-condition', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1080L)
        acceptanceCriteriaTypePostCondition.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceCriteriaTypePostCondition)

        SystemEntity acceptanceCriteriaTypeOthers = new SystemEntity(version: 0, key: 'Others', value: 'Others', type: systemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1096L)
        acceptanceCriteriaTypeOthers.id = getSystemEntityId(companyId, pluginId)
        create(acceptanceCriteriaTypeOthers)
    }

    /**
     * Method to create default note entity type for project track
     */
    public void createDefaultNoteEntityTypeForPT(long companyId) {
        long pluginId = PluginConnector.PROJECT_TRACK_ID
        SystemEntity notePtTask = new SystemEntity(version: 0, key: 'Task', value: 'Note Entity Type Pt Task', type: systemEntityTypeCacheUtility.TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1094L)
        notePtTask.id = getSystemEntityId(companyId, pluginId)
        create(notePtTask)
    }

    public void createDefaultDataProcessTypeForArms(long companyId) {
        long pluginId = PluginConnector.ARMS_ID

        SystemEntity processTypeIssue = new SystemEntity(version: 0, key: 'Issue', value: 'Issue', type: systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1150L)
        processTypeIssue.id = getSystemEntityId(companyId, pluginId)
        create(processTypeIssue)

        SystemEntity processTypeForward = new SystemEntity(version: 0, key: 'Forward', value: 'Forward', type: systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1151L)
        processTypeForward.id = getSystemEntityId(companyId, pluginId)
        create(processTypeForward)

        SystemEntity processTypePurchase = new SystemEntity(version: 0, key: 'Purchase', value: 'Purchase', type: systemEntityTypeCacheUtility.ARMS_PROCESS_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1152L)
        processTypePurchase.id = getSystemEntityId(companyId, pluginId)
        create(processTypePurchase)
    }

    public void createDefaultDataInstrumentTypeForArms(long companyId) {
        long pluginId = PluginConnector.ARMS_ID

        SystemEntity instrumentTypePo = new SystemEntity(version: 0, key: 'PO', value: 'PO', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1153L)
        instrumentTypePo.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypePo)

        SystemEntity instrumentTypeEft = new SystemEntity(version: 0, key: 'EFT', value: 'EFT', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1154L)
        instrumentTypeEft.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypeEft)

        SystemEntity instrumentTypeOnline = new SystemEntity(version: 0, key: 'Online', value: 'Online', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1155L)
        instrumentTypeOnline.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypeOnline)

        SystemEntity instrumentTypeCashCollection = new SystemEntity(version: 0, key: 'Cash collection', value: 'Cash collection', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1156L)
        instrumentTypeCashCollection.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypeCashCollection)

        SystemEntity instrumentTypeTt = new SystemEntity(version: 0, key: 'TT', value: 'TT', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1157L)
        instrumentTypeTt.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypeTt)

        SystemEntity instrumentTypeMt = new SystemEntity(version: 0, key: 'MT', value: 'MT', type: systemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1158L)
        instrumentTypeMt.id = getSystemEntityId(companyId, pluginId)
        create(instrumentTypeMt)
    }

    public void createDefaultDataPaymentMethodForArms(long companyId) {
        long pluginId = PluginConnector.ARMS_ID

        SystemEntity payMethodBankDeposit = new SystemEntity(version: 0, key: 'Bank deposit', value: 'Bank deposit', type: systemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1160L)
        payMethodBankDeposit.id = getSystemEntityId(companyId, pluginId)
        create(payMethodBankDeposit)

        SystemEntity payMethodCashCollection = new SystemEntity(version: 0, key: 'Cash collection', value: 'Cash collection', type: systemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1161L)
        payMethodCashCollection.id = getSystemEntityId(companyId, pluginId)
        create(payMethodCashCollection)
    }

    public void createDefaultDataTaskStatusForArms(long companyId) {
        long pluginId = PluginConnector.ARMS_ID

        SystemEntity pendingTask = new SystemEntity(version: 0, key: 'Pending task', value: 'Pending task', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1162L)
        pendingTask.id = getSystemEntityId(companyId, pluginId)
        create(pendingTask)

        SystemEntity taskStatusNewTask = new SystemEntity(version: 0, key: 'New task', value: 'New task', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1163L)
        taskStatusNewTask.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusNewTask)

        SystemEntity taskStatusIncludeInLst = new SystemEntity(version: 0, key: 'Included in list', value: 'Included in list', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1164L)
        taskStatusIncludeInLst.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusIncludeInLst)

        SystemEntity taskStatusDecisionTaken = new SystemEntity(version: 0, key: 'Decision taken', value: 'Decision taken', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1165L)
        taskStatusDecisionTaken.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusDecisionTaken)

        SystemEntity taskStatusDecisionApproved = new SystemEntity(version: 0, key: 'Decision approved', value: 'Decision approved', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1166L)
        taskStatusDecisionApproved.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusDecisionApproved)

        SystemEntity taskStatusDisbursed = new SystemEntity(version: 0, key: 'Disbursed', value: 'Disbursed', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1167L)
        taskStatusDisbursed.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusDisbursed)

        SystemEntity taskStatusCanceled = new SystemEntity(version: 0, key: 'Canceled', value: 'Canceled', type: systemEntityTypeCacheUtility.ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1168L)
        taskStatusCanceled.id = getSystemEntityId(companyId, pluginId)
        create(taskStatusCanceled)
    }

    public void createDefaultDataForRmsTaskNote(long companyId) {
        long pluginId = PluginConnector.ARMS_ID
        SystemEntity rmsTaskNote = new SystemEntity(version: 0, key: 'RmsTask', value: 'Note Entity Type RmsTask', type: SystemEntityTypeCacheUtility.TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1181L)
        rmsTaskNote.id = getSystemEntityId(companyId, pluginId)
        create(rmsTaskNote)
    }

    public void createDefaultDataForArmsAppUserEntity(long companyId) {
        long pluginId = PluginConnector.ARMS_ID
        SystemEntity exchangeHouse = new SystemEntity(version: 0, key: 'Exchange House', value: 'User Mapping Exchange House', type: SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1186L)
        exchangeHouse.id = getSystemEntityId(companyId, pluginId)
        create(exchangeHouse)
    }

    public void createDefaultDataForDocument(long companyId) {
        long pluginId = PluginConnector.DOCUMENT_ID
        SystemEntity vendorPostgre = new SystemEntity(version: 0, key: 'PostgreSQL', value: 'org.postgresql.Driver', type: SystemEntityTypeCacheUtility.DOC_DATABASE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1393L)
        vendorPostgre.id = getSystemEntityId(companyId, pluginId)
        create(vendorPostgre)
        SystemEntity vendorMysql = new SystemEntity(version: 0, key: 'MySQL', value: 'com.mysql.jdbc.Driver', type: SystemEntityTypeCacheUtility.DOC_DATABASE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1394L)
        vendorMysql.id = getSystemEntityId(companyId, pluginId)
        create(vendorMysql)

        SystemEntity all = new SystemEntity(version: 0, key: 'All', value: 'All', type: SystemEntityTypeCacheUtility.DOC_CONTENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13101L)
        all.id = getSystemEntityId(companyId, pluginId)
        create(all)
        SystemEntity file = new SystemEntity(version: 0, key: 'File', value: 'File', type: SystemEntityTypeCacheUtility.DOC_CONTENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13102L)
        file.id = getSystemEntityId(companyId, pluginId)
        create(file)
        SystemEntity article = new SystemEntity(version: 0, key: 'Article', value: 'Article', type: SystemEntityTypeCacheUtility.DOC_CONTENT_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13103L)
        article.id = getSystemEntityId(companyId, pluginId)
        create(article)

    }

    public void createDefaultDataForSarbTaskReviseStatus(long companyId) {
        long pluginId = PluginConnector.SARB_ID
        SystemEntity movedForCancel = new SystemEntity(version: 0, key: 'Moved for cancel', value: 'Moved for cancel', type: SystemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1298L)
        movedForCancel.id = getSystemEntityId(companyId, pluginId)
        create(movedForCancel)

        SystemEntity movedForReplace = new SystemEntity(version: 0, key: 'Moved for replace', value: 'Moved for replace', type: SystemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1299L)
        movedForReplace.id = getSystemEntityId(companyId, pluginId)
        create(movedForReplace)

        SystemEntity movedForRefund = new SystemEntity(version: 0, key: 'Moved for refund', value: 'Moved for refund', type: SystemEntityTypeCacheUtility.SARB_TASK_REVISE_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12100L)
        movedForRefund.id = getSystemEntityId(companyId, pluginId)
        create(movedForRefund)
    }

}
