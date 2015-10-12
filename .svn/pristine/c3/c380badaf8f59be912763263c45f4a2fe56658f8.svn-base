package com.athena.mis.accounting.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.service.AccDivisionService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'AccDivisionCacheUtility'
 */
@Component('accDivisionCacheUtility')
class AccDivisionCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccDivisionService accDivisionService
    @Autowired
    ProjectCacheUtility projectCacheUtility

    public static final String NAME = 'name'

    /**
     * pull all list of accDivision objects and store list in cache
     */
    public void init() {
        List list = accDivisionService.list()
        super.setList(list)
    }

    /**
     * get search result list of accDivision objects by project name
     *      Used in : SearchAccDivisionActionService
     * @param query -project name
     * @return -list of accDivision
     */
    public Map searchByProject(String query, BaseService baseService) {
        List<Project> lstProjects = (List<Project>) projectCacheUtility.search(NAME, query) //search by name
        List lstDivisions = []
        List<AccDivision> lstAllDivisions = (List<AccDivision>) super.list()
        for (int i = 0; i < lstProjects.size(); i++) {
            List lstTemp = lstAllDivisions.findAll { it.projectId == lstProjects[i].id }
            if (lstTemp.size() > 0) {
                lstDivisions.addAll(lstTemp)
            }
        }
        int end = lstDivisions.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstDivisions.size()
        List lstResult = lstDivisions.subList(baseService.start, end)
        return [list: lstResult, count: lstDivisions.size()]
    }

    /**
     * get AccDivision object; searching by exact accDivision name
     *      Used in : CreateAccDivisionActionService
     * @param accDivisionName -AccDivision.name
     * @return -if exact AccDivision found then return accDivision object otherwise return null
     */
    public AccDivision readByName(String accDivisionName) {
        accDivisionName = accDivisionName.trim()
        String existingAccDivisionName
        List<AccDivision> accDivisionList = super.list()
        for (int i = 0; i < accDivisionList.size(); i++) {
            existingAccDivisionName = accDivisionList[i].name
            if (existingAccDivisionName.equalsIgnoreCase(accDivisionName)) {
                return accDivisionList[i]
            }
        }
        return null
    }

    /**
     * get AccDivision object; searching by exact accDivision name & id
     *      Used in : UpdateAccDivisionActionService
     * @param accDivisionName -AccDivision.name
     * @param accDivisionId -AccDivision.Id
     * @return -if exact AccDivision found then return accDivision object otherwise return null
     */
    public AccDivision readByNameForUpdate(String accDivisionName, long accDivisionId) {
        accDivisionName = accDivisionName.trim()
        String existingAccDivisionName
        List<AccDivision> accDivisionList = super.list()
        for (int i = 0; i < accDivisionList.size(); i++) {
            existingAccDivisionName = accDivisionList[i].name
            if (existingAccDivisionName.equalsIgnoreCase(accDivisionName)
                    && accDivisionList[i].id != accDivisionId) {
                return accDivisionList[i]
            }
        }
        return null
    }

    /**
     * get active list of accDivision objects of a specific project
     * @param projectId -Project.id
     * @return -list of accDivision object
     */
    public List<AccDivision> listByProjectIdAndIsActive(long projectId) {
        List<AccDivision> newAccDivisionList = []
        List<AccDivision> accDivisionList = super.list()

        for (int i = 0; i < accDivisionList.size(); i++) {
            if ((accDivisionList[i].projectId == projectId) && (accDivisionList[i].isActive)) {
                newAccDivisionList << accDivisionList[i]
            }
        }
        return newAccDivisionList
    }

    /**
     * get list of active AccDivisionIds by list of ProjectIds
     * @param lstProjectIds -Project.id
     * @return -list of long value
     */
    public List<Long> listActiveDivisionIdsByProjectIds(List<Long> lstProjectIds) {
        List<Long> lstDivisionIds = []
        List<AccDivision> accDivisionList = super.list()
        for (int i = 0; i < accDivisionList.size(); i++) {
            AccDivision division = accDivisionList[i]
            if ((division.isActive) && (lstProjectIds.contains(division.projectId))) {
                lstDivisionIds << division.id
            }
        }
        lstDivisionIds << new Long(0)//if selected project(s) has not division OR to get all voucher(s) created by divisionId = 0
        return lstDivisionIds
    }
}
