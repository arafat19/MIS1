package com.athena.mis.integration.qsmeasurement

import com.athena.mis.PluginConnector

public abstract class QsMeasurementPluginConnector extends PluginConnector {

    // Get count of QsMeasurement by budget id
    public abstract int countQSMeasurementByBudgetId(long budgetId)

    // Get sum of QsMeasurement by budget id
    public abstract double getQsSumOfBudget(long budgetId, boolean isGovt)

    // Get gross receivable amount of QsMeasurement(both internal and govt.) by project id
    public abstract Map getGrossReceivableQs(long projectId)

    public abstract void bootStrap(boolean isSchema, boolean isData)
}