package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsProcessInstrumentMapping
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessInstrumentMappingCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * RmsProcessInstrumentMappingService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete)
 */
class RmsProcessInstrumentMappingService extends BaseService {

    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    RmsProcessInstrumentMappingCacheUtility rmsProcessInstrumentMappingCacheUtility

    public List list() {
        return RmsProcessInstrumentMapping.list( readOnly: true)
    }
    /**
     * get count of total ProcessInstrumentMapping by companyId
     * @return int -count of total ProcessInstrumentMapping
     */
    public int countByCompanyId(long companyId) {
        int count = RmsProcessInstrumentMapping.countByCompanyId(companyId)
        return count
    }

    /**
     * get count of ProcessInstrumentMapping by ProcessType And InstrumentType
     * @param proInsMapping -ProcessInstrumentMapping object
     * @return -int count
     */
    public int countByProcessTypeAndInstrumentTypeAndCompanyId(RmsProcessInstrumentMapping proInsMapping, long companyId) {
        int count = RmsProcessInstrumentMapping.countByProcessTypeAndInstrumentTypeAndCompanyId(proInsMapping.processType, proInsMapping.instrumentType, companyId)
        return count
    }

    /**
     * get count of ProcessInstrumentMapping by ProcessType, InstrumentType and not equal id
     * @param proInsMapping -ProcessInstrumentMapping object
     * @return -int count
     */
    public int countByProcessTypeAndInstrumentTypeAndCompanyIdAndIdNotEqual(RmsProcessInstrumentMapping proInsMapping, long companyId) {
        int count = RmsProcessInstrumentMapping.countByProcessTypeAndInstrumentTypeAndCompanyIdAndIdNotEqual(proInsMapping.processType, proInsMapping.instrumentType, companyId, proInsMapping.id)
        return count
    }

    /**
     * Get ProcessInstrumentMapping object by Id
     * @param id -Id of ProcessInstrumentMapping
     * @return -object of ProcessInstrumentMapping
     */
    public RmsProcessInstrumentMapping read(long id) {
        RmsProcessInstrumentMapping processInstrumentMapping = RmsProcessInstrumentMapping.read(id)
        return processInstrumentMapping
    }

    /**
     * Save ProcessInstrumentMapping object into DB
     * @param ProcessInstrumentMapping -ProcessInstrumentMapping object
     * @return -saved ProcessInstrumentMapping object
     */
    public RmsProcessInstrumentMapping create(RmsProcessInstrumentMapping proInsMapping) {
        RmsProcessInstrumentMapping savedProInsMapping = proInsMapping.save()
        return savedProInsMapping
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_process_instrument_mapping SET
            version=:newVersion,
            process_type=:processType,
            instrument_type=:instrumentType
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update ProcessInstrumentMapping object into DB
     * @param ProcessInstrumentMapping -ProcessInstrumentMapping object
     * @return -an integer containing the value of update count
     */
    public int update(RmsProcessInstrumentMapping proInsMapping) {
        Map queryParams = [
                id: proInsMapping.id,
                newVersion: proInsMapping.version + 1,
                version: proInsMapping.version,
                processType: proInsMapping.processType,
                instrumentType: proInsMapping.instrumentType
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ProcessInstrumentMapping information')
        }
        return updateCount;
    }

    private static final String DELETE_QUERY = """
        DELETE FROM rms_process_instrument_mapping
        WHERE
            id=:id
    """
    /**
     * Delete ProcessInstrumentMapping object from DB
     * @param id -id of ProcessInstrumentMapping object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ProcessInstrumentMapping information')
        }
        return deleteCount;
    }

    public void createDefaultDataForRmsProcessInstrumentMapping(long companyId) {
        SystemEntity processIssue = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1150, companyId)
        long processIssueId = processIssue.id
        SystemEntity processForward = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1151, companyId)
        long processForwardId = processForward.id
        SystemEntity processPurchase = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1152, companyId)
        long processPurchaseId = processPurchase.id

        SystemEntity instrumentPo = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1153, companyId)
        long instrumentPoId = instrumentPo.id
        SystemEntity instrumentEft = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1154, companyId)
        long instrumentEftId = instrumentEft.id
        SystemEntity instrumentOnline = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1155, companyId)
        long instrumentOnlineId = instrumentOnline.id
        SystemEntity instrumentCashCollection = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1156, companyId)
        long instrumentCashCollectionId = instrumentCashCollection.id

        SystemEntity instrumentTt = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1157, companyId)
        long instrumentTtId = instrumentTt.id
        SystemEntity instrumentTm = (SystemEntity) SystemEntity.findByReservedIdAndCompanyId(1158, companyId)
        long instrumentTmId = instrumentTm.id

        new RmsProcessInstrumentMapping(version: 0, processType: processIssueId, instrumentType: instrumentPoId, companyId: companyId).save()
        new RmsProcessInstrumentMapping(version: 0, processType: processIssueId, instrumentType: instrumentEftId, companyId: companyId).save()
        new RmsProcessInstrumentMapping(version: 0, processType: processIssueId, instrumentType: instrumentOnlineId, companyId: companyId).save()

        new RmsProcessInstrumentMapping(version: 0, processType: processForwardId, instrumentType: instrumentOnlineId, companyId: companyId).save()
        new RmsProcessInstrumentMapping(version: 0, processType: processForwardId, instrumentType: instrumentCashCollectionId, companyId: companyId).save()

        new RmsProcessInstrumentMapping(version: 0, processType: processPurchaseId, instrumentType: instrumentTtId, companyId: companyId).save()
        new RmsProcessInstrumentMapping(version: 0, processType: processPurchaseId, instrumentType: instrumentTmId, companyId: companyId).save()

    }

}
