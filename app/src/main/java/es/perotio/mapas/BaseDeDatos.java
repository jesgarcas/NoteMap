package es.perotio.mapas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {

    public static String DB_PATH = "/data/data/es.perotio.mapas/databases/";
    public static String DB_NAME = "db_mapas";
    private final Context myContext;
    public static int v_db = 2;

    String sqlCreate = "CREATE TABLE lugares (_id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, lat DOUBLE, lon DOUBLE, descripcion TEXT, foto TEXT, fecha TEXT)";
    String sqlUpdate = "ALTER TABLE lugares ADD COLUMN fecha TEXT;";

    public BaseDeDatos(Context contexto, String nombre, CursorFactory factory,
                       int version) {

        super(contexto, nombre, factory, version);
        this.myContext = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db.isReadOnly()) {
            db = getWritableDatabase();
        }
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Cuando haya cambios en la estructura deberemos
        // incluir el código
        // SQL necesario para actualizar la base de datos
        if (newVersion > oldVersion) {
            db.execSQL(sqlUpdate);
        }
    }

}
