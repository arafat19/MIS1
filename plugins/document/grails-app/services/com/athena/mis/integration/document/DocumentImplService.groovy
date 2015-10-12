package com.athena.mis.integration.document

import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

class DocumentImplService extends DocumentPluginConnector {

    static transactional = false
    static lazyInit = false

    @Autowired
    DocSessionUtil docSessionUtil1

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(DOCUMENT, this);
        return true
    }

    @Override
    public String getName() {
        return DOCUMENT;
    }

    @Override
    public int getId() {
        return DOCUMENT_ID;
    }

    DocumentDefaultDataBootStrapService documentDefaultDataBootStrapService
    DocumentSchemaUpdateBootStrapService documentSchemaUpdateBootStrapService
    DocumentBootStrapService documentBootStrapService
    SystemEntityService systemEntityService

    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) documentDefaultDataBootStrapService.init()
        if (isSchema) documentSchemaUpdateBootStrapService.init()
        documentBootStrapService.init()
    }

    // get object of sysConfiguration
    public Object readSysConfig(String key, long companyId) {
        return docSysConfigurationCacheUtility.readByKeyAndCompanyId(key, companyId)
    }

    //init Document Cache Utility
    public void initDocumentSysConfigCacheUtility() {
        docSysConfigurationCacheUtility.init()
    }

    //Read Category Object By categoryId
    public Object readCategory(long id) {
        return docCategoryCacheUtility.read(id)
    }

    //Read Category Label
    public String getLabelCategory() {
        String categoryLabel = Tools.EMPTY_SPACE
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, docSessionUtil.appSessionUtil.companyId)
        if (sysConfiguration) {
            categoryLabel = sysConfiguration.value
        }
        return categoryLabel
    }

    //Read Sub Category Object By categoryId
    public Object readSubCategory(long id) {
        return docSubCategoryCacheUtility.read(id)
    }

    //Read Sub Category Label
    public String getLabelSubCategory() {
        String subCategoryLabel = Tools.EMPTY_SPACE
        SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, docSessionUtil1.appSessionUtil.companyId)
        if (sysConfiguration) {
            subCategoryLabel = sysConfiguration.value
        }
        return subCategoryLabel
    }


    public List listDbVendor() {
        List<SystemEntity> lstDbVendor = systemEntityService.listByType(systemEntityTypeCacheUtility.DOC_DATABASE_VENDOR)
        return lstDbVendor
    }

    public List listDocContentType() {
        List<SystemEntity> lstDocContentType = systemEntityService.listByType(systemEntityTypeCacheUtility.DOC_CONTENT_TYPE)
        return lstDocContentType
    }
}
