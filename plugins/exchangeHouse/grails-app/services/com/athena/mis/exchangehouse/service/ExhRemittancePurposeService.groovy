package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.exchangehouse.utility.ExhRemittancePurposeCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhRemittancePurposeService extends BaseService {

    static transactional = false
    @Autowired
    ExhRemittancePurposeCacheUtility exhRemittancePurposeCacheUtility

    @Transactional(readOnly = true)
    public List<ExhRemittancePurpose> init() {
        List<ExhRemittancePurpose> exhRemittancePurposeList = ExhRemittancePurpose.list(
                sort: exhRemittancePurposeCacheUtility.DEFAULT_SORT_PROPERTY,
                order: exhRemittancePurposeCacheUtility.SORT_ORDER_DESCENDING,
                readOnly: true
        )
        return exhRemittancePurposeList
    }

    //create a remittance purpose
    public ExhRemittancePurpose create(ExhRemittancePurpose remittancePurpose) {
        ExhRemittancePurpose newRemittancePurpose = remittancePurpose.save();

        return newRemittancePurpose;
    }

/**
 * Updates a supplied remittancePurpose
 *
 * @param remittancePurpose RemittancePurpose to be updated
 * @return updated remittancePurpose if saved successfully, otherwise throw RuntimeException
 */
    public Integer update(ExhRemittancePurpose remittancePurpose) {
        String query = """UPDATE exh_remittance_purpose SET
                          version=${remittancePurpose.version + 1},
                          name=:name,
                          code=:code
                      WHERE
                          id=${remittancePurpose.id} AND
                          version=${remittancePurpose.version}"""

        Map queryParams = [name: remittancePurpose.name,
                            code: remittancePurpose.code
                            ]

        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Remittance Purpose")
        }
        remittancePurpose.version = remittancePurpose.version + 1

        return (new Integer(updateCount));
    }

    /**
     * Deletes a RemittancePurpose
     *
     * @param remittancePurposeId primary key of the remittancePurpose being deleted
     * @return
     */
    public Boolean delete(Long id) {

        ExhRemittancePurpose remittancePurposeInstance = ExhRemittancePurpose.get(id)
        if (remittancePurposeInstance == null) {
            return new Boolean(false)
        }

        remittancePurposeInstance.delete()
        return new Boolean(true)
    }

	public void createDefaultData(long companyId) {
		ExhRemittancePurpose familyPurpose = new ExhRemittancePurpose(name: 'Family Support')
		familyPurpose.companyId = companyId
		familyPurpose.save()

		ExhRemittancePurpose salaryPurpose = new ExhRemittancePurpose(name: 'Job Salary')
		salaryPurpose.companyId = companyId
		salaryPurpose.save()

		ExhRemittancePurpose donationPurpose = new ExhRemittancePurpose(name: 'Donation')
		donationPurpose.companyId = companyId
		donationPurpose.save()
	}

}
