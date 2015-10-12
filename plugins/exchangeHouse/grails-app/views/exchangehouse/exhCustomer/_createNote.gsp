<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Customer Note
        </div>
    </div>

    <form id='createNoteForm' name="createNoteForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <g:hiddenField name="customerId"/>
            <div class="form-group">
                <label class="col-md-2 control-label label-optional" for="lblCustomerId">Customer Code:</label>

                <div class="col-md-4">
                    <span id="lblCustomerId"></span>
                </div>
                <label class="col-md-2 control-label label-optional" for="lblPhotoIdType">Photo ID Type:</label>

                <div class="col-md-4">
                    <span id="lblPhotoIdType"></span>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional" for="lblCustomerName">Name:</label>

                <div class="col-md-4">
                    <span id="lblCustomerName"></span>
                </div>
                <label class="col-md-2 control-label label-optional" for="lblEmail">Email:</label>

                <div class="col-md-4">
                    <span id="lblEmail"></span>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional" for="lblNationality">Nationality:</label>

                <div class="col-md-4">
                    <span id="lblNationality"></span>
                </div>
                <label class="col-md-2 control-label label-optional" for="lblAddress">Address:</label>

                <div class="col-md-4">
                    <span id="lblAddress"></span>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional" for="lblPhone">Phone:</label>

                <div class="col-md-4">
                    <span id="lblPhone"></span>
                </div>
                <label class="col-md-2 control-label label-optional" for="lblSourceOfFund">Source of Fund:</label>

                <div class="col-md-4">
                    <span id="lblSourceOfFund"></span>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="note">Note</label>

                <div class="col-md-10">
                    <textarea class="k-textbox" type="text" id="note" name="note" required validationMessage="Required"></textarea>
                    <span class="k-invalid-msg" data-for="note"></span>
                </div>
            </div>
        </div>


        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex=""
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex=""
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>
