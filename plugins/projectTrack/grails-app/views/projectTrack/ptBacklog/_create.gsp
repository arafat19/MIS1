<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Backlog
        </div>
    </div>

    <form id='backlogForm' name='backlogForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="moduleId">Module:</label>

                        <div class="col-md-6">
                            <ptk:dropDownModule
                                    tabindex="1"
                                    dataModelName="dropDownModule"
                                    name="moduleId"
                                    required="true">
                            </ptk:dropDownModule>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="moduleId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="priorityId">Priority:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    tabindex="2"
                                    dataModelName="dropDownPriority"
                                    name="priorityId"
                                    typeId="${SystemEntityTypeCacheUtility.PT_BACKLOG_PRIORITY}"
                                    required="true">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="priorityId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="useCaseId">Use Case ID:</label>

                        <div class="col-md-9">
                            <input class="k-textbox" id="useCaseId" name="useCaseId" maxlength="255" tabindex="3"
                                   placeholder=""/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="url">URL:</label>

                        <div class="col-md-9">
                            <input class="k-textbox" id="url" name="url" maxlength="255" tabindex="4"
                                   placeholder=""/>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="actor">As a:</label>

                        <div class="col-md-7">
                            <input class="k-textbox" id="actor" name="actor" maxlength="255" tabindex="5"
                                   placeholder="" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="actor"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="purpose">I want to:</label>

                        <div class="col-md-7">
                            <textarea type="text" class="k-textbox" id="purpose" name="purpose" rows="2" maxlength="255"
                                      tabindex="6" required validationMessage="Required" placeholder=""></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="purpose"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="benefit">So that:</label>

                        <div class="col-md-7">
                            <textarea type="text" class="k-textbox" id="benefit" name="benefit" rows="2" maxlength="255"
                                      tabindex="7" required validationMessage="Required" placeholder=""></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="benefit"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="8"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="9"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
