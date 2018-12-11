package ipead.com.br.newandroidbancodepreco.database;

/**
 * Created by daniel
 *
 */
public class UsuarioScriptBD {

    public static final String TABLE = "usuario";
    public static final String COLUMN_ID = "idUsuario";
    public static final String COLUMN_IDGRUPO = "idGrupoUsuario";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_SENHA = "senha";
    public static final String createUsuario =
        "CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_IDGRUPO + " INTEGER," +
            COLUMN_NOME + " VARCHAR(100)," +
            COLUMN_LOGIN + " VARCHAR(45)," +
            COLUMN_SENHA + " VARCHAR(255)" + ");";

}
