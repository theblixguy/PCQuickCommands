package ssrij.pcqc.pcquickcommands;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.jsoup.Jsoup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {

	static String server = "server URL here";
	static String username = "server username here";
	static String password = "server password here";
	private String pcqcusername = "";
	private ProgressDialog pd;
	ArrayAdapter<String> adapter;
	ArrayList<String> listItems = new ArrayList<String>();
	JSONArray jsonArray = new JSONArray();
	SharedPreferences settings;
	String client_curver;
	String server_client_curver;
	String download_client_curver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		settings = getSharedPreferences("signupdetails", 0);
		client_curver = settings.getString("client_curver", "1.1");
		try {
			final boolean firstTime;
			String usname = settings.getString("username", "default");
			firstTime = settings.getBoolean("firstrun", false);
			if (usname.equals("default")){

				SharedPreferences.Editor editor = settings.edit();
				editor.putString("client_curver", "1.1");
				editor.commit();

				Intent intent1 = new Intent(MainActivity.this, FirstTimeSwitchActivity.class);
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

						if (firstTime == true) {
							ShowSetupUI();
						}

						setUpStuff();
					}

				});
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void ShowSetupUI() {
		Intent setupUI = new Intent(MainActivity.this, FirstRunActivity.class);
		setupUI.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(setupUI);
		overridePendingTransition(R.anim.slide_up, R.anim.no_change);
	}

	public void ShowAboutPage() {
		Intent aboutPage = new Intent(MainActivity.this, AboutPageActivity.class);
		aboutPage.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(aboutPage);
		overridePendingTransition(R.anim.slide_up, R.anim.no_change);
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
			ShowAboutPage();
			return true;
		case R.id.action_showuname:
			SharedPreferences settings = getSharedPreferences("signupdetails", 0);
			String usname = settings.getString("username", "default");
			Toast.makeText(getApplicationContext(), "Your username is: " + usname, Toast.LENGTH_LONG).show();
			return true;

		case R.id.action_switch:
			try {
				Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
				loginPage.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(loginPage);
				overridePendingTransition(R.anim.slide_up, R.anim.no_change);
				finish(); 
			} catch (Exception e) {
				Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
				loginPage.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(loginPage);
				overridePendingTransition(R.anim.slide_up, R.anim.no_change);
			}
			return true;
		case R.id.action_ratereview:
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ssrij.pcqc.pcquickcommands")));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=ssrij.pcqc.pcquickcommands")));
			}
			return true;
		case R.id.action_clientdownload:
			ShowSetupUI();
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

	public void CheckForUpdate() {
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				try {       
					String curVersion = getPackageManager().getPackageInfo("ssrij.pcqc.pcquickcommands", 0).versionName;
					String newVersion = curVersion;
					boolean newVersionAvailable = false;
					newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=ssrij.pcqc.pcquickcommands&hl=en")
							.timeout(30000)
							.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
							.referrer("http://www.google.com")
							.get()
							.select("div[itemprop=softwareVersion]")
							.first()
							.ownText();
					newVersionAvailable = (parseValue(curVersion) < parseValue(newVersion)) ? true : false;

					if (newVersionAvailable == true){
						runOnUiThread(new Runnable() {
							public void run() {
								showUpdateDialog();
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();
	}

	public void showUpdateDialog() {
		AlertDialog.Builder DialogBld = new AlertDialog.Builder(this);
		DialogBld.setPositiveButton("Update later", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		DialogBld.setNegativeButton("Update now", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ssrij.pcqc.pcquickcommands")));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=ssrij.pcqc.pcquickcommands")));
				}
			}
		});

		DialogBld.setMessage("A new version of this app is available on the Play Store! Would you like to update?");
		DialogBld.setTitle("Update available");
		DialogBld.show();
	}

	private long parseValue(String string) {
		string = string.trim();
		if( string.contains( "." )){ 
			final int index = string.lastIndexOf( "." );
			return parseValue( string.substring( 0, index ))* 100 + parseValue( string.substring( index + 1 )); 
		}
		else {
			return Long.valueOf( string ); 
		}
	}



	@SuppressLint("CutPasteId") public void ExecuteCommand(View v) {

		// Save command
		try {
			EditText CmdToSend = (EditText)findViewById(R.id.editText1);
			ListView lv = (ListView) findViewById(R.id.listView1);
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
			lv.setAdapter(adapter);
			listItems.add(CmdToSend.getText().toString());
			jsonArray.put(CmdToSend.getText().toString());
			adapter.notifyDataSetChanged();
			SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = data.edit();
			editor.putString("CommandsHistory", jsonArray.toString());
			editor.commit();
		} 

		catch (Exception e) { 

		}


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
		CheckForUpdate();
		CheckForClientAppUpdate();
		setUpSlidingMenu();
	}

	public void CheckForClientAppUpdate() {
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				try {
					URL version_url = new URL("http://www.getcrush.co/PCQuickCommandsClient/clientapp_curver.txt");
					BufferedReader in = new BufferedReader(new InputStreamReader(version_url.openStream()));
					server_client_curver = in.readLine();
					in.close();

					URL download_url = new URL("http://www.getcrush.co/PCQuickCommandsClient/update_url.txt");
					BufferedReader in1 = new BufferedReader(new InputStreamReader(download_url.openStream()));
					download_client_curver = in1.readLine();
					in1.close();

				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}

				if (server_client_curver.equals(client_curver) == false) {
					runOnUiThread(new Runnable() {
						public void run() {
							showClientAppUpdateDialog();
						}
					});
				}
			}
		};
		thread.start();
	}

	public void showClientAppUpdateDialog() {
		AlertDialog.Builder DialogBld = new AlertDialog.Builder(this);
		DialogBld.setPositiveButton("Close (Dont remind me again)", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("client_curver", server_client_curver);
				editor.commit();
				dialog.cancel();
			}
		});

		DialogBld.setMessage("A new version of the client app (version " + server_client_curver + ") is available for download. You should update it now to ensure maximum stability and compatibility. You can download it from: "  + download_client_curver);
		DialogBld.setTitle("Client app update available");
		DialogBld.show();
	}

	public void setUpSlidingMenu() {
		final SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setBehindWidth(500);
		menu.setShadowWidthRes(R.dimen.slidingmenuWidth);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu);

		final ListView lv = (ListView) findViewById(R.id.listView1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
		lv.setAdapter(adapter);
		SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
		try {
			JSONArray jsonArray2 = new JSONArray(data.getString("CommandsHistory", "[]"));
			for (int i = 0; i < jsonArray2.length(); i++) {
				jsonArray.put(jsonArray2.getString(i));
				listItems.add(jsonArray2.getString(i));
			}
		} catch (Exception e) {}
		adapter.notifyDataSetChanged();

		lv.setClickable(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String command = lv.getItemAtPosition(position).toString();
				EditText CmdTxt = (EditText)findViewById(R.id.editText1);
				CmdTxt.setText(command);
				menu.toggle();
			}
		});
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
