package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 3/19/14.
 */
@Component("rmsTaskStatusCacheUtility")
class RmsTaskStatusCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService
    @Autowired
    RmsSessionUtil rmsSessionUtil

    // System entity type
    public static final long ENTITY_TYPE = 11716
    private static final String SORT_ON_NAME = "id"

    // Reserved System Entity id
    public static final long PENDING_TASK = 1162
    public static final long NEW_TASK = 1163
    public static final long INCLUDED_IN_LIST = 1164
    public static final long DECISION_TAKEN = 1165
    public static final long DECISION_APPROVED = 1166
    public static final long DISBURSED = 1167
    public static final long CANCELED = 1168

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE, SORT_ON_NAME)
        super.setList(list)
    }

    /**
     * Return all status except pending task
     * @return List of valid status
     */
    private Map<Long, List> lstValidStatus = new HashMap<Long, List>()

    public List<Long> listAllValidTaskStatusIds() {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstStatus = lstValidStatus.get(companyId.toLong())
        if (lstStatus) return lstStatus
        SystemEntity newTask = (SystemEntity) readByReservedAndCompany(NEW_TASK, companyId)
        SystemEntity includedInList = (SystemEntity) readByReservedAndCompany(INCLUDED_IN_LIST, companyId)
        SystemEntity decisionTaken = (SystemEntity) readByReservedAndCompany(DECISION_TAKEN, companyId)
        SystemEntity decisionApproved = (SystemEntity) readByReservedAndCompany(DECISION_APPROVED, companyId)
        SystemEntity disbursed = (SystemEntity) readByReservedAndCompany(DISBURSED, companyId)
        SystemEntity canceled = (SystemEntity) readByReservedAndCompany(CANCELED, companyId)
        List<Long> lst = [newTask.id, includedInList.id, decisionTaken.id, decisionApproved.id, disbursed.id, canceled.id]
        lstValidStatus.put(companyId.toLong(), lst)
        return lst
    }

}
