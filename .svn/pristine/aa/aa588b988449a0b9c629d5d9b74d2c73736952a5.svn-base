package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.model.RmsTaskListSummaryModel

class RmsTaskListSummaryModelService extends BaseService {

    public List<RmsTaskListSummaryModel> listUnResolvedTaskList(BaseService baseService) {
        return RmsTaskListSummaryModel.withCriteria {
            neProperty('totalCount', 'includedInListCount')
            neProperty('totalCount', 'decisionApprovedCount')
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        }
    }

    public int countUnResolvedTaskList() {
        List counts = RmsTaskListSummaryModel.withCriteria {
            neProperty('totalCount', 'includedInListCount')
            neProperty('totalCount', 'decisionApprovedCount')
            projections { rowCount() }
        }
        int count = counts[0]
        return count
    }

}
