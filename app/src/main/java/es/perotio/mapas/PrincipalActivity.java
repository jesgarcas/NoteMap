package es.perotio.mapas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PrincipalActivity extends Activity {
	/** Called when the activity is first created. */

	private static final int REQUEST_CAPTURE_IMAGE = 110 , REQUEST_ACCESS_FINE_LOCATION = 111, REQUEST_WRITE_STORAGE = 112;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		Button lista = (Button) findViewById(R.id.blista);
		Button mapav2 = (Button) findViewById(R.id.bmapav2);

		// Adquirir fecha
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String fecha = df.format(c.getTime());
		Log.e("Fecha", fecha);
		
		

		lista.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(PrincipalActivity.this,
						ListaLugaresActivity.class));
			}
		});


		mapav2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(PrincipalActivity.this,
						MapaV2.class));
			}
		});
        //Dado que a partir de Android 6.0 (nivel de API 23) es necesario solicitar ciertos permisos
        //al usuario, procedo a solicitar los que pueden ser necesarios tanto ahora como en posibles
        //versiones de la aplicacion.

        boolean hasPermissionPhoneState = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionPhoneState) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAPTURE_IMAGE);
        }

        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }

        boolean hasPermissionWrite = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionWrite) {
            ActivityCompat.requestPermissions(PrincipalActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
	}


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_CAPTURE_IMAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                    //Toast.makeText(PrincipalActivity.this, R.string.conceder_camara, Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                    //Toast.makeText(PrincipalActivity.this, R.string.conceder_ubicacion, Toast.LENGTH_LONG).show();
                }
            }

            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    finish();
                    startActivity(getIntent());
                } else
                {
                    //Toast.makeText(PrincipalActivity.this, R.string.conceder_memoria, Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}