package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListAppMailActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    AppMailService appMailService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String SHOW_APP_MAIL_FAILURE_MESSAGE = "Failed to load app mail page"
    private static final String LST_APP_MAILS = "lstAppMail"


    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            // if url found no param of plugin then set application plugin as default
            String plugin = params.plugin ? params.plugin : 1
            int pluginId = Integer.parseInt(plugin)

            // if plugin id is not desired then redirect to application plugin as default
            if (pluginId >= 14) {
                pluginId = 1
            }

            long companyId = appSessionUtil.getCompanyId()
            List appMailList = appMailService.findAllByCompanyIdAndPluginId(companyId, pluginId)
            int count = appMailService.countByCompanyIdAndPluginId(companyId, pluginId)
            result.put(LST_APP_MAILS, appMailList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_APP_MAIL_FAILURE_MESSAGE)
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List appMailList = (List) executeResult.get(LST_APP_MAILS)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultAppMailList = wrapListInGridEntity(appMailList, start)
            Map gridObj = [page: pageNumber, total: count, rows: resultAppMailList]
            return gridObj
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_APP_MAIL_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_APP_MAIL_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_APP_MAIL_FAILURE_MESSAGE)
            return result
        }
    }


    private List wrapListInGridEntity(List<AppMail> appMailList, int start) {
        List lstWrapAppMail = [] as List
        int counter = start + 1
        for (int i = 0; i < appMailList.size(); i++) {
            AppMail appMail = appMailList[i]
            GridEntity obj = new GridEntity()

            obj.id = appMail.id
            obj.cell = [
                    counter,
                    appMail.id,
                    appMail.subject,
                    appMail.transactionCode,
                    appMail.roleIds,
                    appMail.isActive ? Tools.YES : Tools.NO,
                    appMail.isManualSend ? Tools.YES : Tools.NO,
                    appMail.controllerName,
                    appMail.actionName
            ]
            lstWrapAppMail << obj
            counter++
        }
        return lstWrapAppMail
    }


}
