package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.utility.RmsSessionUtil
import org.springframework.beans.factory.annotation.Autowired

/**
 * RmsTaskListService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete)
 */
class RmsTaskListService extends BaseService {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    /**
     * Get taskList object by Id
     * @param id -Id of taskList
     * @return -object of taskList
     */
    public RmsTaskList read(long id) {
        RmsTaskList taskList = RmsTaskList.findByIdAndCompanyId(id, rmsSessionUtil.appSessionUtil.getCompanyId(), [readOnly : true])
        return taskList
    }

    /**
     * Get last taskList by exchangeHouseId
     * @param exchangeHouseId
     * @return -list taskList
     */
    public List getPreviousTaskList(long exchangeHouseId) {
        String queryStr = """
            SELECT name
            FROM rms_task_list
            WHERE id = (SELECT MAX(id)
                        FROM rms_task_list
                        WHERE exchange_house_id = :exchangeHouseId)
        """
        Map queryParams = [
                exchangeHouseId: exchangeHouseId
        ]
        List lstTaskIds = executeSelectSql(queryStr, queryParams)
        return lstTaskIds
    }

    /**
     * Get taskList object by name
     * @param name
     * @return -taskList object
     */
    public RmsTaskList findByNameIlike(String name) {
        RmsTaskList taskList = RmsTaskList.findByNameIlike(name)
        return taskList
    }

    /**
     * Save taskList object into DB
     * @param taskList -taskList object
     * @return -saved taskList object
     */
    public RmsTaskList create(RmsTaskList taskList) {
        RmsTaskList savedTaskList = taskList.save()
        return savedTaskList
    }

    private static final String UPDATE_QUERY_FOR_LIST_RENAME="""
        UPDATE rms_task_list
        SET name=:name,
            version=version+1
        WHERE id=:id
    """
    public updateForRenameList(String listName, RmsTaskList rmsTaskList){
        Map queryParams=[
                name:listName,
                id:rmsTaskList.id,
                version:rmsTaskList.version+1
        ]
        int updateCount=(int)executeUpdateSql(UPDATE_QUERY_FOR_LIST_RENAME,queryParams)
        if(updateCount<=0){
            throw new RuntimeException('Error occured while renaming task list')
        }
        return updateCount
    }

}
