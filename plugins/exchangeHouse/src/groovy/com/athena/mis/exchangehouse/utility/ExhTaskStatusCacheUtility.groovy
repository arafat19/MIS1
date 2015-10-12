package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * SQL: SELECT * FROM system_entity WHERE type = 2003;
 * Entity id range: 2000201 to 2000301.
 * e.g. status-canceled = 957; status-pending = 958; status-new = 958;  sent-to-bank = 959;
 * - sent-to-other-bank = 960; resolved-by-other-bank = 961; status-un-approved = 962;
 * Reserved Obj: N/A.
 **/
@Component("exhTaskStatusCacheUtility")
class ExhTaskStatusCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService
    @Autowired
    ExhSessionUtil exhSessionUtil


    // Sys entity type
    private static final long ENTITY_TYPE = 2003;

    // Entity id get from reserved system entity
    public static final long STATUS_CANCELLED_TASK = 956;
    public static final long STATUS_PENDING_TASK = 957;
    public static final long STATUS_NEW_TASK = 958;
    public static final long STATUS_SENT_TO_BANK = 959;
    public static final long STATUS_SENT_TO_OTHER_BANK = 960;
    public static final long STATUS_RESOLVED_BY_OTHER_BANK = 961;
    public static final long STATUS_UN_APPROVED = 962;
    public static final long STATUS_REFUND_TASK = 997;

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        setList(list)
    }

    public List<SystemEntity> listForExchangeHouse() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<SystemEntity> lstExh = []
        lstExh << readByReservedAndCompany(STATUS_NEW_TASK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_RESOLVED_BY_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_CANCELLED_TASK,companyId)
        return lstExh
    }

    public List<SystemEntity> listForAgent() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<SystemEntity> lstExh = []
        lstExh << readByReservedAndCompany(STATUS_NEW_TASK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_RESOLVED_BY_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_CANCELLED_TASK,companyId)
        lstExh << readByReservedAndCompany(STATUS_PENDING_TASK,companyId)
        return lstExh
    }
    public List<SystemEntity> listForCustomer() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<SystemEntity> lstExh = []
        lstExh << readByReservedAndCompany(STATUS_NEW_TASK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_SENT_TO_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_RESOLVED_BY_OTHER_BANK,companyId)
        lstExh << readByReservedAndCompany(STATUS_CANCELLED_TASK,companyId)
        lstExh << readByReservedAndCompany(STATUS_UN_APPROVED,companyId)
        return lstExh
    }

	public List<Long> listTaskStatusForSarb() {
		long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
		List<Long> lstStatus = []
		lstStatus << readByReservedAndCompany(STATUS_SENT_TO_BANK,companyId).id
		lstStatus << readByReservedAndCompany(STATUS_SENT_TO_OTHER_BANK,companyId).id
		lstStatus << readByReservedAndCompany(STATUS_RESOLVED_BY_OTHER_BANK,companyId).id
		return lstStatus
	}

    public List<Long> listTaskStatusForExcludingSarb() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<Long> lstStatus = []
        lstStatus << readByReservedAndCompany(STATUS_PENDING_TASK,companyId).id
        lstStatus << readByReservedAndCompany(STATUS_UN_APPROVED,companyId).id
        lstStatus << readByReservedAndCompany(STATUS_NEW_TASK,companyId).id
        return lstStatus
    }
}
