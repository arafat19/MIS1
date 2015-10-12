package com.athena.mis.document.utility;

import com.athena.mis.document.entity.DocDbInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mirza-Ehsan on 6/30/14.
 */
public class DocJdbcConnection {
    Connection con = null;
    DocDbInstance docDbInstance = null;

    public DocJdbcConnection(DocDbInstance dbInstance) {
        try {
            docDbInstance = dbInstance;
            Class.forName(docDbInstance.getDriver());
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
    }

    private void createConnection() {
        try {
            con = DriverManager.getConnection(docDbInstance.getConnectionString());
            System.out.println("Connection created successfully........");
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    private void closeConnection() {
        try {
            this.con.close();
            System.out.println("Connection closed Successfully.........");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public List runQuery() {
        try {
            createConnection();
            Statement sta = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = sta.executeQuery(docDbInstance.getSqlQuery());
            List lst = convertResultSetToArrayList(rs);
            sta.close();
            closeConnection();
            return lst;
        } catch (SQLException e) {
            System.out.println(e);
            closeConnection();
            return null;
        }
    }

    private List convertResultSetToArrayList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap> list = new ArrayList<HashMap>();
        while (rs.next()) {
            HashMap<String, Object> row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

}
