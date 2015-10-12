package com.athena.mis.exchangehouse.actions.photoidtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhListPhotoIdTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String LST_PHOTO_ID_TYPE="photoIdTypeList"

/**
 * List photoIdType has no pre-condition
 */
    public Object executePreCondition(Object parameters, Object obj) {
        Map outputMap = new HashMap()
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            outputMap.put("hasAccess", new Boolean(false))
            return outputMap
        }
    }

    /**
     * Retrieving a list of photoIdTypes, it may sort and pagingate the resulting
     * photoIdType list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of photoIdTypes with count and page number for pagination
     */
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result=new LinkedHashMap()
        try {
            GrailsParameterMap params= (GrailsParameterMap) parameters
            // @todo-Azam we should sync rp with Grid (front-end) and should not depend on default
            if (!params.rp) {
                params.rp = 15
            }

            if ((!params.sortname) || (params.sortname.toString().equals('id'))) {
                // if no sort name then sort by name/asc
                params.sortname = 'name'
                params.sortorder = 'asc'
            }
            initSearch(params) // initSearch will call initPager()
            List<ExhPhotoIdType> photoIdTypeList = []
            List<ExhPhotoIdType> lstPhotoIdType = exhPhotoIdTypeCacheUtility.list(this)
            int count = exhPhotoIdTypeCacheUtility.count()
            result.put(LST_PHOTO_ID_TYPE,lstPhotoIdType)
            result.put(Tools.COUNT,count)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result = [photoIdTypeList: null, count: 0]
            return result
        }
    }

    /**
     * Wrapping each PhotoIdType entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param photoIdTypeList List of all PhotoIdTypes
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List<ExhPhotoIdType> wrapPhotoIdTypeListInGridEntityList(List<ExhPhotoIdType> photoIdTypeList, int start) {

        List<ExhPhotoIdType> photoIdTypes = []
        int counter = start + 1
        for (int i = 0; i < photoIdTypeList.size(); i++) {
            ExhPhotoIdType photoIdType = photoIdTypeList[i]
            GridEntity obj = new GridEntity()
            obj.id = photoIdType.id
            obj.cell = [
                    counter,
                    photoIdType.id,
                    photoIdType.name,
                    photoIdType.code,
                    photoIdType.isSecondary? Tools.YES : Tools.NO
            ]
            photoIdTypes << obj
            counter++
        }
        return photoIdTypes
    }

    /**
     * List photoIdType has no post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Wrapping photoIdType list into FlexiGrid equivalent row representation
     * with page number and total
     *
     * @param obj photoIdType list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object photoIdTypeResult) {
        LinkedHashMap output
        try {
            Map executeResult=(Map) photoIdTypeResult
            List<ExhPhotoIdType> photoIdTypeList=(List<ExhPhotoIdType>)executeResult.get(LST_PHOTO_ID_TYPE)
            int count=(int)executeResult.get(Tools.COUNT)
            List<ExhPhotoIdType> photoIdTypes = wrapPhotoIdTypeListInGridEntityList(photoIdTypeList, start)
            output = [page: pageNumber, total: count, rows: photoIdTypes]
            return output
        } catch (Exception e) {
            log.error(e.getMessage())
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
