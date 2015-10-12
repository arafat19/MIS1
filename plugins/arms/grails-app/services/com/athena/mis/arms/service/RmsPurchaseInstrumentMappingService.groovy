package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping
import com.athena.mis.arms.utility.RmsSessionUtil
import org.springframework.beans.factory.annotation.Autowired

class RmsPurchaseInstrumentMappingService extends BaseService {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    public RmsPurchaseInstrumentMapping create(RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping) {
        RmsPurchaseInstrumentMapping savedRmsPurchaseInstrumentMapping = rmsPurchaseInstrumentMapping.save()
        return savedRmsPurchaseInstrumentMapping
    }

    public RmsPurchaseInstrumentMapping read(long id) {
        return RmsPurchaseInstrumentMapping.read(id)
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_purchase_instrument_mapping SET
            version=:newVersion,
            company_id=:companyId,
            bank_id=:bankId,
            bank_branch_id=:bankBranchId,
            district_id=:districtId,
            instrument_type_id=:instrumentTypeId,
            commission_script=:commissionScript
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update RmsPurchaseInstrumentMapping object into DB
     * @param RmsPurchaseInstrumentMapping - RmsPurchaseInstrumentMapping object
     * @return - an integer containing the value of update count
     */
    public int update(RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping) {
        Map queryParams = [
                id: rmsPurchaseInstrumentMapping.id,
                newVersion: rmsPurchaseInstrumentMapping.version + 1,
                version: rmsPurchaseInstrumentMapping.version,
                bankId: rmsPurchaseInstrumentMapping.bankId,
                bankBranchId: rmsPurchaseInstrumentMapping.bankBranchId,
                districtId: rmsPurchaseInstrumentMapping.districtId,
                commissionScript: rmsPurchaseInstrumentMapping.commissionScript,
                instrumentTypeId: rmsPurchaseInstrumentMapping.instrumentTypeId,
                companyId: rmsPurchaseInstrumentMapping.companyId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update RmsPurchaseInstrumentMapping information')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM rms_purchase_instrument_mapping
        WHERE
            id=:id
    """
    /**
     * Delete RmsPurchaseInstrumentMapping object from DB
     * @param id - id of RmsPurchaseInstrumentMapping object
     * @return - an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete RmsPurchaseInstrumentMapping information')
        }
        return deleteCount
    }

    public List<RmsPurchaseInstrumentMapping> list(BaseService baseService) {
        return RmsPurchaseInstrumentMapping.findAllByCompanyId(
                rmsSessionUtil.appSessionUtil.getCompanyId(),
                [
                        max: baseService.resultPerPage,
                        offset: baseService.start,
                        sort: baseService.sortColumn,
                        order: baseService.sortOrder,
                        readOnly: true
                ]
        )
    }

    public int count() {
        return RmsPurchaseInstrumentMapping.countByCompanyId(rmsSessionUtil.appSessionUtil.getCompanyId())
    }

    public int countExistingMapping(long bankBranchId, long instrumentTypeId) {
        return RmsPurchaseInstrumentMapping.countByCompanyIdAndBankBranchIdAndInstrumentTypeId(
                rmsSessionUtil.appSessionUtil.getCompanyId(), bankBranchId, instrumentTypeId
        )
    }

    public int countExistingMappingForUpdate(long bankBranchId, long instrumentTypeId, long id) {
        return RmsPurchaseInstrumentMapping.findAllByCompanyIdAndBankBranchIdAndInstrumentTypeIdAndIdNotEqual(
                rmsSessionUtil.appSessionUtil.getCompanyId(), bankBranchId, instrumentTypeId, id, [readOnly: true]
        ).size()
    }

    public RmsPurchaseInstrumentMapping readByCompanyIdAndBranchIdAndInstrumentTypeId(long companyId, long branchId, long instrumentTypeId) {
        return RmsPurchaseInstrumentMapping.findByCompanyIdAndBankBranchIdAndInstrumentTypeId(
                companyId, branchId, instrumentTypeId, [readOnly: true]
        )
    }
}
