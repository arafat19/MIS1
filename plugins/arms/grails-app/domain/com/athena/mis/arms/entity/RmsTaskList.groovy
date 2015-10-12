package com.athena.mis.arms.entity

/**
 * Module  - ARMS
 * Purpose - Entity that contains all properties of TaskList
 */
class RmsTaskList {

    long id                         // primary key (Auto generated by its own sequence)
    int version                     // object version persisted in DB

    String name                     // Name of task list
    long exchangeHouseId            // RmsExchangeHouse.id

    long createdBy                  // AppUser.id
    Date createdOn                  // Object creation DateTime
    long companyId                  // company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'rms_task_list_id_seq']
        exchangeHouseId index: 'rms_task_list_exchange_house_id_idx'
        companyId index: 'rms_task_list_company_id_idx'
        createdBy index: 'rms_task_list_created_by_id_idx'
        name unique: 'companyId'
    }

    static constraints = {
    }
}
