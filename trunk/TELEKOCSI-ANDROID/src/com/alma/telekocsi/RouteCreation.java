package com.alma.telekocsi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.alma.telekocsi.dao.itineraire.Itineraire;
import com.alma.telekocsi.dao.profil.Profil;
import com.alma.telekocsi.dao.trajet.Trajet;
import com.alma.telekocsi.session.Session;
import com.alma.telekocsi.session.SessionFactory;

public class RouteCreation extends ARunnableActivity {
	static final int ROUTE_FREQUENCY_DIALOG = 0;
	
	private Button startRouteCreationButton;
	private Button cancelRouteCreationButton;
	private OnClickListener onClickListener = null;
	
	private Button routeFreq;
	private Spinner placesCount;
	private RadioGroup automaticRoute;
	private EditText departure;
	private EditText arrival;
	private EditText departureTime;
	private EditText arrivalTime;
	private EditText price;
	private EditText comment;
	private Session session;
	private Profil profile;

	/**
	 * Tableau de frequence de la meme taille que 
	 */
	private boolean[] frequencies = new boolean[]{false,false,false,false,false,false,false,false};;
		                
	@Override
    public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        setContentView(R.layout.route_creation);
        
        startRouteCreationButton = (Button)findViewById(R.id.start_route_creation_button);
        startRouteCreationButton.setOnClickListener(getOnClickListener());

        cancelRouteCreationButton = (Button)findViewById(R.id.cancel_route_creation_button);
        cancelRouteCreationButton.setOnClickListener(getOnClickListener());
        
        placesCount = (Spinner)findViewById(R.id.route_creation_places_count_value);
        automaticRoute = (RadioGroup)findViewById(R.id.automatic_route_radio_group);
        departure = (EditText)findViewById(R.id.route_creation_departure_user);
        arrival = (EditText)findViewById(R.id.route_creation_arrival_user);
        departureTime = (EditText)findViewById(R.id.route_creation_departure_time_user);
        arrivalTime = (EditText)findViewById(R.id.route_creation_arrival_time_user);
        price = (EditText)findViewById(R.id.route_creation_price_user);
        comment = (EditText)findViewById(R.id.route_creation_comment_user);
        routeFreq = (Button)findViewById(R.id.route_creation_frequence_user);
        session = SessionFactory.getCurrentSession(this);
        profile = session.getActiveProfile();
        
        routeFreq.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(frequencies==null){
					frequencies = new boolean[]{false,false,false,false,false,false,false,false};
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(RouteCreation.this);
				builder.setTitle(R.string.route_creation_frequence);
				builder.setMultiChoiceItems(R.array.weekday_list, frequencies,new DialogInterface.OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						frequencies[which] = isChecked;
					}
					
				});
				final AlertDialog dialog;
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(dialog!=null) dialog.dismiss();
					}
					
				});
				dialog = builder.create();
				dialog.show();
			}
		});
        
	}
	
	private OnClickListener getOnClickListener(){
		if(onClickListener==null){
			onClickListener = makeOnClickListener();
		}
		return onClickListener;
	}
	
	private OnClickListener makeOnClickListener(){
		
		return new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(v==cancelRouteCreationButton){
					goBack();
				}
				else if(v==startRouteCreationButton){
					startRouteCreation();
				}
			}
			
		};
	}
	
	private void goBack(){
		finish();
	}
	
	private void startRouteCreation(){
		startProgressDialog(this);
    	Thread thread = new Thread(this);
    	thread.start();
		stopProgressDialog();
	}
	
	/**
	 * Cr�ation du trajet
	 * @return true en cas de succ�s
	 */
	protected boolean doCreateRoute(){		
		RadioButton rb = (RadioButton)findViewById(automaticRoute.getCheckedRadioButtonId());
		boolean autoroute = rb!=null && getString(R.string.yes).equals(rb.getText().toString());

		Itineraire itineraire = new Itineraire();
		itineraire.setLieuDepart(departure.getText().toString());
		itineraire.setLieuDestination(arrival.getText().toString());
		itineraire.setCommentaire(comment.getText().toString());
		itineraire.setIdProfil(profile.getId());
		itineraire.setPlaceDispo(Integer.valueOf(placesCount.getSelectedItem().toString()));
		itineraire.setAutoroute(autoroute);
		
		//La fréquence du trajet
		String freq = "";
		for(boolean freqBool : frequencies) freq += freqBool?"O":"N";
		itineraire.setFrequenceTrajet(freq);
		
		itineraire = session.save(itineraire);

		if(itineraire!=null){
			Trajet trajet = new Trajet();
			trajet.setAutoroute(autoroute);
			trajet.setPlaceDispo(Integer.valueOf(placesCount.getSelectedItem().toString()));
			trajet.setLieuDepart(itineraire.getLieuDepart());
			trajet.setLieuDestination(itineraire.getLieuDestination());
			trajet.setFrequenceTrajet(itineraire.getFrequenceTrajet());
			trajet.setHoraireDepart(departureTime.getText().toString());
			trajet.setHoraireArrivee(arrivalTime.getText().toString());
			trajet.setCommentaire(comment.getText().toString());
			trajet.setIdProfilConducteur(profile.getId());
			trajet.setIdItineraire(itineraire.getId());
			trajet.setNbrePoint(Integer.valueOf(price.getText().toString()));
			trajet = session.save(trajet);
			
			return trajet!=null;
		}
		
		return false;
	}

	@Override
	public void run() {
		RouteCreation self = this;
		//FIXME Ajouter la v�rifaction des valeurs
		if(doCreateRoute()){
			goBack();
		}
		else{
			final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(self);
			alertBuilder.setTitle(R.string.app_name);
			alertBuilder.setMessage(getString(R.string.route_creation_failed));
			alertBuilder.show();
		}
	}
	
}
