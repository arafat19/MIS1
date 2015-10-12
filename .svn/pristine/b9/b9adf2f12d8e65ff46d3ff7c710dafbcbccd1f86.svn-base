<%@ page import="com.athena.mis.application.entity.Project" %>


<div id="project" class="k-content">

<g:render template='/application/project/create'/>


%{--<table id="flex1" style="display:none"></table>--}%


%{--toolbar of grid--}%
<script type="text/x-kendo-template" id="gridToolbar">
    <!--<div class="toolbar">-->
        <!--<button data-bind="events:{ click: create}" id="edit" name="edit" type="submit" data-role="button"-->
                <!--class="k-button k-button-icontext"-->
                <!--role="button"-->
                <!--aria-disabled="false"><span class="k-icon k-i-edit"></span>Edit P-->
        <!--</button>-->

        <!--<button data-bind="events:{ click: remove}" id="deleteProject" name="deleteProject" type="submit" data-role="button"-->
                <!--class="k-button k-button-icontext"-->
                <!--role="button"-->
                <!--aria-disabled="false"><span class="k-icon k-i-minimize"></span>Delete p-->
        <!--</button>-->
    <!--</div>-->


        <!--<div class="btn-group btn-group-sm">-->
              <!--<button type="button" class="btn btn-default">Show Details</button>-->
              <!--<button type="button" class="btn btn-default">Edit</button>-->
              <!--<button type="button" class="btn btn-default">Delete</button>-->
        <!--</div>-->


<ul id="menu">
    <li style="height: 25px;" data-bind="events:{ click: create}">
        Edit
    </li>
    <li style="height: 25px;">
        Delete
    </li>
    <li style="height: 25px;">
        Reports
        <ul>
            <li>Download in PDF</li>
            <li>Download in CSV</li>
        </ul>
    </li>
</ul>



</script>
%{--toolbar of grid--}%



<div id="grid"></div>
<script>
    var projectModel;
    var dataSource;
    var grid;

    $(document).ready(function () {

        dataSource = new kendo.data.DataSource({
            transport: {
                dataType: "json",
                read: {
                    url: "/project/list",
                    dataType: "json",
                    type:"post"
                },
                create: {
                    type: "POST",
                    url: "http://localhost:8080/project/create"
                },
                destroy: {
                    type: "POST",
                    url: "http://localhost:8080/project/delete"
                },
                update: {
                    type: "POST",
                    url: "http://localhost:8080/project/update"
                }

            },
            schema: {
                data: "projectList",
                model: {
                    fields: {
                        id: { type: "number" },
                        name: { type: "string" },
                        code: { type: "string" },
                        description: { type: "string" },
                        contentCount: { type: "number" },
                        createdOn: { type: "date" }
                    }
                }
            },
            parameterMap: function (data, type) {
                if (type == "create") {
                    alert('parameterMap of Create is called');
                    // send the created data items as the "models" service parameter encoded in JSON
                    return { models: kendo.stringify(data.models) };
                }
            },
            pageSize: 15,
            serverPaging: true,
            serverFiltering: true,
            serverSorting: true
        });


        grid =  $("#grid").kendoGrid({
            dataSource: dataSource,
            height: 300,
            filterable: true,
            sortable: true,
            resizable: true,
            pageable: true,
            selectable: "row",
            toolbar: kendo.template($("#gridToolbar").html()),
            columns: [
                {
                    field: "id",
                    title: "ID",
//                        format: "{0:c}",
                    filterable: false
                },
                "name",
                {
                    field: "code",
                    filterable: false
                },
                {
                    field: "address",
                    title: "Address",
                    filterable: false
                },
                {
                    field: "description",
                    title: "Description",
                    filterable: false
                },
                {
                    field: "createdOn",
                    title: "Created On",
                    format: "{0:dd MMM yyyy On h:mm:ss tt}",
                    width: "260px",
                    filterable: false
                }
            ]
        });




        // MVVM
        projectModel = kendo.observable({

            // project model with default value assignment.
            project: {
                id:0,
                name: "",
                code: "ABC",
                description: "desc",
                contentCount: 0,
                createdOn: ""
            },
            create: function (e) {

                var validator = $("#projectForm").kendoValidator().data("kendoValidator");

                // checking click side validation.
                if (validator.validate()) {


                    /*
                     // To pop last item of dataSource without removing data.
                     var item = dataSource.at(dataSource.data().length -1);
                     dataSource.pushDestroy(item);

                     // create a new data item
                     dataSource.insert(0, {
                     name: projectModel.get("project.name"),
                     code: projectModel.get("project.code"),
                     description: projectModel.get("project.description"),
                     address: projectModel.get("project.address")
                     });
                     */


                    /*
                     dataSource.add({
                     name: projectModel.get("project.name"),
                     code: projectModel.get("project.code"),
                     description: projectModel.get("project.description"),
                     address: projectModel.get("project.address")
                     });

                     */
                    dataSource.data().unshift({
                        id: projectModel.get("project.id"),
                        name: projectModel.get("project.name"),
                        code: projectModel.get("project.code"),
                        description: projectModel.get("project.description"),
                        contentCount: projectModel.get("project.contentCount"),
                        createdOn: projectModel.get("project.createdOn")
                    });



                    alert("datasource is created with new object");



                    // save the created data item
                    dataSource.sync();
                }
                else {
                    alert("InValid object");
                }
            },
            edit: function (e) {
                var model = grid.dataItem(grid.select());

                // ensure user to select a row.
                if (!model) {
                    alert("Please select a row");
                    return;
                }

                // populate data from grid model.
                this.set("project.name", model.name);
                this.set("project.code", model.code);
                this.set("project.address", model.address);
                this.set("project.description", model.description);

            },
            update: function (e) {
                alert("In update func");
            },
            remove: function (e) {
                alert("In Delete func");
            }

        });

        kendo.bind($("#project"), projectModel);

    });



    $(".deleteProject").bind("click", function () {
        alert("wanna delete?");
    });


    $(document).ready(function() {
        $("#menu").kendoMenu();
    });


</script>

</div>