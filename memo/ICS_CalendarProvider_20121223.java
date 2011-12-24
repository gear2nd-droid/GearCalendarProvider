package jp.gear.IcsTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class IcsTestActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//	layout
		this.setTitle("CalendarProviderTest - Calendars");
		TableLayout table = new TableLayout(this);
		HorizontalScrollView scroll = new HorizontalScrollView(this);
		scroll.addView(table);
		TableRow row;
		TextView text;
		setContentView(scroll);
		
		//	calendars
		ContentResolver cr = this.getContentResolver();
		String[] projection = {
			Calendars._ID, Calendars.NAME, 
			Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE, 
			Calendars.CALENDAR_DISPLAY_NAME, Calendars.CALENDAR_COLOR, 
			Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CALENDAR_TIME_ZONE, 
			Calendars.OWNER_ACCOUNT, Calendars.SYNC_EVENTS
		};
		int colCount = projection.length;
		Cursor c = cr.query(Calendars.CONTENT_URI, 
				projection, null, null, null);
		
		//	create layout
		{
			//	row header
			row = new TableRow(this);
			for(int i=0;i<colCount;i++){
				text = new TextView(this);	row.addView(text);	
				text.setText("("+i+")"+projection[i]);
			}
			table.addView(row);
		}
		if(c.moveToFirst()){
			ArrayList list = new ArrayList<Map<String, String>>();
			Map indexMap = new HashMap<String, Integer>();
			
			for(int i=0;i<colCount;i++){
				indexMap.put(projection[i], c.getColumnIndex(projection[i]));
			}
			
			do{
				Map map = new HashMap<String, String>();
				row = new TableRow(this);
				for(int i=0;i<colCount;i++){
					String str = c.getString((Integer)indexMap.get(projection[i]));
					map.put(projection[i], str);
					text = new TextView(this);	row.addView(text);	
					text.setText(str);
				}
				list.add(map);
				table.addView(row);
			}while(c.moveToNext());
		}
	}
}