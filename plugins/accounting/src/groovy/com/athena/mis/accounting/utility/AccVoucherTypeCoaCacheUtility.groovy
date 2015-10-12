package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.service.AccVoucherTypeCoaService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccVoucherTypeCoaCacheUtility'
 */
@Component("accVoucherTypeCoaCacheUtility")
class AccVoucherTypeCoaCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccVoucherTypeCoaService accVoucherTypeCoaService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    public static final String SORT_BY_VOUCHER_ID = "accVoucherTypeId"
    public static final String COA_ID = "id"
    public static final String COA_CODE = "code"
    public static final String COA_DESCRIPTION = "description"
    public static final String COA_CUSTOM_NAME = "customName"
    public static final String COA_SOURCE_ID = "accSourceId"

    /**
     * pull all list of AccVoucherTypeCoa objects and store list in cache
     */
    public void init() {
        List list = accVoucherTypeCoaService.list()
        super.setList(list)
    }

    // return list of COA(description+code and id) for a given voucher type (used in create voucher)
    public List listCoa(SystemEntity voucherType) {

        List<AccVoucherTypeCoa> lstMapping = (List<AccVoucherTypeCoa>) super.list()
        List<AccVoucherTypeCoa> lstFiltered = []
        List<AccChartOfAccount> lstCoa = []
        lstFiltered = lstMapping.findAll {it.accVoucherTypeId == voucherType.id}

        for (int i = 0; i < lstFiltered.size(); i++) {
            AccChartOfAccount coa = (AccChartOfAccount) accChartOfAccountCacheUtility.read(lstFiltered[i].coaId)
            Map customCoa = new LinkedHashMap()
            customCoa.put(COA_ID, coa.id)
            customCoa.put(COA_CODE, coa.code)
            customCoa.put(COA_DESCRIPTION, coa.description)
            customCoa.put(COA_SOURCE_ID, coa.accSourceId)
            customCoa.put(COA_CUSTOM_NAME, coa.description + Tools.PARENTHESIS_START + coa.code + Tools.PARENTHESIS_END)
            lstCoa << customCoa
        }
        return lstCoa
    }

    /**
     * get specific AccVoucherTypeCoa object by voucherTypeId & COA id
     * @param voucherTypeId -SystemEntity.id
     * @param coaId -AccChartOfAccount.id
     * @return -if specific AccVoucherTypeCoa object found then return that object; otherWise return null
     */
    public AccVoucherTypeCoa readByVoucherTypeIdAndCoaId(long voucherTypeId, long coaId) {
        List<AccVoucherTypeCoa> lstMapping = (List<AccVoucherTypeCoa>) super.list()
        for (int i = 0; i < lstMapping.size(); i++) {
            if (lstMapping[i].accVoucherTypeId == voucherTypeId && lstMapping[i].coaId == coaId) {
                return lstMapping[i]
            }
        }
        return null
    }

    /**
     * get specific AccVoucherTypeCoa object by id, voucherTypeId & COA id
     * @param voucherTypeCoaId -AccVoucherTypeCoa.id
     * @param voucherTypeId -SystemEntity.id
     * @param coaId -AccChartOfAccount.id
     * @return -if specific AccVoucherTypeCoa object found then return that object; otherWise return null
     */
    public AccVoucherTypeCoa readByVoucherTypeIdAndCoaIdForUpdate(long voucherTypeCoaId, long voucherTypeId, long coaId) {
        List<AccVoucherTypeCoa> lstMapping = (List<AccVoucherTypeCoa>) super.list()
        for (int i = 0; i < lstMapping.size(); i++) {
            if (lstMapping[i].accVoucherTypeId == voucherTypeId && lstMapping[i].coaId == coaId
                    && lstMapping[i].id != voucherTypeCoaId) {
                return lstMapping[i]
            }
        }
        return null
    }
}
