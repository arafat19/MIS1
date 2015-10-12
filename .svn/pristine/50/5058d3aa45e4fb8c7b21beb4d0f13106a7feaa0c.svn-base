package com.athena.mis.integration.arms

import com.athena.mis.PluginConnector

public abstract class ArmsPluginConnector extends PluginConnector {

    public abstract void bootStrap(boolean isSchema, boolean isData)

    public abstract void initProcessTypeCacheUtility()

    public abstract List listProcessType()

    public abstract void initInstrumentTypeCacheUtility()

    public abstract List listInstrumentType()

    public abstract void initPaymentMethodCacheUtility()

    public abstract List listPaymentMethod()

    public abstract void initTaskStatusCacheUtility()

    public abstract List listTaskStatus()

    // get reserved system entity of process type
    public abstract Object readByReservedProcessType(long reservedId, long companyId)

    // get reserved system entity of instrument type
    public abstract Object readByReservedInstrumentType(long reservedId, long companyId)

    // get reserved system entity of payment method type
    public abstract Object readByReservedPaymentMethodType(long reservedId, long companyId)

    // get reserved system entity of task status type
    public abstract Object readByReservedTaskStatusType(long reservedId, long companyId)

    public abstract Object readByExchangeHouseId(long exhId)

    public abstract void initSession()

    public abstract void addAppUserExchangeHouse(Object appUserEntity)

    public abstract void updateAppUserExchangeHouse(Object appUserEntity)

    public abstract void deleteAppUserExchangeHouse(long id)

    public abstract Object getProcessTypeIssue()

    public abstract Object getProcessTypeForward()

    public abstract Object getProcessTypePurchase()
}
