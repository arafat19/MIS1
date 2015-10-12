-- Sequence: acc_chart_of_account_id_seq

SET @acc_chart_of_account_start= SELECT COALESCE(MAX(acc_chart_of_account.id),0) FROM acc_chart_of_account;
SET @acc_chart_of_account_start=  CAST(@acc_chart_of_account_start AS INTEGER)+1;

DROP SEQUENCE acc_chart_of_account_id_seq;

CREATE SEQUENCE acc_chart_of_account_id_seq
  INCREMENT 1
  MINVALUE @acc_chart_of_account_start
  MAXVALUE 2147483647
  START @acc_chart_of_account_start
  CACHE 1;


-- Sequence: acc_custom_group_id_seq

SET @acc_custom_group_start= SELECT COALESCE(MAX(acc_custom_group.id),0) FROM acc_custom_group;
SET @acc_custom_group_start=  CAST(@acc_custom_group_start AS INTEGER)+1;

DROP SEQUENCE acc_custom_group_id_seq;

CREATE SEQUENCE acc_custom_group_id_seq
  INCREMENT 1
  MINVALUE @acc_custom_group_start
  MAXVALUE 2147483647
  START @acc_custom_group_start
  CACHE 1;


-- Sequence: acc_division_id_seq

SET @acc_division_start= SELECT COALESCE(MAX(acc_division.id),0) FROM acc_division;
SET @acc_division_start=  CAST(@acc_division_start AS INTEGER)+1;

DROP SEQUENCE acc_division_id_seq;

CREATE SEQUENCE acc_division_id_seq
  INCREMENT 1
  MINVALUE @acc_division_start
  MAXVALUE 2147483647
  START @acc_division_start
  CACHE 1;

-- Sequence: acc_group_id_seq

SET @acc_group_start= SELECT COALESCE(MAX(acc_group.id),0) FROM acc_group;
SET @acc_group_start=  CAST(@acc_group_start AS INTEGER)+1;

DROP SEQUENCE acc_group_id_seq;

CREATE SEQUENCE acc_group_id_seq
  INCREMENT 1
  MINVALUE @acc_group_start
  MAXVALUE 2147483647
  START @acc_group_start
  CACHE 1;

-- Sequence: acc_sub_account_id_seq

SET @acc_sub_account_start= SELECT COALESCE(MAX(acc_sub_account.id),0) FROM acc_sub_account;
SET @acc_sub_account_start=  CAST(@acc_sub_account_start AS INTEGER)+1;

DROP SEQUENCE acc_sub_account_id_seq;

CREATE SEQUENCE acc_sub_account_id_seq
  INCREMENT 1
  MINVALUE @acc_sub_account_start
  MAXVALUE 2147483647
  START @acc_sub_account_start
  CACHE 1;

-- Sequence: acc_tier1_id_seq

SET @acc_tier1_start= SELECT COALESCE(MAX(acc_tier1.id),0) FROM acc_tier1;
SET @acc_tier1_start=  CAST(@acc_tier1_start AS INTEGER)+1;

DROP SEQUENCE acc_tier1_id_seq;

CREATE SEQUENCE acc_tier1_id_seq
  INCREMENT 1
  MINVALUE @acc_tier1_start
  MAXVALUE 2147483647
  START @acc_tier1_start
  CACHE 1;

-- Sequence: acc_tier2_id_seq

SET @acc_tier2_start= SELECT COALESCE(MAX(acc_tier2.id),0) FROM acc_tier2;
SET @acc_tier2_start=  CAST(@acc_tier2_start AS INTEGER)+1;

DROP SEQUENCE acc_tier2_id_seq;

CREATE SEQUENCE acc_tier2_id_seq
  INCREMENT 1
  MINVALUE @acc_tier2_start
  MAXVALUE 2147483647
  START @acc_tier2_start
  CACHE 1;

-- Sequence: acc_tier3_id_seq

SET @acc_tier3_start= SELECT COALESCE(MAX(acc_tier3.id),0) FROM acc_tier3;
SET @acc_tier3_start=  CAST(@acc_tier3_start AS INTEGER)+1;

DROP SEQUENCE acc_tier3_id_seq;

CREATE SEQUENCE acc_tier3_id_seq
  INCREMENT 1
  MINVALUE @acc_tier3_start
  MAXVALUE 2147483647
  START @acc_tier3_start
  CACHE 1;

-- Sequence: acc_type_id_seq

SET @acc_type_start= SELECT COALESCE(MAX(acc_type.id),0) FROM acc_type;
SET @acc_type_start=  CAST(@acc_type_start AS INTEGER)+1;

DROP SEQUENCE acc_type_id_seq;

CREATE SEQUENCE acc_type_id_seq
  INCREMENT 1
  MINVALUE @acc_type_start
  MAXVALUE 2147483647
  START @acc_type_start
  CACHE 1;

-- Sequence: acc_voucher_details_id_seq

SET @acc_voucher_details_start= SELECT COALESCE(MAX(acc_voucher_details.id),0) FROM acc_voucher_details;
SET @acc_voucher_details_start=  CAST(@acc_voucher_details_start AS INTEGER)+1;

DROP SEQUENCE acc_voucher_details_id_seq;

CREATE SEQUENCE acc_voucher_details_id_seq
  INCREMENT 1
  MINVALUE @acc_voucher_details_start
  MAXVALUE 9223372036854775807
  START @acc_voucher_details_start
  CACHE 1;

-- Sequence: acc_voucher_id_seq

SET @acc_voucher_start= SELECT COALESCE(MAX(acc_voucher.id),0) FROM acc_voucher;
SET @acc_voucher_start=  CAST(@acc_voucher_start AS INTEGER)+1;

DROP SEQUENCE acc_voucher_id_seq;

CREATE SEQUENCE acc_voucher_id_seq
  INCREMENT 1
  MINVALUE @acc_voucher_start
  MAXVALUE 9223372036854775807
  START @acc_voucher_start
  CACHE 1;

-- Sequence: acc_voucher_type_coa_id_seq

SET @acc_voucher_type_coa_start= SELECT COALESCE(MAX(acc_voucher_type_coa.id),0) FROM acc_voucher_type_coa;
SET @acc_voucher_type_coa_start=  CAST(@acc_voucher_type_coa_start AS INTEGER)+1;

DROP SEQUENCE acc_voucher_type_coa_id_seq;

CREATE SEQUENCE acc_voucher_type_coa_id_seq
  INCREMENT 1
  MINVALUE @acc_voucher_type_coa_start
  MAXVALUE 2147483647
  START @acc_voucher_type_coa_start
  CACHE 1;

-- Sequence: app_user_id_seq

SET @app_user_start= SELECT COALESCE(MAX(app_user.id),0) FROM app_user;
SET @app_user_start=  CAST(@app_user_start AS INTEGER)+1;

DROP SEQUENCE app_user_id_seq;

CREATE SEQUENCE app_user_id_seq
  INCREMENT 1
  MINVALUE @app_user_start
  MAXVALUE 2147483647
  START @app_user_start
  CACHE 1;

-- Sequence: budget_details_id_seq

SET @budget_details_start= SELECT COALESCE(MAX(budget_details.id),0) FROM budget_details;
SET @budget_details_start=  CAST(@budget_details_start AS INTEGER)+1;

DROP SEQUENCE budg_budget_details_id_seq;

CREATE SEQUENCE budg_budget_details_id_seq
  INCREMENT 1
  MINVALUE @budget_details_start
  MAXVALUE 9223372036854775807
  START @budget_details_start
  CACHE 1;

-- Sequence: budg_budget_id_seq

SET @budget_start= SELECT COALESCE(MAX(budget.id),0) FROM budget;
SET @budget_start=  CAST(@budget_start AS INTEGER)+1;

DROP SEQUENCE budg_budget_id_seq;

CREATE SEQUENCE budg_budget_id_seq
  INCREMENT 1
  MINVALUE @budget_start
  MAXVALUE 9223372036854775807
  START @budget_start
  CACHE 1;

-- Sequence: budg_budget_type_id_seq

SET @budget_type_start= SELECT COALESCE(MAX(budg_budget_type.id),0) FROM budg_budget_type;
SET @budget_type_start=  CAST(@budget_type_start AS INTEGER)+1;

DROP SEQUENCE budg_budget_type_id_seq;

CREATE SEQUENCE budg_budget_type_id_seq
  INCREMENT 1
  MINVALUE @budget_type_start
  MAXVALUE 2147483647
  START @budget_type_start
  CACHE 1;

-- Sequence: company_id_seq

SET @company_start= SELECT COALESCE(MAX(company.id),0) FROM company;
SET @company_start=  CAST(@company_start AS INTEGER)+1;

DROP SEQUENCE company_id_seq;

CREATE SEQUENCE company_id_seq
  INCREMENT 1
  MINVALUE @company_start
  MAXVALUE 2147483647
  START @company_start
  CACHE 1;

-- Sequence: item_id_seq

SET @item_start= SELECT COALESCE(MAX(item.id),0) FROM item;
SET @item_start=  CAST(@item_start AS INTEGER)+1;

DROP SEQUENCE item_id_seq;

CREATE SEQUENCE item_id_seq
  INCREMENT 1
  MINVALUE @item_start
  MAXVALUE 9223372036854775807
  START @item_start
  CACHE 1;

-- Sequence: inv_production_details_id_seq

SET @inv_production_details_start= SELECT COALESCE(MAX(inv_production_details.id),0) FROM inv_production_details;
SET @inv_production_details_start=  CAST(@inv_production_details_start AS INTEGER)+1;

DROP SEQUENCE inv_production_details_id_seq;

CREATE SEQUENCE inv_production_details_id_seq
  INCREMENT 1
  MINVALUE @inv_production_details_start
  MAXVALUE 9223372036854775807
  START @inv_production_details_start
  CACHE 1;

-- Sequence: inv_production_line_item_id_seq

SET @inv_production_line_item_start= SELECT COALESCE(MAX(inv_production_line_item.id),0) FROM inv_production_line_item;
SET @inv_production_line_item_start=  CAST(@inv_production_line_item_start AS INTEGER)+1;

DROP SEQUENCE inv_production_line_item_id_seq;

CREATE SEQUENCE inv_production_line_item_id_seq
  INCREMENT 1
  MINVALUE @inv_production_line_item_start
  MAXVALUE 9223372036854775807
  START @inv_production_line_item_start
  CACHE 1;

-- Sequence: inv_site_id_seq

SET @inv_site_start= SELECT COALESCE(MAX(inv_site.id),0) FROM inv_site;
SET @inv_site_start=  CAST(@inv_site_start AS INTEGER)+1;

DROP SEQUENCE inv_site_id_seq;

CREATE SEQUENCE inv_site_id_seq
  INCREMENT 1
  MINVALUE @inv_site_start
  MAXVALUE 2147483647
  START @inv_site_start
  CACHE 1;

-- Sequence: inv_site_transaction_details_id_seq

SET @inv_site_transaction_details_start= SELECT COALESCE(MAX(inv_site_transaction_details.id),0) FROM inv_site_transaction_details;
SET @inv_site_transaction_details_start=  CAST(@inv_site_transaction_details_start AS INTEGER)+1;

DROP SEQUENCE inv_site_transaction_details_id_seq;

CREATE SEQUENCE inv_site_transaction_details_id_seq
  INCREMENT 1
  MINVALUE @inv_site_transaction_details_start
  MAXVALUE 9223372036854775807
  START @inv_site_transaction_details_start
  CACHE 1;

-- Sequence: inv_site_transaction_id_seq

SET @inv_site_transaction_start= SELECT COALESCE(MAX(inv_site_transaction.id),0) FROM inv_site_transaction;
SET @inv_site_transaction_start=  CAST(@inv_site_transaction_start AS INTEGER)+1;

DROP SEQUENCE inv_site_transaction_id_seq;

CREATE SEQUENCE inv_site_transaction_id_seq
  INCREMENT 1
  MINVALUE @inv_site_transaction_start
  MAXVALUE 9223372036854775807
  START @inv_site_transaction_start
  CACHE 1;

-- Sequence: inv_site_transaction_summary_id_seq

SET @inv_site_transaction_summary_start= SELECT COALESCE(MAX(inv_site_transaction_summary.id),0) FROM inv_site_transaction_summary;
SET @inv_site_transaction_summary_start=  CAST(@inv_site_transaction_summary_start AS INTEGER)+1;

DROP SEQUENCE inv_site_transaction_summary_id_seq;

CREATE SEQUENCE inv_site_transaction_summary_id_seq
  INCREMENT 1
  MINVALUE @inv_site_transaction_summary_start
  MAXVALUE 9223372036854775807
  START @inv_site_transaction_summary_start
  CACHE 1;

-- Sequence: inv_store_id_seq

SET @inv_store_start= SELECT COALESCE(MAX(inv_store.id),0) FROM inv_store;
SET @inv_store_start=  CAST(@inv_store_start AS INTEGER)+1;

DROP SEQUENCE inv_store_id_seq;

CREATE SEQUENCE inv_store_id_seq
  INCREMENT 1
  MINVALUE @inv_store_start
  MAXVALUE 2147483647
  START @inv_store_start
  CACHE 1;

-- Sequence: inv_store_transaction_details_id_seq

SET @inv_store_transaction_details_start= SELECT COALESCE(MAX(inv_store_transaction_details.id),0) FROM inv_store_transaction_details;
SET @inv_store_transaction_details_start=  CAST(@inv_store_transaction_details_start AS INTEGER)+1;

DROP SEQUENCE inv_store_transaction_details_id_seq;

CREATE SEQUENCE inv_store_transaction_details_id_seq
  INCREMENT 1
  MINVALUE @inv_store_transaction_details_start
  MAXVALUE 9223372036854775807
  START @inv_store_transaction_details_start
  CACHE 1;

-- Sequence: inv_store_transaction_id_seq

SET @inv_store_transaction_start= SELECT COALESCE(MAX(inv_store_transaction.id),0) FROM inv_store_transaction;
SET @inv_store_transaction_start=  CAST(@inv_store_transaction_start AS INTEGER)+1;

DROP SEQUENCE inv_store_transaction_id_seq;

CREATE SEQUENCE inv_store_transaction_id_seq
  INCREMENT 1
  MINVALUE @inv_store_transaction_start
  MAXVALUE 9223372036854775807
  START @inv_store_transaction_start
  CACHE 1;

-- Sequence: inv_store_transaction_summary_id_seq

SET @inv_store_transaction_summary_start= SELECT COALESCE(MAX(inv_store_transaction_summary.id),0) FROM inv_store_transaction_summary;
SET @inv_store_transaction_summary_start=  CAST(@inv_store_transaction_summary_start AS INTEGER)+1;

DROP SEQUENCE inv_store_transaction_summary_id_seq;

CREATE SEQUENCE inv_store_transaction_summary_id_seq
  INCREMENT 1
  MINVALUE @inv_store_transaction_summary_start
  MAXVALUE 9223372036854775807
  START @inv_store_transaction_summary_start
  CACHE 1;

-- Sequence: supplier_id_seq

SET @supplier_start= SELECT COALESCE(MAX(supplier.id),0) FROM supplier;
SET @supplier_start=  CAST(@supplier_start AS INTEGER)+1;

DROP SEQUENCE supplier_id_seq;

CREATE SEQUENCE supplier_id_seq
  INCREMENT 1
  MINVALUE @supplier_start
  MAXVALUE 2147483647
  START @supplier_start
  CACHE 1;

-- Sequence: supplier_item_id_seq

SET @supplier_item_start= SELECT COALESCE(MAX(supplier_item.id),0) FROM supplier_item;
SET @supplier_item_start=  CAST(@supplier_item_start AS INTEGER)+1;

DROP SEQUENCE supplier_item_id_seq;

CREATE SEQUENCE supplier_item_id_seq
  INCREMENT 1
  MINVALUE @supplier_item_start
  MAXVALUE 9223372036854775807
  START @supplier_item_start
  CACHE 1;

-- Sequence: inv_temp_site_transaction_details_id_seq

SET @inv_temp_site_transaction_details_start= SELECT COALESCE(MAX(inv_temp_site_transaction_details.id),0) FROM inv_temp_site_transaction_details;
SET @inv_temp_site_transaction_details_start=  CAST(@inv_temp_site_transaction_details_start AS INTEGER)+1;

DROP SEQUENCE inv_temp_site_transaction_details_id_seq;

CREATE SEQUENCE inv_temp_site_transaction_details_id_seq
  INCREMENT 1
  MINVALUE @inv_temp_site_transaction_details_start
  MAXVALUE 9223372036854775807
  START @inv_temp_site_transaction_details_start
  CACHE 1;

-- Sequence: indent_details_id_seq

SET @indent_details_start= SELECT COALESCE(MAX(indent_details.id),0) FROM indent_details;
SET @indent_details_start=  CAST(@indent_details_start AS INTEGER)+1;

DROP SEQUENCE indent_details_id_seq;

CREATE SEQUENCE indent_details_id_seq
  INCREMENT 1
  MINVALUE @indent_details_start
  MAXVALUE 9223372036854775807
  START @indent_details_start
  CACHE 1;

-- Sequence: proc_indent_id_seq

SET @indent_start= SELECT COALESCE(MAX(indent.id),0) FROM indent;
SET @indent_start=  CAST(@indent_start AS INTEGER)+1;

DROP SEQUENCE proc_indent_id_seq;

CREATE SEQUENCE proc_indent_id_seq
  INCREMENT 1
  MINVALUE @indent_start
  MAXVALUE 9223372036854775807
  START @indent_start
  CACHE 1;

-- Sequence: budg_project_budget_type_id_seq

SET @project_budget_type_start= SELECT COALESCE(MAX(budg_project_budget_type.id),0) FROM budg_project_budget_type;
SET @project_budget_type_start=  CAST(@budg_project_budget_type AS INTEGER)+1;

DROP SEQUENCE budg_project_budget_type_id_seq;

CREATE SEQUENCE budg_project_budget_type_id_seq
  INCREMENT 1
  MINVALUE @budg_project_budget_type_start
  MAXVALUE 9223372036854775807
  START @budg_project_budget_type_start
  CACHE 1;

-- Sequence: project_id_seq

SET @project_start= SELECT COALESCE(MAX(project.id),0) FROM project;
SET @project_start=  CAST(@project_start AS INTEGER)+1;

DROP SEQUENCE project_id_seq;

CREATE SEQUENCE project_id_seq
  INCREMENT 1
  MINVALUE @project_start
  MAXVALUE 2147483647
  START @project_start
  CACHE 1;

-- Sequence: purchase_order_details_id_seq

SET @purchase_order_details_start= SELECT COALESCE(MAX(proc_purchase_order_details.id),0) FROM proc_purchase_order_details;
SET @purchase_order_details_start=  CAST(@purchase_order_details_start AS INTEGER)+1;

DROP SEQUENCE purchase_order_details_id_seq;

CREATE SEQUENCE purchase_order_details_id_seq
  INCREMENT 1
  MINVALUE @purchase_order_details_start
  MAXVALUE 9223372036854775807
  START @purchase_order_details_start
  CACHE 1;

-- Sequence: purchase_order_id_seq

SET @purchase_order_start= SELECT COALESCE(MAX(purchase_order.id),0) FROM purchase_order;
SET @purchase_order_start=  CAST(@purchase_order_start AS INTEGER)+1;

DROP SEQUENCE purchase_order_id_seq;

CREATE SEQUENCE purchase_order_id_seq
  INCREMENT 1
  MINVALUE @purchase_order_start
  MAXVALUE 9223372036854775807
  START @purchase_order_start
  CACHE 1;

-- Sequence: purchase_request_details_id_seq

SET @purchase_request_details_start= SELECT COALESCE(MAX(proc_purchase_request_details.id),0) FROM proc_purchase_request_details;
SET @purchase_request_details_start=  CAST(@purchase_request_details_start AS INTEGER)+1;

DROP SEQUENCE purchase_request_details_id_seq;

CREATE SEQUENCE purchase_request_details_id_seq
  INCREMENT 1
  MINVALUE @purchase_request_details_start
  MAXVALUE 9223372036854775807
  START @purchase_request_details_start
  CACHE 1;

-- Sequence: purchase_request_id_seq

SET @purchase_request_start= SELECT COALESCE(MAX(proc_purchase_request.id),0) FROM proc_purchase_request;
SET @purchase_request_start=  CAST(@purchase_request_start AS INTEGER)+1;

DROP SEQUENCE purchase_request_id_seq;

CREATE SEQUENCE purchase_request_id_seq
  INCREMENT 1
  MINVALUE @purchase_request_start
  MAXVALUE 9223372036854775807
  START @purchase_request_start
  CACHE 1;

-- Sequence: request_map_id_seq

SET @request_map_start= SELECT COALESCE(MAX(request_map.id),0) FROM request_map;
SET @request_map_start=  CAST(@request_map_start AS INTEGER)+1;

DROP SEQUENCE request_map_id_seq;

CREATE SEQUENCE request_map_id_seq
  INCREMENT 1
  MINVALUE @request_map_start
  MAXVALUE 9223372036854775807
  START @request_map_start
  CACHE 1;

-- Sequence: role_id_seq

--SET @role_start= SELECT COALESCE(MAX(role.id),0) FROM role;
--SET @role_start=  CAST(@role_start AS INTEGER)+1;

--DROP SEQUENCE role_id_seq;

--CREATE SEQUENCE role_id_seq
--  INCREMENT 1
--  MINVALUE @role_start
--  MAXVALUE 2147483647
--  START @role_start
--  CACHE 1;

-- Sequence: transport_cost_id_seq

SET @transport_cost_start= SELECT COALESCE(MAX(proc_transport_cost.id),0) FROM proc_transport_cost;
SET @transport_cost_start=  CAST(@transport_cost_start AS INTEGER)+1;

DROP SEQUENCE transport_cost_id_seq;

CREATE SEQUENCE transport_cost_id_seq
  INCREMENT 1
  MINVALUE @transport_cost_start
  MAXVALUE 9223372036854775807
  START @transport_cost_start
  CACHE 1;

-- Sequence: user_project_id_seq

SET @user_project_start= SELECT COALESCE(MAX(user_project.id),0) FROM user_project;
SET @user_project_start=  CAST(@user_project_start AS INTEGER)+1;

DROP SEQUENCE user_project_id_seq;

CREATE SEQUENCE user_project_id_seq
  INCREMENT 1
  MINVALUE @user_project_start
  MAXVALUE 2147483647
  START @user_project_start
  CACHE 1;

-- Sequence: user_site_id_seq

SET @user_site_start= SELECT COALESCE(MAX(user_site.id),0) FROM user_site;
SET @user_site_start=  CAST(@user_site_start AS INTEGER)+1;

DROP SEQUENCE user_site_id_seq;

CREATE SEQUENCE user_site_id_seq
  INCREMENT 1
  MINVALUE @user_site_start
  MAXVALUE 2147483647
  START @user_site_start
  CACHE 1;

-- Sequence: user_store_id_seq

SET @user_store_start= SELECT COALESCE(MAX(user_store.id),0) FROM user_store;
SET @user_store_start=  CAST(@user_store_start AS INTEGER)+1;

DROP SEQUENCE user_store_id_seq;

CREATE SEQUENCE user_store_id_seq
  INCREMENT 1
  MINVALUE @user_store_start
  MAXVALUE 2147483647
  START @user_store_start
  CACHE 1;

-- Sequence: vehicle_id_seq

SET @vehicle_start= SELECT COALESCE(MAX(vehicle.id),0) FROM vehicle;
SET @vehicle_start=  CAST(@vehicle_start AS INTEGER)+1;

DROP SEQUENCE vehicle_id_seq;

CREATE SEQUENCE vehicle_id_seq
  INCREMENT 1
  MINVALUE @vehicle_start
  MAXVALUE 2147483647
  START @vehicle_start
  CACHE 1;

  --------- Script for bHIP ---------
  ALTER TABLE bhp_member ALTER COLUMN introducer_id DROP NOT NULL;
