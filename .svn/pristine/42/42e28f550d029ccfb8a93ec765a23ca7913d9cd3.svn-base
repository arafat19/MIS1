package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhPostalCode

class ExhPostalCodeService extends BaseService {

    static transactional = false

    public List list() {
        return ExhPostalCode.list(readOnly: true)
    }

    private static final String INSERT_QUERY = """
            INSERT INTO exh_postal_code(id, version, code, company_id)
            VALUES (NEXTVAL('exh_postal_code_id_seq'),:version, :code, :companyId);
    """

    public ExhPostalCode create(ExhPostalCode postalCode) {
        Map queryParams = [
                version: postalCode.version,
                code: postalCode.code,
                companyId: postalCode.companyId
        ]

        List<ExhPostalCode> result = (List<ExhPostalCode>) executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert postal code information')
        }

        int postalCodeId = (int) result[0][0]
        postalCode.id = postalCodeId
        return postalCode
    }


    private static final String UPDATE_QUERY = """
        UPDATE exh_postal_code SET
              version=:newVersion,
              code=:code
        WHERE
              id=:id AND
              version=:version
    """

    /**
     * Update postalCode object in DB
     * @param postalCode -postalCode object
     * @return -an integer containing the value of update count
     */
    public int update(ExhPostalCode postalCode) {

        Map queryParams = [
                id: postalCode.id,
                newVersion: postalCode.version + 1,
                version: postalCode.version,
                code: postalCode.code
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update postalCode information')
        }
        postalCode.version = postalCode.version + 1
        return updateCount;
    }
    private static final String DELETE_QUERY =
            """
                    DELETE FROM exh_postal_code
                       WHERE
                          id=:id
    """

    /**
     * Delete postalCode object from DB
     * @param id -id of postalCode object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {

        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete postal code information')
        }
        return deleteCount;
    }

    public ExhPostalCode read(long id) {
        ExhPostalCode postalCode = ExhPostalCode.read(id)
        return postalCode
    }

    public ExhPostalCode readByCodeAndCompanyId(String code, long companyId) {
        ExhPostalCode exhPostalCode = ExhPostalCode.findByCodeIlikeAndCompanyId(code, companyId, [readOnly: true])
        return exhPostalCode
    }

    public boolean checkDuplicateCode(String code, long id, long companyId) {
        int count = ExhPostalCode.countByCodeIlikeAndIdNotEqualAndCompanyId(code, id, companyId)
        return (count > 0)
    }

    public void createDefaultDataForExhPostalCode(long companyId) {

        new ExhPostalCode(companyId: companyId, code: '8605').save(flush: true)
        new ExhPostalCode(companyId: companyId, code: '7489').save(flush: true)
        new ExhPostalCode(companyId: companyId, code: '1344').save(flush: true)

    }

}
