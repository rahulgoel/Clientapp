package com.irodov.clientapp;




import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuList extends ListFragment {
	String s[];

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {

		
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		s = args.getStringArray("Places");
		Log.d("Menulist:Length","Its"+s.length);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < s.length; i++) {
			adapter.add(new SampleItem(s[i], android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		MainActivity m =(MainActivity)getActivity();
		switch (position) {
		case 0:
	//		newContent = new ColorFragment(R.color.red);
			m.clearPlacesList();
			m.loadNewPlaces("clothing_store");
			m.showContent();
			Log.d("Menulist","ITS happening");
			break;
		case 1:
			m.clearPlacesList();
			m.loadNewPlaces("shoe_store");
			m.showContent();
	//		newContent = new ColorFragment(R.color.green);
			break;
		case 2:
//			newContent = new ColorFragment(R.color.blue);
			break;
		case 3:
	//		newContent = new ColorFragment(android.R.color.white);
			break;
		case 4:
		//	newContent = new ColorFragment(android.R.color.black);
			break;
		}
/*		if (newContent != null)
			switchFragment(newContent);*/
	}
	
	
	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
}
