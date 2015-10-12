package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.District
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DistrictService extends BaseService {

    static transactional = false

    @Autowired
    DistrictCacheUtility districtCacheUtility

    @Transactional(readOnly = true)
    public List<District> list() {
        List<District> districtList = District.list(sort: districtCacheUtility.DEFAULT_SORT_PROPERTY, order: districtCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
        return districtList
    }

    //create a District
    public District create(District district) {
        District newDistrict = district.save()
        return newDistrict
    }

    public District read(long districtId) {
        District district = District.read(districtId)
        return district
    }

    private static final String UPDATE_QUERY = """
                UPDATE district SET
                    version=:newVersion,
                    name=:name,
                    is_global = :isGlobal,
                    updated_by =:updatedBy,
                    updated_on =:updatedOn
                WHERE
                    id=:id AND
                    version=:oldVersion
    """

    /**
     * Updates a supplied district
     * @param district District to be updated
     * @return updated district if saved successfully, otherwise throw RuntimeException
     */
    public Integer update(District district) {
        Map queryParams = [
                newVersion: district.version + 1,
                name: district.name,
                id: district.id,
                oldVersion: district.version,
                updatedOn: DateUtility.getSqlDateWithSeconds(district.updatedOn),
                updatedBy: district.updatedBy,
                isGlobal: district.isGlobal
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update District")
        }
        district.version = district.version + 1

        return (new Integer(updateCount));
    }

    /**
     * Deletes a District
     *
     * @param districtId primary key of the district being deleted
     * @return
     */
    public Boolean delete(Long id) {
        District districtInstance = District.get(id)
        if (districtInstance == null) {
            return new Boolean(false)
        }

        districtInstance.delete()
        return new Boolean(true)
    }

    /**
     * Used to create District
     * @param name - District name to check duplicate
     * @return - duplicate count
     */
    public int countByNameIlike(String name) {
        int count = District.countByNameIlike(name)
        return count
    }
    /**
     * Used to update District
     * @param name - District name to check duplicate
     * @return - duplicate count
     */
    public int countByNameAndIdNotEqual(String name, long id) {
        int count = District.countByNameAndIdNotEqual(name, id)
        return count
    }

    public int countByIsGlobal(Boolean isGlobal) {
        int count = District.countByIsGlobal(isGlobal)
        return count
    }

    public int countByIsGlobalAndIdNotEqual(Boolean isGlobal, long districtId) {
        int count = District.countByIsGlobalAndIdNotEqual(isGlobal, districtId)
        return count
    }

    public void createDefaultData(long companyId) {
        new District(version: 0, name: 'ANY DISTRICT', companyId: companyId, isGlobal: Boolean.TRUE, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new District(version: 0, name: 'Dhaka', companyId: companyId, isGlobal: Boolean.FALSE, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new District(version: 0, name: 'Chittagong', companyId: companyId, isGlobal: Boolean.FALSE, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
    }
}
