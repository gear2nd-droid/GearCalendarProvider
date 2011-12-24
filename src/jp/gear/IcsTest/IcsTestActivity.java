package jp.gear.IcsTest;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jp.gear.IcsTest.CalendarApi.IcsCalendarProvider;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IcsTestActivity extends Activity {
	

	String calTitle = "";
	int calColor = Color.GRAY;
	long calId = IcsCalendarProvider.FALSE_ID;
	String calTimezone = TimeZone.getDefault().getDisplayName();
	
	long eventId = IcsCalendarProvider.FALSE_ID;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//	calendar select
		final IcsCalendarProvider provider = new IcsCalendarProvider(this);
		List<Map<String, String>> listCalendar = provider.selectCalendars();
		
		Map<String, String> calMap;
		
		for(int i=0;i<listCalendar.size();i++){
			calMap = listCalendar.get(i);
			if(provider.checkWritableCalendar(calMap)){
				calTitle = provider.getCalendarName(calMap);
				calColor = provider.getCalendarColor(calMap);
				calId = provider.getCalendarId(calMap);
				calTimezone = provider.getCalendarTimezone(calMap);
				break;
			}
		}
		
		//	layout
		this.setTitle("CalendarProviderTest - Calendars");
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		
		final TextView text = new TextView(this);
		layout.addView(text);
		text.setText("(" + calId + ")" + calTitle);
		text.setTextColor(calColor);
		
		//	insert test
		Button btn;
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("add event ontime");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				long longId = provider.insertEvent(calId, calTimezone, 
						"ontime", "test_text", "test_place");
				if(longId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "ADD FALSE", 
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(v.getContext(), "ADD SUCCESS : " + String.valueOf(longId), 
							Toast.LENGTH_SHORT).show();
					eventId = longId;
				}
			}
		});
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("add event span");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				start.add(Calendar.DATE, -1);
				end.add(Calendar.DATE, -1);
				end.add(Calendar.HOUR, 1);
				long longId = provider.insertEvent(calId, calTimezone, 
						"span", "test_text", "test_place", 
						start, end, IcsCalendarProvider.HAS_ALARM_FALSE);
				if(longId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "ADD FALSE", 
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(v.getContext(), "ADD SUCCESS : " + String.valueOf(longId), 
							Toast.LENGTH_SHORT).show();
					eventId = longId;
				}
			}
		});
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("add event today");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				date.add(Calendar.DATE, -1);
				long longId = provider.insertEvent(calId, calTimezone, 
						"today", "test_text", "test_place", 
						date, IcsCalendarProvider.HAS_ALARM_FALSE);
				if(longId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "ADD FALSE", 
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(v.getContext(), "ADD SUCCESS : " + String.valueOf(longId), 
							Toast.LENGTH_SHORT).show();
					eventId = longId;
				}
			}
		});
		
		//	delete test
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("prev event delete");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(eventId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "FALSE : PREV ID", 
							Toast.LENGTH_SHORT).show();
				}else{
					if(provider.deleteEvent(eventId)){
						Toast.makeText(v.getContext(), "DELETE SUCCESS : " + eventId, 
								Toast.LENGTH_SHORT).show();
						eventId = IcsCalendarProvider.FALSE_ID;
					}else{
						Toast.makeText(v.getContext(), "DELETE FALSE", 
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		//	select test
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("prev event select");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(eventId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "FALSE : PREV ID", 
							Toast.LENGTH_SHORT).show();
				}else{
					Map<String, String> map = provider.selectEvent(eventId);
					if(map!=null){
						Toast.makeText(v.getContext(), "SELECT SUCCESS : " + eventId + "\n" + map.get(Events.TITLE), 
								Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(v.getContext(), "SELECT FALSE", 
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("today select");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Calendar start = Calendar.getInstance();
				start.set(Calendar.HOUR, 0);
				start.set(Calendar.MINUTE, 0);
				start.set(Calendar.SECOND, 0);
				Calendar end = Calendar.getInstance();
				end.set(Calendar.HOUR, 23);
				end.set(Calendar.MINUTE, 59);
				end.set(Calendar.SECOND, 59);
				List<Map<String, String>> list = provider.selectEvents(calTimezone, start, end);
				if(list!=null){
					String str = "SELECT SUCCESS TODAY COUNT : " + list.size();
					str += "\nSELECT SPAN <" + String.valueOf(start.getTimeInMillis()) + 
							"-" + String.valueOf(end.getTimeInMillis()) + ">";
					for(int i=0;i<list.size();i++){
						Map<String, String> item = list.get(i);
						str += "\n" + i + "(" + item.get(Events._ID) + ") : " + item.get(Events.TITLE) + 
								" <" + item.get(Events.DTSTART) + "-" + item.get(Events.DTEND) + ">";
					}
					Toast.makeText(v.getContext(), str, 
							Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(v.getContext(), "SELECT FALSE", 
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		final EditText textScan = new EditText(this);
		layout.addView(textScan);
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("title select");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Editable title = textScan.getText();
				List<Map<String, String>> list = provider.selectEvents(title.toString());
				if(list!=null){
					String str = "SELECT SUCCESS TODAY COUNT : " + list.size();
					str += "\nSELECT TITLE <" + title + ">";
					for(int i=0;i<list.size();i++){
						Map<String, String> item = list.get(i);
						str += "\n" + i + "(" + item.get(Events._ID) + ") : " + item.get(Events.TITLE);
					}
					Toast.makeText(v.getContext(), str, 
							Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(v.getContext(), "SELECT FALSE", 
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn = new Button(this);
		layout.addView(btn);
		btn.setText("title prev update");
		btn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(eventId==IcsCalendarProvider.FALSE_ID){
					Toast.makeText(v.getContext(), "FALSE : PREV ID", 
							Toast.LENGTH_SHORT).show();
				}else{
					Map<String, String> map = provider.selectEvent(eventId);
					if(map!=null){
						String title = map.get(Events.TITLE);
						title += map.get(Events._ID);
						long event_id = provider.getEventId(map);
						provider.updateEvent(event_id, IcsCalendarProvider.FALSE_ID, 
								null, title, null, null, null, null, null, null);
						Toast.makeText(v.getContext(), "UPDATE SUCCESS : " + eventId + "\n" + map.get(Events.TITLE), 
								Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(v.getContext(), "UPDATE FALSE", 
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
}