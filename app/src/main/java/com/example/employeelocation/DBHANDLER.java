package com.example.employeelocation;

// junction Software pvt ltd
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DBHANDLER extends SQLiteOpenHelper {
	Context context;

	public DBHANDLER(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE EMP(data TEXT, status TEXT)");

		Log.d("onCreate()", "RUN");
		Toast.makeText(context, "onCreate()  \n Table Created",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("Drop Table EMP");

		Log.d("onUpgrade()", "Run");
		Toast.makeText(context, "onUpgrade() \n Table Drop", Toast.LENGTH_LONG)
				.show();
		onCreate(db);

	}

	public void addData(String data, String status) {

		SQLiteDatabase db = super.getWritableDatabase();
		ContentValues c1 = new ContentValues();
		// c1.put("ID", this.j);
		c1.put("data", data);
		c1.put("status", status);

		if (db.insert("EMP", null, c1) == -1) {
		//	Toast.makeText(context, "Problem", Toast.LENGTH_LONG).show();
			Toast.makeText(context, "FAILL!!!!", Toast.LENGTH_LONG)
					.show();
		} else
		
			Toast.makeText(context, "New Entry Successfull", Toast.LENGTH_LONG)
					.show();
		/*
		 * } else Toast.makeText(c, "Duplicate entry not allowed",
		 * Toast.LENGTH_LONG).show(); //
		 */
		db.close();

	}

	public void putStatus(String abc, String status) {
		SQLiteDatabase db = super.getWritableDatabase();
		Cursor cq = db.rawQuery("Select * from EMP where data=?",
				new String[] { abc });
		while (cq.moveToNext()) {
			db.delete("EMP", null,null);
			Toast.makeText(context, "Deleted", Toast.LENGTH_LONG)
			.show();
		}

		cq.close();
		db.close();

	}



	public String[] search() {
		
	SQLiteDatabase db = super.getReadableDatabase();
	
		Cursor cs1 = db.rawQuery("Select * from EMP where status=?", new String[]{"false"});

		 String s[]=new String[cs1.getCount()];
		 int i=0;
		while (cs1.moveToNext()) {
			 s[i++]=cs1.getString(cs1.getColumnIndex("data"));

		}

		cs1.close();
		db.close();
	
		return s;
	}

}
