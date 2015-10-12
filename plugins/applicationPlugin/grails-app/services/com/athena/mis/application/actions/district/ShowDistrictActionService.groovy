package com.athena.mis.application.actions.district

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ShowDistrictActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String NAME = 'name'
    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * List district has no pre-condition
     */
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
        LinkedHashMap result = null
        try {

            if (!params.rp) {
                params.rp = 15;
            }
            initPager(params)
            sortColumn = NAME
            sortOrder = ASCENDING_SORT_ORDER

            List<District> districtList = []
            List<District> tempDistricts = districtCacheUtility.list()
            tempDistricts.each {
                districtList << it
            }

            int count = districtList.size()
            int end = (start + resultPerPage) > count ? count : (start + resultPerPage)
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
        List districts = [] as List
        try {
            int counter = start + 1
            districtList.each { District district ->
                GridEntity obj = new GridEntity()
                obj.id = district.id
                obj.cell = [counter, "${district.id}", "${district.name}"]
                districts << obj
                counter++
            };
            return districts;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return districts;
        }
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
        Map output = null
        try {
            List<District> districtList = (List<District>) districtResult.districtList
            int count = (int) districtResult.count

            List districts = (List) wrapDistrictListInGridEntityList(districtList, start)
            output = [page: pageNumber, total: count, rows: districts]

            return [districtListJSON: output]
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
        // do nothing
        return null
    }
}

