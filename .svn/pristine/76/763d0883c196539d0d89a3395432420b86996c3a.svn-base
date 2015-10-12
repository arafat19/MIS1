package com.athena.mis.sarb.service

import com.athena.mis.BaseService
import com.athena.mis.sarb.entity.SarbProvince

/**
 * SarbProvinceService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */

class SarbProvinceService extends BaseService{

    /**
     * @return - list of province
     */
    public List list(){
        return SarbProvince.list(readOnly: true)
    }


    private static final String INSERT_QUERY = """
            INSERT INTO sarb_province(id, version, name, company_id)
            VALUES (NEXTVAL('sarb_province_id_seq'),:version, :name, :companyId);
    """

    /**
     * Save province object into DB
     * @param province -province object
     * @return -saved province object
     */
    public SarbProvince create(SarbProvince province) {
        Map queryParams = [
                version: province.version,
                name: province.name,
                companyId: province.companyId
        ]

        List<SarbProvince> result = (List<SarbProvince>)executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert postal code information')
        }

        int provinceId = (int) result[0][0]
        province.id = provinceId
        return province
    }

    /**
     * Update province object in DB
     * @param province -province object
     * @return -an integer containing the value of update count
     */
    private static final String UPDATE_QUERY = """
        UPDATE sarb_province SET
              version=:newVersion,
              name=:name
        WHERE
              id=:id AND
              version=:version
    """

    public int update(SarbProvince province) {

        Map queryParams = [
                id: province.id,
                newVersion: province.version + 1,
                version: province.version,
                name:province.name
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update province information')
        }
        province.version = province.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY =
            """
                    DELETE FROM sarb_province
                       WHERE
                          id=:id
    """

    /**
     * Delete province object from DB
     * @param id -id of province object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {

        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete province information')
        }
        return deleteCount;
    }

    /**
     * applicable only for create default province
     */
    public SarbProvince read(long id){
        SarbProvince postalCode= SarbProvince.read(id)
        return  postalCode
    }

    public void createDefaultDataForSarbProvince(long companyId){

        new SarbProvince(name: 'GAUTENG ', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'LIMPOPO ', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'NORTH WEST', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'WESTERN CAPE', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'EASTERN CAPE', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'NORTHERN CAPE', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'FREE STATE', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'MPUMALANGA ', companyId: companyId).save(flush: true)
        new SarbProvince(name: 'KWAZULU NATAL', companyId: companyId).save(flush: true)

    }

}
