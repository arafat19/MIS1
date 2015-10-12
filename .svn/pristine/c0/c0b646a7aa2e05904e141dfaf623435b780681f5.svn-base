<%@ page import="com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Bug
        </div>
    </div>

    <form id='ptBugForm' name='ptBugForm' enctype="multipart/form-data" class="form-horizontal form-widgets"
          method="post"
          role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <app:systemEntityByReserved
                    name="bugStatusId"
                    reservedId="${PtBugStatusCacheUtility.SUBMITTED_RESERVED_ID}"
                    typeId="${SystemEntityTypeCacheUtility.PT_BUG_STATUS}">
            </app:systemEntityByReserved>

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
                        <label class="col-md-3 control-label label-required" for="title">Title:</label>

                        <div class="col-md-6">
                            <input class="k-textbox" id="title" name="title" tabindex="1" maxlength="255"
                                   placeholder="Title" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="title"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="stepToReproduce">Steps To Reproduce:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="stepToReproduce" name="stepToReproduce" rows="2"
                                      tabindex="2" maxlength="255" placeholder="Step 1:
                                      Step 2:
                                      Step n:" required
                                      validationMessage="Required"></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="stepToReproduce"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="note">Note:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="note" name="note" rows="2" maxlength="255"
                                      tabindex="3" placeholder="255 Char Max"></textarea>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="status">Status:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownStatus"
                                    name="status"
                                    typeId="${SystemEntityTypeCacheUtility.PT_BUG_STATUS}"
                                    elements="${[PtBugStatusCacheUtility.SUBMITTED_RESERVED_ID,
                                            PtBugStatusCacheUtility.FIXED_RESERVED_ID,
                                            PtBugStatusCacheUtility.CLOSED_RESERVED_ID]}"
                                    required="true"
                                    validationMessage="Required">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="status"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="severity">Severity:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownSeverity"
                                    name="severity"
                                    typeId="${SystemEntityTypeCacheUtility.PT_BUG_SEVERITY}"
                                    required="true"
                                    tabindex="4">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="severity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="type">Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownType"
                                    name="type"
                                    typeId="${SystemEntityTypeCacheUtility.PT_BUG_TYPE}"
                                    required="true"
                                    tabindex="5">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="type"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="contentObj">Attachment:</label>

                        <div class="col-md-6">
                            <input type="file" tabindex="6" id="contentObj" name="contentObj"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetBugForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>