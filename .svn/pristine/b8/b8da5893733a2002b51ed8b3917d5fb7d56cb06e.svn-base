<div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
    <div class="panel-heading">
        <div class="panel-title">
            Update SMS
        </div>
    </div>

    <form name='smsForm' id="smsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="transactionCode">Trans. Code:</label>

                <div class="col-md-5"><span id="transactionCode"></span></div>

                <label class="col-md-1 control-label label-optional" for="isActive">Active:</label>

                <div class="col-md-5"><g:checkBox tabindex="1" id="isActive" name="isActive"/></div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="description">Description:</label>

                <div class="col-md-11"><span id="description"></span></div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="recipients">Recipients:</label>

                <div class="col-md-11">
                    <input type="text" class="k-textbox" id="recipients" name="recipients" tabindex="2"
                           placeholder="Comma separated phone numbers e.g. +88017XXXXXXXX,+88018XXXXXXXX" required validationMessage="Required"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="recipients">URL:</label>

                <div class="col-md-11">
                    <input type="text" class="k-textbox" id="url" name="url" tabindex="2"
                           placeholder="URL of sms" required validationMessage="Required"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="body">Body:</label>

                <div class="col-md-11">
                    <textarea type="text" class="k-textbox" id="body" name="body" rows="5"
                              placeholder="Body part of SMS..." required="" tabindex="3"
                              validationMessage="Value is Required"></textarea>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false" onclick='resetSmsForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
