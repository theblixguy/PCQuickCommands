package ssrij.pcqc.pcquickcommands;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FirstTimeSwitchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firsttimeswitch);

	}

	public void LoginPCQC(View v) {
		Intent intent = new Intent(FirstTimeSwitchActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up, R.anim.no_change);
		finish();
	}

	public void SignUpPCQC(View v) {
		Intent intent = new Intent(FirstTimeSwitchActivity.this, SignUpActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up, R.anim.no_change);
		finish();
	}

}
