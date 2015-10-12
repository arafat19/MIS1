package com.athena.mis.integration.arms

import com.athena.mis.arms.utility.*
import org.springframework.beans.factory.annotation.Autowired

class ArmsImplService extends ArmsPluginConnector {

    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    UserRmsExchangeHouseCacheUtility userRmsExchangeHouseCacheUtility

    static transactional = false
    static lazyInit = false
    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(ARMS, this);
        return true
    }

    @Override
    public String getName() {
        return ARMS;
    }

    @Override
    public int getId() {
        return ARMS_ID;
    }

    ArmsDefaultDataBootStrapService armsDefaultDataBootStrapService
    ArmsSchemaUpdateBootStrapService armsSchemaUpdateBootStrapService
    ArmsBootStrapService armsBootStrapService

    void initProcessTypeCacheUtility() {
        rmsProcessTypeCacheUtility.init()
    }

    List listProcessType() {
        return rmsProcessTypeCacheUtility.listByIsActive()
    }

    void initInstrumentTypeCacheUtility() {
        rmsInstrumentTypeCacheUtility.init()
    }

    List listInstrumentType() {
        return rmsInstrumentTypeCacheUtility.listByIsActive()
    }

    void initPaymentMethodCacheUtility() {
        rmsPaymentMethodCacheUtility.init()
    }

    List listPaymentMethod() {
        return rmsPaymentMethodCacheUtility.listByIsActive()
    }

    void initTaskStatusCacheUtility() {
        rmsTaskStatusCacheUtility.init()
    }

    List listTaskStatus() {
        return rmsTaskStatusCacheUtility.listByIsActive()
    }

    // get reserved system entity of process type
    public Object readByReservedProcessType(long reservedId, long companyId) {
        return rmsProcessTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of instrument type
    public Object readByReservedInstrumentType(long reservedId, long companyId) {
        return rmsInstrumentTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of payment method type
    public Object readByReservedPaymentMethodType(long reservedId, long companyId) {
        return rmsPaymentMethodCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of task status type
    public Object readByReservedTaskStatusType(long reservedId, long companyId) {
        return rmsTaskStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) armsDefaultDataBootStrapService.init()
        if (isSchema) armsSchemaUpdateBootStrapService.init()
        armsBootStrapService.init()
    }

    public Object readByExchangeHouseId(long exhId) {
        return rmsExchangeHouseCacheUtility.read(exhId)
    }

    public void initSession() {
        rmsSessionUtil.init()
    }

    public void addAppUserExchangeHouse(Object appUserEntity) {
        userRmsExchangeHouseCacheUtility.add(appUserEntity, userRmsExchangeHouseCacheUtility.SORT_ON_ID, userRmsExchangeHouseCacheUtility.SORT_ORDER_ASCENDING)
    }

    public void updateAppUserExchangeHouse(Object appUserEntity) {
        userRmsExchangeHouseCacheUtility.update(appUserEntity, userRmsExchangeHouseCacheUtility.SORT_ON_ID, userRmsExchangeHouseCacheUtility.SORT_ORDER_ASCENDING)
    }

    public void deleteAppUserExchangeHouse(long id) {
        userRmsExchangeHouseCacheUtility.delete(id)
    }

    public Object getProcessTypeIssue() {
        return RmsProcessTypeCacheUtility.ISSUE
    }

    public Object getProcessTypeForward() {
        return RmsProcessTypeCacheUtility.FORWARD
    }

    public Object getProcessTypePurchase() {
        return RmsProcessTypeCacheUtility.PURCHASE
    }
}
