package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Delete ptAcceptanceCriteria object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeletePtAcceptanceCriteriaActionService'
 */
class DeletePtAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

	PtAcceptanceCriteriaService ptAcceptanceCriteriaService

    private final Logger log = Logger.getLogger(getClass())

	private static final String ACCEPTANCE_CRITERIA_DELETE_SUCCESS_MSG = "Acceptance criteria has been successfully deleted"
	private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete acceptance criteria"

	/**
	 * Checking pre condition and association before deleting the ptAcceptanceCriteria object
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
			long acceptanceCriteriaId = Long.parseLong(parameterMap.id.toString())
			PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) ptAcceptanceCriteriaService.read(acceptanceCriteriaId)    // get acceptance criteria object
			// check whether selected acceptance criteria object exists or not
			if (!acceptanceCriteria) {
				result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
				return result
			}
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
			return result
		}
	}

	/**
	 * Delete ptAcceptanceCriteria object from DB
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
			long acceptanceCriteriaId = Long.parseLong(parameterMap.id.toString())
			ptAcceptanceCriteriaService.delete(acceptanceCriteriaId)    // delete ptAcceptanceCriteria object from DB
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
		result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_DELETE_SUCCESS_MSG)
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
