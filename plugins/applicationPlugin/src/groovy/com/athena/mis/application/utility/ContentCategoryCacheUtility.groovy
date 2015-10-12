package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.service.ContentCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('contentCategoryCacheUtility')
class ContentCategoryCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ContentCategoryService contentCategoryService

    public final String SORT_ON_NAME = "name"

    public static final String IMAGE_TYPE_PHOTO = "PHOTO"
    public static final String IMAGE_TYPE_SIGNATURE = "SIGNATURE"
    public static final String IMAGE_TYPE_LOGO = "LOGO"
    public static final String IMAGE_TYPE_PHOTO_ID = "PHOTO_ID"         // e.g. Passport, Voter ID
    public static final String IMAGE_TYPE_SCREEN_SHOT = "SCREEN_SHOT"

    public void init() {
        List list = contentCategoryService.list()
        super.setList(list)
    }

    // Following method will return a object instance from app scope for systemContentCategory
    public Object readBySystemContentCategory(String systemContentCategory) {
        List<ContentCategory> lstAll = (List<ContentCategory>) super.list()
        int listSize = lstAll.size()
        for (int i = 0; i < listSize; i++) {
            if (lstAll[i].systemContentCategory.equals(systemContentCategory)) {
                return lstAll[i]
            }
        }
        return null
    }

    public int countByNameAndContentTypeId(String name, long contentTypeId) {
        List<ContentCategory> lstTemp = super.list()
        List<ContentCategory> lstContentCategory = []
        for (int i = 0; i < lstTemp.size(); i++) {
            ContentCategory contentCategory = lstTemp[i]
            if ((contentCategory.name.equalsIgnoreCase(name)) && (contentCategory.contentTypeId == contentTypeId)) {
                lstContentCategory << contentCategory
            }
        }
        return lstContentCategory.size()
    }

    public int countByNameAndContentTypeIdAndIdNotEqual(String name, long contentTypeId, long id) {
        List<ContentCategory> lstTemp = super.list()
        List<ContentCategory> lstContentCategory = []
        for (int i = 0; i < lstTemp.size(); i++) {
            ContentCategory contentCategory = lstTemp[i]
            if ((contentCategory.name.equalsIgnoreCase(name)) && (contentCategory.contentTypeId == contentTypeId)
                    && (contentCategory.id != id)) {
                lstContentCategory << contentCategory
            }
        }
        return lstContentCategory.size()
    }

    public List<ContentCategory> listByContentTypeId(long contentTypeId) {
        List<ContentCategory> lstTemp = super.list()
        List<ContentCategory> lstContentCategory = []
        for (int i = 0; i < lstTemp.size(); i++) {
            ContentCategory contentCategory = lstTemp[i]
            if ((contentCategory.contentTypeId == contentTypeId) && (!contentCategory.isReserved)) {
                lstContentCategory << contentCategory
            }
        }
        return lstContentCategory
    }

    public int countByContentTypeId(long contentTypeId) {
        List<ContentCategory> lstTemp = super.list()
        List<ContentCategory> lstContentCategory = []
        for (int i = 0; i < lstTemp.size(); i++) {
            ContentCategory contentCategory = lstTemp[i]
            if (contentCategory.contentTypeId == contentTypeId) {
                lstContentCategory << contentCategory
            }
        }
        return lstContentCategory.size()
    }
}

