databaseChangeLog = {

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-1") {
		createTable(tableName: "acc_bank_statement") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_bank_statPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "balance", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "bank_acc_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "bank_ref", type: "varchar(255)")

			column(name: "cheque_no", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_ref", type: "varchar(255)")

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "credit", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "debit", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "narrative", type: "varchar(255)")

			column(name: "transaction_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "transaction_ref", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "transaction_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-2") {
		createTable(tableName: "acc_chart_of_account") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_chart_of_PK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_custom_group_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "acc_group_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_source_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "business_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "source_category_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "system_acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "tier1", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "tier2", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "tier3", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "tier4", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "tier5", type: "int4") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-3") {
		createTable(tableName: "acc_custom_group") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_custom_grPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-4") {
		createTable(tableName: "acc_division") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_divisionPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-5") {
		createTable(tableName: "acc_financial_year") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_financialPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "is_current", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-6") {
		createTable(tableName: "acc_group") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_groupPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_reserved", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-7") {
		createTable(tableName: "acc_iou_purpose") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_iou_purpoPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_iou_slip_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "indent_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-8") {
		createTable(tableName: "acc_iou_slip") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_iou_slipPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "approved_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "employee_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "indent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purpose_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "sent_for_approval", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "total_purpose_amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-9") {
		createTable(tableName: "acc_ipc") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_ipcPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "ipc_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-10") {
		createTable(tableName: "acc_lc") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_lcPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "bank", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "lc_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-11") {
		createTable(tableName: "acc_lease_account") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_lease_accPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "end_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "installment_volume", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "institution", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "interest_rate", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "no_of_installment", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "start_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-12") {
		createTable(tableName: "acc_sub_account") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_sub_accouPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "coa_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-13") {
		createTable(tableName: "acc_tier1") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_tier1PK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-14") {
		createTable(tableName: "acc_tier2") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_tier2PK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_tier1id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-15") {
		createTable(tableName: "acc_tier3") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_tier3PK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_tier1id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_tier2id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-16") {
		createTable(tableName: "acc_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_typePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "order_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "prefix", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "system_acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-17") {
		createTable(tableName: "acc_voucher") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_voucherPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "cheque_date", type: "date")

			column(name: "cheque_no", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "cr_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "dr_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "financial_month", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "financial_year", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "instrument_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "instrument_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_voucher_posted", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "module_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "note", type: "varchar(255)")

			column(name: "posted_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "trace_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")

			column(name: "voucher_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "voucher_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-18") {
		createTable(tableName: "acc_voucher_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_voucher_dPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount_cr", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "amount_dr", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "coa_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "division_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "particulars", type: "varchar(255)")

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "row_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "source_category_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "source_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "source_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "voucher_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-19") {
		createTable(tableName: "acc_voucher_type_coa") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "acc_voucher_tPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acc_voucher_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "coa_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-20") {
		createTable(tableName: "app_group") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "app_groupPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-21") {
		createTable(tableName: "app_mail") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "app_mailPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "body", type: "varchar(2040)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "mime_type", type: "varchar(255)")

			column(name: "role_ids", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "subject", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "transaction_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-22") {
		createTable(tableName: "app_user") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "app_userPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "activation_link", type: "varchar(255)")

			column(name: "cell_number", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "employee_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "has_signature", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "ip_address", type: "varchar(255)")

			column(name: "is_activated_by_mail", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_company_user", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "login_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "next_expire_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-23") {
		createTable(tableName: "app_user_entity") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "app_user_entiPK")
			}

			column(name: "app_user_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "entity_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "entity_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-24") {
		createTable(tableName: "budg_budget") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "budg_budgetPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "billable", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "budget_item", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "budget_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "budget_type_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "content_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "contract_rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "details", type: "varchar(1000)") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "pr_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "status", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "unit_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-25") {
		createTable(tableName: "budg_budget_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "budg_budget_dPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "is_consumed_against_fixed_asset", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "pr_details_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-26") {
		createTable(tableName: "budg_budget_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "budg_budget_tPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-27") {
		createTable(tableName: "budg_project_budget_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "budg_project_PK")
			}

			column(name: "budget_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-28") {
		createTable(tableName: "company") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "companyPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address1", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "address2", type: "varchar(255)")

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "country_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "currency_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_default", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-29") {
		createTable(tableName: "content_category") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "content_categPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "content_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "extension", type: "varchar(255)")

			column(name: "height", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "is_reserved", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "max_size", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")

			column(name: "width", type: "int4") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-30") {
		createTable(tableName: "country") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "countryPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "currency_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "nationality", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "phone_number_pattern", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-31") {
		createTable(tableName: "currency") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "currencyPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "is_to_currency", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "symbol", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-32") {
		createTable(tableName: "customer") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "customerPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_of_birth", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)")

			column(name: "full_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "nick_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "phone_no", type: "varchar(255)")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-33") {
		createTable(tableName: "designation") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "designationPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "short_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-34") {
		createTable(tableName: "employee") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "employeePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "date_of_birth", type: "date")

			column(name: "date_of_join", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "designation_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)")

			column(name: "full_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "mobile_no", type: "varchar(255)")

			column(name: "nick_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-35") {
		createTable(tableName: "entity_content") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "entity_contenPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "caption", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "content", type: "bytea") {
				constraints(nullable: "false")
			}

			column(name: "content_category_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "content_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "entity_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "entity_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "extension", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "file_name", type: "varchar(255)")

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-36") {
		createTable(tableName: "entity_note") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "entity_notePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "entity_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "entity_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "note", type: "varchar(1000)") {
				constraints(nullable: "false")
			}

			column(name: "plugin_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-37") {
		createTable(tableName: "exh_agent") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_agentPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "balance", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "city", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "commission_logic", type: "varchar(1500)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "country_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "credit_limit", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "currency_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "phone", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-38") {
		createTable(tableName: "exh_agent_currency_posting") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_agent_curPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "agent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "currency_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "task_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-39") {
		createTable(tableName: "exh_bank") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_bankPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-40") {
		createTable(tableName: "exh_bank_branch") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_bank_branPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)")

			column(name: "bank_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)")

			column(name: "control_no", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "district_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_drawn_on", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sme_service_center", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "within_dhaka_zone", type: "bool") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-41") {
		createTable(tableName: "exh_beneficiary") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_beneficiaPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "account_no", type: "varchar(255)")

			column(name: "address", type: "varchar(255)")

			column(name: "bank", type: "varchar(255)")

			column(name: "bank_branch", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "district", type: "varchar(255)")

			column(name: "email", type: "varchar(255)")

			column(name: "first_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "is_sanction_exception", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "last_name", type: "varchar(255)")

			column(name: "middle_name", type: "varchar(255)")

			column(name: "phone", type: "varchar(255)")

			column(name: "photo_id_no", type: "varchar(255)")

			column(name: "photo_id_type", type: "varchar(255)")

			column(name: "relation", type: "varchar(255)")

			column(name: "thana", type: "varchar(255)")

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-42") {
		createTable(tableName: "exh_currency_conversion") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_currency_PK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "from_currency", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "to_currency", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-43") {
		createTable(tableName: "exh_customer") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_customerPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "address_verified_status", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "agent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_reg_no", type: "varchar(255)")

			column(name: "country_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_of_birth", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "date_of_incorporation", type: "varchar(255)")

			column(name: "declaration_amount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "declaration_end", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "declaration_start", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)")

			column(name: "is_sanction_exception", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "phone", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "photo_id_expiry_date", type: "date")

			column(name: "photo_id_no", type: "varchar(255)")

			column(name: "photo_id_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "post_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "profession", type: "varchar(255)")

			column(name: "source_of_fund", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "visa_expire_date", type: "date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-44") {
		createTable(tableName: "exh_customer_beneficiary_mapping") {
			column(name: "customer_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "beneficiary_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-45") {
		createTable(tableName: "exh_customer_trace") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_customer_PK")
			}

			column(name: "action", type: "char(1)") {
				constraints(nullable: "false")
			}

			column(name: "action_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "address_verified_status", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "agent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_reg_no", type: "varchar(255)")

			column(name: "country_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_of_birth", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "date_of_incorporation", type: "varchar(255)")

			column(name: "declaration_amount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "declaration_end", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "declaration_start", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)")

			column(name: "is_sanction_exception", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "phone", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "photo_id_expiry_date", type: "date")

			column(name: "photo_id_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "photo_id_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "post_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "profession", type: "varchar(255)")

			column(name: "source_of_fund", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "visa_expire_date", type: "date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-46") {
		createTable(tableName: "exh_district") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_districtPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-47") {
		createTable(tableName: "exh_photo_id_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_photo_id_PK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-48") {
		createTable(tableName: "exh_regular_fee") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_regular_fPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "logic", type: "varchar(1500)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-49") {
		createTable(tableName: "exh_remittance_purpose") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_remittancPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-50") {
		createTable(tableName: "exh_sanction") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_sanctionPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "address1", type: "varchar(255)")

			column(name: "address2", type: "varchar(255)")

			column(name: "address3", type: "varchar(255)")

			column(name: "address4", type: "varchar(255)")

			column(name: "address5", type: "varchar(255)")

			column(name: "address6", type: "varchar(255)")

			column(name: "alias_type", type: "varchar(255)")

			column(name: "country", type: "varchar(255)")

			column(name: "country_of_birth", type: "varchar(255)")

			column(name: "created_on", type: "date")

			column(name: "dob", type: "varchar(255)")

			column(name: "group_id", type: "varchar(255)")

			column(name: "group_type", type: "varchar(255)")

			column(name: "last_update", type: "date")

			column(name: "name", type: "varchar(500)") {
				constraints(nullable: "false")
			}

			column(name: "nationality", type: "varchar(255)")

			column(name: "ni_number", type: "varchar(255)")

			column(name: "other_information", type: "varchar(1000)")

			column(name: "passport_details", type: "varchar(1000)")

			column(name: "position", type: "varchar(255)")

			column(name: "post_or_zip", type: "varchar(255)")

			column(name: "regime", type: "varchar(255)")

			column(name: "title", type: "varchar(255)")

			column(name: "town_of_birth", type: "varchar(255)")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-51") {
		createTable(tableName: "exh_task") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_taskPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "agent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount_in_foreign_currency", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "amount_in_local_currency", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "approved_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_on", type: "timestamp")

			column(name: "beneficiary_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "beneficiary_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "commission", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "conversion_rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "current_status", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "customer_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "customer_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "discount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "from_currency_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "outlet_bank_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "outlet_branch_id", type: "int8")

			column(name: "outlet_district_id", type: "int8")

			column(name: "paid_by", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "paid_by_no", type: "varchar(255)")

			column(name: "payment_method", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "pin_no", type: "varchar(255)")

			column(name: "ref_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "regular_fee", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "remittance_purpose", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "task_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "to_currency_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-52") {
		createTable(tableName: "exh_task_trace") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "exh_task_tracPK")
			}

			column(name: "action", type: "char(1)") {
				constraints(nullable: "false")
			}

			column(name: "action_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "agent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount_in_foreign_currency", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "amount_in_local_currency", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "approved_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_on", type: "timestamp")

			column(name: "beneficiary_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "conversion_rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "current_status", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "customer_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "discount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "from_currency_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "paid_by", type: "int4") {
				constraints(nullable: "false")
			}


			column(name: "paid_by_no", type: "varchar(255)")

			column(name: "payment_method", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "pin_no", type: "varchar(255)")

			column(name: "ref_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "regular_fee", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "remittance_purpose", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "task_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "task_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "to_currency_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-53") {
		createTable(tableName: "fxd_category_maintenance_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fxd_category_PK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "maintenance_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-54") {
		createTable(tableName: "fxd_fixed_asset_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fxd_fixed_assPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "cost", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "current_inventory_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "expire_date", type: "date")

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "owner_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "po_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-55") {
		createTable(tableName: "fxd_fixed_asset_trace") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fxd_fixed_assPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_current", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_date", type: "date") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-56") {
		createTable(tableName: "fxd_maintenance") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fxd_maintenanPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "maintenance_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "maintenance_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-57") {
		createTable(tableName: "fxd_maintenance_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "fxd_maintenanPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-58") {
		createTable(tableName: "inv_inventory") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "inv_inventoryPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "is_factory", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-59") {
		createTable(tableName: "inv_inventory_transaction") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "inv_inventoryPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "inv_production_line_item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_approved", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "transaction_entity_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_entity_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-60") {
		createTable(tableName: "inv_inventory_transaction_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "inv_inventoryPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "acknowledged_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "actual_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "adjustment_parent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_on", type: "timestamp")

			column(name: "comments", type: "varchar(255)")

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "fifo_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_transaction_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "invoice_acknowledged_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_current", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_increase", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "lifo_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "mrf_no", type: "varchar(255)")

			column(name: "overhead_cost", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "shrinkage", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "stack_measurement", type: "varchar(255)")

			column(name: "supplied_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "supplier_chalan", type: "varchar(255)")

			column(name: "transaction_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "transaction_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")

			column(name: "vehicle_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "vehicle_number", type: "varchar(255)")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-61") {
		createTable(tableName: "inv_production_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "inv_productioPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "material_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "overhead_cost", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "production_item_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "production_line_item_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-62") {
		createTable(tableName: "inv_production_line_item") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "inv_productioPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-63") {
		createTable(tableName: "item") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "itemPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "category_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_finished_product", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_individual_entity", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "unit", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "valuation_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-64") {
		createTable(tableName: "proc_indent") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_indentPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "approved_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "from_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "to_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "total_price", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-65") {
		createTable(tableName: "proc_indent_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_indent_dPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "indent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_description", type: "varchar(1000)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "unit", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-66") {
		createTable(tableName: "proc_purchase_order") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_purchasePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_project_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "discount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "mode_of_payment", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "payment_method_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "total_price", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "total_vat_tax", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "tr_cost_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "tr_cost_total", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-67") {
		createTable(tableName: "proc_purchase_order_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_purchasePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_count", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_order_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "store_in_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")

			column(name: "vat_tax", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-68") {
		createTable(tableName: "proc_purchase_request") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_purchasePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_project_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "indent_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "target", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-69") {
		createTable(tableName: "proc_purchase_request_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_purchasePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "po_quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-70") {
		createTable(tableName: "proc_terms_and_condition") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_terms_anPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "details", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_order_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-71") {
		createTable(tableName: "proc_transport_cost") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "proc_transporPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "purchase_order_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-72") {
		createTable(tableName: "project") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "projectPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)")

			column(name: "code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-73") {
		createTable(tableName: "qs_measurement") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "qs_measuremenPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "comments", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "is_govt_qs", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "qs_measurement_date", type: "date") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "numeric(16,4)") {
				constraints(nullable: "false")
			}

			column(name: "site_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "timestamp")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-74") {
		createTable(tableName: "request_map") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "request_mapPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "config_attribute", type: "varchar(1000)") {
				constraints(nullable: "false")
			}

			column(name: "feature_name", type: "varchar(255)")

			column(name: "is_common", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_viewable", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "plugin_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "transaction_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "url", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-75") {
		createTable(tableName: "role") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "role_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-76") {
		createTable(tableName: "role_feature_mapping") {
			column(name: "role_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "transaction_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "plugin_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-77") {
		createTable(tableName: "role_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "role_typePK")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-78") {
		createTable(tableName: "supplier") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "supplierPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "account_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "address", type: "varchar(255)")

			column(name: "bank_account", type: "varchar(255)")

			column(name: "bank_name", type: "varchar(255)")

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "supplier_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-79") {
		createTable(tableName: "supplier_item") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "supplier_itemPK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-80") {
		createTable(tableName: "sys_configuration") {
			column(name: "id", type: "int4") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "sys_configuraPK")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(1000)") {
				constraints(nullable: "false")
			}

			column(name: "key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "plugin_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-81") {
		createTable(tableName: "system_entity") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "system_entityPK")
			}

			column(name: "is_active", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "is_reserved", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-82") {
		createTable(tableName: "system_entity_type") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "system_entityPK")
			}

			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "end_index", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "start_index", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-83") {
		createTable(tableName: "theme") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "themePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "updated_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "updated_on", type: "date")

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-84") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-85") {
		createTable(tableName: "vehicle") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vehiclePK")
			}

			column(name: "version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-86") {
		createTable(tableName: "vw_acc_cash_flow_details") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vw_acc_cash_fPK")
			}

			column(name: "balance_activities", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "coa_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "coa_description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "source_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-87") {
		createTable(tableName: "vw_acc_iou_details") {
			column(name: "iou_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "iou_purpose_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "purpose_amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "purpose_description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_purpose_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-88") {
		createTable(tableName: "vw_acc_source_balance") {
			column(name: "source_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "dr_balance", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "cr_balance", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "customer_full_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "employee_full_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_cr_balance", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_dr_balance", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_account_description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "supplier_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-89") {
		createTable(tableName: "vw_acc_supplier_payment") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "vw_acc_suppliPK")
			}

			column(name: "po_project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "remaining", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "source_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "total_paid", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "total_po", type: "float8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-90") {
		createTable(tableName: "vw_acc_voucher_with_details") {
			column(name: "voucher_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "coa_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "acc_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount_cr", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "amount_dr", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "cheque_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "coa_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "coa_description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "cr_balance", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "division_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "dr_balance", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "group_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "particulars", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "posted_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "row_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "source_category_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "source_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "source_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "str_amount_cr", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_amount_dr", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_voucher_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "trace_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "voucher_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "voucher_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "voucher_type_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-91") {
		createTable(tableName: "vw_acc_voucher_with_type") {
			column(name: "voucher_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "voucher_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "cheque_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "cr_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "dr_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "instrument_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "instrument_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_voucher_posted", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "str_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_voucher_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "trace_no", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "voucher_date", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "voucher_type_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-92") {
		createTable(tableName: "vw_budg_budget_details_with_item") {
			column(name: "budget_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_type_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "str_quantity", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_rate", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_total_cost", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "total_cost", type: "float8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-93") {
		createTable(tableName: "vw_budg_budget_status") {
			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "contract_value", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "project_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "revenue_margin", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "total_budget", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-94") {
		createTable(tableName: "vw_budg_budget_with_project") {
			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "billable", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "budget_details", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "budget_item", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "budget_quantity", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "budget_type_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "unit_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-95") {
		createTable(tableName: "vw_fixed_asset_details") {
			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "po_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_cost", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "inventory_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "inventory_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_purchase_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-96") {
		createTable(tableName: "vw_fixed_asset_trace") {
			column(name: "fixed_asset_trace_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "inventory_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "is_current", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "model_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_transaction_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-97") {
		createTable(tableName: "vw_fxd_maintenance") {
			column(name: "maintenance_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "company_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_by_user_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "maintenance_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "maintenance_type_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "model_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_maintenance_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-98") {
		createTable(tableName: "vw_proc_po_for_store_in") {
			column(name: "po_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "str_po_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-99") {
		createTable(tableName: "vw_proc_po_status") {
			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "po_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "total_budget", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "total_po", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-100") {
		createTable(tableName: "vw_proc_purchase_request_details") {
			column(name: "purchase_request_details_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "quantity_with_unit", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "str_rate", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_total_cost", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "total_cost", type: "float8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-101") {
		createTable(tableName: "vw_proc_purchase_request_with_budget_project") {
			column(name: "purchase_request_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "approved_by_project_director_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_item", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "created_by", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_count", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "purchase_request_target", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-102") {
		createTable(tableName: "vw_proc_supplier_wise_po") {
			column(name: "po_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "created_on", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "fixed_asset_quantity", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "item_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "item_type_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "item_unit", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "po_amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "quantity", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "rate", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "store_in_amount", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "store_in_quantity", type: "float8") {
				constraints(nullable: "false")
			}

			column(name: "str_fixed_asset_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_fixed_asset_quantity", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_po_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_quantity", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_rate", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_store_in_amount", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_store_in_quantity", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "supplier_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-103") {
		createTable(tableName: "vw_qs_measurement_with_budget_inventory") {
			column(name: "qsm_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "budget_item", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "budget_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "is_govt_qs", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "qsm_quantity", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "site_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_qsm_created_on", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_qsm_date", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-104") {
		createTable(tableName: "vw_qs_status") {
			column(name: "project_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "project_version", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "project_code", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "str_achieved_intern", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "work_certified_intern", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-105") {
		addPrimaryKey(columnNames: "customer_id, beneficiary_id", constraintName: "exh_customer_PK", tableName: "exh_customer_beneficiary_mapping")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-106") {
		addPrimaryKey(columnNames: "role_type_id, transaction_code", constraintName: "role_feature_PK", tableName: "role_feature_mapping")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-107") {
		addPrimaryKey(columnNames: "role_id, user_id", constraintName: "user_rolePK", tableName: "user_role")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-108") {
		addPrimaryKey(columnNames: "iou_id, iou_purpose_id", constraintName: "vw_acc_iou_dePK", tableName: "vw_acc_iou_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-109") {
		addPrimaryKey(columnNames: "source_type_id, dr_balance", constraintName: "vw_acc_sourcePK", tableName: "vw_acc_source_balance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-110") {
		addPrimaryKey(columnNames: "voucher_details_id, coa_id", constraintName: "vw_acc_vouchePK", tableName: "vw_acc_voucher_with_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-111") {
		addPrimaryKey(columnNames: "voucher_id, voucher_type_id", constraintName: "vw_acc_vouchePK", tableName: "vw_acc_voucher_with_type")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-112") {
		addPrimaryKey(columnNames: "budget_details_id, quantity", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_details_with_item")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-113") {
		addPrimaryKey(columnNames: "project_id, budget_count", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-114") {
		addPrimaryKey(columnNames: "budget_id, project_id", constraintName: "vw_budg_budgePK", tableName: "vw_budg_budget_with_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-115") {
		addPrimaryKey(columnNames: "fixed_asset_details_id, po_id", constraintName: "vw_fixed_assePK", tableName: "vw_fixed_asset_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-116") {
		addPrimaryKey(columnNames: "fixed_asset_trace_id, fixed_asset_details_id", constraintName: "vw_fixed_assePK", tableName: "vw_fixed_asset_trace")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-117") {
		addPrimaryKey(columnNames: "maintenance_id, item_id", constraintName: "vw_fxd_maintePK", tableName: "vw_fxd_maintenance")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-118") {
		addPrimaryKey(columnNames: "po_id, supplier_id", constraintName: "vw_proc_po_foPK", tableName: "vw_proc_po_for_store_in")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-119") {
		addPrimaryKey(columnNames: "project_id, po_count", constraintName: "vw_proc_po_stPK", tableName: "vw_proc_po_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-120") {
		addPrimaryKey(columnNames: "purchase_request_details_id, purchase_request_id", constraintName: "vw_proc_purchPK", tableName: "vw_proc_purchase_request_details")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-121") {
		addPrimaryKey(columnNames: "purchase_request_id, project_id", constraintName: "vw_proc_purchPK", tableName: "vw_proc_purchase_request_with_budget_project")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-122") {
		addPrimaryKey(columnNames: "po_id, item_id", constraintName: "vw_proc_supplPK", tableName: "vw_proc_supplier_wise_po")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-123") {
		addPrimaryKey(columnNames: "qsm_id, budget_item", constraintName: "vw_qs_measurePK", tableName: "vw_qs_measurement_with_budget_inventory")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-124") {
		addPrimaryKey(columnNames: "project_id, project_version", constraintName: "vw_qs_statusPK", tableName: "vw_qs_status")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-127") {
		createIndex(indexName: "acc_bank_statement_bank_acc_id_idx", tableName: "acc_bank_statement") {
			column(name: "bank_acc_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-128") {
		createIndex(indexName: "acc_bank_statement_company_id_idx", tableName: "acc_bank_statement") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-129") {
		createIndex(indexName: "acc_bank_statement_created_by_idx", tableName: "acc_bank_statement") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-130") {
		createIndex(indexName: "acc_chart_of_account_acc_custom_group_id_idx", tableName: "acc_chart_of_account") {
			column(name: "acc_custom_group_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-131") {
		createIndex(indexName: "acc_chart_of_account_acc_group_id_idx", tableName: "acc_chart_of_account") {
			column(name: "acc_group_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-132") {
		createIndex(indexName: "acc_chart_of_account_acc_source_id_idx", tableName: "acc_chart_of_account") {
			column(name: "acc_source_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-133") {
		createIndex(indexName: "acc_chart_of_account_acc_type_id_idx", tableName: "acc_chart_of_account") {
			column(name: "acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-134") {
		createIndex(indexName: "acc_chart_of_account_code_idx", tableName: "acc_chart_of_account") {
			column(name: "code")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-135") {
		createIndex(indexName: "acc_chart_of_account_company_id_idx", tableName: "acc_chart_of_account") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-136") {
		createIndex(indexName: "acc_chart_of_account_created_by_idx", tableName: "acc_chart_of_account") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-137") {
		createIndex(indexName: "acc_chart_of_account_is_active_idx", tableName: "acc_chart_of_account") {
			column(name: "is_active")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-138") {
		createIndex(indexName: "acc_chart_of_account_source_category_id_idx", tableName: "acc_chart_of_account") {
			column(name: "source_category_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-139") {
		createIndex(indexName: "acc_chart_of_account_system_acc_type_id_idx", tableName: "acc_chart_of_account") {
			column(name: "system_acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-140") {
		createIndex(indexName: "acc_chart_of_account_tier1_idx", tableName: "acc_chart_of_account") {
			column(name: "tier1")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-141") {
		createIndex(indexName: "acc_chart_of_account_tier2_idx", tableName: "acc_chart_of_account") {
			column(name: "tier2")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-142") {
		createIndex(indexName: "acc_chart_of_account_tier3_idx", tableName: "acc_chart_of_account") {
			column(name: "tier3")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-143") {
		createIndex(indexName: "acc_chart_of_account_tier4_idx", tableName: "acc_chart_of_account") {
			column(name: "tier4")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-144") {
		createIndex(indexName: "acc_chart_of_account_tier5_idx", tableName: "acc_chart_of_account") {
			column(name: "tier5")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-145") {
		createIndex(indexName: "acc_custom_group_company_id_idx", tableName: "acc_custom_group") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-146") {
		createIndex(indexName: "acc_division_company_id_idx", tableName: "acc_division") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-147") {
		createIndex(indexName: "acc_division_created_by_idx", tableName: "acc_division") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-148") {
		createIndex(indexName: "acc_division_project_id_idx", tableName: "acc_division") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-149") {
		createIndex(indexName: "acc_division_updated_by_idx", tableName: "acc_division") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-150") {
		createIndex(indexName: "acc_financial_year_company_id_idx", tableName: "acc_financial_year") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-151") {
		createIndex(indexName: "acc_financial_year_created_by_idx", tableName: "acc_financial_year") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-152") {
		createIndex(indexName: "acc_financial_year_updated_by_idx", tableName: "acc_financial_year") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-153") {
		createIndex(indexName: "acc_group_company_id_idx", tableName: "acc_group") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-154") {
		createIndex(indexName: "acc_iou_purpose_acc_indent_details_id_idx", tableName: "acc_iou_purpose") {
			column(name: "indent_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-155") {
		createIndex(indexName: "acc_iou_purpose_acc_iou_slip_id_idx", tableName: "acc_iou_purpose") {
			column(name: "acc_iou_slip_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-156") {
		createIndex(indexName: "acc_iou_purpose_created_by_idx", tableName: "acc_iou_purpose") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-157") {
		createIndex(indexName: "acc_iou_purpose_updated_by_idx", tableName: "acc_iou_purpose") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-158") {
		createIndex(indexName: "acc_iou_slip_approved_by_idx", tableName: "acc_iou_slip") {
			column(name: "approved_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-159") {
		createIndex(indexName: "acc_iou_slip_company_by_idx", tableName: "acc_iou_slip") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-160") {
		createIndex(indexName: "acc_iou_slip_created_by_idx", tableName: "acc_iou_slip") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-161") {
		createIndex(indexName: "acc_iou_slip_employee_id_idx", tableName: "acc_iou_slip") {
			column(name: "employee_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-162") {
		createIndex(indexName: "acc_iou_slip_indent_by_idx", tableName: "acc_iou_slip") {
			column(name: "indent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-163") {
		createIndex(indexName: "acc_iou_slip_project_id_idx", tableName: "acc_iou_slip") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-164") {
		createIndex(indexName: "acc_iou_slip_updated_by_idx", tableName: "acc_iou_slip") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-165") {
		createIndex(indexName: "acc_ipc_company_by_idx", tableName: "acc_ipc") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-166") {
		createIndex(indexName: "acc_ipc_created_by_idx", tableName: "acc_ipc") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-167") {
		createIndex(indexName: "acc_ipc_project_id_idx", tableName: "acc_ipc") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-168") {
		createIndex(indexName: "acc_ipc_updated_by_idx", tableName: "acc_ipc") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-169") {
		createIndex(indexName: "acc_lc_company_id_idx", tableName: "acc_lc") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-170") {
		createIndex(indexName: "acc_lc_created_by_idx", tableName: "acc_lc") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-171") {
		createIndex(indexName: "acc_lc_item_idx", tableName: "acc_lc") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-172") {
		createIndex(indexName: "acc_lc_supplier_idx", tableName: "acc_lc") {
			column(name: "supplier_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-173") {
		createIndex(indexName: "acc_lc_updated_by_idx", tableName: "acc_lc") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-174") {
		createIndex(indexName: "acc_lease_account_company_by_idx", tableName: "acc_lease_account") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-175") {
		createIndex(indexName: "acc_lease_account_created_by_idx", tableName: "acc_lease_account") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-176") {
		createIndex(indexName: "acc_lease_account_item_idx", tableName: "acc_lease_account") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-177") {
		createIndex(indexName: "acc_lease_account_updated_by_idx", tableName: "acc_lease_account") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-178") {
		createIndex(indexName: "acc_sub_account_company_id_idx", tableName: "acc_sub_account") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-179") {
		createIndex(indexName: "acc_sub_account_created_by_idx", tableName: "acc_sub_account") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-180") {
		createIndex(indexName: "acc_sub_account_updated_by_idx", tableName: "acc_sub_account") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-181") {
		createIndex(indexName: "acc_tier1_acc_type_id_idx", tableName: "acc_tier1") {
			column(name: "acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-182") {
		createIndex(indexName: "acc_tier1_company_id_idx", tableName: "acc_tier1") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-183") {
		createIndex(indexName: "acc_tier2_acc_tier1id_idx", tableName: "acc_tier2") {
			column(name: "acc_tier1id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-184") {
		createIndex(indexName: "acc_tier2_acc_type_id_idx", tableName: "acc_tier2") {
			column(name: "acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-185") {
		createIndex(indexName: "acc_tier2_company_id_idx", tableName: "acc_tier2") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-186") {
		createIndex(indexName: "acc_tier3_acc_tier1id_idx", tableName: "acc_tier3") {
			column(name: "acc_tier1id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-187") {
		createIndex(indexName: "acc_tier3_acc_tier2id_id_idx", tableName: "acc_tier3") {
			column(name: "acc_tier2id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-188") {
		createIndex(indexName: "acc_tier3_acc_type_id_idx", tableName: "acc_tier3") {
			column(name: "acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-189") {
		createIndex(indexName: "acc_tier3_company_id_idx", tableName: "acc_tier3") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-190") {
		createIndex(indexName: "acc_type_company_id_idx", tableName: "acc_type") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-191") {
		createIndex(indexName: "acc_type_system_acc_type_id_idx", tableName: "acc_type") {
			column(name: "system_acc_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-192") {
		createIndex(indexName: "acc_voucher_company_id_idx", tableName: "acc_voucher") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-193") {
		createIndex(indexName: "acc_voucher_created_by_idx", tableName: "acc_voucher") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-194") {
		createIndex(indexName: "acc_voucher_instrument_id_idx", tableName: "acc_voucher") {
			column(name: "instrument_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-195") {
		createIndex(indexName: "acc_voucher_instrument_type_id_idx", tableName: "acc_voucher") {
			column(name: "instrument_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-196") {
		createIndex(indexName: "acc_voucher_posted_by_idx", tableName: "acc_voucher") {
			column(name: "posted_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-197") {
		createIndex(indexName: "acc_voucher_project_id_idx", tableName: "acc_voucher") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-198") {
		createIndex(indexName: "acc_voucher_trace_no_idx", tableName: "acc_voucher") {
			column(name: "trace_no")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-199") {
		createIndex(indexName: "acc_voucher_updated_by_idx", tableName: "acc_voucher") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-200") {
		createIndex(indexName: "acc_voucher_voucher_date_idx", tableName: "acc_voucher") {
			column(name: "voucher_date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-201") {
		createIndex(indexName: "acc_voucher_voucher_type_id_idx", tableName: "acc_voucher") {
			column(name: "voucher_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-202") {
		createIndex(indexName: "acc_voucher_details_coa_id_idx", tableName: "acc_voucher_details") {
			column(name: "coa_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-203") {
		createIndex(indexName: "acc_voucher_details_created_by_idx", tableName: "acc_voucher_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-204") {
		createIndex(indexName: "acc_voucher_details_division_id_idx", tableName: "acc_voucher_details") {
			column(name: "division_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-205") {
		createIndex(indexName: "acc_voucher_details_group_id_idx", tableName: "acc_voucher_details") {
			column(name: "group_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-206") {
		createIndex(indexName: "acc_voucher_details_project_id_idx", tableName: "acc_voucher_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-207") {
		createIndex(indexName: "acc_voucher_details_source_category_id_idx", tableName: "acc_voucher_details") {
			column(name: "source_category_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-208") {
		createIndex(indexName: "acc_voucher_details_source_id_idx", tableName: "acc_voucher_details") {
			column(name: "source_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-209") {
		createIndex(indexName: "acc_voucher_details_source_type_id_idx", tableName: "acc_voucher_details") {
			column(name: "source_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-210") {
		createIndex(indexName: "acc_voucher_details_voucher_id_idx", tableName: "acc_voucher_details") {
			column(name: "voucher_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-211") {
		createIndex(indexName: "acc_voucher_type_coa_acc_voucher_type_id_idx", tableName: "acc_voucher_type_coa") {
			column(name: "acc_voucher_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-212") {
		createIndex(indexName: "acc_voucher_type_coa_coa_id_idx", tableName: "acc_voucher_type_coa") {
			column(name: "coa_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-213") {
		createIndex(indexName: "acc_voucher_type_coa_created_by_idx", tableName: "acc_voucher_type_coa") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-214") {
		createIndex(indexName: "acc_voucher_type_coa_updated_by_idx", tableName: "acc_voucher_type_coa") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-215") {
		createIndex(indexName: "app_group_company_id_idx", tableName: "app_group") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-216") {
		createIndex(indexName: "app_group_created_by_idx", tableName: "app_group") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-217") {
		createIndex(indexName: "app_group_updated_by_idx", tableName: "app_group") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-218") {
		createIndex(indexName: "app_mail_company_id_idx", tableName: "app_mail") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-219") {
		createIndex(indexName: "app_user_company_id_idx", tableName: "app_user") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-220") {
		createIndex(indexName: "app_user_employee_id_idx", tableName: "app_user") {
			column(name: "employee_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-221") {
		createIndex(indexName: "app_user_login_id_idx", tableName: "app_user") {
			column(name: "login_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-222") {
		createIndex(indexName: "app_user_username_idx", tableName: "app_user") {
			column(name: "username")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-223") {
		createIndex(indexName: "app_user_entity_app_user_id_idx", tableName: "app_user_entity") {
			column(name: "app_user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-224") {
		createIndex(indexName: "app_user_entity_entity_id_idx", tableName: "app_user_entity") {
			column(name: "entity_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-225") {
		createIndex(indexName: "app_user_entity_entity_type_id_idx", tableName: "app_user_entity") {
			column(name: "entity_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-226") {
		createIndex(indexName: "budget_budget_item_idx", tableName: "budg_budget") {
			column(name: "budget_item")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-227") {
		createIndex(indexName: "budget_budget_type_id_idx", tableName: "budg_budget") {
			column(name: "budget_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-228") {
		createIndex(indexName: "budget_company_id_idx", tableName: "budg_budget") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-229") {
		createIndex(indexName: "budget_created_by_idx", tableName: "budg_budget") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-230") {
		createIndex(indexName: "budget_project_id_idx", tableName: "budg_budget") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-231") {
		createIndex(indexName: "budget_unit_id_idx", tableName: "budg_budget") {
			column(name: "unit_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-232") {
		createIndex(indexName: "budget_updated_by_idx", tableName: "budg_budget") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-233") {
		createIndex(indexName: "budget_details_budget_id_idx", tableName: "budg_budget_details") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-234") {
		createIndex(indexName: "budget_details_company_id_idx", tableName: "budg_budget_details") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-235") {
		createIndex(indexName: "budget_details_created_by_idx", tableName: "budg_budget_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-236") {
		createIndex(indexName: "budget_details_item_id_idx", tableName: "budg_budget_details") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-237") {
		createIndex(indexName: "budget_details_project_id_idx", tableName: "budg_budget_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-238") {
		createIndex(indexName: "budget_details_updated_by_idx", tableName: "budg_budget_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-239") {
		createIndex(indexName: "budg_budget_type_company_id_idx", tableName: "budg_budget_type") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-240") {
		createIndex(indexName: "budg_project_budget_type_budget_type_id_idx", tableName: "budg_project_budget_type") {
			column(name: "budget_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-241") {
		createIndex(indexName: "budg_project_budget_type_project_id_idx", tableName: "budg_project_budget_type") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-242") {
		createIndex(indexName: "company_country_id_idx", tableName: "company") {
			column(name: "country_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-243") {
		createIndex(indexName: "company_created_by_idx", tableName: "company") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-244") {
		createIndex(indexName: "company_currency_id_idx", tableName: "company") {
			column(name: "currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-245") {
		createIndex(indexName: "company_updated_by_idx", tableName: "company") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-246") {
		createIndex(indexName: "content_category_company_id_idx", tableName: "content_category") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-247") {
		createIndex(indexName: "content_category_content_type_id_idx", tableName: "content_category") {
			column(name: "content_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-248") {
		createIndex(indexName: "content_category_created_by_idx", tableName: "content_category") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-249") {
		createIndex(indexName: "content_category_updated_by_idx", tableName: "content_category") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-250") {
		createIndex(indexName: "code_uniq_1386147896329", tableName: "country", unique: "true") {
			column(name: "code")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-251") {
		createIndex(indexName: "country_currency_id_idx", tableName: "country") {
			column(name: "currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-252") {
		createIndex(indexName: "name_uniq_1386147896330", tableName: "country", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-253") {
		createIndex(indexName: "symbol_uniq_1386147896331", tableName: "currency", unique: "true") {
			column(name: "symbol")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-254") {
		createIndex(indexName: "customer_company_id_idx", tableName: "customer") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-255") {
		createIndex(indexName: "designation_company_id_idx", tableName: "designation") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-256") {
		createIndex(indexName: "designation_created_by_idx", tableName: "designation") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-257") {
		createIndex(indexName: "designation_updated_by_idx", tableName: "designation") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-258") {
		createIndex(indexName: "employee_company_id_idx", tableName: "employee") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-259") {
		createIndex(indexName: "employee_created_by_idx", tableName: "employee") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-260") {
		createIndex(indexName: "employee_designation_id_idx", tableName: "employee") {
			column(name: "designation_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-261") {
		createIndex(indexName: "employee_updated_by_idx", tableName: "employee") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-262") {
		createIndex(indexName: "entity_content_budget_id_idx", tableName: "entity_content") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-263") {
		createIndex(indexName: "entity_content_company_id_idx", tableName: "entity_content") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-264") {
		createIndex(indexName: "entity_content_content_category_id_idx", tableName: "entity_content") {
			column(name: "content_category_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-265") {
		createIndex(indexName: "entity_content_content_type_id_idx", tableName: "entity_content") {
			column(name: "content_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-266") {
		createIndex(indexName: "entity_content_created_by_idx", tableName: "entity_content") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-267") {
		createIndex(indexName: "entity_content_entity_id_idx", tableName: "entity_content") {
			column(name: "entity_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-268") {
		createIndex(indexName: "entity_content_entity_type_id_idx", tableName: "entity_content") {
			column(name: "entity_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-269") {
		createIndex(indexName: "entity_content_updated_by_idx", tableName: "entity_content") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-270") {
		createIndex(indexName: "entity_note_company_id_idx", tableName: "entity_note") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-271") {
		createIndex(indexName: "entity_note_created_by_idx", tableName: "entity_note") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-272") {
		createIndex(indexName: "entity_note_entity_type_id_idx", tableName: "entity_note") {
			column(name: "entity_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-273") {
		createIndex(indexName: "entity_note_plugin_id_idx", tableName: "entity_note") {
			column(name: "entity_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-274") {
		createIndex(indexName: "entity_note_updated_by_idx", tableName: "entity_note") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-275") {
		createIndex(indexName: "exh_agent_company_id_idx", tableName: "exh_agent") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-276") {
		createIndex(indexName: "exh_agent_country_id_idx", tableName: "exh_agent") {
			column(name: "country_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-277") {
		createIndex(indexName: "exh_agent_created_by_idx", tableName: "exh_agent") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-278") {
		createIndex(indexName: "exh_agent_currency_id_idx", tableName: "exh_agent") {
			column(name: "currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-279") {
		createIndex(indexName: "exh_agent_updated_by_idx", tableName: "exh_agent") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-280") {
		createIndex(indexName: "exh_agent_currency_posting_agent_id_idx", tableName: "exh_agent_currency_posting") {
			column(name: "agent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-281") {
		createIndex(indexName: "exh_agent_currency_posting_created_by_idx", tableName: "exh_agent_currency_posting") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-282") {
		createIndex(indexName: "exh_agent_currency_posting_currency_id_idx", tableName: "exh_agent_currency_posting") {
			column(name: "currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-283") {
		createIndex(indexName: "exh_agent_currency_posting_updated_by_idx", tableName: "exh_agent_currency_posting") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-284") {
		createIndex(indexName: "code_uniq_1386147896345", tableName: "exh_bank", unique: "true") {
			column(name: "code")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-285") {
		createIndex(indexName: "name_uniq_1386147896345", tableName: "exh_bank", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-286") {
		createIndex(indexName: "exh_bank_branch_bank_id_idx", tableName: "exh_bank_branch") {
			column(name: "bank_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-287") {
		createIndex(indexName: "exh_bank_branch_district_id_idx", tableName: "exh_bank_branch") {
			column(name: "district_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-288") {
		createIndex(indexName: "exh_beneficiary_company_id_idx", tableName: "exh_beneficiary") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-289") {
		createIndex(indexName: "exh_beneficiary_user_id_idx", tableName: "exh_beneficiary") {
			column(name: "user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-290") {
		createIndex(indexName: "exh_currency_conversion_company_id_idx", tableName: "exh_currency_conversion") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-291") {
		createIndex(indexName: "exh_currency_conversion_user_id_idx", tableName: "exh_currency_conversion") {
			column(name: "user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-292") {
		createIndex(indexName: "exh_customer_agent_id_idx", tableName: "exh_customer") {
			column(name: "agent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-293") {
		createIndex(indexName: "exh_customer_code_idx", tableName: "exh_customer") {
			column(name: "code")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-294") {
		createIndex(indexName: "exh_customer_company_id_idx", tableName: "exh_customer") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-295") {
		createIndex(indexName: "exh_customer_country_id_idx", tableName: "exh_customer") {
			column(name: "country_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-296") {
		createIndex(indexName: "exh_customer_photo_id_type_id_idx", tableName: "exh_customer") {
			column(name: "photo_id_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-297") {
		createIndex(indexName: "exh_customer_user_id_idx", tableName: "exh_customer") {
			column(name: "user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-298") {
		createIndex(indexName: "exh_customer_beneficiary_mapping_beneficiary_id_idx", tableName: "exh_customer_beneficiary_mapping") {
			column(name: "beneficiary_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-299") {
		createIndex(indexName: "exh_customer_beneficiary_mapping_customer_id_idx", tableName: "exh_customer_beneficiary_mapping") {
			column(name: "customer_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-300") {
		createIndex(indexName: "name_uniq_1386147896360", tableName: "exh_district", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-301") {
		createIndex(indexName: "name_uniq_1386147896360", tableName: "exh_photo_id_type", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-302") {
		createIndex(indexName: "exh_regular_fee_company_id_idx", tableName: "exh_regular_fee") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-303") {
		createIndex(indexName: "exh_regular_fee_created_by_idx", tableName: "exh_regular_fee") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-304") {
		createIndex(indexName: "exh_regular_fee_updated_by_idx", tableName: "exh_regular_fee") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-305") {
		createIndex(indexName: "name_uniq_1386147896362", tableName: "exh_remittance_purpose", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-306") {
		createIndex(indexName: "exh_task_agent_id_idx", tableName: "exh_task") {
			column(name: "agent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-307") {
		createIndex(indexName: "exh_task_approved_by_idx", tableName: "exh_task") {
			column(name: "approved_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-308") {
		createIndex(indexName: "exh_task_beneficiary_id_idx", tableName: "exh_task") {
			column(name: "beneficiary_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-309") {
		createIndex(indexName: "exh_task_company_id_idx", tableName: "exh_task") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-310") {
		createIndex(indexName: "exh_task_current_status_idx", tableName: "exh_task") {
			column(name: "current_status")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-311") {
		createIndex(indexName: "exh_task_customer_id_idx", tableName: "exh_task") {
			column(name: "customer_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-312") {
		createIndex(indexName: "exh_task_from_currency_id_idx", tableName: "exh_task") {
			column(name: "from_currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-313") {
		createIndex(indexName: "exh_task_outlet_bank_id_idx", tableName: "exh_task") {
			column(name: "outlet_bank_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-314") {
		createIndex(indexName: "exh_task_outlet_branch_id_idx", tableName: "exh_task") {
			column(name: "outlet_branch_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-315") {
		createIndex(indexName: "exh_task_outlet_district_id_idx", tableName: "exh_task") {
			column(name: "outlet_district_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-316") {
		createIndex(indexName: "exh_task_paid_by_idx", tableName: "exh_task") {
			column(name: "paid_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-317") {
		createIndex(indexName: "exh_task_payment_method_idx", tableName: "exh_task") {
			column(name: "payment_method")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-318") {
		createIndex(indexName: "exh_task_pin_no_idx", tableName: "exh_task") {
			column(name: "pin_no")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-319") {
		createIndex(indexName: "exh_task_ref_no_idx", tableName: "exh_task") {
			column(name: "ref_no")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-320") {
		createIndex(indexName: "exh_task_remittance_purpose_idx", tableName: "exh_task") {
			column(name: "remittance_purpose")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-321") {
		createIndex(indexName: "exh_task_task_type_id_idx", tableName: "exh_task") {
			column(name: "task_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-322") {
		createIndex(indexName: "exh_task_to_currency_id_idx", tableName: "exh_task") {
			column(name: "to_currency_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-323") {
		createIndex(indexName: "exh_task_user_id_idx", tableName: "exh_task") {
			column(name: "user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-324") {
		createIndex(indexName: "fxd_category_maintenance_type_company_id_idx", tableName: "fxd_category_maintenance_type") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-325") {
		createIndex(indexName: "fxd_category_maintenance_type_created_by_idx", tableName: "fxd_category_maintenance_type") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-326") {
		createIndex(indexName: "fxd_category_maintenance_type_item_id_idx", tableName: "fxd_category_maintenance_type") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-327") {
		createIndex(indexName: "fxd_category_maintenance_type_maintenance_type_id_idx", tableName: "fxd_category_maintenance_type") {
			column(name: "maintenance_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-328") {
		createIndex(indexName: "fxd_category_maintenance_type_updated_by_idx", tableName: "fxd_category_maintenance_type") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-329") {
		createIndex(indexName: "fixed_asset_details_company_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-330") {
		createIndex(indexName: "fixed_asset_details_created_by_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-331") {
		createIndex(indexName: "fixed_asset_details_current_inventory_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "current_inventory_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-332") {
		createIndex(indexName: "fixed_asset_details_item_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-333") {
		createIndex(indexName: "fixed_asset_details_owner_type_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "owner_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-334") {
		createIndex(indexName: "fixed_asset_details_po_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "po_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-335") {
		createIndex(indexName: "fixed_asset_details_project_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-336") {
		createIndex(indexName: "fixed_asset_details_purchase_date_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "purchase_date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-337") {
		createIndex(indexName: "fixed_asset_details_supplier_id_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "supplier_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-338") {
		createIndex(indexName: "fixed_asset_details_updated_by_idx", tableName: "fxd_fixed_asset_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-339") {
		createIndex(indexName: "fixed_asset_trace_company_id_idx", tableName: "fxd_fixed_asset_trace") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-340") {
		createIndex(indexName: "fixed_asset_trace_created_by_idx", tableName: "fxd_fixed_asset_trace") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-341") {
		createIndex(indexName: "fixed_asset_trace_fixed_asset_details_id_idx", tableName: "fxd_fixed_asset_trace") {
			column(name: "fixed_asset_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-342") {
		createIndex(indexName: "fixed_asset_trace_inventory_id_idx", tableName: "fxd_fixed_asset_trace") {
			column(name: "inventory_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-343") {
		createIndex(indexName: "fixed_asset_trace_item_id_idx", tableName: "fxd_fixed_asset_trace") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-344") {
		createIndex(indexName: "fxd_maintenance_company_id_idx", tableName: "fxd_maintenance") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-345") {
		createIndex(indexName: "fxd_maintenance_created_by_idx", tableName: "fxd_maintenance") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-346") {
		createIndex(indexName: "fxd_maintenance_fixed_asset_details_id_idx", tableName: "fxd_maintenance") {
			column(name: "fixed_asset_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-347") {
		createIndex(indexName: "fxd_maintenance_item_id_idx", tableName: "fxd_maintenance") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-348") {
		createIndex(indexName: "fxd_maintenance_maintenance_type_id_idx", tableName: "fxd_maintenance") {
			column(name: "maintenance_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-349") {
		createIndex(indexName: "fxd_maintenance_updated_by_idx", tableName: "fxd_maintenance") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-350") {
		createIndex(indexName: "fxd_maintenance_type_company_id_idx", tableName: "fxd_maintenance_type") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-351") {
		createIndex(indexName: "fxd_maintenance_type_created_by_idx", tableName: "fxd_maintenance_type") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-352") {
		createIndex(indexName: "fxd_maintenance_type_updated_by_idx", tableName: "fxd_maintenance_type") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-353") {
		createIndex(indexName: "inv_inventory_company_id_idx", tableName: "inv_inventory") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-354") {
		createIndex(indexName: "inv_inventory_name_idx", tableName: "inv_inventory") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-355") {
		createIndex(indexName: "inv_inventory_project_id_idx", tableName: "inv_inventory") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-356") {
		createIndex(indexName: "inv_inventory_type_id_idx", tableName: "inv_inventory") {
			column(name: "type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-357") {
		createIndex(indexName: "inv_inventory_transaction_budget_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-358") {
		createIndex(indexName: "inv_inventory_transaction_company_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-359") {
		createIndex(indexName: "inv_inventory_transaction_created_by_idx", tableName: "inv_inventory_transaction") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-360") {
		createIndex(indexName: "inv_inventory_transaction_inv_production_line_item_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "inv_production_line_item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-361") {
		createIndex(indexName: "inv_inventory_transaction_inventory_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "inventory_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-362") {
		createIndex(indexName: "inv_inventory_transaction_inventory_type_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "inventory_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-363") {
		createIndex(indexName: "inv_inventory_transaction_project_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-364") {
		createIndex(indexName: "inv_inventory_transaction_transaction_date_idx", tableName: "inv_inventory_transaction") {
			column(name: "transaction_date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-365") {
		createIndex(indexName: "inv_inventory_transaction_transaction_entity_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "transaction_entity_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-366") {
		createIndex(indexName: "inv_inventory_transaction_transaction_entity_type_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "transaction_entity_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-367") {
		createIndex(indexName: "inv_inventory_transaction_transaction_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "transaction_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-368") {
		createIndex(indexName: "inv_inventory_transaction_transaction_type_id_idx", tableName: "inv_inventory_transaction") {
			column(name: "transaction_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-369") {
		createIndex(indexName: "inv_inventory_transaction_updated_by_idx", tableName: "inv_inventory_transaction") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-370") {
		createIndex(indexName: "inv_inventory_transaction_details_actual_quantity_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "actual_quantity")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-371") {
		createIndex(indexName: "inv_inventory_transaction_details_approved_by_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "approved_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-372") {
		createIndex(indexName: "inv_inventory_transaction_details_created_by_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-373") {
		createIndex(indexName: "inv_inventory_transaction_details_fifo_quantity_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "fifo_quantity")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-374") {
		createIndex(indexName: "inv_inventory_transaction_details_fixed_asset_details_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "fixed_asset_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-375") {
		createIndex(indexName: "inv_inventory_transaction_details_fixed_asset_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "fixed_asset_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-376") {
		createIndex(indexName: "inv_inventory_transaction_details_inventory_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "inventory_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-377") {
		createIndex(indexName: "inv_inventory_transaction_details_inventory_transaction_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "inventory_transaction_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-378") {
		createIndex(indexName: "inv_inventory_transaction_details_inventory_type_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "inventory_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-379") {
		createIndex(indexName: "inv_inventory_transaction_details_invoice_acknowledged_by_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "invoice_acknowledged_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-380") {
		createIndex(indexName: "inv_inventory_transaction_details_item_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-381") {
		createIndex(indexName: "inv_inventory_transaction_details_lifo_quantity_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "lifo_quantity")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-382") {
		createIndex(indexName: "inv_inventory_transaction_details_rate_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "rate")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-383") {
		createIndex(indexName: "inv_inventory_transaction_details_transaction_date_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "transaction_date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-384") {
		createIndex(indexName: "inv_inventory_transaction_details_transaction_details_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "transaction_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-385") {
		createIndex(indexName: "inv_inventory_transaction_details_transaction_type_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "transaction_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-386") {
		createIndex(indexName: "inv_inventory_transaction_details_updated_by_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-387") {
		createIndex(indexName: "inv_inventory_transaction_details_vehicle_id_idx", tableName: "inv_inventory_transaction_details") {
			column(name: "vehicle_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-388") {
		createIndex(indexName: "inv_production_details_material_id_idx", tableName: "inv_production_details") {
			column(name: "material_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-389") {
		createIndex(indexName: "inv_production_details_production_item_type_id_idx", tableName: "inv_production_details") {
			column(name: "production_item_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-390") {
		createIndex(indexName: "inv_production_details_production_line_item_id_idx", tableName: "inv_production_details") {
			column(name: "production_line_item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-391") {
		createIndex(indexName: "inv_production_line_item_company_id_idx", tableName: "inv_production_line_item") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-392") {
		createIndex(indexName: "item_company_id_idx", tableName: "item") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-393") {
		createIndex(indexName: "item_item_type_id_idx", tableName: "item") {
			column(name: "item_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-394") {
		createIndex(indexName: "item_name_idx", tableName: "item") {
			column(name: "name")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-395") {
		createIndex(indexName: "item_valuation_type_id_idx", tableName: "item") {
			column(name: "valuation_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-396") {
		createIndex(indexName: "proc_indent_approved_by_idx", tableName: "proc_indent") {
			column(name: "approved_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-397") {
		createIndex(indexName: "proc_indent_company_id_idx", tableName: "proc_indent") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-398") {
		createIndex(indexName: "proc_indent_created_by_idx", tableName: "proc_indent") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-399") {
		createIndex(indexName: "proc_indent_project_id_idx", tableName: "proc_indent") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-400") {
		createIndex(indexName: "proc_indent_updated_by_idx", tableName: "proc_indent") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-401") {
		createIndex(indexName: "proc_indent_details_company_id_idx", tableName: "proc_indent_details") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-402") {
		createIndex(indexName: "proc_indent_details_created_by_idx", tableName: "proc_indent_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-403") {
		createIndex(indexName: "proc_indent_details_indent_id_idx", tableName: "proc_indent_details") {
			column(name: "indent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-404") {
		createIndex(indexName: "proc_indent_details_project_id_idx", tableName: "proc_indent_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-405") {
		createIndex(indexName: "proc_indent_details_updated_by_idx", tableName: "proc_indent_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-406") {
		createIndex(indexName: "proc_purchase_order_approved_by_director_id_idx", tableName: "proc_purchase_order") {
			column(name: "approved_by_director_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-407") {
		createIndex(indexName: "proc_purchase_order_approved_by_project_director_id_idx", tableName: "proc_purchase_order") {
			column(name: "approved_by_project_director_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-408") {
		createIndex(indexName: "proc_purchase_order_budget_id_idx", tableName: "proc_purchase_order") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-409") {
		createIndex(indexName: "proc_purchase_order_company_id_idx", tableName: "proc_purchase_order") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-410") {
		createIndex(indexName: "proc_purchase_order_created_by_idx", tableName: "proc_purchase_order") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-411") {
		createIndex(indexName: "proc_purchase_order_created_on_idx", tableName: "proc_purchase_order") {
			column(name: "created_on")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-412") {
		createIndex(indexName: "proc_purchase_order_payment_method_id_idx", tableName: "proc_purchase_order") {
			column(name: "payment_method_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-413") {
		createIndex(indexName: "proc_purchase_order_project_id_idx", tableName: "proc_purchase_order") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-414") {
		createIndex(indexName: "proc_purchase_order_purchase_request_id_idx", tableName: "proc_purchase_order") {
			column(name: "purchase_request_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-415") {
		createIndex(indexName: "proc_purchase_order_supplier_id_idx", tableName: "proc_purchase_order") {
			column(name: "supplier_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-416") {
		createIndex(indexName: "proc_purchase_order_updated_by_idx", tableName: "proc_purchase_order") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-417") {
		createIndex(indexName: "proc_purchase_order_details_budget_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-418") {
		createIndex(indexName: "proc_purchase_order_details_company_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-419") {
		createIndex(indexName: "proc_purchase_order_details_created_by_idx", tableName: "proc_purchase_order_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-420") {
		createIndex(indexName: "proc_purchase_order_details_item_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-421") {
		createIndex(indexName: "proc_purchase_order_details_project_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-422") {
		createIndex(indexName: "proc_purchase_order_details_purchase_order_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "purchase_order_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-423") {
		createIndex(indexName: "proc_purchase_order_details_purchase_request_details_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "purchase_request_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-424") {
		createIndex(indexName: "proc_purchase_order_details_purchase_request_id_idx", tableName: "proc_purchase_order_details") {
			column(name: "purchase_request_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-425") {
		createIndex(indexName: "proc_purchase_order_details_quantity_idx", tableName: "proc_purchase_order_details") {
			column(name: "quantity")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-426") {
		createIndex(indexName: "proc_purchase_order_details_rate_idx", tableName: "proc_purchase_order_details") {
			column(name: "rate")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-427") {
		createIndex(indexName: "proc_purchase_order_details_updated_by_idx", tableName: "proc_purchase_order_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-428") {
		createIndex(indexName: "proc_purchase_request_approved_by_director_id_idx", tableName: "proc_purchase_request") {
			column(name: "approved_by_director_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-429") {
		createIndex(indexName: "proc_purchase_request_approved_by_project_director_id_idx", tableName: "proc_purchase_request") {
			column(name: "approved_by_project_director_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-430") {
		createIndex(indexName: "proc_purchase_request_budget_id_idx", tableName: "proc_purchase_request") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-431") {
		createIndex(indexName: "proc_purchase_request_company_id_idx", tableName: "proc_purchase_request") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-432") {
		createIndex(indexName: "proc_purchase_request_created_by_idx", tableName: "proc_purchase_request") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-433") {
		createIndex(indexName: "proc_purchase_request_indent_id_idx", tableName: "proc_purchase_request") {
			column(name: "indent_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-434") {
		createIndex(indexName: "proc_purchase_request_project_id_idx", tableName: "proc_purchase_request") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-435") {
		createIndex(indexName: "proc_purchase_request_updated_by_idx", tableName: "proc_purchase_request") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-436") {
		createIndex(indexName: "proc_purchase_request_details_budget_details_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "budget_details_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-437") {
		createIndex(indexName: "proc_purchase_request_details_budget_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-438") {
		createIndex(indexName: "proc_purchase_request_details_company_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-439") {
		createIndex(indexName: "proc_purchase_request_details_created_by_idx", tableName: "proc_purchase_request_details") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-440") {
		createIndex(indexName: "proc_purchase_request_details_item_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-441") {
		createIndex(indexName: "proc_purchase_request_details_project_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-442") {
		createIndex(indexName: "proc_purchase_request_details_purchase_request_id_idx", tableName: "proc_purchase_request_details") {
			column(name: "purchase_request_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-443") {
		createIndex(indexName: "proc_purchase_request_details_updated_by_idx", tableName: "proc_purchase_request_details") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-444") {
		createIndex(indexName: "proc_terms_and_condition_budget_id_idx", tableName: "proc_terms_and_condition") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-445") {
		createIndex(indexName: "proc_terms_and_condition_company_id_idx", tableName: "proc_terms_and_condition") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-446") {
		createIndex(indexName: "proc_terms_and_condition_created_by_idx", tableName: "proc_terms_and_condition") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-447") {
		createIndex(indexName: "proc_terms_and_condition_project_id_idx", tableName: "proc_terms_and_condition") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-448") {
		createIndex(indexName: "proc_terms_and_condition_purchase_order_id_idx", tableName: "proc_terms_and_condition") {
			column(name: "purchase_order_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-449") {
		createIndex(indexName: "proc_terms_and_condition_updated_by_idx", tableName: "proc_terms_and_condition") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-450") {
		createIndex(indexName: "proc_transport_cost_purchase_order_id_idx", tableName: "proc_transport_cost") {
			column(name: "purchase_order_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-451") {
		createIndex(indexName: "project_company_id_idx", tableName: "project") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-452") {
		createIndex(indexName: "project_created_by_idx", tableName: "project") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-453") {
		createIndex(indexName: "project_updated_by_idx", tableName: "project") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-454") {
		createIndex(indexName: "qs_measurement_budget_id_idx", tableName: "qs_measurement") {
			column(name: "budget_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-455") {
		createIndex(indexName: "qs_measurement_company_id_idx", tableName: "qs_measurement") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-456") {
		createIndex(indexName: "qs_measurement_created_by_idx", tableName: "qs_measurement") {
			column(name: "created_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-457") {
		createIndex(indexName: "qs_measurement_is_govt_qs_idx", tableName: "qs_measurement") {
			column(name: "is_govt_qs")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-458") {
		createIndex(indexName: "qs_measurement_project_id_idx", tableName: "qs_measurement") {
			column(name: "project_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-459") {
		createIndex(indexName: "qs_measurement_qs_measurement_date_idx", tableName: "qs_measurement") {
			column(name: "qs_measurement_date")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-460") {
		createIndex(indexName: "qs_measurement_quantity_idx", tableName: "qs_measurement") {
			column(name: "quantity")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-461") {
		createIndex(indexName: "qs_measurement_site_id_idx", tableName: "qs_measurement") {
			column(name: "site_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-462") {
		createIndex(indexName: "qs_measurement_updated_by_idx", tableName: "qs_measurement") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-463") {
		createIndex(indexName: "transaction_code_uniq_1386147896415", tableName: "request_map", unique: "true") {
			column(name: "transaction_code")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-464") {
		createIndex(indexName: "url_uniq_1386147896415", tableName: "request_map", unique: "true") {
			column(name: "url")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-465") {
		createIndex(indexName: "authority_uniq_1386147896416", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-466") {
		createIndex(indexName: "role_company_id_idx", tableName: "role") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-467") {
		createIndex(indexName: "role_role_type_id_idx", tableName: "role") {
			column(name: "role_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-468") {
		createIndex(indexName: "supplier_company_id_idx", tableName: "supplier") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-469") {
		createIndex(indexName: "supplier_supplier_type_id_idx", tableName: "supplier") {
			column(name: "supplier_type_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-470") {
		createIndex(indexName: "supplier_item_item_id_idx", tableName: "supplier_item") {
			column(name: "item_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-471") {
		createIndex(indexName: "supplier_item_supplier_id_idx", tableName: "supplier_item") {
			column(name: "supplier_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-472") {
		createIndex(indexName: "key_uniq_1386147896420", tableName: "sys_configuration", unique: "true") {
			column(name: "key")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-473") {
		createIndex(indexName: "sys_configuration_company_id_idx", tableName: "sys_configuration") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-474") {
		createIndex(indexName: "system_entity_key_idx", tableName: "system_entity") {
			column(name: "key")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-475") {
		createIndex(indexName: "system_entity_type_idx", tableName: "system_entity") {
			column(name: "type")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-476") {
		createIndex(indexName: "theme_company_id_idx", tableName: "theme") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-477") {
		createIndex(indexName: "theme_updated_by_idx", tableName: "theme") {
			column(name: "updated_by")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-478") {
		createIndex(indexName: "user_role_role_idx", tableName: "user_role") {
			column(name: "role_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-479") {
		createIndex(indexName: "user_role_user_idx", tableName: "user_role") {
			column(name: "user_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-480") {
		createIndex(indexName: "vehicle_company_id_idx", tableName: "vehicle") {
			column(name: "company_id")
		}
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-481") {
		createSequence(sequenceName: "acc_bank_statement_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-482") {
		createSequence(sequenceName: "acc_chart_of_account_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-483") {
		createSequence(sequenceName: "acc_custom_group_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-484") {
		createSequence(sequenceName: "acc_division_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-485") {
		createSequence(sequenceName: "acc_financial_year_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-486") {
		createSequence(sequenceName: "acc_group_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-487") {
		createSequence(sequenceName: "acc_iou_purpose_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-488") {
		createSequence(sequenceName: "acc_iou_slip_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-489") {
		createSequence(sequenceName: "acc_ipc_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-490") {
		createSequence(sequenceName: "acc_lc_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-491") {
		createSequence(sequenceName: "acc_lease_account_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-492") {
		createSequence(sequenceName: "acc_sub_account_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-493") {
		createSequence(sequenceName: "acc_tier1_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-494") {
		createSequence(sequenceName: "acc_tier2_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-495") {
		createSequence(sequenceName: "acc_tier3_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-496") {
		createSequence(sequenceName: "acc_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-497") {
		createSequence(sequenceName: "acc_voucher_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-498") {
		createSequence(sequenceName: "acc_voucher_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-499") {
		createSequence(sequenceName: "acc_voucher_type_coa_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-500") {
		createSequence(sequenceName: "app_group_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-501") {
		createSequence(sequenceName: "app_user_entity_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-502") {
		createSequence(sequenceName: "app_user_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-503") {
		createSequence(sequenceName: "budg_budget_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-504") {
		createSequence(sequenceName: "budg_budget_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-505") {
		createSequence(sequenceName: "budg_budget_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-506") {
		createSequence(sequenceName: "budg_project_budget_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-507") {
		createSequence(sequenceName: "company_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-508") {
		createSequence(sequenceName: "content_category_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-509") {
		createSequence(sequenceName: "country_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-510") {
		createSequence(sequenceName: "currency_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-511") {
		createSequence(sequenceName: "customer_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-512") {
		createSequence(sequenceName: "designation_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-513") {
		createSequence(sequenceName: "employee_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-514") {
		createSequence(sequenceName: "entity_content_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-515") {
		createSequence(sequenceName: "entity_note_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-516") {
		createSequence(sequenceName: "exh_agent_currency_posting_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-517") {
		createSequence(sequenceName: "exh_agent_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-518") {
		createSequence(sequenceName: "exh_bank_branch_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-519") {
		createSequence(sequenceName: "exh_bank_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-520") {
		createSequence(sequenceName: "exh_beneficiary_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-521") {
		createSequence(sequenceName: "exh_currency_conversion_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-522") {
		createSequence(sequenceName: "exh_customer_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-523") {
		createSequence(sequenceName: "exh_customer_trace_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-524") {
		createSequence(sequenceName: "exh_district_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-525") {
		createSequence(sequenceName: "exh_photo_id_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-526") {
		createSequence(sequenceName: "exh_regular_fee_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-527") {
		createSequence(sequenceName: "exh_remittance_purpose_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-528") {
		createSequence(sequenceName: "exh_sanction_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-529") {
		createSequence(sequenceName: "exh_task_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-530") {
		createSequence(sequenceName: "exh_task_trace_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-531") {
		createSequence(sequenceName: "fxd_category_maintenance_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-532") {
		createSequence(sequenceName: "fxd_fixed_asset_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-533") {
		createSequence(sequenceName: "fxd_fixed_asset_trace_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-534") {
		createSequence(sequenceName: "fxd_maintenance_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-535") {
		createSequence(sequenceName: "fxd_maintenance_type_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-536") {
		createSequence(sequenceName: "hibernate_sequence")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-537") {
		createSequence(sequenceName: "inv_inventory_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-538") {
		createSequence(sequenceName: "inv_inventory_transaction_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-539") {
		createSequence(sequenceName: "inv_inventory_transaction_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-540") {
		createSequence(sequenceName: "inv_production_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-541") {
		createSequence(sequenceName: "inv_production_line_item_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-542") {
		createSequence(sequenceName: "item_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-543") {
		createSequence(sequenceName: "proc_indent_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-544") {
		createSequence(sequenceName: "proc_indent_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-545") {
		createSequence(sequenceName: "proc_purchase_order_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-546") {
		createSequence(sequenceName: "proc_purchase_order_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-547") {
		createSequence(sequenceName: "proc_purchase_request_details_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-548") {
		createSequence(sequenceName: "proc_purchase_request_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-549") {
		createSequence(sequenceName: "proc_terms_and_condition_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-550") {
		createSequence(sequenceName: "proc_transport_cost_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-551") {
		createSequence(sequenceName: "project_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-552") {
		createSequence(sequenceName: "qs_measurement_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-553") {
		createSequence(sequenceName: "request_map_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-554") {
		createSequence(sequenceName: "role_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-555") {
		createSequence(sequenceName: "supplier_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-556") {
		createSequence(sequenceName: "supplier_item_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-557") {
		createSequence(sequenceName: "sys_configuration_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-558") {
		createSequence(sequenceName: "vehicle_id_seq")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-125") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FK143BF46A8BCF6D52", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
	}

	changeSet(author: "informationtechnology (generated)", id: "1386147896715-126") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46AE9151943", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "app_user", referencesUniqueColumn: "false")
	}

	include file: 'diff.groovy'
}
