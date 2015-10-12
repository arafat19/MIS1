<%@ page import="com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Update Bug
        </div>
    </div>

    <form id='formMyBug' name='formMyBug' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional">Title:</label>

                <div class="col-md-11">
                    <span id="title"></span>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional">Severity:</label>

                        <div class="col-md-10">
                            <span id="severity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional">Type:</label>

                        <div class="col-md-10">
                            <span id="type"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="status">Status:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownStatus"
                                    name="status"
                                    typeId="${SystemEntityTypeCacheUtility.PT_BUG_STATUS}"
                                    elements="${[PtBugStatusCacheUtility.SUBMITTED_RESERVED_ID,
                                            PtBugStatusCacheUtility.FIXED_RESERVED_ID]}"
                                    required="true"
                                    tabindex="1">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="status"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="note">Note:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="note" name="note" rows="2" maxlength="255"
                                      tabindex="2" placeholder="255 Char Max"></textarea>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional">Steps To Reproduce:</label>

                        <div class="col-md-8">
                            <span id="stepToReproduce"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetMyBugForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>