package es.perotio.mapas;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends CursorAdapter {

	// Adaptador para mostrar un Cursor en un ListView

	private LayoutInflater inflater;
	ImageView foto;

	// Constructor de la clase
	public ItemListAdapter(Context context, Cursor c) {
		super(context, c, true);

		this.inflater = LayoutInflater.from(context);
	}

	// Creamos una instancia del layout para cada elemento
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = inflater.inflate(R.layout.itemrow, parent, false);

		return view;
	}

	// Asignamos cada valor a un campo distinto del layout
	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView itemName = (TextView) view.findViewById(R.id.itemName);
		itemName.setText(cursor.getString(cursor.getColumnIndex("nombre")));
		
		TextView itemFecha = (TextView) view.findViewById(R.id.itemFecha);
		itemFecha.setText(cursor.getString(cursor.getColumnIndex("fecha")));

		TextView itemDescription = (TextView) view.findViewById(R.id.itemDescription);
		itemDescription.setText(cursor.getString(cursor.getColumnIndex("descripcion")));

		String uriFoto = cursor.getString(cursor.getColumnIndex("foto"));
		foto = (ImageView) view.findViewById(R.id.itemImage);

		if (uriFoto.equals("")) {
			foto.setImageResource(R.drawable.no_photo3);
		} else {
			foto.setImageURI(Uri.parse(uriFoto));
		}

	}

}
