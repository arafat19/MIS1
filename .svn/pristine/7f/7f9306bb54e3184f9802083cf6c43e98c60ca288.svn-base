<%@ page import="com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Budget
        </div>
    </div>

    <form id="budgetForm" name="budgetForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="isProduction" id="isProduction"/>

            <app:systemEntityByReserved
                    name="entityTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY}"
                    reservedId="${ContentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-6">
                            <app:dropDownProject
                                    name="projectId"
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownProject"
                                    onchange="onChangeProjectEvent()"
                                    tabindex="1"
                                    addAllAttributes="true">
                            </app:dropDownProject>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="projectId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="budgetScopeId">Budget Scope:</label>

                        <div class="col-md-6">
                            <select id="budgetScopeId"
                                    name="budgetScopeId"
                                    required="required"
                                    validationMessage="Required"
                                    tabindex="2">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="budgetScopeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="budgetItem">Line Item:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="budgetItem" name="budgetItem" tabindex="3"
                                   placeholder="Unique By Project" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="budgetItem"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="budgetQuantity">Budget Quantity:</label>

                        <div class="col-md-6">
                            <input type="text" id="budgetQuantity" name="budgetQuantity" tabindex="4"
                                   placeholder="Quantity of Budget" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="budgetQuantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="unitId">Unit:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    name="unitId"
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownUnit"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_UNIT}"
                                    tabindex="5">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="unitId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="startDate">Start Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="startDate"
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="6">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="startDate"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label id="lblContractrate" class="col-md-3 control-label label-optional"
                               for="contractRate">Contract Rate:</label>

                        <div class="col-md-6">
                            <input type="text" id="contractRate" name="contractRate" tabindex="7"
                                   placeholder="Required for Billable Budget" validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="contractRate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label"
                               for="billable">Billable:</label>

                        <div class="col-md-6">
                            <g:checkBox name="billable" tabindex="8"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="details">Details:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="details" name="details" rows="4" tabindex="9"
                                      placeholder="1000 Char Max" required="" validationMessage="Required"
                                      maxlength="1000"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="endDate">End Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="endDate"
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="10">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="endDate"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="11"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="12"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>