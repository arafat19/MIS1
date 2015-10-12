databaseChangeLog = {
	changeSet(author: "informationtechnology (generated)", id: "1386148408017-20") {
		addColumn(tableName: "country") {
			column(name: "phone_number_pattern", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-21") {
		addColumn(tableName: "system_entity") {
			column(name: "is_reserved", type: "bool") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-22") {
		modifyDataType(columnName: "amount", newDataType: "float8", tableName: "acc_bank_statement")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-23") {
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "transaction_type", tableName: "acc_bank_statement")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-24") {
		modifyDataType(columnName: "company_id", newDataType: "int8", tableName: "acc_group")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-25") {
		modifyDataType(columnName: "amount", newDataType: "float8", tableName: "acc_iou_purpose")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-26") {
		modifyDataType(columnName: "company_id", newDataType: "int8", tableName: "acc_tier1")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-27") {
		modifyDataType(columnName: "company_id", newDataType: "int8", tableName: "acc_tier2")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-28") {
		modifyDataType(columnName: "company_id", newDataType: "int8", tableName: "acc_tier3")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-29") {
		modifyDataType(columnName: "order_id", newDataType: "int4", tableName: "acc_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-30") {
		modifyDataType(columnName: "content_count", newDataType: "int4", tableName: "budg_budget")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-31") {
		modifyDataType(columnName: "unit_id", newDataType: "int4", tableName: "budg_budget")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-32") {
		modifyDataType(columnName: "updated_on", newDataType: "timestamp", tableName: "fxd_maintenance_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-33") {
		addNotNullConstraint(columnDataType: "timestamp", columnName: "updated_on", tableName: "fxd_maintenance_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-34") {
		modifyDataType(columnName: "created_on", newDataType: "timestamp", tableName: "project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-35") {
		modifyDataType(columnName: "updated_on", newDataType: "timestamp", tableName: "project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-36") {
		addPrimaryKey(columnNames: "iou_id, iou_purpose_id", constraintName: "vw_acc_iou_dePK", tableName: "vw_acc_iou_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-37") {
		addPrimaryKey(columnNames: "source_type_id, dr_balance", constraintName: "vw_acc_sourcePK", tableName: "vw_acc_source_balance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-38") {
		addPrimaryKey(columnNames: "voucher_details_id, coa_id", constraintName: "vw_acc_vouchePK", tableName: "vw_acc_voucher_with_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-39") {
		addPrimaryKey(columnNames: "voucher_id, voucher_type_id", constraintName: "vw_acc_vouchePK", tableName: "vw_acc_voucher_with_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-40") {
		addPrimaryKey(columnNames: "budget_details_id, quantity", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_details_with_item")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-41") {
		addPrimaryKey(columnNames: "project_id, budget_count", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-42") {
		addPrimaryKey(columnNames: "budget_id, project_id", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_with_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-43") {
		addPrimaryKey(columnNames: "fixed_asset_details_id, po_id", constraintName: "vw_fixed_assePK", tableName: "vw_fixed_asset_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-44") {
		addPrimaryKey(columnNames: "fixed_asset_trace_id, fixed_asset_details_id", constraintName: "vw_fixed_assePK", tableName: "vw_fixed_asset_trace")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-45") {
		addPrimaryKey(columnNames: "maintenance_id, item_id", constraintName: "vw_fxd_maintePK", tableName: "vw_fxd_maintenance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-46") {
		addPrimaryKey(columnNames: "po_id, supplier_id", constraintName: "vw_proc_po_foPK", tableName: "vw_proc_po_for_store_in")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-47") {
		addPrimaryKey(columnNames: "project_id, po_count", constraintName: "vw_proc_po_stPK", tableName: "vw_proc_po_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-48") {
		addPrimaryKey(columnNames: "purchase_request_details_id, purchase_request_id", constraintName: "vw_proc_purchPK", tableName: "vw_proc_purchase_request_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-49") {
		addPrimaryKey(columnNames: "purchase_request_id, project_id", constraintName: "vw_proc_purchPK", tableName: "vw_proc_purchase_request_with_budget_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-50") {
		addPrimaryKey(columnNames: "po_id, item_id", constraintName: "vw_proc_supplPK", tableName: "vw_proc_supplier_wise_po")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-51") {
		addPrimaryKey(columnNames: "qsm_id, budget_item", constraintName: "vw_qs_measurePK", tableName: "vw_qs_measurement_with_budget_inventory")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-52") {
		addPrimaryKey(columnNames: "project_id, project_version", constraintName: "vw_qs_statusPK", tableName: "vw_qs_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-53") {
		dropIndex(indexName: "acc_prefix_company_id_idx", tableName: "acc_prefix")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-54") {
		dropIndex(indexName: "acc_sub_account_coa_id_idx", tableName: "acc_sub_account")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-55") {
		dropIndex(indexName: "entity_note_entity_id_idx", tableName: "entity_note")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-56") {
		dropIndex(indexName: "fixed_asset_details_current_inventory_id_idx", tableName: "fxd_fixed_asset_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-57") {
		dropIndex(indexName: "inv_inventory_transaction_details_approved_on_idx", tableName: "inv_inventory_transaction_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-58") {
		dropIndex(indexName: "purchase_order_purchase_request_id_idx", tableName: "proc_purchase_order")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-59") {
		dropIndex(indexName: "purchase_order_details_purchase_request_details_id_idx", tableName: "proc_purchase_order_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-60") {
		dropIndex(indexName: "proc_purchase_request_budget_id_idx", tableName: "proc_purchase_request")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-61") {
		dropIndex(indexName: "proc_purchase_request_company_id_idx", tableName: "proc_purchase_request")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-62") {
		dropIndex(indexName: "purchase_request_project_id_idx", tableName: "proc_purchase_request")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-63") {
		dropIndex(indexName: "proc_purchase_request_details_purchase_request_id_idx", tableName: "proc_purchase_request_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-64") {
		dropIndex(indexName: "purchase_request_details_budget_details_id_idx", tableName: "proc_purchase_request_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-65") {
		dropIndex(indexName: "proc_transport_cost_purchase_order_id_idx", tableName: "proc_transport_cost")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-66") {
		dropColumn(columnName: "task_ref_no", tableName: "inv_inventory_transaction_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-67") {
		dropColumn(columnName: "task_ref_no", tableName: "proc_purchase_request")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-68") {
		dropColumn(columnName: "is_dynamic", tableName: "system_entity")
	}


    //Don't understand
	/*changeSet(author: "informationtechnology (generated)", id: "1386148408017-69") {
		dropSequence(schemaName: "public", sequenceName: "entity_comment_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-70") {
		dropSequence(schemaName: "public", sequenceName: "inv_item_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-71") {
		dropSequence(schemaName: "public", sequenceName: "system_entity_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-72") {
		dropSequence(schemaName: "public", sequenceName: "transport_cost_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-73") {
		dropView(schemaName: "public", viewName: "vw_acc_cash_flow_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-74") {
		dropView(schemaName: "public", viewName: "vw_acc_iou_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-75") {
		dropView(schemaName: "public", viewName: "vw_acc_source_balance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-76") {
		dropView(schemaName: "public", viewName: "vw_acc_supplier_payment")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-77") {
		dropView(schemaName: "public", viewName: "vw_acc_voucher_with_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-78") {
		dropView(schemaName: "public", viewName: "vw_acc_voucher_with_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-79") {
		dropView(schemaName: "public", viewName: "vw_budg_budget_details_with_item")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-80") {
		dropView(schemaName: "public", viewName: "vw_budg_budget_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-81") {
		dropView(schemaName: "public", viewName: "vw_budg_budget_with_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-82") {
		dropView(schemaName: "public", viewName: "vw_fixed_asset_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-83") {
		dropView(schemaName: "public", viewName: "vw_fixed_asset_trace")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-84") {
		dropView(schemaName: "public", viewName: "vw_fxd_maintenance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-85") {
		dropView(schemaName: "public", viewName: "vw_inv_inventory_consumable_stock")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-86") {
		dropView(schemaName: "public", viewName: "vw_inv_inventory_stock")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-87") {
		dropView(schemaName: "public", viewName: "vw_inv_inventory_transaction_with_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-88") {
		dropView(schemaName: "public", viewName: "vw_inv_inventory_valuation")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-89") {
		dropView(schemaName: "public", viewName: "vw_proc_po_for_store_in")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-90") {
		dropView(schemaName: "public", viewName: "vw_proc_po_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-91") {
		dropView(schemaName: "public", viewName: "vw_proc_purchase_request_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-92") {
		dropView(schemaName: "public", viewName: "vw_proc_purchase_request_with_budget_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-93") {
		dropView(schemaName: "public", viewName: "vw_proc_supplier_wise_po")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-94") {
		dropView(schemaName: "public", viewName: "vw_qs_measurement_with_budget_inventory")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-95") {
		dropView(schemaName: "public", viewName: "vw_qs_status")
	}*/

	changeSet(author: "informationtechnology (generated)", id: "1386148408017-96") {
		dropTable(tableName: "acc_prefix")
	}
}
