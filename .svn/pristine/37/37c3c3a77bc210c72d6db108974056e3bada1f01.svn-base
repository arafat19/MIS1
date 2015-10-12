/* get fixed asset list by item and project */
package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.UserProjectCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class GetFixedAssetListByItemAndProjectActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load fixed asset list"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String PROJECT_NOT_FOUND = "Selected project not found"
    private static final String ITEM_NOT_FOUND = "Selected item not found"
    private static final String FIXED_ASSET_LIST = "fixedAssetList"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not associated with any project"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if ((!parameterMap.fromDate) && (!parameterMap.toDate) &&
                    (!parameterMap.itemId)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                parameterMap.projectId = 0L
            }
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            if (projectId > 0) {
                Project project = (Project) projectCacheUtility.read(projectId)
                if (!project) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                    return result
                }
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())
            Item item = (Item) itemCacheUtility.read(itemId)
            if (!item) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }

            List<Long> userProjectIds = []
            if (projectId <= 0) {
                userProjectIds = userProjectCacheUtility.listUserProjectIds()
                if (userProjectIds.size() <= 0) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                    return result
                }
            } else {
                userProjectIds << projectId
            }

            Date fromDate = DateUtility.parseMaskedDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedDate(parameterMap.toDate.toString())

            List fixedAssetList = getFixedAssetList(fromDate, toDate, userProjectIds, itemId)

            result.put(FIXED_ASSET_LIST, Tools.listForKendoDropdown(fixedAssetList, null, 'ALL'))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private List<GroovyRowResult> getFixedAssetList(Date fromDate, Date toDate, List<Long> projectIdList, long itemId) {
        String lstProjectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        String queryStr = """
                SELECT iitd.fixed_asset_details_id AS id, fad.name AS name
                  FROM vw_inv_inventory_transaction_with_details iitd
                LEFT JOIN fxd_fixed_asset_details fad ON fad.id = iitd.fixed_asset_details_id
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE  iitd.transaction_type_id =:transactionTypeId AND
                       iitd.project_id IN(${lstProjectIds}) AND
                       iitd.approved_by > 0 AND
                       iitd.fixed_asset_details_id > 0 AND
                       iitd.is_current = TRUE AND
                       iitd.item_id =:itemId AND
                       (iitd.transaction_date BETWEEN :fromDate AND :toDate)
                GROUP BY iitd.fixed_asset_details_id, fad.name ORDER BY fad.name
        """
        Map queryParams = [
                itemId: itemId,
                transactionTypeId: transactionTypeCons.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate)
        ]

        List<GroovyRowResult> fixedAssetList = executeSelectSql(queryStr, queryParams)
        return fixedAssetList
    }
}
