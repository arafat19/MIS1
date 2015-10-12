package com.athena.mis.application.actions.district

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DistrictCacheUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ListDistrictActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String NO_AUTHORITY = "You are not authorized to view this page"
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

/**
 * List district has no pre-condition
 */
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieving a list of districts, it may sort and pagingate the resulting
     * district list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of districts with count and page number for pagination
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {

            // @todo-Azam we should sync rp with Grid (front-end) and should not depend on default
            if (!params.rp) {
                params.rp = 15;
            }

            if ((!params.sortname) || (params.sortname.toString().equals('id'))) {
                // if no sort name then sort by name/asc
                params.sortname = 'name'
                params.sortorder = 'asc'
            }

            initSearch(params) // initSearch will call initPager()
            List<District> districtList = []
            int count = 0

            List<District> tempDistricts = districtCacheUtility.list()
            tempDistricts.each {
                districtList << it
            }

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
        } catch (Exception e) {
            log.error(e.getMessage())
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
        for (int i = 0; i < districtList.size(); i++) {
            District district = districtList[i]
            GridEntity obj = new GridEntity()
            obj.id = district.id
            obj.cell = ["${counter}", "${district.id}", "${district.name}"]
            districts << obj
            counter++
        };
        return districts;
    }

    /**
     * List district has no post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null;
    }

    /**
     * Wrapping district list into FlexiGrid equivalent row representation
     * with page number and total
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
            log.error(ex.getMessage())
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
