package jp.gear.IcsTest.CalendarApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class IcsCalendarProvider {
	//	ContentProvider関連
	Context context;
	public static final int FALSE_ID = 1;
	public static final String HAS_ALARM_TRUE = "1";
	public static final String HAS_ALARM_FALSE = "0";
	public static final String ALL_DAY_TRUE = "1";
	public static final String ALL_DAY_FALSE = "0";
	private static final String[] eventsProjection = {
			Events._ID, Events.CALENDAR_ID, 
			Events.TITLE, Events.DESCRIPTION, Events.EVENT_LOCATION, 
			Events.EVENT_TIMEZONE, Events.DTSTART, Events.DTEND, 
			Events.EVENT_COLOR, Events.ALL_DAY
		};
	private static final String[] calendarsProjection = {
			Calendars._ID, Calendars.NAME, 
			Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE, 
			Calendars.CALENDAR_DISPLAY_NAME, Calendars.CALENDAR_COLOR, 
			Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CALENDAR_TIME_ZONE, 
			Calendars.OWNER_ACCOUNT, Calendars.SYNC_EVENTS
		};

	
	//	Constructor
	public IcsCalendarProvider(Context context) {
		this.context = context;
	}
	
	//	method
	public List<Map<String, String>> selectCalendars() {
		//	calendars
		ContentResolver cr = this.context.getContentResolver();
		
		int colCount = calendarsProjection.length;
		Cursor c = cr.query(Calendars.CONTENT_URI, 
				calendarsProjection, null, null, null);
		//	cursor
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if(c.moveToFirst()){
			Map<String, Integer> indexMap = new HashMap<String, Integer>();
			//	index
			for(int i=0;i<colCount;i++){
				indexMap.put(calendarsProjection[i], c.getColumnIndex(calendarsProjection[i]));
			}
			//	cursor item
			do{
				Map<String, String> map = new HashMap<String, String>();
				for(int i=0;i<colCount;i++){
					String str = c.getString((Integer)indexMap.get(calendarsProjection[i]));
					map.put(calendarsProjection[i], str);
				}
				list.add(map);
			}while(c.moveToNext());
		}else{
			list = null;	//	clear
		}
		
		return list;
	}
	
	public boolean checkWritableCalendar(Map<String, String> map) {
		try{
			String accountType = map.get(Calendars.ACCOUNT_TYPE);
			String accessLevel = map.get(Calendars.CALENDAR_ACCESS_LEVEL);
			String syncEvents = map.get(Calendars.SYNC_EVENTS);
			if(accountType.equals("com.google") && accessLevel.equals("700") && syncEvents.equals("1")){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public long getCalendarId(Map<String, String> map) {
		try{
			String str = map.get(Calendars._ID);
			long id = Long.valueOf(str);
			return id;
		}catch(Exception e){
			return FALSE_ID;
		}
	}
	public String getCalendarName(Map<String, String> map) {
		try{
			String str = map.get(Calendars.CALENDAR_DISPLAY_NAME);
			return str;
		}catch(Exception e){
			return "";
		}
	}
	public int getCalendarColor(Map<String, String> map) {
		try{
			String str = map.get(Calendars.CALENDAR_COLOR);
			int color = Integer.valueOf(str);
			return color;
		}catch(Exception e){
			return Color.GRAY;
		}
	}
	public String getCalendarTimezone(Map<String, String> map) {
		try{
			String str = map.get(Calendars.CALENDAR_TIME_ZONE);
			return str;
		}catch(Exception e){
			return TimeZone.getDefault().getDisplayName();
		}
	}
	
	public boolean hasEventAlarm(Map<String, String> map) {
		try{
			String str = map.get(Events.HAS_ALARM);
			if(str.equals("1")){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	public long getEventId(Map<String, String> map) {
		try{
			String str = map.get(Events._ID);
			long id = Long.valueOf(str);
			return id;
		}catch(Exception e){
			return FALSE_ID;
		}
	}
	
	public long insertEvent(long cal_id, String timezone, 
			String title, String description, String place) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			ContentValues values = new ContentValues();
			//	values
			values.put(Events.CALENDAR_ID, cal_id);
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.EVENT_LOCATION, place);
			values.put(Events.EVENT_TIMEZONE, timezone);
			values.put(Events.DTSTART, Calendar.getInstance().getTimeInMillis());
			values.put(Events.DTEND, Calendar.getInstance().getTimeInMillis());
			//	Uri,long_id
			Uri uri = cr.insert(Events.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			
			return id;			
		}catch(Exception e){
			return FALSE_ID;
		}
	}
	public long insertEvent(long cal_id, String timezone, 
			String title, String description, String place, 
			Calendar start, Calendar end, 
			String hasAlarm) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			ContentValues values = new ContentValues();
			//	values
			values.put(Events.CALENDAR_ID, cal_id);
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.EVENT_LOCATION, place);
			values.put(Events.EVENT_TIMEZONE, timezone);
			values.put(Events.DTSTART, start.getTimeInMillis());
			values.put(Events.DTEND, end.getTimeInMillis());
			values.put(Events.ALL_DAY, "0");	//	時間指定モード
			values.put(Events.HAS_ALARM, hasAlarm);
			//	Uri,long_id
			Uri uri = cr.insert(Events.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			
			return id;
		}catch(Exception e){
			return FALSE_ID;
		}
	}
	public long insertEvent(long cal_id, String timezone, 
			String title, String description, String place, 
			Calendar date, 
			String hasAlarm) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			ContentValues values = new ContentValues();
			//	values
			values.put(Events.CALENDAR_ID, cal_id);
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.EVENT_LOCATION, place);
			values.put(Events.EVENT_TIMEZONE, timezone);
			values.put(Events.DTSTART, date.getTimeInMillis());
			values.put(Events.DTEND, date.getTimeInMillis());
			values.put(Events.ALL_DAY, "1");	//	一日モード
			values.put(Events.HAS_ALARM, hasAlarm);
			//	Uri,long_id
			Uri uri = cr.insert(Events.CONTENT_URI, values);
			long id = Long.parseLong(uri.getLastPathSegment());
			
			return id;
		}catch(Exception e){
			return FALSE_ID;
		}
	}
	
	public boolean deleteEvent(long event_id) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			
			//	Uri,delete
			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
			int rows = cr.delete(uri, null, null);
			if(rows>0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public Map<String, String> selectEvent(long event_id) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			
			int colCount = eventsProjection.length;
			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
			Cursor c = cr.query(uri, eventsProjection, null, null, null);
			//	cursor
			if(c.moveToFirst()){
				Map<String, Integer> indexMap = new HashMap<String, Integer>();
				//	index
				for(int i=0;i<colCount;i++){
					indexMap.put(eventsProjection[i], c.getColumnIndex(eventsProjection[i]));
				}
				//	cursor item
				do{
					Map<String, String> map = new HashMap<String, String>();
					for(int i=0;i<colCount;i++){
						String str = c.getString((Integer)indexMap.get(eventsProjection[i]));
						map.put(eventsProjection[i], str);
					}
					return map;
				}while(c.moveToNext());
			}else{
				return null;	//	clear
			}
		}catch(Exception e){
			return null;
		}
	}
	public List<Map<String, String>> selectEvents(String timezone, Calendar start, Calendar end) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			
			int colCount = eventsProjection.length;
			String selection = Events.DTSTART + ">=? AND " + Events.DTEND + "<=?";
			String[] selectionArgs = new String[]{
				String.valueOf(start.getTimeInMillis()), String.valueOf(end.getTimeInMillis())
			};
			Cursor c = cr.query(Events.CONTENT_URI, 
					eventsProjection, selection, selectionArgs, null);
		//			eventsProjection, null, null, null);
			//	cursor
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if(c.moveToFirst()){
				Map<String, Integer> indexMap = new HashMap<String, Integer>();
				//	index
				for(int i=0;i<colCount;i++){
					indexMap.put(eventsProjection[i], c.getColumnIndex(eventsProjection[i]));
				}
				//	cursor item
				do{
					Map<String, String> map = new HashMap<String, String>();
					for(int i=0;i<colCount;i++){
						String str = c.getString((Integer)indexMap.get(eventsProjection[i]));
						map.put(eventsProjection[i], str);
					}
					list.add(map);
				}while(c.moveToNext());
				return list;
			}else{
				return null;	//	clear
			}
		}catch(Exception e){
			return null;
		}
	}
	public List<Map<String, String>> selectEvents(String title) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			
			int colCount = eventsProjection.length;
			String selection = Events.TITLE + "=?";
			String[] selectionArgs = new String[]{
				title
			};
			Cursor c = cr.query(Events.CONTENT_URI, 
					eventsProjection, selection, selectionArgs, null);
		//			eventsProjection, null, null, null);
			//	cursor
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if(c.moveToFirst()){
				Map<String, Integer> indexMap = new HashMap<String, Integer>();
				//	index
				for(int i=0;i<colCount;i++){
					indexMap.put(eventsProjection[i], c.getColumnIndex(eventsProjection[i]));
				}
				//	cursor item
				do{
					Map<String, String> map = new HashMap<String, String>();
					for(int i=0;i<colCount;i++){
						String str = c.getString((Integer)indexMap.get(eventsProjection[i]));
						map.put(eventsProjection[i], str);
					}
					list.add(map);
				}while(c.moveToNext());
				return list;
			}else{
				return null;	//	clear
			}
		}catch(Exception e){
			return null;
		}
	}
	
	/*public boolean updateEvent(long event_id, long cal_id, String timezone, 
			String title, String description, String place, 
			Calendar start, Calendar end, 
			String hasAlarm, String allDay) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			ContentValues values = new ContentValues();
			//	values
			values.put(Events.CALENDAR_ID, cal_id);
			values.put(Events.TITLE, title);
			values.put(Events.DESCRIPTION, description);
			values.put(Events.EVENT_LOCATION, place);
			values.put(Events.EVENT_TIMEZONE, timezone);
			values.put(Events.DTSTART, start.getTimeInMillis());
			values.put(Events.DTEND, end.getTimeInMillis());
			values.put(Events.ALL_DAY, allDay);
			values.put(Events.HAS_ALARM, hasAlarm);
			//	Uri,long_id
			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
			String where = Events._ID + "=?";
			String[] selectionArgs = new String[]{
				String.valueOf(event_id)
			};
			int rows = cr.update(uri, values, where, selectionArgs);
			if(rows>0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}*/
	public boolean updateEvent(long event_id, long cal_id, String timezone, 
			String title, String description, String place, 
			Calendar start, Calendar end, 
			String hasAlarm, String allDay) {
		try{
			ContentResolver cr = this.context.getContentResolver();
			ContentValues values = new ContentValues();
			//	values
			if(cal_id!=FALSE_ID)	values.put(Events.CALENDAR_ID, cal_id);
			if(title!=null)	values.put(Events.TITLE, title);
			if(description!=null)	values.put(Events.DESCRIPTION, description);
			if(place!=null)	values.put(Events.EVENT_LOCATION, place);
			if(timezone!=null)	values.put(Events.EVENT_TIMEZONE, timezone);
			if(start!=null)	values.put(Events.DTSTART, start.getTimeInMillis());
			if(end!=null)	values.put(Events.DTEND, end.getTimeInMillis());
			if(allDay!=null)	values.put(Events.ALL_DAY, allDay);
			if(hasAlarm!=null)	values.put(Events.HAS_ALARM, hasAlarm);
			//	Uri,long_id
			String where = Events._ID + "=?";
			String[] selectionArgs = new String[]{
				String.valueOf(event_id)
			};
			int rows = cr.update(Events.CONTENT_URI, values, where, selectionArgs);
			if(rows>0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
}
