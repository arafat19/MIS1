package com.athena.mis.intergation.fixedasset.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.service.FixedAssetDetailsService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get fixedAssetDetails object by id, used in inventory consumption details
 * For details go through Use-Case doc named 'GetFixedAssetDetailsByIdImplActionService'
 */
class GetFixedAssetDetailsByIdImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FixedAssetDetailsService fixedAssetDetailsService

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get fixedAssetDetails object by id
     * @param parameters -id of fixed asset
     * @param obj -N/A
     * @return -object of fixedAssetDetails
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            long fixedAssetId = Long.parseLong(parameters.toString())
            return fixedAssetDetailsService.read(fixedAssetId)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
