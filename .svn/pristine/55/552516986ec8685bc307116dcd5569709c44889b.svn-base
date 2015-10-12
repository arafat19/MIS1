package com.athena.mis.application.actions.district

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class SearchDistrictActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String NAME = 'name'
    private static final String ASC = 'asc'
    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(HAS_ACCESS, new Boolean(true))
            } else {
                outputMap.put(HAS_ACCESS, new Boolean(false))
            }
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    Object execute(Object params, Object obj = null) {
        LinkedHashMap result
        try {
            if ((!params.sortname) || (params.sortname.toString().equals('id'))) {
                // if no sort name then sort by name/asc
                params.sortname = NAME
                params.sortorder = ASC
            }

            initSearch(params);
            List<District> districtList = []
            int count = 0

            List<District> tempDistricts = districtCacheUtility.list()
            tempDistricts.each {
                districtList << it
            }
            String escapedQuery = Tools.escapeForRegularExpression(query) // escaping special char for regX
            districtList = districtList.findAll { it.properties.get(queryType) ==~ /(?i).*${escapedQuery}.*/ }

            if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {    // if desc
                districtList.sort { a, b -> b.properties.get(sortColumn)<=>a.properties.get(sortColumn) }
            } else {
                districtList.sort { b, a -> b.properties.get(sortColumn)<=>a.properties.get(sortColumn) }
            }
            count = districtList.size()
            int end = start + resultPerPage > count ? count : start + resultPerPage
            districtList = districtList.subList(start, end)
            result = [districtList: districtList, count: count]

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result = [districtList: null, count: 0]
            return result
        }
    }

    /**
     * Wrapping each District entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param districtList List of all Districts
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private def wrapDistrictListInGridEntityList(List<District> districtList, int start) {

        def districts = []
        def counter = start + 1
        districtList.each { district ->
            GridEntity obj = new GridEntity()
            obj.id = district.id
            obj.cell = [counter, "${district.id}", "${district.name}"]
            districts << obj
            counter++
        };
        return districts;
    }

    /**
     * List district has not post-condition
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }

    /**
     * Wrapping district list retrieved from search into FlexiGrid equivalent
     * row representation with page number and total
     *
     * @param obj district list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object districtResult) {
        LinkedHashMap output
        try {
            List<District> districtList = (List<District>) districtResult.districtList
            int count = (int) districtResult.count
            def districts = wrapDistrictListInGridEntityList(districtList, start)
            output = [page: pageNumber, total: count, rows: districts]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}

