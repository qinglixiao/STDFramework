package com.std.framework.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.std.framework.R;
import com.std.framework.comm.STDActivityManager;
import com.std.framework.comm.STDFragmentManager;
import com.std.framework.interfaces.OnTipClickListener;
import com.std.framework.util.WidgetUtil;
import com.std.framework.widget.dialog.MessageTip;

public class BaseActivity extends AppCompatActivity {
	/**视图管理器*/
	public final STDFragmentManager FragmentManager;

	public BaseActivity() {
		FragmentManager = STDFragmentManager.getInstance(this);
		/**添加activity到栈*/
		STDActivityManager.getInstance().add(this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		/**销毁栈信息*/
		STDActivityManager.getInstance().remove(this);
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBack();
				return true;
		}
		return true;
	};

	public void onBack() {
		finish();
	}

}
