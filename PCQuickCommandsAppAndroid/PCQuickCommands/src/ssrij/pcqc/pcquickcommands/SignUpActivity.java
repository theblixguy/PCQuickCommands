package ssrij.pcqc.pcquickcommands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class SignUpActivity extends Activity {

	static String server = "Enter server path";
	static String username = "Enter username";
	static String password = "Enter password";
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

	public void SaveAndContinue (View v) {
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

				SharedPreferences settings = getSharedPreferences("signupdetails", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("username", usname);
				editor.commit();
				ShowHideProgressDialog(false);
				LaunchMainScreen();
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

}
