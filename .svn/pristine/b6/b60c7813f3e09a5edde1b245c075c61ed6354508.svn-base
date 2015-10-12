<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Adjustment of Approved Production
        </div>

    </div>

    <div id="productionTabs"  class="panel-body" style="height: 340px">
        <g:hiddenField name="isReverse" value="false"/>

        <ul class="nav nav-tabs">
            <li class="active"><a href="#fragment-1" data-toggle="tab">Basic Info</a></li>
            <li><a href="#fragment-2" data-toggle="tab">Raw Materials</a></li>
            <li><a href="#fragment-3" data-toggle="tab">Finished Products</a></li>
        </ul>


        <div class="tab-content">
            <div class="tab-pane active" id="fragment-1">
                <g:render template='/inventory/invInventoryTransaction/basicInfoApprovedProduction'/>
            </div>

            <div class="tab-pane" id="fragment-2">
                <g:render template='/inventory/invInventoryTransaction/rawMaterialsProduction'/>
            </div>

            <div class="tab-pane" id="fragment-3">
                <g:render template='/inventory/invInventoryTransaction/finishedProductProduction'/>
            </div>
        </div>

    </div>

    <div class="panel-footer">
        <button id="adjustment" name="adjustment" type="submit" data-role="button" class="k-button k-button-icontext"
                role="button" aria-disabled="false" onclick='onSubmitProduction();'><span class="k-icon k-i-plus"></span>
            <span id="adjust">Apply Adjustment</span><span id="reverse">Apply Reverse Adjustment</span>
        </button>

        <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                class="k-button k-button-icontext" role="button"
                aria-disabled="false" onclick='clearFormProduction();'><span class="k-icon k-i-close"></span>Cancel
        </button>
    </div>

</div>

<div style="margin-top: 3px">
    <table id="flex1" style="display:none"></table>
</div>

<g:render template='/inventory/invInventoryTransaction/scriptApprovedProdWithConsumpGridInit'/>
<g:render template='/inventory/invInventoryTransaction/scriptApprovedProdWithConsump'/>



