package com.athena.mis.integration.accounting.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


    /**
     * Get FinancialYear object by FinancialYear id
     * For details go through Use-Case doc named 'ReadFinancialYearImplActionService'
     */
class ReadFinancialYearImplActionService extends BaseService implements ActionIntf {

        private final Logger log = Logger.getLogger(getClass());

        /**
         * Do nothing for pre operation
         */
        public Object executePreCondition(Object params, Object obj) {
            return null
        }

        /**
         * Get FinancialYear object by FinancialYear id
         * @param parameters -id of FinancialYear
         * @param obj -N/A
         * @return -object of FinancialYear
         */
        @Transactional(readOnly = true)
        public Object execute(Object parameters, Object obj) {
            try {
                long financialYearId = Long.parseLong(parameters.toString())
                AccFinancialYear financialYear = AccFinancialYear.read(financialYearId)
                return financialYear
            } catch (Exception ex) {
                log.error(ex.getMessage());
                return null
            }
        }

        /**
         * Do nothing for post operation
         */
        public Object executePostCondition(Object parameters, Object obj) {
            return null
        }

        /**
         * Do nothing for build success result for UI
         */
        public Object buildSuccessResultForUI(Object obj) {
            return null
        }

        /**
         * Do nothing for build failure result for UI
         */
        public Object buildFailureResultForUI(Object obj) {
            return null
        }
    }
