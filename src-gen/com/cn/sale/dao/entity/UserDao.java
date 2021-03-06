package com.cn.sale.dao.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.cn.sale.dao.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "user".
*/
public class UserDao extends AbstractDao<User, String> {

    public static final String TABLENAME = "user";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Login_id = new Property(0, String.class, "login_id", true, "LOGIN_ID");
        public final static Property Nick_name = new Property(1, String.class, "nick_name", false, "NICK_NAME");
        public final static Property Password = new Property(2, String.class, "password", false, "PASSWORD");
        public final static Property Ticket = new Property(3, String.class, "ticket", false, "TICKET");
        public final static Property Token = new Property(4, String.class, "token", false, "TOKEN");
        public final static Property Group_id = new Property(5, String.class, "group_id", false, "GROUP_ID");
        public final static Property Portrait = new Property(6, String.class, "portrait", false, "PORTRAIT");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"user\" (" + //
                "\"LOGIN_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: login_id
                "\"NICK_NAME\" TEXT UNIQUE ," + // 1: nick_name
                "\"PASSWORD\" TEXT NOT NULL ," + // 2: password
                "\"TICKET\" TEXT," + // 3: ticket
                "\"TOKEN\" TEXT," + // 4: token
                "\"GROUP_ID\" TEXT," + // 5: group_id
                "\"PORTRAIT\" TEXT);"); // 6: portrait
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"user\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        String login_id = entity.getLogin_id();
        if (login_id != null) {
            stmt.bindString(1, login_id);
        }
 
        String nick_name = entity.getNick_name();
        if (nick_name != null) {
            stmt.bindString(2, nick_name);
        }
        stmt.bindString(3, entity.getPassword());
 
        String ticket = entity.getTicket();
        if (ticket != null) {
            stmt.bindString(4, ticket);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(5, token);
        }
 
        String group_id = entity.getGroup_id();
        if (group_id != null) {
            stmt.bindString(6, group_id);
        }
 
        String portrait = entity.getPortrait();
        if (portrait != null) {
            stmt.bindString(7, portrait);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // login_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nick_name
            cursor.getString(offset + 2), // password
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ticket
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // token
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // group_id
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // portrait
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setLogin_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNick_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPassword(cursor.getString(offset + 2));
        entity.setTicket(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setToken(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGroup_id(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPortrait(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(User entity, long rowId) {
        return entity.getLogin_id();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(User entity) {
        if(entity != null) {
            return entity.getLogin_id();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
