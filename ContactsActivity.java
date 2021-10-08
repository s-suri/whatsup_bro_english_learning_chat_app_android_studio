package com.some.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.some.notes.Model.Chat;
import com.some.notes.Model.CityDataItem;
import com.some.notes.Model.Comment;
import com.some.notes.Model.DownloadModel;
import com.some.notes.Model.SqlUser;
import com.some.notes.Notifications.Token;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class ContactsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private PersonUserChatsAdapter adapter;
    private String filter = "";
    RecyclerView mRecyclerView;
    List<DownloadModel> downloadModels=new ArrayList<>();
    Realm realm;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID = "";
    String AES = "AES";
    String outputString;
    private PersonChatsUserDBHelper dbHelper;
    private String saveCurrentTime1, saveCurrentDate1;
    public static TextView last_email_name;
    public static String email;
    Intent intent;
    TextView ic_more, password_null;
    RelativeLayout menu_relative;
    TextView log_out, change_password, data_upload, add_contacts;
    DatabaseReference reference;
    Window window;
    private List<DownloadModel> mPeopleList;
    TextView search;
    public static boolean update = false;
    int SPLASH_TIME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        realm = Realm.getDefaultInstance();

        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPeopleList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference(currentUserID);
        search = findViewById(R.id.search_users);


        intent = getIntent();
        email = intent.getStringExtra("name");

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

        List<DownloadModel> downloadModelsLocal = getAllDownloads();
        if (downloadModelsLocal != null) {
            if (downloadModelsLocal.size() > 0) {
                downloadModels.addAll(downloadModelsLocal);
                for (int i = 0; i < downloadModels.size(); i++) {

                    }
            }
        }


        dbHelper = new PersonChatsUserDBHelper(this);


        adapter = new PersonUserChatsAdapter(downloadModels, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);


     //   populaterecyclerView(filter);


    }
    private RealmResults<DownloadModel> getAllDownloads(){
        Realm realm=Realm.getDefaultInstance();
        return realm.where(DownloadModel.class).equalTo("preMessage", currentUserID).findAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beneficiary_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.toString().trim().length() == 0) {


            adapter.notifyDataSetChanged();

        } else {
            newText = newText.toLowerCase();
            ArrayList<DownloadModel> newList = new ArrayList<>();
            for (DownloadModel beneficiary : mPeopleList) {
                String name = beneficiary.getMessage().toLowerCase();
                if (name.contains(newText)) {
                    newList.add(beneficiary);
                }
            }
            adapter.setFilter(newList);
        }
            return true;

}


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
//                Toast.makeText(context, "Image Updated successfully.", Toast.LENGTH_SHORT).show();
        }

        public void updatePersonLastMessage(long personId, Context context, String lastmsg) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  "+ TABLE_NAME +" SET  lastmessage ='"+ lastmsg + "'  WHERE _id='" + personId + "'");
            Toast.makeText(context, "last mess Updated successfully.", Toast.LENGTH_SHORT).show();


        }

    }

    public class PersonUserChatsAdapter extends RecyclerView.Adapter<PersonUserChatsAdapter.ViewHolder> {

        private Context mContext;
        private RecyclerView mRecyclerV;

        public void setFilter(ArrayList<DownloadModel> newList) {
            mPeopleList = new ArrayList<>();
            mPeopleList.addAll(newList);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CircleImageView profileImage;
            TextView last_msg, userName, user_delete,password_null;
            private TextView userStatus, unseen, sender_unseen;


            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;


                profileImage = itemView.findViewById(R.id.users_profile_image);
                userName = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                user_delete = itemView.findViewById(R.id.user_delete);
                unseen = itemView.findViewById(R.id.unseen);
                last_msg = itemView.findViewById(R.id.last_msg);
                password_null = itemView.findViewById(R.id.password_null);



            }
        }

        public void add(int position, DownloadModel person) {
            mPeopleList.add(position, person);
            notifyItemInserted(position);
        }

        public void remove(int position) {
            mPeopleList.remove(position);
            notifyItemRemoved(position);
        }






        // Provide a suitable constructor (depends on the kind of dataset)
        public PersonUserChatsAdapter(List<DownloadModel> myDataset, Context context) {
            mPeopleList = myDataset;
            mContext = context;

            String outputString,convertString;
        }

        // Create new views (invoked by the layout manager)
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

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

      //      Collections.sort(mPeopleList,SqlUser.BY_NAME_ALPHABETICAL);

            final DownloadModel person = mPeopleList.get(position);

            holder.userName.setText(person.getMessage());
            holder.last_msg.setVisibility(View.VISIBLE);
            holder.last_msg.setText(person.getLastSendMessage());

            Picasso.get().load(person.getImagrUrl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);

            holder.last_msg.setVisibility(View.VISIBLE);


            DatabaseReference Rootref = FirebaseDatabase.getInstance().getReference("Users").child(person.getReceiver());
            Rootref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String retImage = dataSnapshot.child("imageurl").getValue().toString();
                        String bio = dataSnapshot.child("bio").getValue().toString();


                        Picasso.get().load(retImage).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
                        }
                        else {
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


            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.layout.getContext(),MessageActivity.class);
                    intent.putExtra("userid",person.getReceiver());
                    intent.putExtra("name",email+person.getBio());
                    intent.putExtra("USER_ID", person.getId());
                    holder.layout.getContext().startActivity(intent);
                    finish();
                }
            });



        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }


        public  void filterList(ArrayList<DownloadModel> filtered){
            mPeopleList = filtered;
            notifyDataSetChanged();
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



}