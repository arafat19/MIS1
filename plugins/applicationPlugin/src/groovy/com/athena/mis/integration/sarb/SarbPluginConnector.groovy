package com.athena.mis.integration.sarb

import com.athena.mis.PluginConnector

public abstract class SarbPluginConnector extends PluginConnector {
    // init bootstrap of sarb plugin
    public abstract void bootStrap(boolean isSchema, boolean isData)

	public abstract List provinceList()

	public abstract boolean createSarbCustomerDetails(Object params, long customerId)

	public abstract boolean updateSarbCustomerDetails(Object params)

	public abstract boolean createSarbCustomerDetailsTrace(Object params)

	public abstract Object readSarbCustomerDetails(long customerId)

	public abstract String validatePhotoIdNo(String photoIdNo, String customerPhotoTypeCode, String countryCode)

    public abstract void initSarbSysConfiguration()

    public abstract String validateTaskDetails(String remittancePurposeCode, photoIdTypeCode)

    public abstract void initSarbTaskReviseStatus()

    public abstract List<Object> listSarbTaskReviseStatus()

    public abstract Object readByReservedSarbTaskReviseStatus(long reservedId, long companyId)

}
