<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Acceptance Criteria
        </div>
    </div>

    <form name='acceptanceCriteriaForm' id="acceptanceCriteriaForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="idea">Idea:</label>

                        <div class="col-md-11">
                            <pt:backlogIdea id="idea" backlogId="${backlogId}">
                            </pt:backlogIdea>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="type">Type:</label>

                        <div class="col-md-7">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownType"
                                    name="type"
                                    typeId="${SystemEntityTypeCacheUtility.PT_ACCEPTANCE_CRITERIA_TYPE}"
                                    required="true"
                                    tabindex="1"
                                    validationMessage="Required">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="type"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="criteria">Criteria:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="criteria" name="criteria" rows="2"
                                      required validationMessage="Required" tabindex="2" placeholder=""></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
