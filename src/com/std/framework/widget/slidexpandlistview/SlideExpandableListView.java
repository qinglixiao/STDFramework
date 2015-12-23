package com.std.framework.widget.slidexpandlistview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.std.framework.widget.slidexpandlistview.ActionSlideExpandableListView.OnExPandListener;

/**
 * Simple subclass of listview which does nothing more than wrap
 * any ListAdapter in a SlideExpandalbeListAdapter
 */
class SlideExpandableListView extends ListView {
	private SlideExpandableListAdapter adapter;

	public SlideExpandableListView(Context context) {
		super(context);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Collapses the currently open view.
	 *
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if(adapter!=null) {
			return adapter.collapseLastOpen();
		}
		return false;
	}

	public void setAdapter(ListAdapter adapter) {
		this.adapter = new SlideExpandableListAdapter(adapter);
		super.setAdapter(this.adapter);
	}
	
	/**
	 * 
	 * 描          述 ：设置列表展开事件，此方法须在设置适配器之后调用
	 * 创建日期  : 2013-7-9
	 * 作           者 ： lx
	 * 修改日期  : 
	 * 修   改   者 ：
	 * @version   : 1.0
	 * @param onExPandListener
	 *
	 */
	public void setOnExPandListener(OnExPandListener onExPandListener){
		if(adapter != null)
			adapter.setOnExpandListener(onExPandListener);
	}

	/**
	 * Registers a OnItemClickListener for this listview which will
	 * expand the item by default. Any other OnItemClickListener will be overriden.
	 *
	 * To undo call setOnItemClickListener(null)
	 *
	 * Important: This method call setOnItemClickListener, so the value will be reset
	 */
	public void enableExpandOnItemClick() {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				SlideExpandableListAdapter adapter = (SlideExpandableListAdapter)getAdapter();
				adapter.getExpandToggleButton(view).performClick();
			}
		});
	}


	@Override
	public Parcelable onSaveInstanceState() {
		return adapter.onSaveInstanceState(super.onSaveInstanceState());
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if(!(state instanceof AbstractSlideExpandableListAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		AbstractSlideExpandableListAdapter.SavedState ss = (AbstractSlideExpandableListAdapter.SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());

		adapter.onRestoreInstanceState(ss);
	}
}