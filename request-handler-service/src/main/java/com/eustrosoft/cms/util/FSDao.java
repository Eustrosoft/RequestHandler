package com.eustrosoft.cms.util;

import com.eustrosoft.cms.dbdatasource.ranges.FileType;
import com.eustrosoft.core.db.ExecStatus;
import com.eustrosoft.core.db.Query;
import com.eustrosoft.core.db.dao.BasicDAO;
import com.eustrosoft.core.model.FDir;
import com.eustrosoft.core.model.FFile;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import static com.eustrosoft.cms.util.DBStatements.getBlobDetails;
import static com.eustrosoft.cms.util.DBStatements.getBlobLength;
import static com.eustrosoft.core.db.util.DBUtils.setByteaOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setIntOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setLongOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setNull;
import static com.eustrosoft.core.db.util.DBUtils.setStringOrNull;

public final class FSDao extends BasicDAO {

    public FSDao(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    public Long getFileLength(Long zoid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement blobLengthPS = getBlobLength(connection, zoid);
        try {
            ResultSet resultSet = blobLengthPS.executeQuery();
            if (resultSet != null) {
                resultSet.next();
                return resultSet.getLong("sum");
            }
        } finally {
            blobLengthPS.close();
        }
        return -1L;
    }

    public InputStream getFileInputStream(String zoid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement blobDetailsPS = getBlobDetails(connection, zoid);
        try {
            ResultSet resultSet = blobDetailsPS.executeQuery();
            Vector<InputStream> streams = new Vector<>();
            while (resultSet.next()) {
                streams.add(resultSet.getBinaryStream("chunk"));
            }
            return new SequenceInputStream(streams.elements());
        } finally {
            blobDetailsPS.close();
        }
    }


    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name, String mimeType) throws SQLException {
        return createFFile(objectZoid, objectVer, parentVer, type, name, mimeType,null, null);
    }

    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name, String mimeType,
                                  Integer securityLevel, String description) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement
                = connection.prepareStatement("SELECT FS.create_FFile(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        setLongOrNull(statement, 1, Long.parseLong(objectZoid));
        setLongOrNull(statement, 2, Long.parseLong(objectVer));
        setLongOrNull(statement, 3, parentVer == null ? null : Long.parseLong(parentVer));
        setStringOrNull(statement, 4, name);
        setStringOrNull(statement, 5, type.getValue());
        setStringOrNull(statement, 6, "N");
        setStringOrNull(statement, 7, mimeType);
        setStringOrNull(statement, 8, description);
        setNull(statement, 9, Types.BIGINT);
        setNull(statement, 10, Types.BIGINT);
        setNull(statement, 11, Types.VARCHAR);
        setNull(statement, 12, Types.VARCHAR);
        setNull(statement, 13, Types.BIGINT);
        setNull(statement, 14, Types.BIGINT);
        setNull(statement, 15, Types.VARCHAR);
        setNull(statement, 16, Types.VARCHAR);
        ExecStatus status = new ExecStatus();
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            status.fillFromResultSet(resultSet);
            statement.close();
            resultSet.close();
        }
        return status;
    }


    public ExecStatus createFDir(Long objectZoid, Long objectVer, Long parentZrid,
                                 Long fileZoid, String name, String description) throws SQLException {
        return createFDir(objectZoid, objectVer, parentZrid, fileZoid, name, null, description);
    }

    public ExecStatus createFDir(Long objectZoid, Long objectVer, Long parentZrid,
                                 Long fileZoid, String name, String mimetype, String description) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement
                = connection.prepareStatement("SELECT FS.create_FDir(?, ?, ?, ?, ?, ?, ?)");
        setLongOrNull(statement, 1, objectZoid);
        setLongOrNull(statement, 2, objectVer);
        setLongOrNull(statement, 3, 1L);
        setLongOrNull(statement, 4, fileZoid);
        setStringOrNull(statement, 5, name);
        setStringOrNull(statement, 6, mimetype);
        setStringOrNull(statement, 7, description);
        ExecStatus status = new ExecStatus();
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            status.fillFromResultSet(resultSet);
            statement.close();
            resultSet.close();
        }
        return status;
    }

    public ExecStatus createFBlob(String zoid, String zver, String zpid,
                                  String hex, String chunk, String chunkSize, String crc32) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement = connection.prepareStatement("SELECT FS.create_FBlob(?, ?, ?, ?, ?, ?, ?)");
        setLongOrNull(statement, 1, Long.parseLong(zoid));
        setLongOrNull(statement, 2, Long.parseLong(zver));
        setLongOrNull(statement, 3, Long.parseLong(zpid));
        setByteaOrNull(statement, 4, hex);
        setLongOrNull(statement, 5, Long.parseLong(chunk));
        setLongOrNull(statement, 6, Long.parseLong(chunkSize));
        setIntOrNull(statement, 7, Integer.parseInt(crc32.substring(3), 16));
        ExecStatus status = new ExecStatus();
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            status.fillFromResultSet(resultSet);
            statement.close();
            resultSet.close();
        }
        return status;
    }

    public ExecStatus deleteFDir(Long zoid, Long zrid, Long zver) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT FS.delete_FDir(?, ?, ?)");
        setLongOrNull(preparedStatement, 1, zoid);
        setLongOrNull(preparedStatement, 2, zver);
        setLongOrNull(preparedStatement, 3, zrid);
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    public ResultSet getDirectoryByNameAndId(Long dirId, String dirName) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM FS.V_FDir WHERE ZOID = ? AND fname = ?"
        );
        preparedStatement.setLong(1, dirId);
        preparedStatement.setString(2, dirName);
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    public void renameFile(Long zoid, String name, String targetName) throws Exception {
        FDir fDir = getFDir(zoid);
        if (fDir == null) {
            throw new Exception("FDir was null while renaming");
        }
        fDir.setFileName(targetName);
        updateFDir(fDir);
    }

    public FFile getFFile(Long zoid, String name) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM FS.V_FFile WHERE zoid = ? AND name = ?");
        setLongOrNull(statement, 1, zoid);
        setStringOrNull(statement, 2, name);
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            try {
                resultSet.next();
                return new FFile(resultSet);
            } finally {
                statement.close();
                resultSet.close();
            }
        }
        return null;
    }

    public FDir getFDirByFileId(Long fileId, String name) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM FS.V_FDir WHERE f_id = ? AND fname = ?");
        setLongOrNull(statement, 1, fileId);
        setStringOrNull(statement, 2, name);
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            try {
                boolean next = resultSet.next();
                return new FDir(resultSet);
            } finally {
                statement.close();
                resultSet.close();
            }
        }
        return null;
    }


    public FDir getFDir(Long zoid) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM FS.V_FDir WHERE zoid = ?");
        setLongOrNull(statement, 1, zoid);
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            try {
                resultSet.next();
                return new FDir(resultSet);
            } finally {
                statement.close();
                resultSet.close();
            }
        }
        return null;
    }

    public FDir getFDir(Long fId, String name) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM FS.V_FDir WHERE f_id = ? AND fname = ?");
        setLongOrNull(statement, 1, fId);
        setStringOrNull(statement, 2, name);
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery();
            try {
                resultSet.next();
                return new FDir(resultSet);
            } finally {
                statement.close();
                resultSet.close();
            }
        }
        return null;
    }

    public void updateFDir(FDir fDir) throws Exception {
        ExecStatus status = null;
        ExecStatus fDirOpen = null;
        try {
            if (fDir == null) {
                throw new Exception("FDir is null while updating.");
            }
            status = openObject("FS.F", fDir.getFileId());
            fDirOpen = openObject("FS.F", fDir.getZoid());
            fDir.setZver(fDirOpen.getZver());
            if (status.isOk() && fDirOpen.isOk()) {
                Connection connection = getPoolConnection().get();
                PreparedStatement statement = fDir.toUpdatePrepareStatement(connection);
                ExecStatus updatedStatus = new ExecStatus();
                if (statement != null) {
                    ResultSet resultSet = statement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    statement.close();
                    resultSet.close();
                }
            }
            ExecStatus execStatus = commitObject("FS.F", status.getZoid(), status.getZver());
            ExecStatus execStatus1 = commitObject("FS.F", fDirOpen.getZoid(), fDirOpen.getZver());
            System.out.println();
        } catch (Exception exception) {
            try {
                rollbackObject("FS.F", status.getZoid(), status.getZver());
            } catch (Exception ex) {}
            try {
                rollbackObject("FS.F", fDirOpen.getZoid(), fDirOpen.getZver());
            } catch (Exception ex) {}
            throw new IllegalArgumentException("Exception occurred while updating");
        }
    }

    public void updateFFile(FFile fFile) throws Exception {
        ExecStatus status = null;
        try {
            if (fFile == null) {
                throw new Exception("FFile is null while updating.");
            }
            status = openObject("FS.F", fFile.getZoid());
            fFile.setZver(status.getZver());
            if (status.isOk()) {
                Connection connection = getPoolConnection().get();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("FS.update_FFile")
                                .leftBracket()
                                .add(fFile.toUpdateString())
                                .rightBracket()
                                .buildWithSemicolon()
                                .toString()
                );
                ExecStatus updatedStatus = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
            }
        } finally {
            if (status != null) {
                commitObject("FS.F", status.getZoid(), status.getZver());
            }
        }
    }
}
