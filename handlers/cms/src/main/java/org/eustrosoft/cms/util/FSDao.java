package org.eustrosoft.cms.util;

import lombok.SneakyThrows;
import org.eustrosoft.cms.dbdatasource.ranges.FileType;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.core.db.Query;
import org.eustrosoft.core.db.dao.BasicDAO;
import org.eustrosoft.core.model.FDir;
import org.eustrosoft.core.model.FFile;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import static org.eustrosoft.cms.util.DBStatements.getBlobDetails;
import static org.eustrosoft.cms.util.DBStatements.getBlobLength;
import static org.eustrosoft.core.db.util.DBUtils.setLongOrNull;

public final class FSDao extends BasicDAO {

    public FSDao(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    @SneakyThrows
    public Long getFileLength(Long zoid) {
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

    @SneakyThrows
    public InputStream getFileInputStream(String zoid) {
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


    @SneakyThrows
    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FFile")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, '%s', '%s', '%s', null, null, null, null, null, null, null, null, null, null",
                                objectZoid,
                                objectVer,
                                parentVer,
                                name,
                                type.getValue(),
                                "N" // TODO
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name, String securityLevel, String description) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FFile")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, '%s', '%s', '%s', null, null, null, null, null, null, null, null, null, null",
                                objectZoid,
                                objectVer,
                                parentVer,
                                name,
                                type.getValue(),
                                "N" // TODO
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }


    @SneakyThrows
    public ExecStatus createFDir(Long objectZoid, Long objectVer, Long parentZrid,
                                 Long fileZoid, String name, String description) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FDir")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, %s, '%s', null, '%s'",
                                objectZoid,
                                objectVer,
                                "1", // TODO
                                fileZoid,
                                name,
                                description
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFBlob(String zoid, String zver, String zpid,
                                  String hex, String chunk, String shunkSize, String crc32) {
        Connection connection = getPoolConnection().get();
        String query = Query.builder()
                .select()
                .add("FS.create_FBlob")
                .leftBracket()
                .add(String.format(
                        "%s, %s, %s, '\\x%s', %s, %s, %s",
                        zoid,
                        zver,
                        zpid,
                        hex,
                        chunk,
                        shunkSize,
                        Integer.parseInt(crc32.substring(3), 16)
                ))
                .rightBracket()
                .buildWithSemicolon()
                .toString();
        Statement statement = connection.createStatement();
        ExecStatus status = new ExecStatus();
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery(query);
            status.fillFromResultSet(resultSet);
            statement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus deleteFDir(Long zoid, Long zrid, Long zver) {
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

    @SneakyThrows
    public ResultSet getDirectoryByNameAndId(Long dirId, String dirName) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = %s", DBConstants.ZOID, dirId, DBConstants.FILE_ID, dirName))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    @SneakyThrows
    public void renameFile(Long zoid, String name, String targetName) {
        FDir fDir = getFDir(zoid, name);
        FFile fFile = getFFile(zoid, name);
        if (fDir == null) {
            throw new Exception("FDir was null while renaming");
        }
        fDir.setFileName(targetName);
        updateFDir(fDir);
        if (fFile == null) {
            throw new Exception("FFile was null while renaming");
        }
        fFile = getFFile(zoid, name);
        fFile.setFileName(targetName);
        updateFFile(fFile);
    }

    @SneakyThrows
    public FFile getFFile(Long zoid, String name) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FFile")
                        .where(String.format("%s = %s and %s = '%s'", DBConstants.ZOID, zoid, DBConstants.NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                resultSet.next();
                return new FFile(resultSet);
            } finally {
                preparedStatement.close();
                resultSet.close();
            }
        }
        return null;
    }

    @SneakyThrows
    public FDir getFDir(Long zoid, String name) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = '%s'", DBConstants.FILE_ID, zoid, DBConstants.F_NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                boolean next = resultSet.next();
                return new FDir(resultSet);
            } finally {
                preparedStatement.close();
                resultSet.close();
            }
        }
        return null;
    }

    @SneakyThrows
    public void updateFDir(FDir fDir) {
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
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("FS.update_FDir")
                                .leftBracket()
                                .add(fDir.toUpdateString())
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
            if (fDirOpen != null) {
                commitObject("FS.F", fDirOpen.getZoid(), fDirOpen.getZver());
            }
        }
    }

    @SneakyThrows
    public void updateFFile(FFile fFile) {
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