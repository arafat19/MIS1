import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.application.AppDefaultDataBootStrapService
import com.athena.mis.integration.application.AppSchemaUpdateBootStrapService
import com.athena.mis.integration.application.ApplicationBootStrapService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.integration.projecttrack.ProjectTrackPluginConnector
import com.athena.mis.integration.qsmeasurement.QsMeasurementPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.beans.factory.annotation.Autowired

class BootStrap {
    AppSchemaUpdateBootStrapService appSchemaUpdateBootStrapService
    AppDefaultDataBootStrapService appDefaultDataBootStrapService
    ApplicationBootStrapService applicationBootStrapService

    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    QsMeasurementPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ProjectTrackPluginConnector projectTrackImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired
    CompanyCacheUtility companyCacheUtility

    def init = { servletContext ->
        Boolean initDefaultData = Boolean.FALSE
        Boolean initDefaultSchema = Boolean.FALSE
/*        // Read the config to check default data insert
        String strConfigData = ConfigurationHolder.config.application.bootstrap.initDefaultData
        initDefaultData = Boolean.parseBoolean(strConfigData)
        // Read the config to check default data insert
        String strConfigSchema = ConfigurationHolder.config.application.bootstrap.initDefaultSchema
        initDefaultSchema = Boolean.parseBoolean(strConfigSchema)*/

        int count = SystemEntity.count()
        if (count == 0) {
            initDefaultData = Boolean.TRUE
            initDefaultSchema = Boolean.TRUE
        }

        if (initDefaultSchema.booleanValue()) {
            appSchemaUpdateBootStrapService.init()
        }
        if (initDefaultData.booleanValue()) {
            appDefaultDataBootStrapService.init()   // insert data
        }


        companyCacheUtility.init()  // init company cache utility for initializing other cache utilities

        if (budgetImplService) budgetImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (inventoryImplService) inventoryImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (procurementImplService) procurementImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (accountingImplService) accountingImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (qsMeasurementImplService) qsMeasurementImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (fixedAssetImplService) fixedAssetImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (exchangeHouseImplService) exchangeHouseImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (projectTrackImplService) projectTrackImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (armsImplService) armsImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (sarbImplService) sarbImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())
        if (documentImplService) documentImplService.bootStrap(initDefaultSchema.booleanValue(), initDefaultData.booleanValue())

        applicationBootStrapService.init()
    }
}
