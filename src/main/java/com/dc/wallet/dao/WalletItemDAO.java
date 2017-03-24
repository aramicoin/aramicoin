package com.dc.wallet.dao;

import com.dc.core.base.H2BasicDao;
import com.dc.core.base.ModelSqlVo;
import com.dc.core.global.DCCoreConfig;
import com.dc.core.global.H2Config;
import com.dc.wallet.bean.WalletItem;
import com.google.common.collect.Lists;
import com.ms.libs.log.Log;
import com.ms.libs.log.Logger;
import com.ms.libs.util.DBUtil;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.List;


public class WalletItemDAO extends H2BasicDao<WalletItem> {

    public static final String MODEL_CLAZZ_NAME = WalletItem.class.getSimpleName();


    private static Log log = Logger.getLog(DCCoreConfig.LogName);


    private static boolean HasCheckCreateTable = false;

    private String db = H2Config.dbName;

    public WalletItemDAO() {
        init();
    }

    public static WalletItemDAO me() {
        return new WalletItemDAO();
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
        sqlBuilder.append("filePath VARCHAR(1000)").append(",");
        sqlBuilder.append("addTime DATE").append(")");

        String sql = sqlBuilder.toString();
        String tableName = getTableName();

        if (!HasCheckCreateTable) {
            HasCheckCreateTable = true;


            HasCheckCreateTable = execute(String.format(sql, tableName));
        }
    }

    @Override
    public WalletItem resultSetToModel(ResultSet rs) throws SQLException {


        String addressVal = rs.getString("Address");
        String labelVal = rs.getString("Label");
        String filePathVal = rs.getString("FilePath");
        Date addTimeVal = rs.getDate("AddTime");

        WalletItem walletItem = new WalletItem();
        walletItem.setAddress(addressVal);
        walletItem.setLabel(labelVal);
        walletItem.setFilePath(filePathVal);
        walletItem.setAddTime(addTimeVal);

        return walletItem;
    }


    public boolean add(WalletItem walletItem) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        ModelSqlVo fieldsText = walletItem.insertModelSqlVo();

        try {
            conn = getConnection();
            String sql = String.format("INSERT INTO %s(address,Label,FilePath,AddTime) VALUES(%s)", getTableName(), fieldsText.getFieldValueJoins());
            ps = conn.prepareStatement(sql);


            int i = 1;
            ps.setString(i++, walletItem.getAddress());
            ps.setString(i++, walletItem.getLabel());
            ps.setString(i++, walletItem.getFilePath());
            ps.setDate(i++, new java.sql.Date(walletItem.getAddTime().getTime()));

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean update(WalletItem walletItem) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        ModelSqlVo modelSqlVo = walletItem.updateFieldsText("peerName");

        try {
            conn = getConnection();
            String sql = String.format("update %s set Label=?,FilePath=?,AddTime=? where Address=?", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, walletItem.getLabel());
            ps.setString(i++, walletItem.getFilePath());
            ps.setDate(i++, new java.sql.Date(walletItem.getAddTime().getTime()));
            ps.setString(i++, walletItem.getAddress());

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


    public WalletItem findByPeerName(String peerName) {
        WalletItem walletItem = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where peerName = ?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setString(1, peerName);

            rs = ps.executeQuery();
            if (rs.next()) {
                walletItem = resultSetToModel(rs);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByPeerName"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return walletItem;
    }


    public List<WalletItem> findListByIp(String ip) {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where ip = ?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setString(1, ip);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByIp"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public WalletItem findOneByIp(String ip) {
        List<WalletItem> retVal = findListByIp(ip);
        if (retVal == null || retVal.size() == 0) {
            return null;
        } else {
            return retVal.get(0);
        }
    }


    public int countByState(int state) {
        int result = 0;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = String.format("select count(*) as total from %s where state = ?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setInt(1, state);

            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("total");
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "countByState"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return result;
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


    public int countByIp(String ip) {
        int result = 0;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = String.format("select count(*) as total from %s where ip = ?", getTableName());

            ps = conn.prepareStatement(sql);
            ps.setString(1, ip);

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


    public List<WalletItem> findListButLimit(int size) {
        List<WalletItem> retVal = Lists.newArrayList();

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
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListButLimit"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<WalletItem> findListByActiveTimeOrder(int size, String order) {
        List<WalletItem> retVal = Lists.newArrayList();

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
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeOrder"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<WalletItem> findListByActiveTimeOrder(int startIndex, int size, String order) {
        List<WalletItem> retVal = Lists.newArrayList();

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
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeOrder"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<WalletItem> findRandListByactiveTimeGe(int activeTime, int size) {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where activeTime>=? order by rand() limit 0,?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setInt(1, activeTime);
            ps.setInt(2, size);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeOrder"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<WalletItem> findListByActiveTimeGe(int activeTime, int size) {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where activeTime>=? order by activeTime asc limit 0,?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setInt(1, activeTime);
            ps.setInt(2, size);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeGe"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }

    public List<WalletItem> findAll() {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s", getTableName());
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
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


    public boolean updateIPPortByPeerName(String peerName, String ip, int port) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        try {
            conn = getConnection();
            String sql = String.format("update %s set port=?, ip=? where peerName=?", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setInt(i++, port);
            ps.setString(i++, ip);
            ps.setString(i++, peerName);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "updatePortByIp"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean updateActiveTimeByPeerName(String peerName, long activeTime) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        try {
            conn = getConnection();
            String sql = String.format("update %s set activeTime=? where peerName=?", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, activeTime);
            ps.setString(i++, peerName);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "updateActiveTimeByPeerName"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean connectErrorTotalAddOne(String ip) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        try {
            conn = getConnection();
            String sql = String.format("update %s set connectErrorTotal=connectErrorTotal+1 where ip=?", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, ip);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "connectErrorTotalAddOne"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public boolean connectErrorTotalRemoveOne(String ip) {
        int result = 0;

        PreparedStatement ps = null;
        Connection conn = null;

        try {
            conn = getConnection();
            String sql = String.format("update %s set connectErrorTotal=connectErrorTotal+1 where ip=? and connectErrorTotal>0", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, ip);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "connectErrorTotalRemoveOne"), e);
        } finally {
            DBUtil.close(ps);
            closeConn(conn);
        }

        return result > 0;
    }


    public List<WalletItem> findListByActiveTimeGt(long activeTime) {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where activeTime>?", getTableName());
            ps = conn.prepareStatement(sql);
            ps.setLong(1, activeTime);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeGt"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public List<WalletItem> findListByActiveTimeGtAndIpNeq(long activeTime, String ip) {
        List<WalletItem> retVal = Lists.newArrayList();

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s where activeTime>? and ip!=?", getTableName());
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, activeTime);
            ps.setString(i++, ip);

            rs = ps.executeQuery();
            while (rs.next()) {
                WalletItem walletItem = resultSetToModel(rs);
                retVal.add(walletItem);
            }

        } catch (SQLException e) {

            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findListByActiveTimeGtAndIpNeq"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


    public WalletItem findOneByLastActiveTime() {
        WalletItem retVal = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = String.format("select * from %s order by activeTime desc limit 0,1", getTableName());
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            if (rs.next()) {
                retVal = resultSetToModel(rs);
            }

        } catch (SQLException e) {
            log.error(String.format("Error:%s", MODEL_CLAZZ_NAME, "findOneByLastActiveTime"), e);
        } finally {
            DBUtil.close(rs, ps);
            closeConn(conn);
        }

        return retVal;
    }


}
