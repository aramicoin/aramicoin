package com.dc.wallet.dao;

import com.dc.core.base.H2BasicDao;
import com.dc.core.base.ModelSqlVo;
import com.dc.core.global.DCCoreConfig;
import com.dc.core.global.H2Config;
import com.dc.wallet.bean.AddressBook;
import com.google.common.collect.Lists;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;
import com.ms.libs.util.DBUtil;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.List;


public class AddressBookDAO extends H2BasicDao<AddressBook> {

    public static final String MODEL_CLAZZ_NAME = AddressBook.class.getSimpleName();


    private static Log log = Logger.getLog(DCCoreConfig.LogName);


    private static boolean HasCheckCreateTable = false;

    private String db = H2Config.dbName;

    public AddressBookDAO() {
        init();
    }

    public static AddressBookDAO me() {
        return new AddressBookDAO();
    }

    @Override
    public String getTableName() {
        return MODEL_CLAZZ_NAME;
    }

    @Override
    public String getDbName() {
        return this.db;
    }

    @Override
    public JdbcConnectionPool getConnectionPool() {
        return H2Config.connPool;
    }

    private void init() {

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("CREATE TABLE IF NOT EXISTS %s(");
        sqlBuilder.append("address VARCHAR(50) PRIMARY KEY").append(",");
        sqlBuilder.append("label VARCHAR(200)").append(",");
        sqlBuilder.append("addTime DATE").append(")");

        String sql = sqlBuilder.toString();
        String tableName = getTableName();

        if (!HasCheckCreateTable) {
            HasCheckCreateTable = true;


            HasCheckCreateTable = execute(String.format(sql, tableName));
        }
    }

    @Override
    public AddressBook resultSetToModel(ResultSet rs) throws SQLException {


        String addressVal = rs.getString("Address");
        String labelVal = rs.getString("Label");
        Date addTimeVal = rs.getDate("AddTime");

        AddressBook addressBook = new AddressBook();
        addressBook.setAddress(addressVal);
        addressBook.setLabel(labelVal);
        addressBook.setAddTime(addTimeVal);

        return addressBook;
    }


    public boolean add(AddressBook addressBook) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        ModelSqlVo fieldsText = addressBook.insertModelSqlVo();

        try {
            conn = getConnection();
            String sql = String.format("INSERT INTO %s(address,label,addTime) VALUES(%s)", getTableName(), fieldsText.getFieldValueJoins());
            ps = conn.prepareStatement(sql);


            int i = 1;
            ps.setString(i++, addressBook.getAddress());
            ps.setString(i++, addressBook.getLabel());
            ps.setDate(i++, new Date(addressBook.getAddTime().getTime()));

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean update(AddressBook addressBook) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        ModelSqlVo modelSqlVo = addressBook.updateFieldsText("peerName");

        try {
            conn = getConnection();
            String sql = String.format("update %s set Address=?,Label=?,AddTime=? where %s", getTableName(), modelSqlVo.getFieldValueJoins());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, addressBook.getAddress());
            ps.setString(i++, addressBook.getLabel());
            ps.setDate(i++, new Date(addressBook.getAddTime().getTime()));


            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "update"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean deleteByPeerName(String PeerName) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("delete from %s where PeerName=?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setString(1, PeerName);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "deleteByPeerName"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean deleteByAddress(String address) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("delete from %s where address=?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setString(1, address);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "deleteByAddress"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean isExistByAddress(String address) {
        boolean result = false;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = String.format("select count(*) as total from %s where address = ?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setString(1, address);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "exist"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return result;
    }


    public boolean truncateTable() {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("TRUNCATE TABLE %s", getTableName());
            ps = conn.prepareStatement(sql);
            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error:%s", e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public List<AddressBook> findListButLimit(int size) {
        List<AddressBook> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s limit 0,?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setInt(1, size);

            rs = ps.executeQuery();
            while (rs.next()) {
                AddressBook addressBook = resultSetToModel(rs);
                retVal.add(addressBook);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListButLimit"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<AddressBook> findListByActiveTimeOrder(int size, String order) {
        List<AddressBook> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s order by activeTime %s limit 0,?", getTableName(), order);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, size);

            rs = ps.executeQuery();
            while (rs.next()) {
                AddressBook addressBook = resultSetToModel(rs);
                retVal.add(addressBook);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeOrder"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<AddressBook> findListByActiveTimeOrder(int startIndex, int size, String order) {
        List<AddressBook> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s order by activeTime %s limit ?,?", getTableName(), order);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, startIndex);
            ps.setInt(2, size);
            rs = ps.executeQuery();
            while (rs.next()) {
                AddressBook addressBook = resultSetToModel(rs);
                retVal.add(addressBook);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeOrder"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }

    public List<AddressBook> findAll() {
        List<AddressBook> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s", getTableName());
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                AddressBook addressBook = resultSetToModel(rs);
                retVal.add(addressBook);
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findAll"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<String> findAllAddress() {
        List<String> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select address from %s", getTableName());
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                String address = rs.getString("address");
                retVal.add(address);
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findAllAddress"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public int countByLabelAndAddress(String labelVal, String addressVal) {
        int result = 0;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = String.format("select count(*) as total from %s where label = ? and address = ?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setString(1, labelVal);
            ps.setString(2, addressVal);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("total");
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "countByIp"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return result;
    }


}
