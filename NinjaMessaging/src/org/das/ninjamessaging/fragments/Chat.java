package org.das.ninjamessaging.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.das.ninjamessaging.R;
import org.das.ninjamessaging.fragmentactivities.MainActivity;
import org.das.ninjamessaging.utils.ConexionBD;
import org.das.ninjamessaging.utils.LaBD;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Chat extends Fragment {

	//atributtes
	private Button enviar;
	private EditText mensaje;
	private ListView listMessages;
	private ArrayList<String> datos;
	private ArrayAdapter<String> adaptador;
	private String hablandoCon;
	private Context context;
	static final String TAG = "NinjaMessaging";
    private AtomicInteger msgId = new AtomicInteger();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View aView = inflater.inflate(R.layout.fragment_chat, container, false);
		
		configurarComponentes(aView);
		
		return aView;
	}


	private void configurarComponentes(View aView) {
		hablandoCon = getActivity().getIntent().getStringExtra("opcionSeleccionada");
		datos = new ArrayList<String>();
		adaptador= new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, datos);
		
		enlazar(aView);
		listMessages.setAdapter(adaptador);

		if(hablandoCon != null) {
			updateList(hablandoCon);
		}
		
		enviar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//si hay texto, y si estoy hablando con alguien, envio el mensaje, si no, aviso.
				if(mensaje.getText().length() != 0 && hablandoCon != null) {
					LaBD.getMiBD(getActivity()).anadirMensaje(hablandoCon, 
							mensaje.getText().toString(), 1);
					
					
					sendToGCM();

			        mensaje.setText("");
					updateList(hablandoCon);
					
				} else {
					Toast.makeText(getActivity(), getString(R.string.error_chat), Toast.LENGTH_SHORT).show();
				}
				
			}

			private void sendToGCM() {
				new AsyncTask<String, Void, String>() {

					@Override
				    protected String doInBackground(String... params) {
				        String msg = "";
				        try {
				            
				        	ConexionBD.getMiConexionBD(getActivity().getApplicationContext())
				            .enviarMensaje(params[0]);
				            
				            msg = "Sent message";
				        } catch (IOException ex) {
				            msg = "Error :" + ex.getMessage();
				        }
				        return msg;
				    }

				    @Override
				    protected void onPostExecute(String msg) {
				    	
				    }
				}.execute(mensaje.getText().toString(), null, null);
			}
		});
	}


	private void enlazar(View aView) {
		mensaje = (EditText) aView.findViewById(R.id.message);
		enviar = (Button) aView.findViewById(R.id.send);
		
		listMessages = (ListView) aView.findViewById(R.id.listMessages);
	}

	/**
	 * Metodo que actualiza la lista de mensajes
	 * @param user indica la persona con la que estamos chateando
	 */
	public void updateList(String user) {
		hablandoCon = user;
		Cursor aCursor = LaBD.getMiBD(getActivity().getApplicationContext()).getMessagesWithUser(user);
		String nombre;
		String texto;
		int enviadoPorMi = 0; //0 = enviado por el otro, 1 = enviado por mi
		adaptador.clear();
		if(aCursor.moveToFirst()) {
			
			do {
				
				nombre = aCursor.getString(0);
				texto = aCursor.getString(1);
				enviadoPorMi = aCursor.getInt(2);
				if(enviadoPorMi == 0) {
					adaptador.add(nombre + ": " + texto);
				} else {
					adaptador.add("Yo: " + texto);
				}
				
				
			} while(aCursor.moveToNext());
			aCursor.close();
		}
	}
	
	
	//Generado por android

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_chat, container,
					false);
			
			
			
			return rootView;
		}
		
		
		
		
	}

}
