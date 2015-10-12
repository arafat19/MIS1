<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Customer Details
        </div>

    </div>
    <div  id="customerTabs" class="panel-body" style="min-height: 260px">

        <ul class="nav nav-tabs">
            <li class="active"><a href="#fragmentBasicInfo" data-toggle="tab">Basic Info</a></li>
            <li><a href="#fragmentAdditionalInfo" data-toggle="tab">Additional Info</a></li>
            <li><a href="#fragmentIdentityInfo" data-toggle="tab">Identity Info</a></li>
        </ul>


        <div class="tab-content">
            <div class="tab-pane active" id="fragmentBasicInfo">
                <g:render template='/exchangehouse/exhCustomer/basicInfoForAgent'/>
            </div>

            <div class="tab-pane" id="fragmentAdditionalInfo">
                <g:render template='/exchangehouse/exhCustomer/additionalInfoForAgent'/>
            </div>

            <div class="tab-pane" id="fragmentIdentityInfo">
                <g:render template='/exchangehouse/exhCustomer/identityInfoForAgent'/>
            </div>
        </div>

    </div>

    <div class="panel-footer">
        <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                role="button" tabindex="13"
                aria-disabled="false" onclick='return onSubmitCustomer();'><span class="k-icon k-i-plus"></span>Create
        </button>

        <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                class="k-button k-button-icontext" role="button" tabindex="14"
                aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
        </button>
    </div>
</div>