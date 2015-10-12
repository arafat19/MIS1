<%@ page import="com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.ContentTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            <span id="lblFormTitle"></span>
        </div>
    </div>

    <form id="entityContentForm" name='entityContentForm' enctype="multipart/form-data"
          class="form-horizontal form-widgets" method="post" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="entityTypeId" id="entityTypeId"/>
            <input type="hidden" name="entityId" id="entityId"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <div class="col-md-3 control-label">
                            <span id="lblEntityTypeName"></span>
                        </div>

                        <div class="col-md-9">
                            <span id="lblEntityName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="caption">Caption:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="caption" name="caption" rows="2"
                                      placeholder="255 Char Max" tabindex="1"
                                      required validationMessage="Required"></textarea>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="caption"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="expirationDate">Expire Date:</label>

                        <div class="col-md-6">
                            <app:dateControl name="expirationDate" placeholder="dd/MM/yyyy" value="" tabindex="5">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="contentTypeId">Content Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="2"
                                    name="contentTypeId"
                                    onchange="updateContentCategoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_TYPE}"
                                    dataModelName="dropDownContentTypeId">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="contentTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="contentCategoryId">Category:</label>

                        <div class="col-md-6">
                            <select name="contentCategoryId"
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="3"
                                    id="contentCategoryId">
                            </select>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="contentCategoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label id="labelAttachment" class="col-md-3 control-label label-required"
                               for="contentObj">Attachment:</label>

                        <div class="col-md-6">
                            <input type="file" tabindex="4" id="contentObj" name="contentObj"
                                   validationMessage="Required"/>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="contentObj"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="6"
                    aria-disabled="false" onclick='resetEntityContentForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

<div class="form-group">
    <table id="flex1" style="display:none"></table>
</div>
