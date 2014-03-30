package ssrij.pcqc.pcquickcommands;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MenuActivity extends Activity {

	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
	}

}
