package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ContentTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class DispatchImageForCustomerActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    EntityContentService entityContentService
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        try {
            LinkedHashMap paramsMap = (LinkedHashMap) parameters
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            // pull system entity type(Customer) object
            SystemEntity contentEntityTypeCustomer = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_EXH_CUSTOMER, companyId)
            // pull system entity type(Image) object
            SystemEntity contentTypeImage = (SystemEntity) contentTypeCacheUtility.readByReservedAndCompany(contentTypeCacheUtility.CONTENT_TYPE_IMAGE_ID, companyId)

            long entityId = Long.parseLong(paramsMap.id)
            long contentTypeId = contentTypeImage.id
            long entityTypeId = contentEntityTypeCustomer.id
            EntityContent entityImage = entityContentService.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, entityId, contentTypeId)
            return entityImage
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
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
}
