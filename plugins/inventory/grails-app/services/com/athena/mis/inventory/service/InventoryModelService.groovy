package com.athena.mis.inventory.service

import com.athena.mis.BaseService

class InventoryModelService extends BaseService {

    public static final String INV_CONSUMABLE_STOCK_MODEL = """
    DROP TABLE IF EXISTS vw_inv_inventory_consumable_stock;
    DROP VIEW IF EXISTS vw_inv_inventory_consumable_stock;
    CREATE OR REPLACE VIEW vw_inv_inventory_consumable_stock AS
    SELECT iitd.inventory_id, iitd.item_id, COALESCE(sum(
        CASE
            WHEN iitd.is_increase = true THEN iitd.actual_quantity
            ELSE NULL::numeric
        END), 0::numeric) - COALESCE(sum(
        CASE
            WHEN iitd.is_increase = false THEN iitd.actual_quantity
            ELSE NULL::numeric
        END), 0::numeric) AS consumeable_stock
    FROM inv_inventory_transaction_details iitd
    GROUP BY iitd.inventory_id, iitd.item_id;
    """

    public static final String INV_STOCK = """
    DROP TABLE IF EXISTS vw_inv_inventory_stock;
    DROP VIEW IF EXISTS vw_inv_inventory_stock;
    CREATE OR REPLACE VIEW vw_inv_inventory_stock AS
    SELECT iitd.inventory_id, iitd.item_id, COALESCE(sum(
        CASE
            WHEN iitd.is_increase = true AND iitd.approved_by > 0 THEN iitd.actual_quantity
            ELSE NULL::numeric
        END), 0::numeric) - COALESCE(sum(
        CASE
            WHEN iitd.is_increase = false AND iitd.approved_by > 0 THEN iitd.actual_quantity
            ELSE NULL::numeric
        END), 0::numeric) AS available_stock, iit.project_id
    FROM inv_inventory_transaction_details iitd
    LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
    GROUP BY iitd.inventory_id, iitd.item_id, iit.project_id;
    """

    public static final String INV_TRANSACTION_WITH_DETAILS = """
    DROP TABLE IF EXISTS vw_inv_inventory_transaction_with_details;
    DROP VIEW IF EXISTS vw_inv_inventory_transaction_with_details;
    CREATE OR REPLACE VIEW vw_inv_inventory_transaction_with_details AS
    SELECT iitd.id, iitd.acknowledged_by, iitd.actual_quantity, iitd.approved_by, iitd.approved_on,
    iitd.created_by, iitd.created_on, iitd.fifo_quantity, iitd.inventory_id, iitd.inventory_transaction_id,
    iitd.inventory_type_id, iitd.item_id, iitd.lifo_quantity, iitd.mrf_no, iitd.rate, iitd.shrinkage,
    iitd.stack_measurement, iitd.supplied_quantity, iitd.supplier_chalan, iitd.transaction_date,
    iitd.transaction_details_id, iitd.updated_by, iitd.updated_on, iitd.vehicle_id, iitd.vehicle_number,
    iitd.fixed_asset_id, iitd.fixed_asset_details_id, iit.transaction_type_id, iitd.transaction_type_id AS
    transaction_details_type_id, iitd.is_increase, iitd.adjustment_parent_id, iitd.is_current, iit.budget_id,
    iit.inv_production_line_item_id, iit.project_id, iit.transaction_entity_id, iit.transaction_entity_type_id,
    iit.transaction_id, iitd.overhead_cost, iit.company_id
    FROM inv_inventory_transaction_details iitd
    LEFT JOIN inv_inventory_transaction iit ON iitd.inventory_transaction_id = iit.id;
    """

    public static final String INV_VALUATION = """
    DROP TABLE IF EXISTS vw_inv_inventory_valuation;
    DROP VIEW IF EXISTS vw_inv_inventory_valuation;
    CREATE OR REPLACE VIEW vw_inv_inventory_valuation AS
    SELECT iitd.inventory_id, inv.name AS inventory_name, item.id AS item_id, item.name AS item_name,
    (ltrim(to_char(vw.available_stock, '9,999,99,99,990.99'::text)) || ' '::text) || item.unit::text AS str_available_stock, vw.available_stock,
        CASE
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (127))
                THEN ltrim(to_char(sum(iitd.rate * (iitd.actual_quantity - iitd.fifo_quantity)), '9,999,99,99,990.99'::text))
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (128))
                THEN ltrim(to_char(sum(iitd.rate * (iitd.actual_quantity - iitd.lifo_quantity)), '9,999,99,99,990.99'::text))
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (129))
                THEN ltrim(to_char(sum(iitd.rate * iitd.actual_quantity) / sum(iitd.actual_quantity) * vw.available_stock, '9,999,99,99,990.99'::text))
            ELSE '0'::text
        END AS str_total_amount,
        CASE
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (127))
                THEN sum(iitd.rate * (iitd.actual_quantity - iitd.fifo_quantity))
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (128))
                THEN sum(iitd.rate * (iitd.actual_quantity - iitd.lifo_quantity))
            WHEN item.valuation_type_id IN (SELECT id from system_entity WHERE reserved_id IN (129))
                THEN sum(iitd.rate * iitd.actual_quantity) / sum(iitd.actual_quantity) * vw.available_stock
            ELSE 0::numeric
        END AS total_amount, se.key AS valuation_type, se.id AS valuation_type_id
    FROM inv_inventory_transaction_details iitd
    LEFT JOIN item ON item.id = iitd.item_id
    LEFT JOIN inv_inventory inv ON inv.id = iitd.inventory_id
    LEFT JOIN system_entity se ON se.id = item.valuation_type_id
    LEFT JOIN vw_inv_inventory_stock vw ON vw.inventory_id = iitd.inventory_id AND vw.item_id = iitd.item_id
    WHERE iitd.is_increase = true AND iitd.approved_by > 0
    GROUP BY iitd.inventory_id, inv.name, item.id, item.name, item.valuation_type_id, vw.available_stock, se.key, se.id, item.unit
    ORDER BY iitd.inventory_id, item.name;
    """

    public void createDefaultSchema() {
        executeSql(INV_CONSUMABLE_STOCK_MODEL)
        executeSql(INV_STOCK)
        executeSql(INV_TRANSACTION_WITH_DETAILS)
        executeSql(INV_VALUATION)
    }
}
