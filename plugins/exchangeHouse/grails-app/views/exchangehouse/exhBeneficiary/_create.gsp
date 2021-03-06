<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Beneficiary
        </div>

    </div>
        <div  id="beneficiaryTabs" class="panel-body" style="min-height: 270px">

            <ul class="nav nav-tabs">
                <li class="active"><a href="#fragmentBasicInfo" data-toggle="tab">Basic Info</a></li>
                <li><a href="#fragmentDisbursementInfo" data-toggle="tab">Disbursement Info</a></li>
                <li><a href="#fragmentLinkedTransaction" data-toggle="tab">Linked Transaction</a></li>
            </ul>


            <div class="tab-content">
                <div class="tab-pane active" id="fragmentBasicInfo">
                    <g:render template='/exchangehouse/exhBeneficiary/basicInfo'/>
                </div>

                <div class="tab-pane" id="fragmentDisbursementInfo">
                    <g:render template='/exchangehouse/exhBeneficiary/disbursementInfo'/>
                </div>

                <div class="tab-pane" id="fragmentLinkedTransaction">
                    <g:render template='/exchangehouse/exhBeneficiary/linkedTransaction'/>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false" onclick='return onSubmitBeneficiary();'><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
</div>
