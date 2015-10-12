package com.athena.mis.integration.qsmeasurement

import com.athena.mis.integration.qsmeasurement.actions.GetGrossReceivableQsImplActionService
import com.athena.mis.integration.qsmeasurement.actions.GetQsSumOfBudgetImplActionService
import com.athena.mis.integration.qsmeasurement.actions.QsCountByBudgetIdImplActionService

class QsMeasurementImplService extends QsMeasurementPluginConnector {

    static transactional = false
    static lazyInit = false

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(QS, this);
        return true
    }

    @Override
    public String getName() {
        return QS;
    }

    @Override
    public int getId() {
        return QS_ID;
    }

    QsCountByBudgetIdImplActionService qsCountByBudgetIdImplActionService
    GetQsSumOfBudgetImplActionService getQsSumOfBudgetImplActionService
    GetGrossReceivableQsImplActionService getGrossReceivableQsImplActionService

    QsDefaultDataBootStrapService qsDefaultDataBootStrapService
    QsSchemaUpdateBootStrapService qsSchemaUpdateBootStrapService
    QsBootStrapService qsBootStrapService

    // Get count of QsMeasurement by budget id
    int countQSMeasurementByBudgetId(long budgetId) {
        return (int) qsCountByBudgetIdImplActionService.execute(budgetId, null)
    }

    // Get sum of QsMeasurement by budget id
    double getQsSumOfBudget(long budgetId, boolean isGovt) {
        return (double) getQsSumOfBudgetImplActionService.execute(budgetId, isGovt)
    }

    // Get gross receivable amount of QsMeasurement(both internal and govt.) by project id
    Map getGrossReceivableQs(long projectId) {
        return (LinkedHashMap) getGrossReceivableQsImplActionService.execute(projectId, null)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) qsDefaultDataBootStrapService.init()
        if (isSchema) qsSchemaUpdateBootStrapService.init()
        qsBootStrapService.init()
    }
}
