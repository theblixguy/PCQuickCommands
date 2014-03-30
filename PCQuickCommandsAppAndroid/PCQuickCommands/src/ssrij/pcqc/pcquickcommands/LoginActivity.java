package ssrij.pcqc.pcquickcommands;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

	}

	public void VerifyAndContinue(View v) {
		boolean hasConnection = checkNetworkState(getApplicationContext());
		if (hasConnection == false) {
			showAlertDialog("No internet connection", "You need to be connected to the internet in order to log in");
		} 

		else {

			SaveAndContinue();
		}
	}

	public void SaveAndContinue() {
		EditText username = (EditText)findViewById(R.id.editText1Login);
		String usname = username.getText().toString();
		SharedPreferences settings = getSharedPreferences("signupdetails", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("username", usname);
		editor.putBoolean("firstrun", true);
		editor.commit();
		LaunchMainScreen();
	}

	public void LaunchMainScreen() {
		Intent a = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(a);
		finish();
	}

	public void showAlertDialog(String title, String message) {
		AlertDialog.Builder DialogBld = new AlertDialog.Builder(this);
		DialogBld.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		DialogBld.setMessage(message);
		DialogBld.setTitle(title);
		DialogBld.show();
	}

	public static boolean checkNetworkState(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo infos[] = conMgr.getAllNetworkInfo();
		for (NetworkInfo info : infos) {
			if (info.getState() == State.CONNECTED)
				return true;
		}
		return false;
	}

}
