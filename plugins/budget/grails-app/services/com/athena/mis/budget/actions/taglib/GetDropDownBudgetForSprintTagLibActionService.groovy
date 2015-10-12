package com.athena.mis.budget.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for Budget objects */

class GetDropDownBudgetForSprintTagLibActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    BudgetService budgetService
    @Autowired
    UnitCacheUtility unitCacheUtility

    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String SPRINT_ID = 'sprint_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map dropDownAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            String id = attrs.get(ID)
            String url = attrs.get(URL)
            String name = attrs.get(NAME)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            String strTransId = attrs.get(SPRINT_ID)
            if ((!name) || (!dataModelName) || (!strTransId) || (strTransId.length() == 0)) {
                return dropDownAttributes
            }
            long transId = Long.parseLong(strTransId)
            dropDownAttributes.put(SPRINT_ID, new Long(transId))           // set sprintId
            dropDownAttributes.put(ID, id)                                  // set id
            dropDownAttributes.put(NAME, name)                              // set name
            dropDownAttributes.put(DATA_MODEL_NAME, dataModelName)          // set dataModelName
            dropDownAttributes.put(URL, url)                                // set url

            // Set default values for optional parameters
            dropDownAttributes.put(CLASS, attrs.class)
            dropDownAttributes.put(TAB_INDEX, attrs.tabindex)
            dropDownAttributes.put(ON_CHANGE, attrs.onchange)
            dropDownAttributes.put(HINTS_TEXT, PLEASE_SELECT)
            dropDownAttributes.put(SHOW_HINTS, Boolean.TRUE)
            dropDownAttributes.put(DEFAULT_VALUE, null)
            dropDownAttributes.put(REQUIRED, Boolean.FALSE)
            dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

            if (attrs.hints_text) {
                dropDownAttributes.put(HINTS_TEXT, attrs.hints_text)
            }
            if (attrs.show_hints) {
                boolean showHints = Boolean.parseBoolean(attrs.show_hints)
                dropDownAttributes.put(SHOW_HINTS, new Boolean(showHints))
            }
            if (attrs.default_value) {
                String strDefaultValue = attrs.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                dropDownAttributes.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (attrs.required) {
                dropDownAttributes.put(REQUIRED, REQUIRED)
                dropDownAttributes.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
                if (attrs.validation_message) {
                    dropDownAttributes.put(VALIDATION_MESSAGE, attrs.validation_message)
                }
            }

            dropDownAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return dropDownAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            dropDownAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return dropDownAttributes
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /** Get the list of Inventory objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map dropDownAttributes = (Map) parameters
            Long sprintId = (Long) dropDownAttributes.get(SPRINT_ID)
            // get sprint object(parent object)
            BudgSprint sprintObj = budgSprintService.read(sprintId)
            // get list of budget with in sprint data range
            List<GroovyRowResult> lstSprintBudget = listOfSprintBudget(sprintObj)
            String html = buildDropDown(lstSprintBudget, dropDownAttributes, sprintId)
            return html
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Generate the html for select
     * @param lstSprintBudget - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstSprintBudget, Map dropDownAttributes, long sprintId) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = Tools.EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        if (defaultValue) {
            lstSprintBudget = modifySprintBudgetList(lstSprintBudget, defaultValue.longValue(), sprintId)
        }

        String strDefaultValue = defaultValue ? defaultValue : Tools.EMPTY_SPACE
        if (showHints.booleanValue()) {
            lstSprintBudget = Tools.listForKendoDropdown(lstSprintBudget, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String jsonData = lstSprintBudget as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                        \$('#${name}').kendoDropDownList({
                            dataTextField:'name',
                            dataValueField:'id',
                            dataSource:${jsonData},
                            value:'${strDefaultValue}'
                        });
                        ${dataModelName} = \$("#${name}").data("kendoDropDownList");
                });
            </script>
        """
        return html + script
    }

    private static final String SELECT_QUERY = """
            SELECT budget.id AS id, budget.budget_item AS name,budget.details AS details,entity.key AS unit,
                  (budget.budget_quantity-coalesce(SUM(sprint_budget.quantity),0)) AS quantity
            FROM budg_budget budget
            LEFT JOIN budg_sprint_budget sprint_budget ON sprint_budget.budget_id = budget.id
            LEFT JOIN system_entity entity ON entity.id = budget.unit_id
            WHERE budget.project_id = :projectId
            AND budget.id NOT IN (SELECT budget_id FROM budg_sprint_budget WHERE sprint_id = :sprintId)
            AND :endDate >= budget.start_date AND budget.end_date >= :startDate
            GROUP BY budget.id,budget.budget_item,budget.budget_quantity,budget.details,entity.key
            HAVING (budget.budget_quantity-coalesce(SUM(sprint_budget.quantity),0)) > 0
            ORDER BY budget.budget_item ASC

    """
    /**
     *
     * @param sprint - BudgSprint.id
     * @return - lst of budget
     */
    private List<GroovyRowResult> listOfSprintBudget(BudgSprint sprint) {
        Map queryParams = [
                sprintId: sprint.id,
                projectId: sprint.projectId,
                startDate: DateUtility.getSqlDate(sprint.startDate),
                endDate: DateUtility.getSqlDate(sprint.endDate)
        ]
        List<GroovyRowResult> lstBudget = executeSelectSql(SELECT_QUERY, queryParams)
        return lstBudget
    }
    /**
     * Set sprint's budget for kendo dropDown
     * @param lstSprintBudget - lst of sprint's budget
     * @param budgetId - budget id
     * @param sprintId - sprint id
     */
    private List<GroovyRowResult> modifySprintBudgetList(List<GroovyRowResult> lstSprintBudget, long budgetId, long sprintId) {
            List<GroovyRowResult> lstBudget= listOfBudget(budgetId, sprintId)

            lstSprintBudget << [id: lstBudget[0].id, name: lstBudget[0].budget_item, unit: lstBudget[0].unit, quantity: lstBudget[0].quantity, details: lstBudget[0].details]
        return lstSprintBudget
    }

    private static final String SELECT_BUDGET_QUERY = """
         SELECT budget.id AS id , budget.budget_item AS budget_item , unit.key AS unit,budget.details AS details,
               ((budget.budget_quantity-coalesce((SELECT SUM(quantity) FROM budg_sprint_budget WHERE budget_id = :budgetId),0)) + sprint_budget.quantity) AS quantity
            FROM budg_sprint_budget sprint_budget
            LEFT JOIN budg_budget budget ON budget.id = sprint_budget.budget_id
            LEFT JOIN system_entity unit ON unit.id = budget.unit_id
            WHERE budget.id = :budgetId
            AND sprint_budget.sprint_id = :sprintId
            GROUP BY budget.id,budget.budget_item,budget.budget_quantity,budget.details,unit.key,sprint_budget.quantity
    """
    /**
     *
     * @param budgetId - budget id
     * @param sprintId - sprint id
     * @return - lst of budget
     */
    private List<GroovyRowResult> listOfBudget(long budgetId, long sprintId) {
        Map queryParams = [
                budgetId: budgetId,
                sprintId: sprintId
        ]
        List<GroovyRowResult> lstBudget = executeSelectSql(SELECT_BUDGET_QUERY, queryParams)
        return lstBudget
    }
}
