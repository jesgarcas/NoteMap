package es.perotio.mapas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaV2 extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapav2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Centramos el mapa en Madrid, con vision de toda España
        mMap.moveCamera(CameraUpdateFactory
                .newLatLng(new LatLng(40.4381311,-3.81962)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
        //Cargamos las ubicaciones
        new Carga_Sitios();

        //Click en mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Intent i = new Intent();

                Double lat = point.latitude;
                Double lon = point.longitude;

                i.setAction("crear");
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                i.setClass(MapaV2.this,
                        EditarLugarActivity.class);
                startActivityForResult(i, 1);
                //Lo mostramos

            }

        });

        //Click en Marker

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {


            @Override
            public boolean onMarkerClick(Marker marker) {
                String n = marker.getSnippet();
                Intent i = new Intent();
                i.setClass(MapaV2.this,
                        MostrarLugarActivity.class);
                i.putExtra("snippet", n);

                startActivityForResult(i, 1);

                return true;
            }

        });

    }

    //Carga de ubicaciones en el mapa
    public class Carga_Sitios {
        BaseDeDatos db2;
        SQLiteDatabase db;
        Cursor c = null;
        int j;
        int i = 0;

        //Para agregar el marco negro a la imagen
        private Bitmap addBlackBorder(Bitmap bmp, int borderSize) {
            Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
            Canvas canvas = new Canvas(bmpWithBorder);
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp, borderSize, borderSize, null);
            return bmpWithBorder;
        }

        public Carga_Sitios() {
            //limpiamos
            mMap.clear();

            //Cargamos la info de la BD
            db2 = new BaseDeDatos(MapaV2.this, "db_mapas",
                    null, BaseDeDatos.v_db);
            db = db2.getWritableDatabase();

            c = db
                    .rawQuery(
                            " SELECT lat, lon, nombre, descripcion, foto, _id, fecha FROM lugares",
                            null);
            j = c.getCount();

            if (c.moveToFirst()) {

                do {
                    double vlat = c.getDouble(0);
                    double vlon = c.getDouble(1);
                    String nombre = c.getString(2);
                    String descripcion = c.getString(3);

                    String fotopath = c.getString(4);

                    int id = c.getInt(5);
                    String fecha = c.getString(6);


                    String datos = nombre + ";" + descripcion + ";" + fotopath
                            + ";" + vlat + ";" + vlon + ";" + id + ";"
                            + fecha;
                    //Gestion de la miniatura
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                    Bitmap b;
                    Bitmap bitmapResized;
                    BitmapDescriptor fotobm;
                    
                    if (fotopath.equals("")) {
                        b = BitmapFactory.decodeResource(getResources(),R.drawable.no_photo2);
                    } else {
                        b = BitmapFactory.decodeFile(fotopath,bmOptions);
                        b = addBlackBorder(b,30);
                    }

                    bitmapResized = Bitmap.createScaledBitmap(b, 100, 75, false);
                    fotobm = BitmapDescriptorFactory.fromBitmap(bitmapResized);

                    //Añadimos el marker
                    mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(vlat, vlon))
                                    .title(descripcion)
                                    .snippet(datos)
                                    .icon(fotobm)


                    );
                    //Centramos la Camara al nuevo marker
                    mMap.moveCamera(CameraUpdateFactory

                            .newLatLng(new LatLng(vlat, vlon)));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    i++;

                } while (c.moveToNext());

            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Extraemos el resultado
            // String resultado = data.getStringExtra("resultado");

            if (resultCode == RESULT_OK) {
                // Cargamos los lugares en el mapa
                new Carga_Sitios();
            }
        }
    }






}
