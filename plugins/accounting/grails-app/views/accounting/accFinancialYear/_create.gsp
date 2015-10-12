<%@ page import="com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.utility.Tools" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Financial Year
        </div>
    </div>

    <form id="financialYearForm" name="financialYearForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>
        <app:systemEntityByReserved
                name="entityTypeId"
                typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY}"
                reservedId="${ContentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_FINANCIAL_YEAR}">
        </app:systemEntityByReserved>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="startDate">Start Date:</label>

                <div class="col-md-3">
                    <app:dateControl name="startDate"
                                     tabindex="1"
                                     value=""
                                     placeholder="dd/MM/yyyy"
                                     required="true"
                                     validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="startDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="endDate">End Date:</label>

                <div class="col-md-3">
                    <app:dateControl name="endDate"
                                     tabindex="2"
                                     value=""
                                     placeholder="dd/MM/yyyy"
                                     required="true"
                                     validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="endDate"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

