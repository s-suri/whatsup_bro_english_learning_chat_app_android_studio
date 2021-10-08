package com.some.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.some.notes.Adapter.PersonEmailDBHelper;
import com.some.notes.ImoticonsText.Android8.Android8EmoticonProvider;
import com.some.notes.ImoticonsText.widget.EmoticonTextView;
import com.some.notes.Model.Chat;
import com.some.notes.Model.Comment;

import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.GroupMessage;
import com.some.notes.Model.ItemClickListener;
import com.some.notes.Model.Person;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.SqlUser;
import com.some.notes.Notifications.Token;
import com.some.notes.Uploads.MainActivityUpload;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.icu.text.MessagePattern.ArgType.SELECT;
import static com.some.notes.SqlChatsActivity.PersonChatsUserDBHelper.TABLE_NAME;

public  class  SqlChatsActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 600000000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private static final int PERMISSION_REQUEST_CODE = 101;
    boolean lastMessage = false;
    private MassageActivityInstagram.PersonDBHelper sqliteHelper;
    private RecyclerView.LayoutManager mLayoutManager;
    private PersonUserChatsAdapter adapter;
    private String filter = "";
    RecyclerView  mRecyclerView;
    private FloatingActionButton fab_Load, fab_status;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID = "";
    String AES = "AES";
    List<DownloadModel> downloadModels=new ArrayList<>();
    Realm realm;
    private String outputString;
    private PersonChatsUserDBHelper dbHelper;
    private String saveCurrentTime1, saveCurrentDate1;
    public static TextView last_email_name;
    public static String email;
    Intent intent;
    int SPLASH_TIME = 3000;
    int ACTIVITY_OPEN = 1;
    TextView search, ic_more;
    private TextView password_null;
    RelativeLayout menu_relative;
    TextView log_out, change_password, data_upload,add_contacts,notes;
    DatabaseReference reference;
    private String saveCurrentTime, saveCurrentDate;
    Window window;
    AdView mAdView;
    String bio,fullname;
    private List<DownloadModel> mPeopleList;
    private SwipeRefreshLayout swipeRecyclerview;
    private String theLastMessage;
    public static boolean update = false;
    float x1,x2,y1,y2;
    TextView study_chats,chats,requests,line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sql_chat_activity);

        realm = Realm.getDefaultInstance();
        checkPermission();

        mAdView = new AdView(this);

        mAdView.setAdSize(AdSize.BANNER);

        mAdView.setAdUnitId(" ca-app-pub-2729927781686503/9944068977");


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });


        lastMessage = true;

        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }



        mPeopleList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference(currentUserID);

        fab_Load = findViewById(R.id.fab_friends);
        fab_status = findViewById(R.id.fab_status);
        last_email_name = findViewById(R.id.last_email_name);
        search = findViewById(R.id.search);
        swipeRecyclerview= findViewById(R.id.swipeRecyclerview);
        password_null = findViewById(R.id.password_null);
        notes = findViewById(R.id.notes);



                List<DownloadModel> downloadModelsLocal = getAllDownloads();
                if (downloadModelsLocal != null) {
                    if (downloadModelsLocal.size() > 0) {
                        downloadModels.addAll(downloadModelsLocal);
                        for (int i = 0; i < downloadModels.size(); i++) {

                            String s = Integer.toString(i);
                            Toast.makeText(SqlChatsActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    }
                }


        intent = getIntent();
        email = intent.getStringExtra("name");
        last_email_name.setText(email);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SqlChatsActivity.this, ContactsActivity.class);
                intent.putExtra("name",email);
                startActivity(intent);
            }
        });
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();


        dbHelper = new PersonChatsUserDBHelper(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.sql_chats_users);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        adapter = new PersonUserChatsAdapter(downloadModels,SqlChatsActivity.this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        menu_relative = findViewById(R.id.menu_relative);
        log_out = findViewById(R.id.log_out);
        change_password = findViewById(R.id.change_password);
        ic_more = findViewById(R.id.ic_more);
        data_upload = findViewById(R.id.data_upload);
        add_contacts = findViewById(R.id.add_contacts);
        study_chats = findViewById(R.id.study_chats);
        chats= findViewById(R.id.chats);
        line = findViewById(R.id.line);
        requests = findViewById(R.id.request);


        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SqlChatsActivity.this,SqlRequest.class);
                intent.putExtra("name",email);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(study_chats, "studychats");
                pairs[1] = new Pair<View, String>(search, "search");
                pairs[2] = new Pair<View, String>(ic_more, "icmore");
                pairs[3] = new Pair<View, String>(chats, "chats");
                pairs[4] = new Pair<View, String>(line, "line");
                pairs[5] = new Pair<View, String>(requests, "requests");


                @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)SqlChatsActivity.this, pairs);


                startActivity(intent, options.toBundle());


            }
        });


        menu_relative.setVisibility(View.GONE);
        ic_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                menu_relative.setVisibility(View.VISIBLE);

                //Code to start timer and take action after the timer ends
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        menu_relative.setVisibility(View.GONE);

                    }
                }, SPLASH_TIME);

            }
        });


        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_relative.setVisibility(View.GONE);
                state("offline");
                SendUserToLoginActivity();
                mAuth.signOut();

            }
        });


        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_relative.setVisibility(View.GONE);
                Intent i = new Intent(SqlChatsActivity.this,LoginChangePasspword.class);
                startActivity(i);

                finish();
            }
        });


        data_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_relative.setVisibility(View.GONE);

                RequestNewGroup();
                Intent intent = new Intent(SqlChatsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        add_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu_relative.setVisibility(View.GONE);

                ChatsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Group");

                ChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            Calendar calForDate1 = Calendar.getInstance();
                            SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                            Calendar calForTime1 = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                            saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());



                            String fullname =  snapshot.child("groupName").getValue().toString();
                            String sender =  snapshot.child("sender").getValue().toString();
                            //  String receiver =  snapshot.child("receiver").getValue().toString();
                            //    String imageurl =  snapshot.child("imageurl").getValue().toString();
                            //    String messageID = snapshot.child("messageID").getValue().toString();
                            String randomId = snapshot.child("randomId").getValue().toString();


                            addText(fullname,sender,"hb","k",randomId,"group",saveCurrentTime,saveCurrentDate,email + randomId,currentUserID,"false",fullname,sender);




                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                DatabaseReference  Ref = FirebaseDatabase.getInstance().getReference(currentUserID);


                Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Calendar calForDate1 = Calendar.getInstance();
                            SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                            saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                            Calendar calForTime1 = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                            saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());


                            String fullname =  snapshot.child("fullname").getValue().toString();
                            String sender  =  snapshot.child("sender").getValue().toString();
                            String receiver =  snapshot.child("receiver").getValue().toString();
                            String imageurl =  snapshot.child("imageurl").getValue().toString();
                            String messageID = snapshot.child("messageID").getValue().toString();
                            String randomId = snapshot.child("randomId").getValue().toString();

                            addText(fullname,sender,receiver,messageID,randomId,"single",saveCurrentTime,saveCurrentDate,email + randomId,currentUserID,"false",imageurl,sender);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        readUser();

        readGroup();



        swipeRecyclerview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                readUser();

                readGroup();

                adapter.notifyDataSetChanged();

                swipeRecyclerview.setRefreshing(false);

                Intent mySuperIntent = new Intent(SqlChatsActivity.this, SqlChatsActivity.class);
                mySuperIntent.putExtra("name",email);
                startActivity(mySuperIntent);
                finish();

            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SqlChatsActivity.this,SqlRequest.class);
                intent.putExtra("name",email);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(study_chats, "studychats");
                pairs[1] = new Pair<View, String>(search, "search");
                pairs[2] = new Pair<View, String>(ic_more, "icmore");
                pairs[3] = new Pair<View, String>(chats, "chats");
                pairs[4] = new Pair<View, String>(line, "line");
                pairs[5] = new Pair<View, String>(requests, "requests");


                @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)SqlChatsActivity.this, pairs);


                startActivity(intent, options.toBundle());

            }
        });

        fab_Load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      dbHelper.deleteAllData();
                Intent intent = new Intent(SqlChatsActivity.this, FindFriendActivity.class);
                startActivity(intent);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (y1 < y2){
                    Intent intent = new Intent(SqlChatsActivity.this,SqlRequest.class);
                    intent.putExtra("name",email);
                    Pair[] pairs = new Pair[6];
                    pairs[0] = new Pair<View, String>(study_chats, "studychats");
                    pairs[1] = new Pair<View, String>(search, "search");
                    pairs[2] = new Pair<View, String>(ic_more, "icmore");
                    pairs[3] = new Pair<View, String>(chats, "chats");
                    pairs[4] = new Pair<View, String>(line, "line");
                    pairs[5] = new Pair<View, String>(requests, "requests");


                    @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) SqlChatsActivity.this, pairs);


                    startActivity(intent, options.toBundle());



                }
                break;
        }
        return false;
    }

    private void state(String online) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).child("userState");
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
        saveCurrentDate = currendateFormat.format(calForDate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("state", online);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("time", saveCurrentTime);
        reference.updateChildren(hashMap);
    }

//
//    private void populaterecyclerView(String filter) {
//        dbHelper = new PersonChatsUserDBHelper(this);
//        adapter = new PersonUserChatsAdapter(dbHelper.peopleList(filter), this, mRecyclerView);
//        mRecyclerView.setAdapter(adapter);
//    }

    private String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decvalue = c.doFinal(decodeValue);
        String decryptvalue = new String(decvalue);
        return decryptvalue;

    }

    private String encrypt(String Data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptValue;

    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("Please group Name :");
        final EditText groupNameField = new EditText(getApplicationContext());
        groupNameField.setHint("e.g Coding cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(SqlChatsActivity.this, "Please wirte Group Name...", Toast.LENGTH_SHORT).show();
                } else {
                    CreatNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }

    private void CreatNewGroup(final String groupName) {
        String currentUserid = mAuth.getCurrentUser().getUid();


        DatabaseReference  RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    bio = dataSnapshot.child("bio").getValue().toString();
                    fullname = dataSnapshot.child("fullname").getValue().toString();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RootRef.child("Group").child(currentUserid).child("groupName").setValue(groupName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserid).child("Group").child(groupName);

                            HashMap<String, Object> hashMap1 =new HashMap<>();

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                            saveCurrentDate = currendateFormat.format(calForDate.getTime());


                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                            saveCurrentTime = currenTimeFormat.format(calForTime.getTime());

                            hashMap1.put("time", saveCurrentTime);
                            hashMap1.put("date", saveCurrentDate);
                            hashMap1.put("fullname", fullname);
                            hashMap1.put("groupName",groupName);
                            hashMap1.put("sender",currentUserID);
                            hashMap1.put("type","group");
                            hashMap1.put("randomId",generateString(12));
                            hashMap1.put("bio", bio);


                            reference.updateChildren(hashMap1);

                            RootRef.child("GroupChatName").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent= new Intent(SqlChatsActivity.this,AddFrientUser.class);
                                    intent.putExtra("groupName",groupName);
                                    startActivity(intent);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SqlChatsActivity.this, R.style.AlertDialog);

                                }
                            });

                        }
                    }
                });
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AEs");
        return secretKeySpec;

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currentUserID).setValue(token1);
    }

    public static class PersonChatsUserDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = email+".db";
        private static final int DATABASE_VERSION = 3 ;
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_SENDER = "sender";
        public static final String COLUMN_PERSON_RECIEVER = "recicver";
        public static final String COLUMN_PERSON_IMAGEURL = "image";
        public static final String COLUMN_PERSON_MESSAGEID = "messageID";
        public static final String COLUMN_PERSON_RANDOMID = "randomId";
        public static final String COLUMN_PERSON_SORTTIME = "sorttime";
        public static final String COLUMN_PERSON_LASTMESSAGE = "lastmessage";
        public static final String COLUMN_PERSON_TYPE = "type";


        public PersonChatsUserDBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
        }


        public PersonChatsUserDBHelper(Context context,String ds) {
            super(context, ds , null, DATABASE_VERSION);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                    COLUMN_PERSON_SENDER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_RECIEVER + " TEXT NOT NULL, " +
                    COLUMN_PERSON_IMAGEURL + " BLOB NOT NULL," +
                    COLUMN_PERSON_MESSAGEID + " TEXT NOT NULL, " +
                    COLUMN_PERSON_RANDOMID + " TEXT NOT NULL," +
                    COLUMN_PERSON_SORTTIME+ " TEXT NOT NULL, " +
                    COLUMN_PERSON_LASTMESSAGE + " TEXT NOT NULL, " +
                    COLUMN_PERSON_TYPE + " TEXT NOT NULL);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // you can implement here migration process
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }
        /**create record**/
        public void saveNewPerson(SqlUser sqlUser) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PERSON_NAME, sqlUser.getFullname());
            values.put(COLUMN_PERSON_SENDER, sqlUser.getSender());
            values.put(COLUMN_PERSON_RECIEVER, sqlUser.getReceiver());
            values.put(COLUMN_PERSON_IMAGEURL, sqlUser.getImageurl());
            values.put(COLUMN_PERSON_MESSAGEID, sqlUser.getMessageID());
            values.put(COLUMN_PERSON_RANDOMID, sqlUser.getRandomId());
            values.put(COLUMN_PERSON_SORTTIME, sqlUser.getSaveCurrentTime1());
            values.put(COLUMN_PERSON_LASTMESSAGE, sqlUser.getLastMessage());
            values.put(COLUMN_PERSON_TYPE, sqlUser.getType());

            // insert
            db.insert(TABLE_NAME,null, values);
            db.close();
        }

        /**Query records, give options to filter results**/
        public List<SqlUser> peopleList(String filter) {
            String query;
            if(filter.equals("")){
                //regular query
                query = "SELECT  * FROM " + TABLE_NAME;
            }else{
                //filter results by filter option provided
                query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY "+ filter;
            }

            List<SqlUser> personLinkedList = new LinkedList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            SqlUser person;

            if (cursor.moveToFirst()) {
                do {
                    person = new SqlUser();

                    person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    person.setFullname(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                    person.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                    person.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                    person.setImageurl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
                    person.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                    person.setRandomId(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RANDOMID)));
                    person.setSaveCurrentTime1(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SORTTIME)));
                    person.setLastMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTMESSAGE)));
                    person.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
                    personLinkedList.add(person);
                } while (cursor.moveToNext());
            }


            return personLinkedList;
        }


        /**Query only 1 record**/
        public SqlUser getPerson(long id){
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT  * FROM " + TABLE_NAME + " WHERE _id="+ id;
            Cursor cursor = db.rawQuery(query, null);

            SqlUser receivedPerson = new SqlUser();
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                receivedPerson.setFullname(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                receivedPerson.setSender(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SENDER)));
                receivedPerson.setReceiver(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RECIEVER)));
                receivedPerson.setImageurl(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGEURL)));
                receivedPerson.setMessageID(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_MESSAGEID)));
                receivedPerson.setRandomId(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_RANDOMID)));
                receivedPerson.setSaveCurrentTime1(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_SORTTIME)));
                receivedPerson.setLastMessage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_LASTMESSAGE)));
                receivedPerson.setType(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_TYPE)));
            }

            return receivedPerson;
        }

        /**delete record**/
        public void deletePersonRecord(long id, Context context) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE _id='"+id+"'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
        }

        public void deleteAllData() {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("delete from "+ TABLE_NAME);
        }

        /**update record**/

        public void updatePersonImage(long personId, Context context, String imageUrl) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  "+TABLE_NAME+" SET  image ='"+ imageUrl + "'  WHERE _id='" + personId + "'");
            Toast.makeText(context, "Image Updated successfully.", Toast.LENGTH_SHORT).show();

        }

        public void updatePersonLastMessage(long personId, Context context, String lastmsg,String time) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  "+ TABLE_NAME +" SET  lastmessage ='"+ lastmsg + "', sorttime ='"+time+"'  WHERE _id='" + personId + "'");
            Toast.makeText(context, "last mess Updated successfully.", Toast.LENGTH_SHORT).show();


        }

    }

    private String generateString(int lenth){
        char[] chasr= "qwertyuiopasdfghjklzxcvbnm".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <lenth ; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }

        return stringBuilder.toString();
    }

    private void readUser() {

        Query query = UsersRef.orderByChild("time");



        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(email).exists()) {

                        WebItems comment = snapshot.getValue(WebItems.class);

                    } else {
                        Calendar calForDate1 = Calendar.getInstance();
                        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                        Calendar calForTime1 = Calendar.getInstance();
                        SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                        saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());



                        String fullname =  snapshot.child("fullname").getValue().toString();
                        String sender =  snapshot.child("sender").getValue().toString();
                        String receiver =  snapshot.child("receiver").getValue().toString();
                        String imageurl =  snapshot.child("imageurl").getValue().toString();
                        String messageID = snapshot.child("messageID").getValue().toString();
                        String randomId = snapshot.child("randomId").getValue().toString();

                        addText(fullname,sender,receiver,messageID,randomId,"single",saveCurrentTime,saveCurrentDate,email + randomId,currentUserID,"false",imageurl,sender);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put(email, true);
                        UsersRef.child(messageID).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SqlChatsActivity.this, "Add Contact...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void readGroup() {

        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Group");


        ChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(email).exists()) {

                        WebItems comment = snapshot.getValue(WebItems.class);

                    } else {
                        Calendar calForDate1 = Calendar.getInstance();
                        SimpleDateFormat currendateFormat1 = new SimpleDateFormat("yyyyMMdd");
                        saveCurrentDate1 = currendateFormat1.format(calForDate1.getTime());


                        Calendar calForTime1 = Calendar.getInstance();
                        SimpleDateFormat currenTimeFormat1 = new SimpleDateFormat("a hhmmss");
                        saveCurrentTime1 = currenTimeFormat1.format(calForTime1.getTime());

                        String fullname =  snapshot.child("groupName").getValue().toString();
                        String sender =  snapshot.child("sender").getValue().toString();
                        String randomId = snapshot.child("randomId").getValue().toString();

                        addText(fullname,sender,"hb","k",randomId,"group",saveCurrentTime,saveCurrentDate,email + randomId,currentUserID,"false",fullname,sender);


                        HashMap<String, Object> map = new HashMap<>();
                        map.put(email, true);
                        ChatsRef.child(fullname).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(SqlChatsActivity.this,LoginActivityInstagram.class);
        startActivity(intent);
        finish();
    }

    public class PersonUserChatsAdapter extends RecyclerView.Adapter<PersonUserChatsAdapter.ViewHolder> {
        private Context mContext;
        private RecyclerView mRecyclerV;

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profileImage;
            EmoticonTextView last_msg;
            TextView  userName, user_delete,password_null,delete_user;
            private TextView userStatus, unseen, sender_unseen;

            public ViewHolder(View itemView) {
                super(itemView);
                profileImage = itemView.findViewById(R.id.users_profile_image);
                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                user_delete = itemView.findViewById(R.id.user_delete);
                unseen = itemView.findViewById(R.id.unseen);
                last_msg = itemView.findViewById(R.id.last_msg);
                password_null = itemView.findViewById(R.id.password_null);
                delete_user = itemView.findViewById(R.id.user_delete);
            }
        }

        public PersonUserChatsAdapter(List<DownloadModel> myDataset, Context context) {
            mPeopleList = myDataset;
            mContext = context;
            String outputString,convertString;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.user_chat_fragment, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

       //     Collections.sort(mPeopleList, SqlUser.BY_NAME_ALPHABETICAL);

            final DownloadModel person = mPeopleList.get(position);


            holder.last_msg.setEmoticonProvider(Android8EmoticonProvider.create());
            holder.last_msg.setEmoticonSize(35);

            if (person.getType().equals("single")) {

                holder.userName.setText(person.getMessage());
                holder.last_msg.setVisibility(View.VISIBLE);

                holder.last_msg.setText(person.getLastSendMessage());

                Picasso.get().load(person.getImagrUrl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);


                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(person.getReceiver()).child(currentUserID);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int unread = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat.getIsseen().equals("false")) {
                                unread++;
                            } else {
                                holder.unseen.setVisibility(View.GONE);
                            }
                        }

                        if (unread == 0) {
                            holder.unseen.setVisibility(View.GONE);
                            holder.unseen.setText("");
                        } else {
                            holder.unseen.setVisibility(View.VISIBLE);
                            holder.unseen.setText("" + unread);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(holder.itemView.getContext(), MessageActivity.class);
                        intent.putExtra("userid", person.getReceiver());
                     //   intent.putExtra("name", email + person.getBio());
                      //  intent.putExtra("USER_ID", person.getId());
                        holder.itemView.getContext().startActivity(intent);


                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        holder.delete_user.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

                holder.delete_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(currentUserID).child(person.getMessageID());
                        rootRef

                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {


                                    dbHelper.deletePersonRecord(person.getId(),holder.itemView.getContext());
                                    adapter.notifyDataSetChanged();

                                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }


                });

                DatabaseReference  ref = FirebaseDatabase.getInstance().getReference("Chats").child(currentUserID).child(person.getReceiver());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment chat = snapshot.getValue(Comment.class);

                            if (!person.getLastSendMessage().equals(chat.getMessage())){

                                if ((chat.getType().equals("text")) && (chat.getLast().equals("false"))){

                                    String convertString;

                                        try {
                                        outputString = decrypt(chat.getMessage(), password_null.getText().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",person.getId()).findFirst();
                                            downloads.setLastSendMessage(outputString);
                                            downloads.setMessageID(chat.getMessageID());
                                            adapter.notifyDataSetChanged();

                                            setChangeItemFilePath(outputString,person.getId(),chat.getMessageID());
                                        }
                                    });

                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("last","true");
                                    ref.child(chat.getMessageID()).updateChildren(map);
                                }
                            }else {
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(person.getReceiver());
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Comment user = dataSnapshot.getValue(Comment.class);
                        if (dataSnapshot.child("imageurl").exists()) {

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(person.getReceiver()).child("imageperson");
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot data) {

                                    if (data.child(email).exists()) {

                                    }
                                    else {
                                        String image = dataSnapshot.child("imageurl").getValue().toString();


                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",person.getId()).findFirst();
                                                downloads.setImagrUrl(image);

                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put(email, true);
                                        db.updateChildren(hashMap);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            Toast.makeText(mContext, "never", Toast.LENGTH_SHORT).show();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } else if (person.getType().equals("group")) {
                holder.userName.setText(person.getMessage());
                holder.last_msg.setVisibility(View.VISIBLE);
                holder.last_msg.setVisibility(View.VISIBLE);
                holder.last_msg.setText(person.getLastSendMessage());

                Picasso.get().load(person.getImagrUrl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference Root = FirebaseDatabase.getInstance().getReference("GroupChats").child(person.getSender()).child(person.getMessage());
                Root.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int unread = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (snapshot.child("sk" + currentUserID).exists()) {
                                holder.unseen.setVisibility(View.GONE);

                            } else {
                                unread++;
                            }
                        }
                        if (unread == 0) {
                            holder.unseen.setVisibility(View.GONE);
                            holder.unseen.setText("");
                        } else {
                            holder.unseen.setVisibility(View.VISIBLE);
                            holder.unseen.setText("" + unread);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(holder.itemView.getContext(),MassageActivityInstagram.class);
                        intent.putExtra("userid",person.getMessage());
//                        intent.putExtra("adminId",person.getSender());
//                        intent.putExtra("USER_ID", person.getId());
//                        intent.putExtra("name",email+person.getBio());
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        holder.delete_user.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

                holder.delete_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference ChatsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Group");

                        ChatsRef.child(person.getMessage()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dbHelper.deletePersonRecord(person.getId(),holder.itemView.getContext());
                                //    populaterecyclerView(filter);
                                adapter.notifyDataSetChanged();

                            }
                        });

                    }
                });

//                DatabaseReference  databaseRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(person.getSender()).child(person.getMessage());
//                databaseRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if (snapshot.child(email+person.getBio()).exists()){
//
//                            }else if (snapshot.child("delete"+currentUserID).exists()){
//
//                            }
//                            else {
//
//                                String messageId = snapshot.child("messageID").getValue().toString();
//                                String time = snapshot.child("time").getValue().toString();
//                                String date = snapshot.child("date").getValue().toString();
//                                String sender = snapshot.child("sender").getValue().toString();
//                                String receiver = snapshot.child("receiver").getValue().toString();
//                                String isseen = snapshot.child("isseen").getValue().toString();
//                                String message = snapshot.child("message").getValue().toString();
//                                String bio = snapshot.child("bio").getValue().toString();
//                                String type = snapshot.child("type").getValue().toString();
//                                String preMessage = snapshot.child("preMessage").getValue().toString();
//                                String lastSendMessage = snapshot.child("lastSendMessage").getValue().toString();
//
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put(email+person.getBio(), true);
//                          //      databaseRef.child(messageId).updateChildren(hashMap);
//
//                          //      GroupMessage sql = new GroupMessage(message,sender,receiver,messageId, bio,type,time,date, lastSendMessage, preMessage,isseen,isseen,message);
//                                //   sqliteHelper.saveNewPerson(sql);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

//                DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(person.getSender()).child(person.getMessage());
//                RootRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            Chat chat = snapshot.getValue(Chat.class);
//                            if (snapshot.child(currentUserID).exists()) {
//                            }
//                            else {
//                                if (firebaseUser != null && chat != null) {
//                                    if (chat.getReceiver().equals(person.getMessage()) && chat.getType().equals("text")) {
//                                        theLastMessage = chat.getLastSendMessage();
//                                        String messageId = chat.getMessageID();
//                                        String sortTime = chat.getMessageID();
//
//                                        try {
//                                            outputString = decrypt(theLastMessage,password_null.getText().toString());
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        realm.executeTransaction(new Realm.Transaction() {
//                                            @Override
//                                            public void execute(Realm realm) {
//                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",person.getId()).findFirst();
//                                                downloads.setLastSendMessage(outputString);
//                                                downloads.setMessageID(messageId);
//                                                adapter.notifyDataSetChanged();
//
//                                            }
//                                        });
//
//
//                                        HashMap<String,Object> hashMap = new HashMap<>();
//                                        hashMap.put(currentUserID,true);
//
//                                        RootRef.child(messageId).updateChildren(hashMap);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });

                DatabaseReference  dsa = FirebaseDatabase.getInstance().getReference("Users").child(person.getSender());
                dsa.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Comment user = dataSnapshot.getValue(Comment.class);
                        if (dataSnapshot.child(person.getMessage()+"image").exists()){

                            DatabaseReference  db = FirebaseDatabase.getInstance().getReference("Users").child(person.getSender()).child(person.getMessage()).child("imagegroup");
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot data) {

                                    if (data.child(email).exists()) {

                                    } else {
                                        String image = dataSnapshot.child(person.getMessage()+"image").getValue().toString();
                                        dbHelper.updatePersonImage(person.getId(),holder.itemView.getContext(),image);
                                        adapter.notifyDataSetChanged();

                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final DownloadModel downloads = realm.where(DownloadModel.class).equalTo("id",person.getId()).findFirst();
                                                downloads.setLastSendMessage(outputString);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put(email,true);

                                        db.updateChildren(hashMap);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                        }
                        else {


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
            else {

            }


        }

        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }

        public void setChangeItemFilePath(final String path, long id,String messageId) {
            Realm realm = Realm.getDefaultInstance();
            int i = 0;
            for (DownloadModel downloadModel : mPeopleList) {
                if (id == downloadModel.getDownloadId()) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mPeopleList.get(finalI).setLastSendMessage(path);
                            mPeopleList.get(finalI).setMessageID(messageId);
                            notifyItemChanged(finalI);
                            realm.copyToRealmOrUpdate(mPeopleList.get(finalI));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                i++;
            }
        }


    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateButtons();
            }
        }.start();
        mTimerRunning = true;
        updateButtons();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        updateButtons();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateButtons() {
        if (mTimerRunning) {
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.GONE);
        } else {
            mButtonStartPause.setText("Start");
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            }
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.INVISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateButtons();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
        else {
            resetTimer();
            startTimer();

        }

    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(SqlChatsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            requestPermission();
        }
        return false;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(SqlChatsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(SqlChatsActivity.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(SqlChatsActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void addText(String message, String sender, String receiver, String messageId, String bio, String type, String time, String date, String lastSendMessage, String preMessage, String isseen, String imageurl, String adminId){
        Number currentnum=realm.where(DownloadModel.class).max("id");
        int nextId;

        if(currentnum==null){
            nextId=1;
        }
        else{
            nextId=currentnum.intValue()+1;
        }
        final DownloadModel downloadModel=new DownloadModel();
        downloadModel.setId(nextId);
        downloadModel.setStatus("Completed");
        downloadModel.setTitle("hello");
        downloadModel.setFile_size("0");
        downloadModel.setProgress("0");
        downloadModel.setIs_paused(false);
        downloadModel.setDownloadId(8878);
        downloadModel.setFile_path(lastSendMessage);
        downloadModel.setMessage(message);
        downloadModel.setSender(sender);
        downloadModel.setReceiver(receiver);
        downloadModel.setMessageID(messageId);
        downloadModel.setIsseen(isseen);
        downloadModel.setLastSendMessage("hello");
        downloadModel.setBio(bio);
        downloadModel.setDate(date);
        downloadModel.setPreMessage(currentUserID);
        downloadModel.setTime(time);
        downloadModel.setType(type);
        downloadModel.setImagrUrl(imageurl);
        downloadModel.setUsername(email);
        downloadModel.setAdminId(adminId);


        downloadModels.add(downloadModel);


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(downloadModel);
                adapter.notifyDataSetChanged();

            }
        });
    }

    private RealmResults<DownloadModel> getAllDownloads(){
        Realm realm=Realm.getDefaultInstance();
        return realm.where(DownloadModel.class).equalTo("preMessage", currentUserID).sort("messageID", Sort.DESCENDING).findAll();
    }

}