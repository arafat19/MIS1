package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.VehicleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("vehicleCacheUtility")
class VehicleCacheUtility extends ExtendedCacheUtility {
    @Autowired
    VehicleService vehicleService
    static final String SORT_ON_NAME = "name"

    public void init() {
        List list = vehicleService.list()
        super.setList(list)
    }
}
