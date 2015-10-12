package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Update ptBacklog object ownerId to 0 in DB and remove it from grid
 *  For details go through Use-Case doc named 'RemoveMyPtBacklogActionService'
 */
class RemoveMyPtBacklogActionService extends BaseService implements ActionIntf{

	PtBacklogService ptBacklogService

	private final Logger log = Logger.getLogger(getClass())

	private static final String BACKLOG_DELETE_SUCCESS_MSG = "Backlog has been successfully deleted"
	private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete backlog"


	/**Checking pre-condition before delete
	 * 1. check if PtBacklog.id exists in parameterMap
	 * 2. check if ptBacklog object exists against that Id
	 * @param parameters -parameters from UI
	 * @param obj -N/A
	 * @return -a map containing all objects necessary for execute
	 * map contains isError(true/false) depending on method success
	 */
	@Transactional(readOnly = true)
	public Object executePreCondition(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			// check required parameters
			if (!parameterMap.id) {
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}
			long backlogId = Long.parseLong(parameterMap.id.toString())
			PtBacklog backlog = ptBacklogService.read(backlogId)    // get backlog object
			// check whether selected backlog object exists or not
			if (!backlog) {
				result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
				return result
			}
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.HAS_ACCESS, Boolean.TRUE)
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
			return result
		}
	}

	/**
	 * Update ptBacklog object ownerId by 0
	 * This function is in transactional boundary and will roll back in case of any exception
	 * @param params -serialized parameters from UI
	 * @param obj -N/A
	 * @return -a map containing isError(true/false) depending on method success
	 */
	@Transactional
	public Object execute(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			long backlogId = Long.parseLong(parameterMap.id.toString())
			ptBacklogService.removeMyBacklog(backlogId)    // delete backlog object from DB
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
			return result
		}
	}

	/**
	 * do nothing for post operation
	 */
	public Object executePostCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Show success message
	 * @param obj -N/A
	 * @return -a map containing success message for UI
	 */
	public Object buildSuccessResultForUI(Object obj) {
		Map result = new LinkedHashMap()
		result.put(Tools.MESSAGE, BACKLOG_DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
		return result
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
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
			return result
		} catch (Exception ex) {
			log.error(ex.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
			return result
		}
	}

}
