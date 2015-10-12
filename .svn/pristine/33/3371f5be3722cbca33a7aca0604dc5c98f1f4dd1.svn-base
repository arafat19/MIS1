package com.athena.mis.accounting.actions.accipc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIpc
import com.athena.mis.accounting.service.AccIpcService
import com.athena.mis.accounting.utility.AccIpcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AccIpc object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccIpcActionService'
 */
class UpdateAccIpcActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "IPC has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Could not update IPC"
    private static final String NOT_FOUND_MESSAGE = "Selected IPC not found"
    private static final String IPC_ALREADY_EXISTS = "This IPC already exists in this project"
    private static final String ACC_IPC = "accIpc"

    AccIpcService accIpcService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * Check different criteria to update AccIpc object
     *      1) Check existence of selected(old) AccIpc object
     *      2) Check duplicate AccIpc object
     * @param params -N/A
     * @param obj -AccIpc object send from controller
     * @return -a map containing AccIpc object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long accIpcId = Long.parseLong(parameterMap.id.toString())
            AccIpc oldAccIpc = AccIpc.read(accIpcId)
            if (!oldAccIpc) {//Check existence of selected(old) AccIpc object
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            //build AccIpc object to update
            AccIpc newAccIpc = buildAccIpcObject(parameterMap, oldAccIpc)

            int totalDuplicateIpcNO = AccIpc.countByIpcNoAndProjectIdAndIdNotEqual(newAccIpc.ipcNo, newAccIpc.projectId, newAccIpc.id)
            if (totalDuplicateIpcNO > 0) {//Check existence of same AccIpc object
                result.put(Tools.MESSAGE, IPC_ALREADY_EXISTS)
                return result
            }
            result.put(ACC_IPC, newAccIpc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * update accIpc object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accIpc Object send from executePreCondition
     * @return -updated accIpc object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccIpc accIpc = (AccIpc) preResult.get(ACC_IPC)

            //update in DB
            accIpcService.update(accIpc)
            //update in cache utility and keep the data sorted
            accIpcCacheUtility.update(accIpc, accIpcCacheUtility.SORT_BY_ID, accIpcCacheUtility.SORT_ORDER_DESCENDING)

            result.put(ACC_IPC, accIpc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated AccIpc object to show on grid
     * @param obj -updated AccIpc object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccIpc accIpc = (AccIpc) receivedResult.get(ACC_IPC)
            Project project = (Project) projectCacheUtility.read(accIpc.projectId)
            GridEntity object = new GridEntity()
            object.id = accIpc.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accIpc.id,
                    accIpc.ipcNo,
                    project.name
            ]
            Map resultMap = [entity: object, version: accIpc.version]
            result.put(Tools.ENTITY, resultMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build accIpc object to update
     * @param params -GrailsParameterMap
     * @param oldAccIpc -AccIpc
     * @return -AccIpc
     */
    private AccIpc buildAccIpcObject(GrailsParameterMap parameterMap, AccIpc oldAccIpc) {
        AccIpc accIpc = new AccIpc(parameterMap)

        AppUser user = accSessionUtil.appSessionUtil.getAppUser()

        accIpc.ipcNo = parameterMap.ipcNo
        accIpc.projectId = Long.parseLong(parameterMap.projectId.toString())

        accIpc.id = oldAccIpc.id
        accIpc.version = oldAccIpc.version
        accIpc.createdBy = oldAccIpc.createdBy
        accIpc.createdOn = oldAccIpc.createdOn
        accIpc.companyId = oldAccIpc.companyId

        accIpc.updatedBy = user.id
        accIpc.updatedOn = new Date()

        return accIpc
    }
}
