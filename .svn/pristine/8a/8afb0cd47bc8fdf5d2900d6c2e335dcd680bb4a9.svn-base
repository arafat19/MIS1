<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Production
        </div>
    </div>

    <form id="searchProdTransactionDetails" name="searchProdTransactionDetails" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate" tabindex="1"
                                     required="true" validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="fromDate"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate" tabindex="2"
                                     required="true" validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="toDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="finishedMaterialId">Prod Line Item:</label>

                <div class="col-md-3">
                    <inv:dropDownInventoryProductionLineItem
                            dataModelName="dropDownProdLineItem"
                            name="prodLineItemId"
                            tabindex="3" required="true"
                            validationMessage="Required"
                            onchange="updateFinishedMaterialList();">
                    </inv:dropDownInventoryProductionLineItem>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="prodLineItemId"></span>
                </div>


                <label class="col-md-2 control-label label-required"
                       for="finishedMaterialId">Finished Material:</label>

                <div class="col-md-3">
                    <select id="finishedMaterialId" name="finishedMaterialId"
                            tabindex="4" required validationMessage="Required">
                    </select>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="finishedMaterialId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </form>
</div>

<div id="application_second_panel" class="panel panel-primary">
    <form id='frmModifyOverheadCost' name='frmModifyOverheadCost' method="post">
        <div class="panel-body">
            <input type="hidden" id="hidFromDate" name="hidFromDate"/>
            <input type="hidden" id="hidToDate" name="hidToDate"/>
            <input type="hidden" id="hidProdLineItemId" name="hidProdLineItemId"/>
            <input type="hidden" id="hidFinishedMaterialId" name="hidFinishedMaterialId"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="overheadCost">New Overhead Cost:</label>

                <div class="col-md-3">
                    <input type="text" id="overheadCost" name="overheadCost" required=""
                           validationMessage="Required" placeholder="Overhead cost" autofocus/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="overheadCost"></span>
                </div>

                <div class="col-md-2">
                    <button id="createModifyOverheadCost" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false"><span class="k-icon k-i-custom"></span>Change
                    </button>
                </div>

            </div>
        </div>
    </form>
</div>





