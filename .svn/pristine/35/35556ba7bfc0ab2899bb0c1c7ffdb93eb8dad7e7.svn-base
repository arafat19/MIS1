package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * render hidden input html of system bank id
 * for details go through use case named "GetSystemBankTagLibActionService"
 */
class GetSystemBankTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String NAME = "name"

    @Autowired
    BankCacheUtility bankCacheUtility

    /**
     * check if required attr exists
     * @param parameters - attr of taglib
     * @param obj - N/A
     * @return
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            if (!attrs.name) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                return result
            }
            String strName = attrs.name
            result.put(NAME, strName)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get system bank and build html
     * @param parameters - N/A
     * @param obj - preResult
     * @return
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            String strName = (String) preResult.get(NAME)
            Bank systemBank = bankCacheUtility.getSystemBank()
            String html = buildBankHtml(systemBank, strName)
            return html
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * build html
     */
    private String buildBankHtml(Bank bank, String strName) {
        String html = """
        <input type="hidden" name="${strName}" id="${strName}" value="${bank ? bank.id : Tools.EMPTY_SPACE}"/>
        """
        return html
    }
}
