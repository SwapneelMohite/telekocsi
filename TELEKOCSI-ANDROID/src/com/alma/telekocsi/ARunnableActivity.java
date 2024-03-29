package com.alma.telekocsi;

import com.alma.telekocsi.dao.profil.Profil;
import com.alma.telekocsi.session.Session;
import com.alma.telekocsi.session.SessionFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class ARunnableActivity extends Activity implements Runnable {

	protected ProgressDialog progress = null;
	
	protected Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if(progress!=null){
				progress.dismiss();
			}
		}
		
	};
	
	@Override
	abstract public void run();
	
	protected void startProgressDialogInNewThread(Context context
										,String title
										,String message
										,boolean indeterminate
										,boolean cancelable){
		progress = ProgressDialog.show(context, title, message, indeterminate, cancelable);
    	Thread thread = new Thread((Runnable)context);
    	thread.start();
	}
	
	protected void startProgressDialogInNewThread(Context context){
		startProgressDialogInNewThread(context, getString(R.string.loading), "", true, false);
	}
	
	protected void stopProgressDialog(){
		handler.sendEmptyMessage(0);
	}
	
	
	
	//==========================================================
	//Gestion du menu a options
	
	protected void showMainMenu(){
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.disconnection_id:
			disconnect();
			return true;
		case R.id.notifications_id:
			showNotifications();
			return true;
		case R.id.main_menu_id:
			showMainMenu();
			return true;
		case R.id.preferences_id:
			showPreferences();
			return true;
		case R.id.profile_id:
			showProfileSettings();
			return true;
		case R.id.routes_id:
			showRoutesManaging();
			return true;
	    }
	    return false;
	}
	
	private void disconnect(){
		//fermeture de la session
		SessionFactory.getCurrentSession(this).logout();
		
		//fermeture de lactivite
		finish();
		
		//retour a la page didentification
		Intent intent = new Intent(this, Identification.class);
		startActivity(intent);
	}
	
	private void showNotifications(){
	}

	private void showPreferences(){
		Intent intent = new Intent(this, Preferences.class);
		startActivityForResult(intent, Preferences.RESULT);
	}
	
	private void showProfileSettings(){		
		Intent intent = new Intent(this, ProfileSettings.class);
		startActivity(intent);
	}
	
	private void showRoutesManaging(){
		Intent intent = new Intent(this, ItineraireManaging.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(getClass().getSimpleName(),"onActivityResult requestCode : " + requestCode);
		if(requestCode == Preferences.RESULT) {
			System.out.println("RESULT");
			saveProfileAndNotify();
		}else{
			System.out.println("RESULT NO");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void saveProfileAndNotify(){
		//recuperation des preferences
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		//et du profil du user
		Session session = SessionFactory.getCurrentSession(this);
		Profil profile = session.getActiveProfile();
		Log.d(getClass().getSimpleName(), "saveProfilAndNotify : " + profile);
		//mise a jour
		profile.setEmail(preferences.getString("email", profile.getEmail()));
		profile.setPseudo(preferences.getString("email", profile.getPseudo()));
		profile.setMotDePasse(preferences.getString("password", profile.getMotDePasse()));
		session.saveProfile(profile);
		System.out.println("PROFILE="+profile);
		Log.i(getClass().getName(), profile.toString());
		//notification rapide
		Toast.makeText(this, getString(R.string.profile_creation_ongoing), Toast.LENGTH_SHORT).show();
	}

}
