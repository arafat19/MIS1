package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update AcceptanceCriteria object and grid data
 *  For details go through Use-Case doc named 'UpdatePtAcceptanceCriteriaActionService'
 */
class UpdatePtAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

	PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    PtBacklogService ptBacklogService
	@Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

	private static final String ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE = "Acceptance criteria could not be updated"
	private static final String ACCEPTANCE_CRITERIA_UPDATE_SUCCESS_MESSAGE = "Acceptance criteria has been updated successfully"
	private static final String OBJ_NOT_FOUND = "Selected acceptance criteria not found"
	private static final String OBJ_MODIFIED = "Acceptance criteria already modified. Try again"
    private static final String NOT_ALLOWED_TO_CREATE = "Not allowed to update A.C for Accepted Task"
	private static final String ACCEPTANCE_CRITERIA_OBJ = "acceptanceCriteria"

	/**
	 * Get parameters from UI and build AcceptanceCriteria object for update
	 * 1. check if id exists in parameterMap
	 * 2. check if old AcceptanceCriteria object exists
	 * @param parameters -serialized parameters from UI
	 * @param obj -N/A
	 * @return -a map containing all objects necessary for execute
	 * map contains isError(true/false) depending on method success
	 */
	@Transactional(readOnly = true)
	public Object executePreCondition(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			// check required parameters
			if (!parameterMap.id) {
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}
			long acceptanceCriteriaId = Long.parseLong(parameterMap.id.toString())
			int acceptanceCriteriaVersion = Integer.parseInt(parameterMap.version.toString())
			PtAcceptanceCriteria oldAcceptanceCriteria = (PtAcceptanceCriteria ) ptAcceptanceCriteriaService.read(acceptanceCriteriaId.longValue()) // get ptAcceptanceCriteria object
			// check whether selected AcceptanceCriteria object exists or not
			if (!oldAcceptanceCriteria) {
				result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
				return result
			}
			if(acceptanceCriteriaVersion != oldAcceptanceCriteria.version) {
				result.put(Tools.MESSAGE, OBJ_MODIFIED)
				return result
			}
            PtBacklog backlog = ptBacklogService.read(oldAcceptanceCriteria.backlogId)
            SystemEntity blStatusAccepted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, backlog.companyId)
            if (backlog.statusId == blStatusAccepted.id) {
                result.put(Tools.MESSAGE, NOT_ALLOWED_TO_CREATE)
                return result
            }
			PtAcceptanceCriteria acceptanceCriteria = buildPtAcceptanceCriteriaObject(parameterMap, oldAcceptanceCriteria)  // build ptAcceptanceCriteria object for update

			result.put(ACCEPTANCE_CRITERIA_OBJ, acceptanceCriteria)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * Update AcceptanceCriteria object in DB
	 * This function is in transactional block and will roll back in case of any exception
	 * @param parameters -N/A
	 * @param obj -map returned from executePreCondition method
	 * @return -a map containing all objects necessary for buildSuccessResultForUI
	 * map contains isError(true/false) depending on method success
	 */
	@Transactional
	public Object execute(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
			LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
			PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) preResult.get(ACCEPTANCE_CRITERIA_OBJ)
			ptAcceptanceCriteriaService.update(acceptanceCriteria)  // update AcceptanceCriteria object in DB
			result.put(ACCEPTANCE_CRITERIA_OBJ, acceptanceCriteria)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 *do nothing for post condition
	 */
	public Object executePostCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Show updated AcceptanceCriteria object in grid
	 * Show success message
	 * @param obj -map returned from execute method
	 * @return -a map containing all objects necessary for grid view
	 * map contains isError(true/false) depending on method success
	 */
	public Object buildSuccessResultForUI(Object obj) {
		Map result = new LinkedHashMap()
		try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
			LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
			PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) executeResult.get(ACCEPTANCE_CRITERIA_OBJ)
			GridEntity object = new GridEntity()    // build grid entity object
            AppUser appUser = (AppUser) appUserCacheUtility.read(acceptanceCriteria.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            SystemEntity accCriteriaType = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.read(acceptanceCriteria.type)
            SystemEntity acceptanceCriteriaStatus = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.read(acceptanceCriteria.statusId)
			object.id = acceptanceCriteria.id
			object.cell = [
					Tools.LABEL_NEW,
                    accCriteriaType.key,
					acceptanceCriteria.criteria,
                    acceptanceCriteriaStatus.key,
					DateUtility.getDateTimeFormatAsString(acceptanceCriteria.createdOn),
                    accCreatedBy
			]
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_SUCCESS_MESSAGE)
			result.put(Tools.ENTITY, object)
			result.put(Tools.VERSION, acceptanceCriteria.version)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * Build failure result in case of any error
	 * @param obj -map returned from previous methods, can be null
	 * @return -a map containing isError = true & relevant error message
	 */
	public Object buildFailureResultForUI(Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			if (obj) {
				LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
				if (preResult.get(Tools.MESSAGE)) {
					result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
					return result
				}
			}
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_UPDATE_FAILURE_MESSAGE)
			return result
		}
	}

	/**
	 * Build AcceptanceCriteria object for update
	 * @param parameterMap -serialized parameters from UI
	 * @param oldAcceptanceCriteria -old AcceptanceCriteria object
	 * @return -updated AcceptanceCriteria object
	 */
	private PtAcceptanceCriteria buildPtAcceptanceCriteriaObject(GrailsParameterMap parameterMap, PtAcceptanceCriteria oldAcceptanceCriteria) {
		PtAcceptanceCriteria acceptanceCriteria = new PtAcceptanceCriteria(parameterMap)
		oldAcceptanceCriteria.criteria = acceptanceCriteria.criteria
		oldAcceptanceCriteria.type = acceptanceCriteria.type
        oldAcceptanceCriteria.completedOn = null
		return oldAcceptanceCriteria
	}
}
