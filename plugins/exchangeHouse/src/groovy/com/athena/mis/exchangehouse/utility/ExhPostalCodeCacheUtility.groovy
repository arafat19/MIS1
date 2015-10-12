package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.service.ExhPostalCodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhPostalCodeCacheUtility')
class ExhPostalCodeCacheUtility extends ExtendedCacheUtility {

	@Autowired
	ExhPostalCodeService exhPostalCodeService

	public final String SORT_ON_CODE = "code";

	public void init() {
		List list = exhPostalCodeService.list();
		super.setList(list)
	}

	public ExhPostalCode readByCodeAndCompanyId(String code, long companyId) {
		ExhPostalCode exhPostalCode = null
		List<ExhPostalCode> lstExhPostalCode = (List<ExhPostalCode>) list()
		for (int i = 0; i < lstExhPostalCode.size(); i++) {
			ExhPostalCode postalCode = lstExhPostalCode[i]
			if(postalCode.code.equalsIgnoreCase(code) && postalCode.companyId == companyId) {
				exhPostalCode = postalCode
				break
			}
		}
		return exhPostalCode
	}
}
