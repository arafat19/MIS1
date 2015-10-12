package com.athena.mis.application.actions.sysconfiguration

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update SysConfiguration object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSysConfigurationActionService'
 */
class UpdateSysConfigurationActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SysConfigurationService sysConfigurationService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility


    private static final String UPDATE_FAILURE_MESSAGE = "System Configuration Information could not be updated"
    private static
    final String UPDATE_SUCCESS_MESSAGE = "System Configuration Information has been updated successfully"
    private static final String NOT_FOUND_MASSAGE = "System-Configuration not found"
    private static final String SYS_CONFIGURATION = "sys_configuration"

    /**
     * Check different criteria to update SysConfiguration object
     *      1) Check access permission to update SysConfiguration object
     *      2) Check existence of old SysConfiguration object
     * @param params -N/A
     * @param obj -SysConfiguration object send from controller
     * @return -a map containing SysConfiguration object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            //Only development user can update SysConfiguration object
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            SysConfiguration newSysConfig = (SysConfiguration) obj
            SysConfiguration oldSysConfig = sysConfigurationService.read(newSysConfig.id)
            if (!oldSysConfig) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }
            oldSysConfig.value = newSysConfig.value
            result.put(SYS_CONFIGURATION, oldSysConfig)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * update SysConfiguration object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -SysConfiguration Object send from controller
     * @return -updated SysConfiguration object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            SysConfiguration newSysConfig = (SysConfiguration) preResult.get(SYS_CONFIGURATION)
//            SysConfiguration oldSysConfiguration = SysConfiguration.findByIdAndCompanyId(sysCon.id, sysCon.companyId, [readOnly: true])
//            oldSysConfiguration.value = sysCon.value                // update only user inputs
            sysConfigurationService.update(newSysConfig)
            updateCacheUtility(newSysConfig) //update cache

            result.put(SYS_CONFIGURATION, newSysConfig)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated SysConfiguration object to show on grid
     * @param obj -updated SysConfiguration object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            SysConfiguration sysCon = (SysConfiguration) executeResult.get(SYS_CONFIGURATION)
            GridEntity object = new GridEntity()
            object.id = sysCon.id
            String description = Tools.makeDetailsShort(sysCon.description, Tools.DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG)
            object.cell = [
                    Tools.LABEL_NEW,
                    sysCon.id,
                    sysCon.key,
                    sysCon.value,
                    description
            ]

            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Init cache utility of a specific plugin
     * @param sysConfiguration -SysConfiguration
     */
    private void updateCacheUtility(SysConfiguration sysConfiguration) {
        // check and re-init sysConfig cache
        switch (sysConfiguration.pluginId) {
            case PluginConnector.INVENTORY_ID:
                inventoryImplService?.initInvSysConfiguration()
                break
            case PluginConnector.ACCOUNTING_ID:
                accountingImplService?.initAccSysConfiguration()
                break
            case PluginConnector.EXCHANGE_HOUSE_ID:
                exchangeHouseImplService?.initExhSysConfigCacheUtility()
                break
            case PluginConnector.SARB_ID:
                sarbImplService?.initSarbSysConfiguration()
                break
            case PluginConnector.DOCUMENT_ID:
                documentImplService?.initDocumentSysConfigCacheUtility()
                break


        }
    }
}
