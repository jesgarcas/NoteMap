package es.perotio.mapas;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class ListaLugaresActivity extends ListActivity {

	public ItemListAdapter adapter;
	Cursor cursor;
	BaseDeDatos db1;
	SQLiteDatabase db;

	public static int PETICION_LISTA = 1;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lista);

		// Cargamos la lista
		Cargar_Lista();
	}

	//Cargador de lista
	protected void Cargar_Lista() {

		db1 = new BaseDeDatos(ListaLugaresActivity.this, "db_mapas", null,
				BaseDeDatos.v_db);
		db = db1.getWritableDatabase();

		cursor = db
				.rawQuery(
						" SELECT lat, lon, nombre, descripcion, foto, _id, fecha FROM lugares ORDER BY nombre, fecha DESC",
						null);

		// Indicamos al adaptador los datos que vamos a mostrar en el ListView
		adapter = new ItemListAdapter(this, cursor);
		setListAdapter(adapter);

		db1.close();

	}

	// Al pulsar en un elemento abrimos la actividad para mostrar el punto
	// seleccionado
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		// Localizamos el elemento que hemos pulsado
		Cursor cursor = (Cursor) adapter.getItem(position);

		// Creamos en Intent para lanzar la actividad y le pasamos el id del
		// punto
		Intent i = new Intent();
		i.setClass(ListaLugaresActivity.this, MostrarLugarActivity.class);

		i.putExtra(
				"snippet",
				cursor.getString(cursor.getColumnIndex("nombre"))
						+ ";"
						+ cursor.getString(cursor.getColumnIndex("descripcion"))
						+ ";" + cursor.getString(cursor.getColumnIndex("foto"))
						+ ";" + cursor.getDouble(cursor.getColumnIndex("lat"))
						+ ";" + cursor.getDouble(cursor.getColumnIndex("lon"))
						+ ";" + cursor.getInt(cursor.getColumnIndex("_id"))
						+ ";" + cursor.getString(cursor.getColumnIndex("fecha")));

		this.startActivityForResult(i, PETICION_LISTA);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PETICION_LISTA) {

			if (resultCode == RESULT_OK) {
				// Cargamos la lista de nuevo
				Cargar_Lista();
			}
		}
	}

	
}
