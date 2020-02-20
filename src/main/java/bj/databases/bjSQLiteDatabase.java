package bj.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class bjSQLiteDatabase {

	private static final String TAG ="bjSQLiteDatabase" ;
	public static String  SDK_DIR= Environment.getExternalStorageDirectory().getAbsolutePath();
	private  String APP_DIR;
	private String DataBaseName;
	private Context context;

	private static final boolean replaceFile=false	;

	public bjSQLiteDatabase(Context context, String databaseFolderName, String dataBaseName, String CallFrom){
		APP_DIR=SDK_DIR+"/"+databaseFolderName+"/";
		DataBaseName=dataBaseName;
		setDataBase(CallFrom);

	}
	private void setDataBase( String CallFrom) {
		this.context=context;
		//Log.e(databaseTAG, " Call From:"+CallFrom);
		File file=new File( APP_DIR);
		File file1=new File(APP_DIR+DataBaseName);
		if(!file.exists()){
			try{
				file.mkdirs();
				file.createNewFile();


			}catch(IOException e){
				//Log.e(databaseTAG, " create Folder error:"+ e.getMessage());
				e.printStackTrace();
			}
		}else {
			if (replaceFile){

				try{
					file1.delete();
					//Log.e(databaseTAG, " delete last:"+ file1.exists());
					copyFromAssets(context.getAssets().open(DataBaseName),
							new FileOutputStream(APP_DIR+DataBaseName));
					//Log.e(databaseTAG, " new File exist:"+ file1.exists());
				}catch (IOException e){
					//Log.e(databaseTAG, " Copy database File error:"+ e.getMessage());
				}
			}

		}

		//Log.e(databaseTAG,"data base file exist: "+(new File(APP_DIR+"/pushNoteAdmin.sqlite")).exists());



	}
	private SQLiteDatabase openDatabase(){
		SQLiteDatabase database;
		try{
			database= SQLiteDatabase. openOrCreateDatabase(APP_DIR+"/pushNoteAdmin.sqlite",null);
		}catch (SQLException e){
			return null;
		}
		return database;
	}

	private void copyFromAssets(InputStream inputStream, FileOutputStream outputStream) {
		byte[] buffer=new byte[1024];
		int length;
		try {
			while((length=inputStream.read(buffer))>0){
				outputStream.write(buffer,0,length);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public boolean rowInsert(String tableName,JSONObject data,String indexColumnName,boolean updateIfExist){
		SQLiteDatabase database;
		database=openDatabase();
		if (database==null){
			Log.e(TAG,"Error in rowInsert: Cant open database!");
			return false;
		}
		String sql;
		boolean updatedata=false;
		String indexValue="";
		try{
			indexValue=data.getString(indexColumnName);
			if (rowsSelect(tableName,indexColumnName +" = '"+indexValue+"'",null).rowsCount>0){
				if (!updateIfExist){
					Log.e(TAG,"Record exist.");
					return false;
				}else {
					updatedata=true;
					Log.e(TAG,"Record exist but be update.");
				}
			}
		}catch (JSONException e){
			Log.e(TAG,"Error to Check that record exist. "+e.getMessage());
		}
		Cursor cursor=	database.rawQuery("select * from " + tableName +" ",null);

		String[] columns=cursor.getColumnNames();
		String[] values=new String[cursor.getColumnCount()];

		for (int i=0;i<columns.length;i++){
			//Log.e(databaseTAG,columns[i].toString());
			if (data.has(columns[i])) {
				try{
					values[i] = data.getString(columns[i]);
				}catch (JSONException e){
					values[i] =null;
				}
			}else {
				values[i]=null;
			}
		}
		if(updatedata) {
			sql = "UPDATE " + tableName + " SET ";
			for (int i=0;i<columns.length;i++){
				if (i==0) {
					sql = sql +columns[i] + "='" + values[i]+"'";
				}else {
					sql = sql+"," +columns[i] + "='" + values[i]+"'";
				}
			}
			sql	=sql+ " where "+indexColumnName +" = '"+indexValue+"'";
		}else {
			sql="INSERT INTO "+tableName+" (";
			for (int i=0;i<columns.length;i++){
				if (i==0) {
					sql = sql +columns[i];
				}else {
					sql=sql	+","+columns[i];
				}
			}
			sql=sql+") VALUES(";
			for (int i=0;i<columns.length;i++){
				if (i==0) {
					sql = sql+"'" +values[i]+"'";
				}else {
					sql=sql	+","+"'" +values[i]+"'";
				}
			}
			sql=sql+")";
		}

		//Log.e(databaseTAG,sql);
		database.execSQL(sql);
		Cursor res= null;
		try {
			res = database.rawQuery("select * from "+tableName+" where "+indexColumnName + " = '"+data.getString(indexColumnName)+"'",null);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if( res.getCount()==0) {
			database.close();
			return false;
		}else{
			database.close();
			return true;
		}

	}
	public dataTableRows rowsSelect(String tableName, String whereSql, String[] whereArgs) {

		//JSONObject data = new JSONObject();
		dataTableRows data=new dataTableRows();
		SQLiteDatabase database;
		database=openDatabase();
		if (database==null){
			Log.e(TAG,"Error in rowsSelect: Cant open database!");
			data.rowsCount=0;
			//data.put("count",0);
			data.columnsDetail=new JSONArray();
			//data.put("columns", new String[0]);
			data.rows=new JSONArray();
			//data.put("rows", new JSONArray());

			return data;
		}
		try {
			String sql;
			sql = "select * from "+tableName+" ";
			if (whereSql.length() > 0) {
				sql = sql + " where " + whereSql;
			}

			//Log.e(TAG,sql);
			try{
				Cursor cursor = database.rawQuery(sql, whereArgs);
				String[] columns = cursor.getColumnNames();
				String[] values = new String[cursor.getColumnCount()];
				JSONArray rows = new JSONArray();
				JSONArray columnsDetail = new JSONArray();
				if (cursor.getCount()==0){
					Log.e(TAG,"No Row found!");
					data.rowsCount=0;
					//data.put("count",0);
					data.columnsDetail=new JSONArray();
					//data.put("columns", new String[0]);
					data.rows=new JSONArray();
					//data.put("rows", new JSONArray());
					return data;
				}
				//Log.e("************************","sql: "+sql+System.lineSeparator()+"Count: "+cursor.getCount());






				cursor.moveToFirst();
				//Log.e("************************","columns count: "+columns.length);
				for (int i = 0; i < columns.length; i++) {
					//Log.e("************************",columns[i]+":"+cursor.getString(cursor.getColumnIndex(columns[i])));
					//Log.e(databaseTAG,columns[i].toString());
					JSONObject ct=new JSONObject();

					ct.put("type",cursor.getType(cursor.getColumnIndex(columns[i])));
					ct.put("name",columns[i]);
					columnsDetail.put(ct);

				}
				while (cursor.isAfterLast() == false) {
					//Log.e("************************",cursor.getString(cursor.getColumnIndex(indexColumnName))+"***********");
					JSONObject dataRow = new JSONObject();

					for (int i = 0; i < columns.length; i++) {
						//Log.e("************************",columns[i]+":"+cursor.getString(cursor.getColumnIndex(columns[i])));
						//Log.e(databaseTAG,columns[i].toString());

						if (cursor.getColumnIndex(columns[i])<0) {

							dataRow.put(columns[i], null);
						}else {
							dataRow.put(columns[i], cursor.getString(cursor.getColumnIndex(columns[i])));
						}


					}
					rows.put(dataRow);

					//Log.e(databaseTAG, cursor.getString(cursor.getColumnIndex("mID")) + "/" + cursor.getString(cursor.getColumnIndex("name")));
					cursor.moveToNext();
				}



				data.rowsCount=cursor.getCount();
				//data.put("count",cursor.getCount());
				data.columnsDetail=columnsDetail;
				//data.put("columns", columnsDetail);
				data.rows=rows;
			}catch (SQLException e1){

			}
			//data.put("rows", rows);

			//Log.e("****************************************","rows 0: "+data.getJSONArray("columns").toString());
		}catch (JSONException e){
			Log.e("************************","error: "+e.getMessage());
		}
		database.close();
		return data;

	}
	public boolean rowDelete(String tableName, String indexColumnName,String indexValue){
		SQLiteDatabase database;
		database=openDatabase();
		if (database==null){
			Log.e(TAG,"Error in rowDelete: Cant open database!");
			return false;
		}
		String sql="DELETE FROM " + tableName+ " WHERE "+indexColumnName + "='"+indexValue+"'";
		try{
			database.execSQL(sql);
		}catch (SQLException e){

		}
		database.close();
		if (rowsSelect(tableName,indexColumnName +" = '"+indexValue+"'",null).rowsCount>0) {
			return false;
		}else {
			return true;
		}

	}
	public JSONArray columns(String tableName){
		SQLiteDatabase database;
		database=openDatabase();
		String sql;
		sql = "select * from " + tableName +" LIMIT 1" ;
		JSONArray columnsDetail = new JSONArray();
		try{
			Cursor cursor = database.rawQuery(sql,null);
			cursor.moveToFirst();
			String[] columns = cursor.getColumnNames();


			if (columns.length>0){
				for (int i = 0; i < columns.length; i++) {
					//Log.e("************************","i:"+i + " columns length:"+columns.length);
					//Log.e(databaseTAG,columns[i].toString());
					JSONObject ct=new JSONObject();

					try	{
						ct.put("type",cursor.getType(cursor.getColumnIndex(columns[i])));
						ct.put("name",columns[i]);
					}catch (JSONException e){

					}
					columnsDetail.put(ct);

				}
			}
		}catch (SQLiteException e){

		}

		return columnsDetail;
	}
	public class dataTableRows{
		public int rowsCount;
		public JSONArray columnsDetail;
		public JSONArray rows;
		public JSONObject row(int rowIndex){
			try {
				return rows.getJSONObject(rowIndex);
			} catch (JSONException e) {
				return null;
			}
		}
		public String columnValue(int rowIndex,String columnName){
			try {
				return row(rowIndex).getString(columnName);
			} catch (JSONException e) {
				return null;
			}
		}

	}
}
