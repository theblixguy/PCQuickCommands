package ssrij.pcqc.pcquickcommands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {

	static String server = "server url here";
	static String username = "username here";
	static String password = "password here";
	private String pcqcusername = "";
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			SharedPreferences settings = getSharedPreferences("signupdetails", 0);
			String usname = settings.getString("username", "default");
			if (usname.equals("default")){
				Intent intent1 = new Intent(MainActivity.this, SignUpActivity.class);
				intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent1);
				finish();
			}
			else {
				pcqcusername = usname;
				Animation a = new TranslateAnimation(1000,0,0,0);
				Animation a1 = new TranslateAnimation(1000,0,0,0);
				Animation a2 = new TranslateAnimation(1000,0,0,0);
				Animation a3 = new TranslateAnimation(1000,0,0,0);
				Animation a4 = new TranslateAnimation(1000,0,0,0);
				
				a.setDuration(1000);
				a1.setDuration(1200);
				a2.setDuration(1400);
				a3.setDuration(1600);
				a4.setDuration(1800);

				TextView v = (TextView)findViewById(R.id.textView1);
				View v1 = (View)findViewById(R.id.View1);
				Spinner v2 = (Spinner)findViewById(R.id.spinner1);
				EditText v3 = (EditText)findViewById(R.id.editText1);
				Button v4 = (Button)findViewById(R.id.button1);
				
				v.clearAnimation();
				v1.clearAnimation();
				v2.clearAnimation();
				v3.clearAnimation();
				v4.clearAnimation();
				
				v.startAnimation(a);
				v1.startAnimation(a1);
				v2.startAnimation(a2);
				v3.startAnimation(a3);
				v4.startAnimation(a4);
				a4.setAnimationListener(new AnimationListener() {    
					@Override
					public void onAnimationStart(Animation animation) {  
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						setUpStuff();
					}
					
				});
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			Toast.makeText(getApplicationContext(), "Made by Suyash Srijan\nVersion 1.0 Beta", Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_showuname:
			SharedPreferences settings = getSharedPreferences("signupdetails", 0);
			String usname = settings.getString("username", "default");
			Toast.makeText(getApplicationContext(), "Your username is: " + usname, Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_ratereview:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ssrij.pcqc.pcquickcommands")));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=ssrij.pcqc.pcquickcommands")));
			}
			return true;
		case R.id.action_clientdownload:
			AlertDialog.Builder showDwnLink = new AlertDialog.Builder(this);
			showDwnLink.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			showDwnLink.setMessage("You can download the client app from:\n\nhttp://www.bit.ly/urlcode");
			showDwnLink.setTitle("Client App download");
			showDwnLink.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, 
	int pos, long id) {
		EditText editText1 = (EditText)findViewById(R.id.editText1);
		switch (pos) {
		case 0:
			editText1.setEnabled(false);
			editText1.setText("SHUTDOWN");
			break;
		case 1:
			editText1.setEnabled(false);
			editText1.setText("RESTART");
			break;
		case 2:
			editText1.setEnabled(false);
			editText1.setText("HIBERNATE");
			break;
		case 3:
			editText1.setEnabled(false);
			editText1.setText("SLEEP");
			break;
		case 4:
			editText1.setEnabled(false);
			editText1.setText("LOGOFF");
			break;
		case 5:
			editText1.setEnabled(false);
			editText1.setText("LOCK");
			break;
		case 6:
			editText1.setText("");
			editText1.setEnabled(true);
			editText1.setHint("Enter run command");
			break;
		case 7:
			editText1.setText("");
			editText1.setEnabled(true);
			editText1.setHint("Enter application name");
			break;
		case 8:
			editText1.setText("");
			editText1.setEnabled(true);
			editText1.setHint("Enter custom command");
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	public void SendCommand(final File commandFile) {

		Thread thread = new Thread()
		{
			@Override
			public void run() {
				try {
					FTPClient ftpClient = new FTPClient();
					ftpClient.connect(InetAddress.getByName(server));
					ftpClient.login(username, password);
					ftpClient.changeWorkingDirectory("/PCQuickCommands");
					ftpClient.changeWorkingDirectory(pcqcusername);
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					BufferedInputStream buffIn = null;
					buffIn = new BufferedInputStream(new FileInputStream(commandFile));
					ftpClient.enterLocalPassiveMode();
					ftpClient.deleteFile("command.txt");
					ftpClient.storeFile("command.txt", buffIn);
					buffIn.close();
					ftpClient.logout();
					ftpClient.disconnect();
					ShowHideProgressDialog(false);
				} catch (IOException e) {
					e.printStackTrace();
					ShowHideProgressDialog(false);
				}  
			}
		};

		thread.start();

	}

	public void ExecuteCommand(View v) {
		boolean hasConnection = checkNetworkState(getApplicationContext());
		if (hasConnection == false) {
			AlertDialog.Builder noInternet = new AlertDialog.Builder(this);
			noInternet.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			noInternet.setMessage("You need to be connected to the internet in order to send a command");
			noInternet.setTitle("Internet connection unavailable");
			noInternet.show();
		} 
		
		else {
			
			ShowHideProgressDialog(true);
			EditText CommandTxtView = (EditText)findViewById(R.id.editText1);
			File commandFile = new File(getFilesDir().getPath() + "command.txt");
			String commandtosend = CommandTxtView.getText().toString();
			CreateCommandFile(commandtosend);
			SendCommand(commandFile);
		}
	}

	public void CreateCommandFile(String text) {
		File commandFile = new File(getFilesDir().getPath() + "command.txt");
		if (commandFile.exists()) {
			try {
				commandFile.delete();
				commandFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		else {
			try {
				commandFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		try {
			FileOutputStream fos = new FileOutputStream(commandFile);
			Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
			
			if (spinner1.getSelectedItemPosition() == 6) {
				String newtext = "RUN ".concat(text);
				fos.write(newtext.getBytes());
				fos.flush();
				fos.close();
			} else if (spinner1.getSelectedItemPosition() == 7) {
				String newtext = "APPLICATION ".concat(text);
				fos.write(newtext.getBytes());
				fos.flush();
				fos.close();
			} else if (spinner1.getSelectedItemPosition() == 8) {
				String newtext = "CUSTOM ".concat(text);
				fos.write(newtext.getBytes());
				fos.flush();
				fos.close();
			} else {
				
				fos.write(text.getBytes());
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ShowHideProgressDialog(boolean showorhide){

		if (showorhide == true) {
			pd = new ProgressDialog(this);
			pd.setTitle("Sending command");
			pd.setMessage("Please wait a moment...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();

		} else if (showorhide == false) {
			if (pd != null) {
				pd.dismiss();
			}
		}

	}

	@Override 
	protected void onDestroy() {
		ShowHideProgressDialog(false);
		super.onDestroy();
	}
	
	public void setUpStuff() {
		Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		R.array.spinner_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter);
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
