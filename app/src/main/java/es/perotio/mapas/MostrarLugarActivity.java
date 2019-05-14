package es.perotio.mapas;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MostrarLugarActivity extends Activity {

	// private Context context;
	private String datos;
	String nombre_db, descripcion_db, foto_db, lat_db, lon_db, id, fecha;
	ImageView foto;
	TextView nombre;
	TextView descripcion;
	boolean cambios_si;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // para quitar el titulo
		setContentView(R.layout.mostrar);

		cambios_si = false;

		// Asignacion de elementos del Layout
		nombre = (TextView) findViewById(R.id.nombre);
		descripcion = (TextView) findViewById(R.id.descripcion);
		foto = (ImageView) findViewById(R.id.foto);
		TextView lat = (TextView) findViewById(R.id.lat);
		TextView lon = (TextView) findViewById(R.id.lon);
		TextView tFecha = (TextView) findViewById(R.id.fecha);

		// Coger Variables del intent
		datos = getIntent().getStringExtra("snippet");
		String[] datalist = datos.split(";");

		nombre_db = datalist[0];
		descripcion_db = datalist[1];
		foto_db = datalist[2];
		lat_db = datalist[3];
		lon_db = datalist[4];
		id = datalist[5];
		fecha = datalist[6];

		// Cargamos textos en el formulario
		nombre.setText(nombre_db);
		descripcion.setText(descripcion_db);
		lat.setText(lat_db);
		lon.setText(lon_db);
		tFecha.setText(fecha);
		// Asignamos no_photo3 cuando no existe un valor definido en la BD
		if (foto_db.equals("")) {
			foto.setImageResource(R.drawable.no_photo3);
		} else {
			poner_foto(foto_db);
		}

		// inicializo los botones
		Button editar = (Button) findViewById(R.id.editar);
		Button cerrar = (Button) findViewById(R.id.cerrar);

		cerrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vuelve_mapa();
			}
		});

		editar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setAction("editar");
				i.putExtra("lat", lat_db);
				i.putExtra("lon", lon_db);
				i.putExtra("nombre", nombre_db);
				i.putExtra("descripcion", descripcion_db);
				i.putExtra("foto", foto_db);
				i.putExtra("id", id);
				i.putExtra("fecha", fecha);
				i.setClass(MostrarLugarActivity.this, EditarLugarActivity.class);

				startActivityForResult(i, 1);

			}
		});
	}

	// Metodo para poner la foto en el ImageView desde la BD
	public void poner_foto(String foto_path) {
		// Genero el fichero de la foto
		File file = new File(foto_path);
		Uri fotoUri = Uri.fromFile(file);

		Bitmap bm;
		try {

			bm = MediaStore.Images.Media.getBitmap(getContentResolver(),
					fotoUri);
			foto.setImageBitmap(bm);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void vuelve_mapa() {
		Intent data = new Intent();
		data.putExtra("resultado", "texto devuelto");
		if (cambios_si) {
			setResult(RESULT_OK, data);
		} else {
			setResult(RESULT_CANCELED, data);
		}
		finish();
	}

	// Proceso que se lanza al volver de la ventana de edicion
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == 1) && (resultCode == RESULT_OK)) {
			Bundle extras = data.getExtras();

			String type = extras.getString("accion");

			if (type.equalsIgnoreCase("borrar")) {

				// Si hemos eliminado el lugar hay que finalizar el mostrar y
				// recargar la DB
				cambios_si = true;
				vuelve_mapa();

			} else if (type.equalsIgnoreCase("editar")) {

				// Si hemos actualizado el punto es necesario recargar los datos
				// en el Layout
				String ruta_foto = extras.getString("foto");
				cambios_si = true;

				nombre.setText(extras.getString("nombre"));
				descripcion.setText(extras.getString("descripcion"));

				// Asignamos una foto por defecto si no la hay en la BD
				if (ruta_foto.equals("")) {
					foto.setImageResource(R.drawable.no_photo3);
				} else {
					poner_foto(ruta_foto);
				}
			}
		}
	}

	public String lee_db(BaseDeDatos db1, int id) {

		String dfoto = "";
		SQLiteDatabase db = db1.getWritableDatabase();

		Cursor c = db
				.rawQuery(
						" SELECT lat, lon, nombre, descripcion, foto, _ID FROM lugares",
						null);
		try {

			if (c.moveToFirst()) {

				do {
					double vlat = c.getDouble(0);
					double vlon = c.getDouble(1);
					String dnombre = c.getString(2);
					String ddescripcion = c.getString(3);
					dfoto = c.getString(4);
					int did = c.getInt(5);

				} while (c.moveToNext());
			}
		} catch (Exception ex) {
			Log.e("Base de Batos", "Error al leer la base de datos");
		}

		db.close();

		return dfoto;
	}

	
	//Metodo que llama el OnClick de la Imagen
	public void abre_foto(View view) {

		//Llamamos al metodo para abrir la foto.
		ver_foto(foto_db);

	}

	public void ver_foto(String ruta) {

		if (ruta.equals("")) {
			Toast.makeText(MostrarLugarActivity.this, "No hay foto",
					Toast.LENGTH_SHORT).show();
		} else {
			File file = new File(ruta);
			Intent i = new Intent();
			i.setAction(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(file), "image/*");
			this.startActivity(i);
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
