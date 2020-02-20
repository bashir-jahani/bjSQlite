package bj.modules.databases.bjSQLite_objects;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

import bj.modules.databases.bjSQLiteDatabase;
import bj.sqlite.R;


public class bjGridView extends LinearLayout {
	private static final String TAG="bjGridView";
	private LinearLayout headerBar;
	private TableLayout dataLayout;
	private ScrollView scrollView;
	private String tableName;

	private ColumnInfo[] mColumnsInfo;
	private bjSQLiteDatabase database;
	private TextView[] headers;

	public bjGridView(Context context) {
		super(context);
	}

	public bjGridView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public bjGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public bjGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setGridView(bjSQLiteDatabase database, String tableName){
		this.database=database;
		headerBar =new LinearLayout(getContext());
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		scrollView=new ScrollView(getContext());
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));


		headerBar.setLayoutParams(layoutParams);
		headerBar.setBackground(getResources().getDrawable(R.drawable.border_corner5));

		dataLayout =new TableLayout(getContext());
		dataLayout.setOrientation(LinearLayout.VERTICAL);
		this.tableName=tableName;
		this.setOrientation(LinearLayout.VERTICAL);
		loadTitles();


	}

	public LinearLayout getHeaderBar(){
		return headerBar;
	}
	public void setHeaderBar(LinearLayout headerBar){
		this.headerBar=headerBar;
	}
	public void setHeaders (TextView[] headers){
		this.headers=headers;
	}
	public TextView[] getHeaders(){
		return headers;
	}
	public TextView getHeader (String columnName){
		for (int i=0;i<mColumnsInfo.length;i++){
			if (mColumnsInfo[i].name.equals(columnName)){
				return headers[i];
			}
		}
		return null;
	}
	public void setHeader (String columnName,TextView newHeader){
		for (int i=0;i<mColumnsInfo.length;i++){
			if (mColumnsInfo[i].name.equals(columnName)){
				headers[i]=newHeader;
				return;
			}
		}
	}
	public ColumnInfo[] getColumnsInfo(){
		return mColumnsInfo;
	}

	public void setColumnInfo(String columnName, ColumnInfo newInfo){
		for (int i=0;i<mColumnsInfo.length;i++){
			if (mColumnsInfo[i].name.equals(columnName)){
				mColumnsInfo[i].name=newInfo.name;
				mColumnsInfo[i].header=newInfo.header;
				mColumnsInfo[i].caption=newInfo.caption;
				mColumnsInfo[i].dataKind=newInfo.dataKind;
				mColumnsInfo[i].width=newInfo.width;
				mColumnsInfo[i].widthPercent=newInfo.widthPercent;
				loadTitles();
			}
		}

	}
	public ColumnInfo getColumnInfo(String columnName){
		for (int i=0;i<mColumnsInfo.length;i++){
			//Log.e("////////////////////////","name: '"+mColumnsInfo[i].name + "' = '"+ columnName+"'" );
			if (mColumnsInfo[i].name.equals(columnName)){
				//Log.e("////////////////////////","find" );
				return mColumnsInfo[i];
			}
		}

		return null;
	}
	public void setCulumns(JSONArray info){
		boolean refresh=false;
		for (int i=0;i<info.length();i++){
			JSONObject inf=null;
			try{
				inf=info.getJSONObject(i);
			}catch (JSONException e){

			}
			try {
				if (inf==null){
					inf=new JSONObject(info.getString(i));
				}
			}catch (JSONException e){
				if (inf==null){
					return;
				}
			}
			try{



				if (inf.has("name")){
					refresh=true;
					ColumnInfo cinf=getColumnInfo(inf.getString("name"));
					if (cinf!=null){
						if (inf.has("visible")){
							cinf.visible=inf.getBoolean("visible");
						}
						if (inf.has("caption")){
							cinf.caption=inf.getString("caption");
						}
						if (inf.has("header")){
							cinf.header=inf.getString("header");
						}
						if (inf.has("widthPercent")){
							cinf.widthPercent=inf.getInt("widthPercent");
						}
						if (inf.has("width")){
							cinf.width=inf.getInt("width");
						}
						if (inf.has("index")){
							cinf.index=inf.getInt("index");
						}
					}
					setColumnInfo(inf.getString("name"),cinf);


				}
			}catch (JSONException e){

			}
		}
		if (refresh){
			loadTitles();
		}
	}
	public void refreshColumnsInfo(){

		Log.e("*/*/*/*/*/*/*/*/*/*/*/","mColumnsInfo Proces");
		JSONArray columns=database.columns(tableName);
		mColumnsInfo=new ColumnInfo[columns.length()];
		for (int i=0 ;i<columns.length();i++){
			ColumnInfo cInfo = null;
			try {
				cInfo=new ColumnInfo(columns.getJSONObject(i).getString("name"),columns.getJSONObject(i).getString("name"),columns.getJSONObject(i).getString("name"),columns.getJSONObject(i).getInt("type"),0,Integer.parseInt(String.valueOf(100/columns.length())),true,i);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		if (cInfo!=null)
			mColumnsInfo[i]=cInfo;
		else
			mColumnsInfo[i]=new ColumnInfo();
		}

	}
	private void loadTitles() {
		if (mColumnsInfo == null || mColumnsInfo.length==0){
			refreshColumnsInfo();
		}
		Arrays.sort(mColumnsInfo,new ColumnInfo.SortbyIndex());

		headerBar.setOrientation(LinearLayout.HORIZONTAL);
		//Log.e("*/*/*/*/*/*/*/*/*/*/*/","mColumnsInfo is null: "+(mColumnsInfo==null));
		headers=new TextView[mColumnsInfo.length];

		for (int i=0;i<mColumnsInfo.length;i++){
			TextView textView=new TextView(getContext());
			textView.setId(i);
			textView.setText(mColumnsInfo[i].header);
			LayoutParams tParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			if (mColumnsInfo[i].width==0){
				tParam.width=0;
				tParam.weight=mColumnsInfo[i].widthPercent;
				tParam.height=LayoutParams.WRAP_CONTENT;
				tParam.gravity= Gravity.START;


			}
			textView.setPadding(5,5,5,5);

			textView.setLayoutParams(tParam);
			if (mColumnsInfo[i].visible) {
				textView.setVisibility(VISIBLE);
			}else{
				textView.setVisibility(GONE);
			}

			headers[i]=textView;
		}
		refreshHeaders(false);
	}
	public void refreshHeaders(boolean reSortNeeded){
		if (reSortNeeded){
			loadTitles();
		}
		if(headerBar.getChildCount() > 0)
			headerBar.removeAllViews();

		for (int i=0 ;i<headers.length;i++){

			headerBar.addView(headers[i]);
		}
		this.removeView(headerBar);
		this.addView(headerBar);
	}
	public void loadData( @Nullable String whereSQL,@Nullable String[] whereARGS){
		dataLayout.removeAllViews();
		bjSQLiteDatabase.dataTableRows rows= database.rowsSelect(tableName,whereSQL,whereARGS);
		if (rows.rowsCount>0){
			for (int i=0;i<rows.rowsCount;i++){
				LinearLayout LLrow=new LinearLayout(getContext());
				LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(0,2,0,2);
				LLrow.setLayoutParams(layoutParams);
				LLrow.setBackground(getResources().getDrawable(R.drawable.border_corner5));
				for (int j=0;j<mColumnsInfo.length;j++){
					TextView textView=new TextView(getContext());
					textView.setId(j);
					textView.setText(rows.columnValue(i,mColumnsInfo[j].name));
					LayoutParams tParam=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					if (mColumnsInfo[j].width==0){
						tParam.width=0;
						tParam.weight=mColumnsInfo[j].widthPercent;
						tParam.height=LayoutParams.MATCH_PARENT;
						tParam.gravity= Gravity.START;


					}
					textView.setPadding(5,5,5,5);

					textView.setLayoutParams(tParam);
					if (mColumnsInfo[j].visible) {
						textView.setVisibility(VISIBLE);
					}else{
						textView.setVisibility(GONE);
					}
					textView.setBackground(getResources().getDrawable(R.drawable.borde_lr));
					textView.setPadding(2,2,2,2);
					LLrow.addView(textView);
				}

				dataLayout.addView(LLrow);
			}
		}else {
			LinearLayout LLrow=new LinearLayout(getContext());
			LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,50);
			LLrow.setLayoutParams(layoutParams);
			LLrow.setBackground(getResources().getDrawable(R.drawable.border_corner5));
			dataLayout.addView(LLrow);

		}
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg","dataLayout Child Count"+dataLayout.getChildCount());
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg","scrollView Child Count"+scrollView.getChildCount());
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg","this Child Count"+this.getChildCount());

		scrollView.removeAllViews();
		this.removeView(scrollView);
		scrollView.addView(dataLayout);

		this.addView(scrollView);
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg#","dataLayout Child Count"+dataLayout.getChildCount());
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg#","scrollView Child Count"+scrollView.getChildCount());
		//Log.e("gggggggggggggggggggggggggggggggggggggggggggggggg#","this Child Count"+this.getChildCount());
	}
	public static class ColumnInfo {
		public String name;
		public String caption;
		public String header;
		public int dataKind;
		public int width;
		public int widthPercent;
		public boolean visible=true;
		public int index;
		public String value;
		public ColumnInfo() {
		}

		public ColumnInfo(String name, String caption, String header, boolean visible, int index) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.visible = visible;
			this.index = index;
		}

		public ColumnInfo(String name, String caption, String header, boolean visible) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.visible = visible;
		}

		public ColumnInfo(String name, String caption, String header, int dataKind, int width, int widthPercent) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.dataKind = dataKind;
			this.width = width;
			this.widthPercent = widthPercent;
		}

		public ColumnInfo(String name, String caption, String header, int dataKind, int index) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.dataKind = dataKind;
			this.index = index;
		}

		public ColumnInfo(String name, String caption, String header, int dataKind) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.dataKind = dataKind;
		}

		public ColumnInfo(String name, String caption, String header) {
			this.name = name;
			this.caption = caption;
			this.header = header;
		}

		public ColumnInfo(String name, String caption, String header, int dataKind, int width, int widthPercent, boolean visible, int index) {
			this.name = name;
			this.caption = caption;
			this.header = header;
			this.dataKind = dataKind;
			this.width = width;
			this.widthPercent = widthPercent;
			this.visible = visible;
			this.index = index;
		}
		public static class SortbyIndex implements Comparator<ColumnInfo>
		{
			// Used for sorting in ascending order of
			// roll number
			public int compare(ColumnInfo a, ColumnInfo b)
			{
				//Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$","a index:"+a.index+ " b index:"+b.index);

				//Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$","return"+(a.index - b.index));
				return a.index - b.index;
			}
		}
	}

}
