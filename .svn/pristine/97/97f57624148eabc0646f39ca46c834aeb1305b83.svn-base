package com.athena.mis.intergation.fixedasset

import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.intergation.fixedasset.actions.GetFixedAssetDetailsByIdImplActionService
import com.athena.mis.intergation.fixedasset.actions.GetFixedAssetListByInvIdImplActionService
import com.athena.mis.intergation.fixedasset.actions.ListByInvIdAndItemIdImplActionService
import groovy.sql.GroovyRowResult

class FixedAssetImplService extends FixedAssetPluginConnector {

    static transactional = false
    static lazyInit = false

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(FIXED_ASSET, this);
        return true
    }

    @Override
    public String getName() {
        return FIXED_ASSET;
    }

    @Override
    public int getId() {
        return FIXED_ASSET_ID;
    }

    GetFixedAssetListByInvIdImplActionService getFixedAssetListByInvIdImplActionService
    ListByInvIdAndItemIdImplActionService listByInvIdAndItemIdImplActionService
    GetFixedAssetDetailsByIdImplActionService getFixedAssetDetailsByIdImplActionService

    FixedAssetBootStrapService fixedAssetBootStrapService
    FxdDefaultDataBootStrapService fxdDefaultDataBootStrapService
    FxdSchemaUpdateBootStrapService fxdSchemaUpdateBootStrapService

    // Return fixedAssetDetails object by id
    Object getFixedAssetDetailsById(long id) {
        return getFixedAssetDetailsByIdImplActionService.execute(id, null)
    }

    // Get fixed asset details list by inventory id and item id
    List getFixedAssetListByInvIdAndItemId(long inventoryId, long itemId) {
        return listByInvIdAndItemIdImplActionService.execute(inventoryId, itemId)
    }

    // Get fixed asset details list by inventory id
    List<GroovyRowResult> getFixedAssetListByInvId(long inventoryId) {
        return getFixedAssetListByInvIdImplActionService.execute(inventoryId, null)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) fxdDefaultDataBootStrapService.init()
        if (isSchema) fxdSchemaUpdateBootStrapService.init()
        fixedAssetBootStrapService.init()
    }
}
