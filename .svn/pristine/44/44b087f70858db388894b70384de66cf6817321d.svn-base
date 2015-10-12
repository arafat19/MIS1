<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Upload Bank Statement File
        </div>
    </div>

    <form name='bankStatementUploadFrm' id="bankStatementUploadFrm"
          method="post"
          enctype="multipart/form-data"
          class="form-horizontal form-widgets" role="form">

        <input name="MAX_FILE_SIZE" value="1048576" type="hidden" id="MAX_FILE_SIZE"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="bankAccId">Bank:</label>

                <div class="col-md-4">
                    <acc:dropDownAccBank
                            dataModelName="dropDownBank"
                            name="bankAccId"
                            required="true"
                            validationMessage="Required">
                    </acc:dropDownAccBank>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="bankAccId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="bankStatementFile">Bank Statement File:</label>

                <div class="col-md-4">
                    <input type='file' name='bankStatementFile' id='bankStatementFile' validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="bankStatementFile"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="uploadButton" name="uploadButton" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Upload Statement(s)
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

<div id='csvErrorDiv'>

</div>

<div style="width:100%; padding-top:1px;padding-left:5px">
    <div id="errorListShow" title="CSV Error(s)" style='display:none;vertical-align:top'>
        <h1 style='vertical-align:top;padding-bottom:7px;color:#ea5200'>List of <em
                style='color:#ea5200'>Error(s)</em> in selected Statements</h1>
        <ul id='csv-error-list' class="error_list"></ul>
    </div>
</div>

<div id="errorDivDupRefNo" class="buttons" style="display: none;"></div>

