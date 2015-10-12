package com.athena.mis.inventory.config


public interface InvSysConfigurationIntf {

    //autoApprove() determines whether inventory transaction will occur in the event of In/Out/Consumption/Production create

    public static final String MIS_INVENTORY_AUTO_APPROVE_ON_CONSUMPTION = "mis.inventory.autoApproveOnConsumption"
    public static final String MIS_INVENTORY_AUTO_APPROVE_ON_OUT = "mis.inventory.autoApproveOnOut"
    public static final String MIS_INVENTORY_AUTO_APPROVE_ON_PRODUCTION_WITH_CONSUMPTION = "mis.inventory.autoApproveOnProductionWithConsumption"
    public static final String INVENTORY_AUTO_APPROVE_IN_FROM_SUPPLIER = "mis.inventory.autoApproveInFromSupplier"
    public static final String INVENTORY_AUTO_APPROVE_IN_FROM_INVENTORY = "mis.inventory.autoApproveInFromInventory"

}