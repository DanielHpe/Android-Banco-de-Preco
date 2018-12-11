package ipead.com.br.newandroidbancodepreco.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

/**
 * Created by daniel
 *
 */
public class MultiBD extends ConfiguracaoScriptBD {

    public static final String BD_INFORMANTE = "inf_coleta";
    public SQLiteDatabase database;
    public String path;

    public MultiBD(String filePath) {
        this.path = filePath;
        File dbFile = new File(path);

        if(dbFile.exists())
            database = SQLiteDatabase.openDatabase(path, null,  SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        else
            database = SQLiteDatabase.openOrCreateDatabase(path, null);
    }

    //CREATE TABLES
    public void createTableGrupo() {
        database.execSQL(createGrupo);
    }

    public void createTableMarca(){
        database.execSQL(createMarca);
    }

    public void createTableProduto(){
        database.execSQL(createProduto);
    }

    public void createTableColeta(){
        database.execSQL(createColeta);
    }

    public long insert(String tableName, ContentValues values) {

        long _id = database.insert(tableName, null, values);

        if(_id >= 0)
            Log.d("Inserido com sucesso", String.valueOf(_id));

        return _id;
    }

    /**
     *
     * @param tableName
     * @param values
     * @param where
     * @param whereArgs
     * @return
     */
    public int update(String tableName, ContentValues values, String where,
                      String[] whereArgs) {
        int _numRowAffected = this.database.update(tableName, values, where, whereArgs);
        return _numRowAffected;
    }

    public void delete(String tableName){

        this.database.execSQL("UPDATE  " + tableName + " SET preco = NULL");

    }

    public void execSQL(String sql){

        try{
            database.execSQL(sql);
//			Log.i("execSQL", sql);
        }
        catch(Exception e){
			Log.e("MultiBD", "Erro execSQL msg: " + e.getMessage() + "  cause: " + e.getCause());
        }
    }

    public Cursor query(String sql, String[] selectionArgs) {
        Cursor c = null;
        try{

            c = database.rawQuery(sql, selectionArgs);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return c;
    }

//    public Cursor query(String tableName, String[] columns, String where,
//                        String[] whereArgs, String groupby, String having, String order,
//                        String limit) {
//        Cursor c = database.query(true, tableName, columns, where, whereArgs, groupby, having, order, limit);
//
//        return c;
//    }

    public void beginTransaction() {
        try {
            database.beginTransaction();
        } catch (Exception e) {
            //log.gravaErro(e);
        }
    }

    public void endTransaction() {
        try {
            database.endTransaction();
        } catch (Exception e) {
            //log.gravaErro(e);
        }
    }

    public void setTransactionSuccessful() {
        try {
            database.setTransactionSuccessful();
        } catch (Exception e) {
            //log.gravaErro(e);
        }
    }

    public void close() {
        if(database != null)
            database.close();
    }
}
