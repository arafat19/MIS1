package com.athena.mis.integration.fixedasset

import com.athena.mis.PluginConnector
import groovy.sql.GroovyRowResult

public abstract class FixedAssetPluginConnector extends PluginConnector{
    // Get FixedAssetDetails object by id
    public abstract Object getFixedAssetDetailsById(long id)

    // Get fixed asset details list by inventory id and item id
    public abstract List getFixedAssetListByInvIdAndItemId(long inventoryId, long itemId)

    // Get fixed asset details list by inventory id
    public abstract List<GroovyRowResult> getFixedAssetListByInvId(long inventoryId)

    public abstract void bootStrap(boolean isSchema, boolean isData)
}