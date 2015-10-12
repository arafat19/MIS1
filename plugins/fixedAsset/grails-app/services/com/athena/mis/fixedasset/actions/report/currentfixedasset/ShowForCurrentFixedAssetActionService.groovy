package com.athena.mis.fixedasset.actions.report.currentfixedasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Show Current Fixed Asset in the Grid
class ShowForCurrentFixedAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String FAILURE_MSG = "Fail to show Current fixed asset report"
    private static final String FIXED_ASSET_LIST = "currentFixedAssetList"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map serviceReturn = listAllFixedAsset()
            List fixedAssetList = serviceReturn.fixedAssetList
            result.put(FIXED_ASSET_LIST, fixedAssetList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String SELECT_QUERY = """
        SELECT DISTINCT ON (item.name) item.name,fad.item_id as id
        FROM fxd_fixed_asset_details fad
        LEFT JOIN item on item.id=fad.item_id
        WHERE fad.company_id=:companyId
        """
    //get fixedAsset list for current fixed asset as category
    private Map listAllFixedAsset() {
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> fixedAssetList = executeSelectSql(SELECT_QUERY, queryParams)
        return [fixedAssetList: fixedAssetList]
    }
}
