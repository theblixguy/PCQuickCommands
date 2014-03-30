package ssrij.pcqc.pcquickcommands;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AboutPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_page);

	}

	public void CloseMe (View v)  {
		finish();
		overridePendingTransition(R.anim.no_change, R.anim.slide_down);
	}

}
