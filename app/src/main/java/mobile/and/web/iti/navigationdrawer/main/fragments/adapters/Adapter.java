package mobile.and.web.iti.navigationdrawer.main.fragments.adapters;

/**
 * Created by Alaa on 3/3/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mobile.and.web.iti.navigationdrawer.update.view.trips.User;

public class Adapter {

    private Context con ;

    Helper dbHelp ;

    public Adapter(Context _context){
        dbHelp=new Helper(_context);
        con = _context;

    }


    public long insertRow(User user)
    {

        SQLiteDatabase db = dbHelp.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dbHelp.fName, user.getfName());
        values.put(dbHelp.lName, user.getlName());
        values.put(dbHelp.password , user.getPassword());
        values.put(dbHelp.confPass, user.getConfPass());
        values.put(dbHelp.email, user.getEmail());
        values.put(dbHelp.gender , user.getGender());

        long id = db.insert(dbHelp.TABLE_NAME,null,values);
        Log.i("nella", user.getGender());

        return id ;


    }



    public User retrievRow (){

        Cursor c ;
        User u = new User();
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        Log.i("nela ","3ml eldb");
        String[] columns = {dbHelp.fName ,dbHelp.lName,dbHelp.password,dbHelp.confPass,dbHelp.email,dbHelp.gender };
        Log.i("nela ","3ml elarray");

        Log.i("nela", columns[0]);

        //  String query = "SELECT"+dbHelp.MSM+ ","+dbHelp.Ph+"FROM" +dbHelp.TABLE_NAME ;
        //  c = db.rawQuery(query,null);

        c = db.query(dbHelp.TABLE_NAME , columns , null , null ,null , null , null );
        Log.i("nela ","3ml el query");

        while (c.moveToNext()){
            u.setfName(c.getString(0));
            u.setlName(c.getString(1));
            u.setPassword(c.getString(2));
            u.setConfPass(c.getString(3));
            u.setEmail(c.getString(4));
            u.setGender(c.getString(5));



        }
        return  u ;
    }





    static   class  Helper extends SQLiteOpenHelper {


        static String DATABASE_NAME = "mydb.db";
        static int DATABASE_VERSION = 1;
        static String TABLE_NAME = "USER_TABLE";
        static String fName ="First_Name";
        static String lName ="Last_Name";
        static String password="Password";
        static String confPass="Confirm_Password";
        static String email="Email";
        static String gender="Gender";

//        static String MSM = "MSM";
//        static String Ph = "Phone";
//        String SQL_CREATE = "CREATE TABLE " +TABLE_NAME+" ("+MSM+" TEXT PRIMARY KEY ,"+ Ph +" TEXT)";

        public Helper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+TABLE_NAME+"( id INTEGER PRIMARY KEY AUTOINCREMENT , " + fName + " TEXT ,"+ lName +" TEXT ," +password +" TEXT ,"+confPass+" TEXT,"+ email +" TEXT,"+gender + " TEXT  );");

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


        }


    }

}
