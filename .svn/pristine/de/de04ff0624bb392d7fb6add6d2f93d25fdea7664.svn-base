package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Country
import com.athena.mis.application.service.CountryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component('countryCacheUtility')
class CountryCacheUtility extends ExtendedCacheUtility {

    @Autowired
    CountryService countryService

    public final String SORT_ON_NAME = "name";

    @Transactional(readOnly = true)
    public void init() {
        List list = countryService.list();
        super.setList(list)
    }

    // return number of same country name in a specific company
    public int countByName(String name){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].name.equalsIgnoreCase(name))
                count++
        }
        return count;
    }
    public int countByNameAndIdNotEqual(String name, long id){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].name.equalsIgnoreCase(name) && lstCountry[i].id != id)
                count++
        }
        return count;
    }
    // return country object pulled by given code in a specific company
    public Country readByCode(String code) {
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].code.equalsIgnoreCase(code))
                return lstCountry[i]
        }
        return null
    }
    // return number of same country code in a specific company
    public int countByCode(String code) {
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].code.equalsIgnoreCase(code))
                count++
        }
        return count
    }
    public int countByCodeAndIdNotEqual(String code, long id) {
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].code.equalsIgnoreCase(code) && lstCountry[i].id != id)
                count++
        }
        return count
    }

    // return number of same country isdCode in a specific company
    public int countByIsdCode(String isdCode){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].isdCode.equalsIgnoreCase(isdCode))
                count++
        }
        return count;
    }
    public int countByIsdCodeAndIdNotEqual(String isdCode, long id){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].isdCode.equalsIgnoreCase(isdCode) && lstCountry[i].id != id)
                count++
        }
        return count;
    }

    // return number of same nationality in a specific company
    public int countByNationality(String nationality){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].nationality.equalsIgnoreCase(nationality))
                count++
        }
        return count;
    }
    public int countByNationalityAndIdNotEqual(String nationality, long id){
        int count = 0;
        List<Country> lstCountry = (List<Country>) list()
        for (int i = 0; i < lstCountry.size(); i++) {
            if (lstCountry[i].nationality.equalsIgnoreCase(nationality) && lstCountry[i].id != id)
                count++
        }
        return count;
    }

}
