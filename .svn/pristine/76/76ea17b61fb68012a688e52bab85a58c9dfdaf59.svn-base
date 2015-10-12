<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Update Mail
        </div>
    </div>

    <form id="appMailForm" name='appMailForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="transactionCode">Trans. Code:</label>
                <div class="col-md-5"><span id="transactionCode"></span></div>

                <label class="col-md-1 control-label label-optional" for="isActive">Active:</label>
                <div class="col-md-4"><g:checkBox tabindex="2" id="isActive" name="isActive"/></div>
            </div>

            <div class="form-group">
                <label id="labelRoleIds" class="col-md-1 control-label label-optional" for="roleIds">Role(s):</label>

                <div class="col-md-11">
                    <input type="text" class="k-textbox" id="roleIds" name="roleIds" tabindex="1"
                           placeholder="Comma separated Role ID(s)" autofocus/>
                </div>

            </div>
            <div class="form-group">
                <label id="labelRecipients" class="col-md-1 control-label label-optional" for="recipients">Recipient(s):</label>

                <div class="col-md-11">
                    <input type="text" class="k-textbox" id="recipients" name="recipients" tabindex="2"
                           placeholder="Comma separated valid email id" autofocus/>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="subject">Subject:</label>
                <div class="col-md-11">
                    <input type="text" class="k-textbox" id="subject" name="subject"  tabindex="3"
                           placeholder="Mail Subject" required validationMessage="Required"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="body">Body:</label>
                <div class="col-md-11">
                    <textarea type="text" class="k-textbox" id="body" name="body" rows="5"
                              placeholder="Body part of mail..." required=""  tabindex="4"
                              validationMessage="Value is Required"></textarea>
                </div>
            </div>

        </div>

        <div class="panel-footer">

            <button id="update" name="update" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"  tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"  tabindex="6"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>
