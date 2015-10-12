package com.athena.mis.integration.document

import com.athena.mis.PluginConnector

public abstract class DocumentPluginConnector extends PluginConnector {

    public abstract void bootStrap(boolean isSchema, boolean isData)

    // get object of sysConfiguration
    public abstract Object readSysConfig(String key, long companyId)

    public abstract void initDocumentSysConfigCacheUtility()

    public abstract Object readCategory(long id)

    public abstract String getLabelCategory()

    public abstract Object readSubCategory(long id)

    public abstract String getLabelSubCategory()

    public abstract List listDbVendor()

    public abstract List listDocContentType()

    }
