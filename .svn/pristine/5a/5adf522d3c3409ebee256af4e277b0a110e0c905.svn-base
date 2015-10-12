package com.athena.mis.integration.application

import com.athena.mis.BaseService

class AppSchemaUpdateBootStrapService extends BaseService{

    public void init() {
        createSequenceForSystemEntity()
    }

    private static final String SQL_SEQ_SYSTEM_ENTITY= """
        CREATE SEQUENCE system_entity_id_seq
          INCREMENT 1
          MINVALUE 1
          MAXVALUE 9223372036854775807
          START 1
          CACHE 1;
        """
    private void createSequenceForSystemEntity(){
        executeSql(SQL_SEQ_SYSTEM_ENTITY)
    }
}
