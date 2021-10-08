package com.some.notes;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.some.notes.Adapter.PersonAdapter;
import com.some.notes.Model.Chat;
import com.some.notes.Model.CityDataItem;
import com.some.notes.Model.Comment;
import com.some.notes.Model.GroupMessage;
import com.some.notes.Model.Person;
import com.some.notes.Model.SampleDataProvider;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.SqlUser;
import com.some.notes.Model.User;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SqlMessageActivity extends AppCompatActivity {


    SqlChatsActivity.PersonChatsUserDBHelper helper;
    public static String sqlId,id;
    public static String hello = "hellosuri";
    DatabaseReference RootRef;
     ImageView reply_image;

    RelativeLayout reply_bottom,relative_layout_message;
    CircleImageView profile_image;
    TextView username, inputPassword, showText_txt, userLastSeen, messageConvert,reply_preview_message,reply_delete,type;
    public static FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send, SendFilesButton,reply_send;
    EditText text_send,reply_edit,reply_password;
    ImageView background_image;
    List<Comment> mchat = new ArrayList<>();
    RecyclerView recyclerView, findFriendLis;
    Intent intent;
    ValueEventListener seenListener;
    public static String userid;
    String messageid, AES = "AES", cheker = "", myUrl = "", saveCurrentTime, saveCurrentDate,saveCurrentTime1, saveCurrentDate1,trans_msg,outputString;;
    private static final int RESULT_LOAD_IMAGE = 1;


    private String filter = "";
    private PersonDBHelper dbHelper;
    private PersonAdapter adapter;
    long receivedPersonId;

    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        RootRef = FirebaseDatabase.getInstance().getReference();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        intent = getIntent();
        userid = intent.getStringExtra("userid");
        id = intent.getStringExtra("name");
        try {
            //get intent to get person id
            receivedPersonId = intent.getLongExtra("USER_ID", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        populaterecyclerView(filter);


    }


    private void populaterecyclerView(String filter) {
        dbHelper = new PersonDBHelper(this);
        adapter = new PersonAdapter(dbHelper.peopleList(filter), this);
        recyclerView.setAdapter(adapter);
    }

    public static class PersonDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = sqlId+"hello.db";
        private static final int DATABASE_VERSION = 3;
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_SENDER = "sender";
        public static final String COLUMN_PERSON_RECIEVER = "recicver";
        public static final String COLUMN_PERSON_IMAGEURL = "image";
        public static final String COLUMN_PERSON_MESSAGEID = "messageID";
        public static final String COLUMN_PERSON_RANDOMID = "randomId";
        public static final String COLUMN_PERSON_DATE = "date";
        public static final String COLUMN_PERSON_TIME = "time";
        public static final String COLUMN_PERSON_TYPE = "type";
        public static final String COLUMN_PERSON_MESSAGE = "message";
        public static final String COLUMN_PERSON_PREVIEW = "preview";
        public static final String COLUMN_PERSON_BIO = "bio";
        public static final String COLUMN_PERSON_ISSEEN = "isseen";

        public static final String COLUMN_PERSON_LASTSENDMESSAGE = "lastsendmessage";


        public PersonDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PERSON_MESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_SENDER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_RECIEVER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_MESSAGEID + " TEXT NOT NULL, " +
                    COLUMN_PERSON_BIO + " TEXT NOT NULL, " +
                    COLUMN_PERSON_TYPE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_TIME + " TEXT NOT NULL, " +
                    COLUMN_PERSON_DATE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_LASTSENDMESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_PREVIEW + " TEXT NOT NULL, " +
                    COLUMN_PERSON_ISSEEN + " TEXT NOT NULL, " +
                    COLUMN_PERSON_IMAGEURL + " BLOB NOT NULL);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // you can implement here migration process
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }

        /**
         * create record
         **/
        public void saveNewPerson(SqlMessage sqlUser) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PERSON_MESSAGE, sqlUser.getMessage());
            values.put(COLUMN_PERSON_SENDER, sqlUser.getSender());
            values.put(COLUMN_PERSON_RECIEVER, sqlUser.getReceiver());
            values.put(COLUMN_PERSON_MESSAGEID, sqlUser.getMessageID());
            values.put(COLUMN_PERSON_BIO, sqlUser.getBio());
            values.put(COLUMN_PERSON_TYPE, sqlUser.getType());
            values.put(COLUMN_PERSON_TIME, sqlUser.getTime());
            values.put(COLUMN_PERSON_DATE, sqlUser.getDate());
            values.put(COLUMN_PERSON_LASTSENDMESSAGE, sqlUser.getLastSendMessage());
            values.put(COLUMN_PERSON_PREVIEW, sqlUser.getPreMessage());
            values.put(COLUMN_PERSON_ISSEEN, sqlUser.getIsseen());
            values.put(COLUMN_PERSON_IMAGEURL, sqlUser.getImagrUrl());

            // insert
            db.insert(TABLE_NAME, null, values);
            db.close();
        }

        /**
         * Query records, give options to filter results
         **/
        public List<SqlMessage> peopleList(String filter) {
            String query;
            if (filter.equals("")) {
                //regular query
                query = "SELECT  * FROM " + TABLE_NAME;
            } else {
                //filter results by filter option provided
                query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + filter;
            }

            List<SqlMessage> personLinkedList = new LinkedList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            SqlMessage person;




            if (cursor.moveToFirst()) {
                do {
                    person = new SqlMessage();

                    person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    person.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGE)));
                    person.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                    person.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                    person.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                    person.setBio(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_BIO)));
                    person.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                    person.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TIME)));
                    person.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_DATE)));
                    person.setLastSendMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTSENDMESSAGE)));
                    person.setPreMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PREVIEW)));
                    person.setIsseen(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));
                    person.setImagrUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
                    personLinkedList.add(person);
                } while (cursor.moveToNext());
            }

            return personLinkedList;
        }

        /**
         * Query only 1 record
         **/
        public SqlMessage getPerson(long id) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT  * FROM " + TABLE_NAME + " WHERE _id=" + id;
            Cursor cursor = db.rawQuery(query, null);

            SqlMessage receivedPerson = new SqlMessage();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                receivedPerson.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGE)));
                receivedPerson.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                receivedPerson.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                receivedPerson.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                receivedPerson.setBio(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_BIO)));
                receivedPerson.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                receivedPerson.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TIME)));
                receivedPerson.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_DATE)));
                receivedPerson.setLastSendMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTSENDMESSAGE)));
                receivedPerson.setPreMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_PREVIEW)));
                receivedPerson.setIsseen(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_ISSEEN)));
                receivedPerson.setImagrUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
            }


            return receivedPerson;


        }


        /**
         * delete record
         **/
        public void deletePersonRecord(long id, Context context) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id='" + id + "'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();

        }

        public void deletePersonRecord(String id, Context context,String firebaseId) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE messageID='" + id + "'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();

            DatabaseReference rf = FirebaseDatabase.getInstance().getReference("DeleteSingleUserMessage").child(userid).child(fuser.getUid());

            rf.child(firebaseId).removeValue();

        }


        public void updateIsseenRecord(long personId, Context context, String sqlMessage) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  " + TABLE_NAME + " SET isseen ='" + "true" + "'  WHERE _id='" + personId + "'");
        }
    }

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
        private List<SqlMessage> mChat;
        private Context mContext;






          public class ViewHolder extends RecyclerView.ViewHolder {


            public ViewHolder(View itemView) {
                super(itemView);


            }
        }



        public PersonAdapter(List<SqlMessage> myDataset, Context context) {
            mChat = myDataset;
            mContext = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
                return new ViewHolder(view);

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {


        }



            @Override
        public int getItemCount() {
            return mChat.size();
        }


    }

    private String generateString(int lenth) {
        char[] chasr = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lenth; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AEs");
        return secretKeySpec;

    }

}