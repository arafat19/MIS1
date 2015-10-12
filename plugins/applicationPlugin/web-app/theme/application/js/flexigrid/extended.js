/**
 * Created by .
 * User: Mohammad Ali Azam
 * Date: 4/17/11
 * Time: 6:22 PM
 *
 */

(function($) {

    /**
     * Added by Azam, July 08, 2010
     */
    $.fn.decreaseCount = function(pCount) {

        var stat = "";
        if (!pCount || pCount == undefined) {
            pCount = 1;
        }

        this.each(function() {

            var currentCount = false;

            var p = this.p;
            var toCount = 0;
            var rPage = false;//parseInt(p.rp);
            if (p.currentCount) {
                rPage = parseInt(p.currentCount);
            } else {
                rPage = parseInt(p.rp);
            }

            if (parseInt(p.pages) > 1) { // more than 1 page
//                if (parseInt(p.page) > 1) {  // Not in first page
//                    currentCount = parseInt(p.total) - ( (parseInt(p.page) - 1) * rPage);
//                } else {
//                    currentCount = rPage;
//                }
                if (parseInt(p.page) == parseInt(p.pages)) {  // on last page
                    currentCount = p.total - ((parseInt(p.page) - 1) * parseInt(p.rp));
                } else {
                    currentCount = rPage;
                }
            } else {
                currentCount = p.total;
            }

            currentCount = currentCount - pCount;
            p.currentCount = p.currentCount - pCount;
            p.total = p.total - pCount;
            toCount = ((parseInt(p.page) - 1) * parseInt(p.rp)) + parseInt(currentCount);

            if (p.total == 0) {
                stat = p.nomsg;
                return;
            }

            if (currentCount == 0) {
                if (parseInt(p.pages) > 1) {
                    if (parseInt(p.page) > 1) this.grid.changePage('prev');
                    else this.grid.populate();  //this.grid.changePage('next');
                }
                currentCount = false;
                p.currentCount = false;
            }

            var r1 = (p.page - 1) * p.rp + 1;
            stat = p.pagestat;

            stat = stat.replace(/{from}/, r1);
            stat = stat.replace(/{to}/, toCount);
            stat = stat.replace(/{total}/, p.total);
        });

        if (stat != false) {
            $('.pPageStat', this.pDiv).html(stat);
        }

    };

    var editableRows = null;

    /** as of yet, all controls will be text box*/
    $.fn.editGrid = function(options) {

        editableRows = new Array();
        var index = 0;
        var colModels = $(options.colModel);
        $('.trSelected', this).each(function (e) {

            var row = $(this);
            var rowId = (options.rowIdPrefix) ? row.attr('id').replace(options.rowIdPrefix, '') : row.attr('id');
            var cells = $('td', row);
            var rowModel = {cells:new Array(), id:rowId};

            var cellIndex = 0;
            cells.each(function (e) {
                var cell = $(this);
                var modelIndex = $.inArray(this, cells);
                var model = getColModel(colModels, modelIndex);
                if (model) {
                    var containerDiv = $('div:nth-child(1)', cell);
                    var contentDiv = $('div:nth-child(1)', containerDiv);

                    var ctrl = createTextControl(model, contentDiv, index);
                    var ctrlId = (options.divIdPrefix)
                        ? contentDiv.attr('id').replace(options.divIdPrefix, '')
                        : contentDiv.attr('id');
                    ctrl.attr('id', ctrlId);
                    contentDiv.children().remove();

                    var savedModel = {index:modelIndex, container:contentDiv, control: ctrl};
                    rowModel.cells[cellIndex++] = savedModel;

                    if (model.beforeRender) model.beforeRender(ctrl);
                    contentDiv.html('').append(ctrl);
                }

            }); // row end
            editableRows[index++] = rowModel;
        });
    };


    function createTextControl(model, contentDiv, index) {

        var ctrl = $("<input type='text'></input>");

        var value = '';
        var formattedValue = contentDiv.attr('formattedValue');
        if (formattedValue) {
            value = formattedValue;
            ctrl.attr('formatted', '1');
        } else {
            value = contentDiv.html();
        }
        ctrl.val(value);

        var oldVal = contentDiv.html();
        ctrl.val(value);
        contentDiv.attr('oldVal', oldVal);

        if (model.cssClass) ctrl.attr('class', model.cssClass);

        return ctrl;
    }

    $.fn.cancelEdit = function() {
        $(editableRows).each(function (e) {
            $(this.cells).each(function (i) {
                var val = this.control.val();
                var parent = this.control.parent();
                parent.html(parent.attr('oldVal'));
            });
        });
        editableRows = null;
    };

    function getColModel(columns, index) {

        var model = null;
        columns.each(function(e) {
            if (model != null) return;
            var cModel = this;
            if (cModel.index == index) {
                model = cModel;
            }
        });
        return model;
    }

    ;

    $.fn.completeEdit = function() {

        $(editableRows).each(function (e) {
            $(this.cells).each(function (i) {
                var val = this.control.attr('newVal');
                var parent = this.control.parent();
                try {
                    if (this.control.attr('formatted') === '1') {
                        parent.attr('formattedValue', this.control.val());
                    }
                } catch (e) {
                }
                this.control.remove();
                parent.html(val);
            });
        });
        editableRows = null;

    };

    $.fn.getRows = function() {
        return $(editableRows);
    }

    $.fn.explodeCellValues = function(delimeter, defaultValue) {

        var result = '';
        $(this).each(function (e) {
            val = $.trim(this.control.val());
            if (! defaultValue) {
                if (val.length > 0) {
                    result += val + delimeter;
                }
            } else {
                if (val.length > 0) {
                    result += val + delimeter;
                } else {
                    result += defaultValue + delimeter;
                }
            }
        });

        // if ends with unwanted delimter
        if (result.endsWith(delimeter)) {
            result = result.substring(0, result.length - delimeter.length);
        }
        return result;
    };

    $.fn.prepareEdit = function(options) {

        editableRows = new Array();
        var index = 0;

        $('.trSelected', this).each(function (e) {
            var row = $(this);
            var rowId = (options.rowIdPrefix) ? row.attr('id').replace(options.rowIdPrefix, '') : row.attr('id');
            var cells = $('td', row);
            var rowModel = {cells:new Array(), id:rowId, rowElement:row};

            var cellIndex = 0;
            cells.each(function (e) {

                var cell = $(this);
                var modelIndex = $.inArray(this, cells);

                var controls = $(':input', cell);
                if (controls.length == 1) {
                    var ctrl = controls.get(0);
                    if (ctrl.type == 'text') {
                        var savedModel = {index:modelIndex, control: $(ctrl)};
                        rowModel.cells[cellIndex++] = savedModel;
                    }
                }
            }); // row end
            editableRows[index++] = rowModel;
        });
    };

    $.fn.deleteCompleted = function() {
        $(editableRows).each(function (e) {
            this.rowElement.remove();
        });
        editableRows = null;
    };

    $.fn.countEqualsResultPerPage = function(previousTotal) {

        var equals = false;
        var recordCount = $('tbody > tr', $(this)).size();
        this.each(function() {
            if (equals) return;
            var p = this.p;
            var rPage = parseInt(p.rp);
            equals = (recordCount == rPage);
        });
        return equals;

    };

})(jQuery);

function regenerateSerial(rows, serialColumnIndex) {

    var firstRow = rows[0];
    var firstSerial = parseInt(firstRow.cell[serialColumnIndex]);

    $(rows).each(function (e) {
        var row = this;
        row.cell[serialColumnIndex] = ++firstSerial;
    });

}

function removeEntityFromGridRows(listModel, deletedRow) {

    var entityId = deletedRow.attr('id').replace('row', '');
    var rows = new Array();
    $(listModel.rows).each(function (e) {
        if (this.id == entityId) {

        } else {
            rows.push(this);
        }
    });
    listModel.rows = rows;

}

function removeGridElement(gridModel,selectedId,grid) {
    var i=0;
    for (i = 0; i < $(gridModel.rows).size(); i++) {
        if (gridModel.rows[i].id == selectedId) {
            gridModel.rows.splice(i, 1);
            break;
        }
    }
    gridModel.total = parseInt(gridModel.total) - 1;
    i=1;
    $($(gridModel.rows)).each(function (e) {
        var row = this;
        this.cell[0] = i++;
    });
    $(grid).flexAddData(gridModel);
}


function removeEntitiesFromGridRows(listModel, deletedRows) {

    var ids = [];
    var index = 0;
    $(deletedRows).each(function (e) {
        ids[index++] = $(this).attr('id').replace('row', '');
    });

    var rows = new Array();
    $(listModel.rows).each(function (e) {
        if ($.inArray(this.id, ids) > -1) {
            // don't push to new array as it was removed
        } else {
            rows.push(this);
        }
    });
    listModel.rows = rows;

}

function updateListModel(listModel, entity, serialColumnIndex) {
    $(listModel.rows).each(function (e) {
        if (this.id == entity.id) {
            copyEntityProperties(this, entity, serialColumnIndex)
            return false;
        }
    });
}

function copyEntityProperties(currentEntity, newEntity, serialColumnIndex) {

    //var cell = [];
    var i;
    var currentCells = currentEntity.cell;
    var newCells = newEntity.cell;

    for (i = 0; i < currentCells.length; i++) {
        if (i != serialColumnIndex) {
            currentCells[i] = newCells[i];
        }
    }
}

function getEmptyGridModel() {
    var emptyModel = new Object();
    emptyModel.page = 1;
    emptyModel.total = 0;
    emptyModel.rows = null;
    return emptyModel;
}




