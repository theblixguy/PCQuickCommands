package ssrij.pcqc.pcquickcommands;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class FirstRunActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_run);

	}

	public void Thanks (View v)  {
		SharedPreferences settings = getSharedPreferences("signupdetails", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("firstrun", false);
		editor.commit();
		finish();
		overridePendingTransition(R.anim.no_change, R.anim.slide_down);
	}

}
