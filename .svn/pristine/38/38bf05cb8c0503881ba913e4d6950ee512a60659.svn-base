package com.athena.mis.intergation.fixedasset

import com.athena.mis.fixedasset.service.FixedAssetModelService
import org.springframework.transaction.annotation.Transactional

class FxdSchemaUpdateBootStrapService {

    FixedAssetModelService fixedAssetModelService

    @Transactional
    public void init() {
        fixedAssetModelService.createDefaultSchema()
    }
}
