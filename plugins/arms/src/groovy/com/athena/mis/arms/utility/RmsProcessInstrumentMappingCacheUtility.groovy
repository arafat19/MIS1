package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.arms.entity.RmsProcessInstrumentMapping
import com.athena.mis.arms.service.RmsProcessInstrumentMappingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("rmsProcessInstrumentMappingCacheUtility")
class RmsProcessInstrumentMappingCacheUtility extends ExtendedCacheUtility{
    @Autowired
    RmsProcessInstrumentMappingService rmsProcessInstrumentMappingService
    public void init() {
        List list = rmsProcessInstrumentMappingService.list()
        super.setList(list)
    }

    public List findByProcessTypeId(long processTypeId, long companyId){
        List<Long> lstInstrument=[]
        List<RmsProcessInstrumentMapping> lstProcessInstrument= (List<RmsProcessInstrumentMapping>) list(companyId)
        for(int i=0;i<lstProcessInstrument.size();i++){
            if(lstProcessInstrument[i].processType==processTypeId){
                lstInstrument<<lstProcessInstrument[i].instrumentType
            }
        }
        return lstInstrument
    }
}
