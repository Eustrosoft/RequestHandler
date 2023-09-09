/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dbdatasource.core;

import com.eustrosoft.cms.dbdatasource.queries.Query;
import com.eustrosoft.cms.dbdatasource.ranges.FileType;
import com.eustrosoft.core.model.*;
import com.eustrosoft.core.model.ranges.MSGPartyRole;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import static com.eustrosoft.core.constants.DBConstants.*;

public final class DBFunctions {
    private final QDBPConnection poolConnection;

    public DBFunctions(QDBPConnection poolConnection) {
        this.poolConnection = poolConnection;
    }

    @SneakyThrows
    public ResultSet selectObject(Long zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("TIS.V_ZObject")
                        .where(String.format("%s = %s", ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    @SneakyThrows
    public Long getFileLength(Long zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement blobLengthPS = DBStatements.getBlobLength(connection, zoid);
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
        Connection connection = poolConnection.get();
        PreparedStatement blobDetailsPS = DBStatements.getBlobDetails(connection, zoid);
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
    public ExecStatus openObject(String type, Long zoid) {
        return openObject(type, zoid, "null");
    }

    @SneakyThrows
    public ExecStatus openObject(String type, Long zoid, String zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.open_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                type, zoid, zver
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
    public ExecStatus createObjectInScope(String type, String scopeZoid) {
        return createObjectInScope(type, scopeZoid, "null");
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, String scopeZoid, String zlvl) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.create_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s::smallint",
                                type, scopeZoid, zlvl
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
                                  FileType type, String name) {
        Connection connection = poolConnection.get();
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
        Connection connection = poolConnection.get();
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
    public ExecStatus commitObject(String type, Long objectZoid, Long objectVer) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.commit_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                type,
                                objectZoid,
                                objectVer
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
        Connection connection = poolConnection.get();
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
                                  String hex, String chunk, String allChunks, String crc32) {
        Connection connection = poolConnection.get();
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
                        allChunks,
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
    public ExecStatus deleteFDir(String zoid, String zrid, String zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.delete_FDir")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s",
                                zoid, zver, zrid
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
    public ResultSet getDirectoryByNameAndId(Long dirId, String dirName) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = %s", ZOID, dirId, FILE_ID, dirName))
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
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FFile")
                        .where(String.format("%s = %s and %s = '%s'", ZOID, zoid, NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
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
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = '%s'", FILE_ID, zoid, F_NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
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
                Connection connection = poolConnection.get();
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
                Connection connection = poolConnection.get();
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

    @SneakyThrows
    public ResultSet getChats() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("MSG.V_CChannel")
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public ExecStatus createChat(MSGChannel channel, Integer slvl) {
        // ??? todo zsid
        ExecStatus objectInScope = createObjectInScope("MSG.C", getUserSid(), slvl == null ? "null" : slvl.toString());
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("MSG.create_cchannel")
                        .leftBracket()
                        .add(String.format(
                                //v_zoid bigint, v_zver bigint, v_zpid bigint, v_subject character varying, v_status character, v_obj_id bigint
                                "%s, %s, %s, '%s', '%s', %s",
                                objectInScope.getZoid(),
                                objectInScope.getZver(),
                                null,
                                channel.getSubject(),
                                channel.getStatus().getValue(),
                                channel.getDocumentId()
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
        if (status.isOk()) {
            createCParty(
                    objectInScope.getZoid().toString(),
                    status.getZver().toString(),
                    new MSGParty(getUserId(), MSGPartyRole.CREATOR, null)
            ); // todo: maybe create a message for creator
        }
        commitObject(
                "MSG.C",
                objectInScope.getZoid(),
                status.getZver()
        );
        return status;
    }

    @SneakyThrows
    public ResultSet getMessages(Long channelId) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .leftBracket()
                        .add("cm.zoid, cm.zver, cm.zlvl, cm.content, cm.msg_id, cm.type, tz.zuid")
                        .rightBracket()
                        .from()
                        .add("MSG.v_cchannel as cc")
                        .add("right outer join")
                        .add("MSG.v_cmsg as cm")
                        .on()
                        .leftBracket()
                        .add("cc.zoid = cm.zoid")
                        .rightBracket()
                        .add("left outer join tis.vh_zobject as tz")
                        .on()
                        .leftBracket()
                        .add("cm.zver = tz.zver")
                        .rightBracket()
                        .where(
                                Query.builder()
                                        .add(channelId)
                                        .eq()
                                        .add("tz.zoid")
                                        .build()
                        )
                        .buildWithSemicolon()
                        .getQuery().toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public ExecStatus createMessage(Long chatId, MSGMessage message) {
        ExecStatus openedChat = openObject("MSG.C", chatId);
        try {
            if (openedChat.isOk()) {
                Connection connection = poolConnection.get();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("MSG.Create_cmsg")
                                .leftBracket()
                                .add(String.format(
                                        //v_zoid bigint, v_zver bigint, v_zpid bigint, v_content character varying, v_msg_id bigint, v_type character varying
                                        "%s, %s, %s, '%s', %s, '%s'",
                                        openedChat.getZoid(),
                                        openedChat.getZver(),
                                        1, // todo
                                        message.getContent(),
                                        message.getAnswerId(),
                                        message.getType().getValue()
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
            return new ExecStatus();
        } finally {
            commitObject("MSG.C", openedChat.getZoid(), openedChat.getZver());
        }
    }

    @SneakyThrows
    private ExecStatus createCParty(String chatId, String chatVer, MSGParty party) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("MSG.create_cparty")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, %s, '%s', %s",
                                chatId,
                                chatVer,
                                1, // todo
                                getUserId(),
                                party.getRole().getValue(),
                                null
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
    public String getUserSid() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_sid");
        String userLogin = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLogin = resultSet.getString("get_user_sid");
            preparedStatement.close();
            resultSet.close();
        }
        return userLogin;
    }

    @SneakyThrows
    public Long getUserId() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user");
        Long userId = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userId = resultSet.getLong("get_user");
            preparedStatement.close();
            resultSet.close();
        }
        return userId;
    }

    @SneakyThrows
    public ResultSet getUserResultSetById(Long id) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("SAM.V_User")
                        .where(String.format("id = %s", id))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public String getUserLogin() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_login");
        String userLogin = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLogin = resultSet.getString("get_user_login");
            preparedStatement.close();
            resultSet.close();
        }
        return userLogin;
    }

    @SneakyThrows
    public String getUserLang() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_lang");
        String userLang = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLang = resultSet.getString("get_user_lang");
            preparedStatement.close();
            resultSet.close();
        }
        return userLang;
    }

    @SneakyThrows
    public Integer getUserSLvl() {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel");
        Integer userSlvl = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userSlvl = resultSet.getInt("get_user_slevel");
            preparedStatement.close();
            resultSet.close();
        }
        return userSlvl;
    }

    @SneakyThrows
    public int[] getUserAvailableSlvl() {
        Connection connection = poolConnection.get();
        int[] slvls = new int[2];
        PreparedStatement minPrep = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel_min");
        if (minPrep != null) {
            ResultSet resultSet = minPrep.executeQuery();
            resultSet.next();
            slvls[0] = resultSet.getInt("get_user_slevel_min");
            minPrep.close();
            resultSet.close();
        }
        PreparedStatement maxPrep = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel_max");
        if (maxPrep != null) {
            ResultSet resultSet = maxPrep.executeQuery();
            resultSet.next();
            slvls[1] = resultSet.getInt("get_user_slevel_max");
            maxPrep.close();
            resultSet.close();
        }
        return slvls;
    }
}
