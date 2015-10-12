<div id="subCategoryDetailsContainer">
    <doc:subCategoriesDetails sub_category_id="${params.subCategoryId ? params.subCategoryId : 0L}"></doc:subCategoriesDetails>
</div>

<div id="subCategoryManagersContainer">
    <doc:subCategoryManagers sub_category_id="${params.subCategoryId ? params.subCategoryId : 0L}"></doc:subCategoryManagers>
</div>

<div class="panel panel-primary uploadContentDiv" style="display: none">
    <div class="panel-heading">
        <div class="panel-title">Add Content</div>
    </div>

    <form id='uploadSubCategoryDocumentForm' name='uploadSubCategoryDocumentForm' enctype="multipart/form-data"
          class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <input type="hidden" name="categoryId" id="categoryId"/>
            <input type="hidden" name="subCategoryId" id="subCategoryId"/>

            <div class="form-group">
                <label id="labelUpload" class="col-md-3 control-label label-required"
                       for="uploadDocument">Upload File:</label>

                <div class="col-md-9">
                    <input type="file" class="form-control-static" id="uploadDocument" name="uploadDocument"
                           validationMessage="Required"/>
                    <span class="k-invalid-msg" data-for="uploadDocument"></span>
                </div>
            </div>

            <div class="form-group">
                    <label id="labelCreateArticle" class="col-md-3 control-label"
                           for="uploadDocument">or</label>

                <div class="col-md-9">
                    <g:remoteLink controller="docArticle"
                                  action="show"
                                  before="showLoadingSpinner(true)"
                                  onComplete="showLoadingSpinner(false)"
                                  params="${[categoryId: params.categoryId, subCategoryId: params.subCategoryId]}"
                                  update="contentHolder">Create Article
                    </g:remoteLink>
                </div>
            </div>

        </div>

        <div class="panel-footer">
            <button name="upload" id="upload" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Upload
            </button>
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetEntityContentForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>


<script type="text/javascript">
    var  subCategoryId, categoryId, uploading, validatorAttachment;

    $(document).ready(function () {
        $('.uploadContentDiv').hide();
        subCategoryId = ${(params?.subCategoryId)?params?.subCategoryId:[]};
        categoryId = ${(params.categoryId)?params.categoryId:[]};
        if (subCategoryId != 0) {
            $('.uploadContentDiv').show();
            $("#subCategoryId").val(subCategoryId);
            $("#categoryId").val(categoryId);
        }
        onLoadUploadSubCategoryDocument();
    });

    function onLoadUploadSubCategoryDocument() {
        initFormWithCustomRule();
        $('#uploadDocument').kendoUpload({multiple: false});
        bindFormEvents();
    }
    function initFormWithCustomRule() {
        validatorAttachment = $("#uploadSubCategoryDocumentForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if ((input[0].type == "file") && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
    }
    function bindFormEvents() {
        var actionUrl = "${createLink(controller: 'docSubCategory',action: 'uploadDocSubCategoryDocument')}";
        $("#uploadSubCategoryDocumentForm").attr('action', actionUrl);

        $('#uploadSubCategoryDocumentForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#upload'), true);
            },
            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    executePostCondition(response);
                    uploading = false;
                    setButtonDisabled($('#upload'), false);
                }
            },
            beforePost: function () {
                if (executePreCondition() == false) {
                    return false;
                }
                return true;
            }
        });
    }
    function executePreCondition() {
        // trim field vales before process.
        trimFormValues($("#uploadSubCategoryDocumentForm"));

        if (!validatorAttachment.validate()) {
            return false;
        }
        return true;
    }
    function executePostCondition(data) {
        var result = eval('(' + data + ')');
        if (result.isError) {
            showError(result.message);
        } else {
            resetEntityContentForm();
            showSuccess(result.message);
        }
    }
    function resetEntityContentForm() {
        clearErrors($("#uploadSubCategoryDocumentForm"));
        setButtonDisabled($('.save'), false);
        // reset kando upload
        $(".k-delete").parent().click();
        $('#upload').html("<span class='k-icon k-i-plus'></span>Upload");
        var actionUrl = "${createLink(controller: 'docSubCategory',action: 'uploadDocSubCategoryDocument')}";
        $("#uploadSubCategoryDocumentForm").attr('action', actionUrl);
    }

</script>
