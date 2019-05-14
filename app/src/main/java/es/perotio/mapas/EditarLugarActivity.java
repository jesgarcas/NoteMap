package es.perotio.mapas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditarLugarActivity extends Activity {
	/** Called when the activity is first created. */

	//Variables
	Double lat;
	Double lon;
	EditText nombre;
	EditText descripcion;
	BaseDeDatos db1;
	int id;
	Button b_eliminar;
	Button b_volver;
	Button b_editar;
	Button b_crear;


	Uri outputFileUri;
	String foto_path;
	ImageView foto;

	// Variables para FOTOS
	final static int CAMARA = 1;
	final static int GALERIA = 2;

	String fecha;




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editar);

		// Preparamos la Base de Datos
		db1 = new BaseDeDatos(this, "db_mapas", null, BaseDeDatos.v_db);
		db1.getWritableDatabase();
		db1.close();
			

		// Iniciamos el Path
		foto_path = "";

		// Inicializamos todo lo relacionado con el Layout
		b_crear = (Button) findViewById(R.id.crear);
		b_editar = (Button) findViewById(R.id.editar);
		b_eliminar = (Button) findViewById(R.id.borrar);
		b_volver = (Button) findViewById(R.id.volver);
		TextView tLat = (TextView) findViewById(R.id.lat);
		TextView tLon = (TextView) findViewById(R.id.lon);
		TextView tFecha = (TextView) findViewById(R.id.fecha);
		TextView titulo = (TextView) findViewById(R.id.titulo);
		nombre = (EditText) findViewById(R.id.nombre);
		descripcion = (EditText) findViewById(R.id.descripcion);
		foto = (ImageView) findViewById(R.id.foto);

		// Obtenemos una fecha para utilizar
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		fecha = df.format(c.getTime());
		Log.e("Fecha", fecha);

		// Capturamos el Intent para determinar la accion (editar o crear)
		Intent i = getIntent();

		if (i.getAction() != null) {

			if (i.getAction().equals("crear")) {

				Bundle extras = i.getExtras();
				lat = (Double) extras.get("lat");
				lon = (Double) extras.get("lon");

				tLat.setText("" + lat);
				tLon.setText("" + lon);
				tFecha.setText(fecha);
				titulo.setText(getResources().getString(R.string.crear));
				// Mostramos / ocultamos botones.
				b_editar.setVisibility(View.GONE);
				b_eliminar.setVisibility(View.GONE);
				b_volver.setVisibility(View.VISIBLE);
			}

			else if (i.getAction().equals("editar")) {

				Bundle extras = i.getExtras();
				String lat = (String) extras.get("lat");
				String lon = (String) extras.get("lon");
				String nom = (String) extras.get("nombre");
				String desc = (String) extras.get("descripcion");
				id = Integer.parseInt((String) extras.get("id"));
				foto_path = (String) extras.get("foto");
				String fecha = (String) extras.get("fecha");


				leer_foto(foto_path);

				tLat.setText(lat);
				tLon.setText(lon);
				tFecha.setText(fecha);
				

                nombre.setText(nom);
				descripcion.setText(desc);
				titulo.setText(getResources().getString(R.string.editar));
				// Mostramos/ocultamos botones
				b_crear.setVisibility(View.GONE);
				b_editar.setVisibility(View.VISIBLE);
				b_eliminar.setVisibility(View.VISIBLE);
			}
		}
	}


	
	//Metodo de creacion
	public synchronized void b_crear(View view) {


        String nom = nombre.getText().toString();
        int n1 = nom.length();

		String des = descripcion.getText().toString();
		int n2 = des.length();

		if (n1 > 0 && n2 > 0) {
			// Guardamos en la BD
			escribe_db(db1, lat, lon, nom, des, foto_path, fecha);
			vuelve_mapa();

		} else {
			Toast.makeText(EditarLugarActivity.this, R.string.campo_obligado,
					Toast.LENGTH_SHORT).show();
		}
	}

	
	// Metodo de edicion
	public synchronized void b_editar(View view) {


        String nom = nombre.getText().toString();
		String desc = descripcion.getText().toString();
		int n1 = nom.length();
		int n2 = desc.length();
		if (n1 > 0 && n2 > 0) {
			// Guardamos en la BD
			update_db(db1, id, desc, nom, foto_path);
			Toast.makeText(EditarLugarActivity.this, getResources().getString(R.string.editado),
					Toast.LENGTH_SHORT).show();
			vuelve_mostrar(desc, nom, foto_path, "editar");

		} else {
			Toast.makeText(EditarLugarActivity.this, R.string.campo_obligado,
					Toast.LENGTH_SHORT).show();
		}

	}

	//Metodo de borrado
	public synchronized void b_borrar(View view) {
		delete_db(db1, id, foto_path);
		Toast.makeText(EditarLugarActivity.this,
				getResources().getString(R.string.borrado), Toast.LENGTH_SHORT)
				.show();
		vuelve_mostrar("", "", "", "borrar");
	}

	//Metodo para volver
	public void b_volver(View view) {
		finish();
	}

	
	public synchronized static void escribe_db(BaseDeDatos db1, double lat,
			double lon, String nombre, String descripcion, String foto,
			String fecha) {

		SQLiteDatabase db = db1.getWritableDatabase();

		// insertamos datos
		ContentValues nuevo_registro = new ContentValues();
		nuevo_registro.put("lat", lat);
		nuevo_registro.put("lon", lon);
		nuevo_registro.put("nombre", nombre);
		nuevo_registro.put("descripcion", descripcion);
		nuevo_registro.put("foto", foto);
		nuevo_registro.put("fecha", fecha);

		db.insert("lugares", null, nuevo_registro);
		db.close();
	}
	//Actualizar la BD
	public synchronized static void update_db(BaseDeDatos db1, int id,
			String descripcion, String nombre, String foto) {

		SQLiteDatabase db = db1.getWritableDatabase();

		// insertamos datos
		ContentValues update_registro = new ContentValues();

		update_registro.put("descripcion", descripcion);
		update_registro.put("nombre", nombre);
		update_registro.put("foto", foto);

		db.update("lugares", update_registro, "_id='" + id + "'", null);
		db.close();
	}

	//Borrar la BD
	public synchronized static void delete_db(BaseDeDatos db1, int id, String foto) {

		SQLiteDatabase db = db1.getWritableDatabase();

		db.delete("lugares", "_id='" + id + "'", null);
		db.close();
		
		// Borramos la foto
		if (foto != "") {
			File image = new File(foto);
			image.delete();
		}
	}

	// Actualizar los puntos en el mapa
	protected synchronized void vuelve_mapa() {
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		finish();
	}

	// Metodo para volver a mostrar activity con los nuevos datos
	protected synchronized void vuelve_mostrar(String descripcion,
			String nombre, String foto, String accion) {
		Intent data = new Intent();
        data.putExtra("descripcion", descripcion);
		data.putExtra("nombre", nombre);
		data.putExtra("foto", foto);
		data.putExtra("accion", accion);
		setResult(RESULT_OK, data);
		finish();
	}

	public synchronized void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == Activity.RESULT_OK) {

			// Redimensionaremos la imagen para no sobrecargar la memoria.
			int scaleWidth = 400, scaleHeigth = 300;

			// Nombre de archivo donde guardaremos la imagen
			String image_name = android.text.format.DateFormat.format(
					"yyyyMMdd_kkmmss", new java.util.Date()).toString();

			// Ubicacion de almacenamiento de imagenes
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/.NoteMap/");

			// si el direcctorio no existe, lo creo
			if (!dir.exists()) {
				System.out.println("creando directorio: " + "NoteMap");
				dir.mkdir();
			}

			// Path completo a la imagen
			File file = new File(dir, "NoteMap_" + image_name + ".png");

			OutputStream outStream = null;

			try {
				// Abrimos el flujo de datos a la ruta de la imagen
				outStream = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			switch (requestCode) {

			case CAMARA:

				Bitmap thumbnail = null;

				try {
					thumbnail = MediaStore.Images.Media.getBitmap(
							getContentResolver(), outputFileUri);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// Redimensionamos la imagen
				Bitmap resized = Bitmap.createScaledBitmap(thumbnail,
						scaleWidth, scaleHeigth, true);

				// Guardamos la imagen en formato PNG
				resized.compress(Bitmap.CompressFormat.PNG, 100, outStream);

				try {
					outStream.flush();
					outStream.close();
				} catch (Exception ex) {

				}

				// Borramos la foto de gran tamaño
				if (outputFileUri != null) {
					File image = new File(outputFileUri.getPath());
					image.delete();
				}

				// Mostramos la imagen en el ImageView del Layout
				foto_path = file.getPath();
				foto.setImageBitmap(resized);
				break;

			case GALERIA:

				Uri selectedImageUri = data.getData();

				Bitmap bitmap = null;

				// Localizamos la imagen seleccionada
				try {
					bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImageUri);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Redimensionamos la imagen
				Bitmap resized_galeria = Bitmap.createScaledBitmap(bitmap,
						scaleWidth, scaleHeigth, true);

				// Guardamos la imagen en PNG
				resized_galeria.compress(Bitmap.CompressFormat.PNG, 100,
						outStream);

				try {
					outStream.flush();
					outStream.close();
				} catch (Exception ex) {

				}

				// Asignamos la imagen al ImageView
				foto_path = file.getPath();
				foto.setImageBitmap(resized_galeria);
				break;
			}
		}
	}

	public synchronized void leer_foto(String foto_path) {
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

	// Lanzamos el dialog para escoger entre Camara y Galeria
	public synchronized void hacerfoto(View view) {

		AlertDialog selectFoto = new AlertDialog.Builder(
				EditarLugarActivity.this)
				.setTitle(R.string.select)
				.setItems(R.array.select_image,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int selected) {
								if (selected == 0) {
									fotoCamara();
								} else if (selected == 1) {
									fotoGaleria();
								}
							}
						}).create();

		selectFoto.show();
	}

	//Para la Galeria
	public synchronized void fotoGaleria() {
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				MediaStore.Images.Media.CONTENT_TYPE);

		startActivityForResult(i, GALERIA);

	}

	//Para la Camara
	private synchronized void fotoCamara() {

		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd_kkmmss");
		String newPicFile = "NoteMap_" + df.format(date) + ".png";

		// Creo el directoio para guardar las fotos
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/.NoteMap/");

		// si el direcctorio no existe, lo creo
		if (!dir.exists()) {
			System.out.println("creando directorio: " + "NoteMap");
			dir.mkdir();
		}

		// Creo el fichero
		File file = new File(dir, newPicFile);
		foto_path = file.getPath();
		outputFileUri = Uri.fromFile(file);

		Log.e("FOTOS_CAMARA", "" + foto_path);

		// Inicio un Intent del sistema para iniciar la CAMARA
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, CAMARA);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
