package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.service.AccGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccGroupCacheUtility'
 */
@Component('accGroupCacheUtility')
class AccGroupCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AccGroupService accGroupService

    public static final String ACC_GROUP_BANK = "BANK"
    public static final String ACC_GROUP_CASH = "CASH"
    public static final String SORT_BY_NAME = "name"

    /**
     * pull all list of AccGroup objects and store list in cache
     */
    public void init() {
        List list = accGroupService.list()
        super.setList(list)
    }

    /**
     * get AccGroup object; searching by exact accGroup name
     *      Used in : CreateAccDivisionActionService
     * @param accGroupName -given group name
     * @return -if exact AccGroup found then return AccGroup object otherwise return null
     */
    public AccGroup readByName(String accGroupName) {
        accGroupName = accGroupName.trim()
        String existingAccGroupName
        List<AccGroup> accGroupList = super.list()
        for (int i = 0; i < accGroupList.size(); i++) {
            existingAccGroupName = accGroupList[i].name
            if (existingAccGroupName.equalsIgnoreCase(accGroupName)) {
                return (AccGroup) accGroupList[i]
            }
        }
        return null
    }

    /**
     * get AccGroup object; searching by exact accGroup name
     *      Used in : UpdateAccGroupActionService
     * @param accGroupName -given group name
     * * @param accGroupId -accGroup.id
     * @return -if exact AccGroup found then return AccGroup object otherwise return null
     */
    public AccGroup readByNameForUpdate(String accGroupName, long accGroupId) {
        accGroupName = accGroupName.trim()
        String existingAccGroupName
        List<AccGroup> accGroupList = super.list()
        for (int i = 0; i < accGroupList.size(); i++) {
            existingAccGroupName = accGroupList[i].name
            if (existingAccGroupName.equalsIgnoreCase(accGroupName)
                    && accGroupList[i].id != accGroupId) {
                return (AccGroup) accGroupList[i]
            }
        }
        return null
    }

    /**
     * get list of accGroup(bank & cash) ids
     * @return -list of long ids
     */
    public List<Long> listOfAccGroupBankCashId() {
        List<Long> accGroupIdList = []
        List<AccGroup> accGroupList = super.list()

        AccGroup accGroupBank = readBySystemAccGroup(ACC_GROUP_BANK)
        AccGroup accGroupCash = readBySystemAccGroup(ACC_GROUP_CASH)

        for (int i = 0; i < accGroupList.size(); i++) {
            if ((accGroupList[i].id == accGroupBank.id) || (accGroupList[i].id == accGroupCash.id)) {
                accGroupIdList << accGroupList[i].id
            }
        }
        return accGroupIdList
    }

    /**
     * Method to get all active reserved group id list(eg : 3,4,7,8,9)
     * @return list-of-active-reserved-group-ids
     */
    public List<Long> listOfActiveReservedGroupIds() {
        List<Long> reservedGroupIdList = []
        List<AccGroup> accGroupList = super.list()
        for (int i = 0; i < accGroupList.size(); i++) {
            if ((accGroupList[i].isActive) && (accGroupList[i].isReserved)) {
                reservedGroupIdList << accGroupList[i].id
            }
        }
        return reservedGroupIdList
    }

    public AccGroup readBySystemAccGroup(String systemAccGroup) {
        List<AccGroup> systemAccGroupList = super.list()
        for (int i = 0; i < systemAccGroupList.size(); i++) {
            String existingSystemAccGroupName = systemAccGroupList[i].systemAccGroup
            if (existingSystemAccGroupName.equals(systemAccGroup)) {
                return (AccGroup) systemAccGroupList[i]
            }
        }
        return null
    }

    /**
     * Method to get all active group ids list(eg : 3,4,7,8,9)
     * @return -list of active group ids
     */
    public List<Long> listOfActiveGroupIds() {
        List<Long> activeGroupIdList = []
        List<AccGroup> accGroupList = super.list()
        for (int i = 0; i < accGroupList.size(); i++) {
            if (accGroupList[i].isActive) {
                activeGroupIdList << accGroupList[i].id
            }
        }
        return activeGroupIdList
    }
}
