package ssrij.pcqc.pcquickcommands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends Activity {

	static String server = "server url here";
	static String username = "username here";
	static String password = "password here";
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
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
			fos.write(text.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void VerifyAndContinue(View v) {
		boolean hasConnection = checkNetworkState(getApplicationContext());
		EditText editText2 = (EditText)findViewById(R.id.editText1);
		final String usname1 = editText2.getText().toString();
		boolean isValidUsername = verifyUsname(usname1);
		if (hasConnection == false) {
			showAlertDialog("No internet connection", "You need to be connected to the internet in order to create an account");
		} 
		
		else {
			if (isValidUsername == false) {
				showAlertDialog("Invalid username", "The username you specified is invalid!\n\nUsername:\n(1) Cannot exceed 12 characters in length\n(2) Cannot be less than 5 characters or zero\n(3) Cannot contain anything other than alphabets(A-Z, a-z) and numbers (0-9)");
			} 
			
			else {
				
				SaveAndContinue();
			}
		}
	}

	public void SaveAndContinue() {
		ShowHideProgressDialog(true);
		EditText editText1 = (EditText)findViewById(R.id.editText1);
		final String usname = editText1.getText().toString();
		String defCommand = "DEFAULT";
		final File commandFile = new File(getFilesDir().getPath() + "command.txt");
		CreateCommandFile(defCommand);
		
		Thread thread = new Thread()
		{
			@Override
			public void run() {

				try {
					FTPClient ftpClient = new FTPClient();
					ftpClient.connect(InetAddress.getByName(server));
					ftpClient.enterLocalPassiveMode();
					ftpClient.login(username, password);
					ftpClient.changeWorkingDirectory("/PCQuickCommands");
					
					boolean directoryExists = ftpClient.changeWorkingDirectory(usname);
					
					if (directoryExists == true){
						
						runOnUiThread(new Runnable() {
							public void run() {
								showAlertDialog("Username not available", "The username you specified already exists! Please specify another one");
							}
						});
						
						ftpClient.logout();
						ftpClient.disconnect();
						ShowHideProgressDialog(false);
					} 
					
					else  {
						ftpClient.makeDirectory(usname);
						ftpClient.changeWorkingDirectory(usname);
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
						BufferedInputStream buffIn = null;
						buffIn = new BufferedInputStream(new FileInputStream(commandFile));
						ftpClient.enterLocalPassiveMode();
						ftpClient.deleteFile("command.txt");
						ftpClient.storeFile("command.txt", buffIn);
						buffIn.close();
						ftpClient.logout();
						ftpClient.disconnect();
						
						SharedPreferences settings = getSharedPreferences("signupdetails", 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("username", usname);
						editor.commit();
						ShowHideProgressDialog(false);
						LaunchMainScreen();
					}

				} catch (SocketException e) {
					e.printStackTrace();
					ShowHideProgressDialog(false);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					ShowHideProgressDialog(false);
				} catch (IOException e) {
					e.printStackTrace();
					ShowHideProgressDialog(false);
				}
			}
			
		};

		thread.start();
	}
	
	public void LaunchMainScreen() {
		Intent a = new Intent(SignUpActivity.this, MainActivity.class);
		a.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(a);
		finish();
	}
	
	public boolean verifyUsname(String usName) {
		boolean isValidUsername = isValidUsname(usName.toString().trim());
		boolean returnVal = true;
		int lenUsname = usName.length();
		
		if (isValidUsername == false) {
			returnVal = false;
		} 
		
		if (lenUsname > 12) {
			returnVal = false;
		}
		
		if (lenUsname < 5) {
			returnVal = false;
		}
		
		return returnVal;
	}
	
	public void ShowHideProgressDialog(boolean showorhide){

		if (showorhide == true) {
			pd = new ProgressDialog(this);
			pd.setTitle("Creating account");
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
	
	public static boolean isValidUsname(String usname)
	{
		boolean isValid = false;
		String expression = "^[a-z_A-Z0-9]*$";
		CharSequence inputUsname = usname;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputUsname);
		if(matcher.matches())
		{
			isValid = true;
		}
		return isValid;
	}

}
