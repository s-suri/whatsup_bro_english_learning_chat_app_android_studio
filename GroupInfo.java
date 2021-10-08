package com.some.notes;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.some.notes.Model.Chat;
import com.some.notes.Model.Comment;
import com.some.notes.Model.Person;
import com.some.notes.Model.SqlUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfo extends AppCompatActivity {
    SqlChatsActivity.PersonChatsUserDBHelper helper;
    private Button UpdateAccoutSetting;
    private TextView edit,userStatus,name,delete_group,adminInformation,description;
    private ImageView userprofileImage;
    private String currentUserid;
    private FirebaseAuth mAuth;
    DatabaseReference RootRef;
    private static  final int GalleryPic = 1;
    private StorageReference UserprofileImageref;
    StorageTask uUploadTask;
    private String filter = "";
    ArrayList<Person> mChats = new ArrayList<>();
    DatabaseReference reference;
    private String saveCurrentTime, saveCurrentDate;
    Window window;
    String id;
    Intent intent;
    public static String groupName;
    String adminId,email;
    String imageUri;
    RecyclerView recyclerView;
    long receivedPersonId;
    private PersonAdapter adapter;
    private PersonEmailDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserprofileImageref = FirebaseStorage.getInstance().getReference().child("Profile Images");

     //   userStatus = findViewById(R.id.user_status);
        name = findViewById(R.id.name);
        adminInformation = findViewById(R.id.adminInformetion);
        description = findViewById(R.id.des);

        userprofileImage =findViewById(R.id.set_profile_image);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        dbHelper = new PersonEmailDBHelper(this);


        groupName = getIntent().getStringExtra("groupName");
        adminId = getIntent().getStringExtra("adminId");
        email = getIntent().getStringExtra("email");

        name.setText(groupName);


        try {
            //get intent to get person id
            receivedPersonId = intent.getLongExtra("USER_ID",1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, receivedPersonId+"", Toast.LENGTH_SHORT).show();

        helper = new SqlChatsActivity.PersonChatsUserDBHelper(this);

        SqlUser queriedPerson = helper.getPerson(receivedPersonId);


 //       Picasso.get().load(queriedPerson.getImageurl()).into(userprofileImage);

        InitializeField();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminId.equals(currentUserid)){
                    Intent intent = new Intent(GroupInfo.this,AddFrientUser.class);
                    intent.putExtra("groupName",groupName);
                    intent.putExtra("adminId",adminId);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(GroupInfo.this, "Sorry' you are not admin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(GroupInfo.this).create();
                alertDialog.setTitle("Do you want to delete?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserid).child("Group").child(groupName);

                               reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(GroupInfo.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                                     SenDUserToMainActivity();
                                            }
                                        }
                                    });

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();





            }
        });

        populaterecyclerView(filter);


        RerieveUserInfo();
        readUser();


    }



    private void populaterecyclerView(String filter) {
        dbHelper = new PersonEmailDBHelper(this);
        adapter = new PersonAdapter(dbHelper.peopleList(filter), this, recyclerView);
        recyclerView.setAdapter(adapter);
    }



    private void RerieveUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(adminId);


       reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) &&(dataSnapshot.hasChild(groupName +"image") && (dataSnapshot.hasChild(groupName +"status"))))
                        {
                            String retriveimage = dataSnapshot.child(groupName +"image").getValue().toString();
                            String retrivename = dataSnapshot.child("fullname").getValue().toString();
                            String retrieveStatus = dataSnapshot.child(groupName +"status").getValue().toString();

                            userStatus.setText(retrieveStatus);
                            adminInformation.setText("Created By :  "+"  " +retrivename);

                            Picasso.get().load(retriveimage).into(userprofileImage);


                        }
                        else if((dataSnapshot.exists()) &&(dataSnapshot.hasChild(groupName +"status"))){

                            String retrieveStatus = dataSnapshot.child(groupName +"status").getValue().toString();
                            String retrivename = dataSnapshot.child("fullname").getValue().toString();

                            userStatus.setText(retrieveStatus);
                            adminInformation.setText("Created By :  "+"  " +retrivename);




                        }
                        else if((dataSnapshot.exists()) &&(dataSnapshot.hasChild(groupName +"image"))){

                            String retriveimage = dataSnapshot.child(groupName +"image").getValue().toString();
                            String retrivename = dataSnapshot.child("fullname").getValue().toString();

                            adminInformation.setText("Created By :  " +"  "+retrivename);

                            Picasso.get().load(retriveimage).into(userprofileImage);

                        }
                        else {


                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49").into(userprofileImage);

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void InitializeField() {
        name = findViewById(R.id.name);
        name.setText(groupName);
        adminInformation = findViewById(R.id.adminInformetion);
        edit = findViewById(R.id.editGroup);
        delete_group = findViewById(R.id.delete_group);
        userStatus = findViewById(R.id.description);
        userprofileImage = findViewById(R.id.set_profile_image);

    }


        private void readUser() {

            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Group Users").child(adminId).child(groupName);

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mChats.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (snapshot.child(id).exists()){

                        }
                        else {
                            String name = snapshot.child("fullname").getValue().toString();
                            String bio = snapshot.child("bio").getValue().toString();
                            String reciever = snapshot.child("receiver").getValue().toString();


                            Person person = new Person(name,reciever,bio,"iwmiwksi");
                            dbHelper.saveNewPerson(person);

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put(id,true);

                            reference1.child(reciever).updateChildren(hashMap);

                        }
                   //     mChats.add(comment);

                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }



    @Override
    protected void onStart() {
        super.onStart();
        state("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        state("offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
        state("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void state(String online) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserid).child("userState");


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




    private void SenDUserToMainActivity() {
        Intent intent = new Intent(GroupInfo.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
        private List<Person> mPeopleList;
        private Context mContext;
        private RecyclerView mRecyclerV;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView username, userStatus;
            ImageView checkTextAddPeople;

            CircleImageView profileImage;

            TextView checkButtonTrue, checkButtonfalse,panding_seen;

            public View layout;

            public ViewHolder(View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.user_profile_name);
                userStatus = itemView.findViewById(R.id.user_status);
                profileImage = itemView.findViewById(R.id.users_profile_image);
                checkButtonTrue = itemView.findViewById(R.id.checkAddPeopleTrue);
                checkButtonfalse = itemView.findViewById(R.id.checkAddPeopleFalse);
                checkTextAddPeople = itemView.findViewById(R.id.checkTextAddPeople);
                panding_seen = itemView.findViewById(R.id.panding_seen);

            }
        }

        public void add(int position, Person person) {
            mPeopleList.add(position, person);
            notifyItemInserted(position);
        }

        public void remove(int position) {
            mPeopleList.remove(position);
            notifyItemRemoved(position);
        }



        // Provide a suitable constructor (depends on the kind of dataset)
        public PersonAdapter(List<Person> myDataset, Context context, RecyclerView recyclerView) {
            mPeopleList = myDataset;
            mContext = context;
            mRecyclerV = recyclerView;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_users_items, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Person model = mPeopleList.get(position);

            holder.userStatus.setVisibility(View.VISIBLE);
            holder.username.setVisibility(View.VISIBLE);
            holder.panding_seen.setVisibility(View.VISIBLE);


            holder.username.setText(model.getName());
            holder.userStatus.setText(model.getOccupation());
            Picasso.get().load(model.getImage()).into(holder.profileImage);



            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference Root = FirebaseDatabase.getInstance().getReference("GroupChats").child(adminId).child(groupName);
            Root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int unread = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (snapshot.child("sk" + model.getAge()).exists()) {
                            holder.panding_seen.setVisibility(View.GONE);

                        } else {
                            unread++;
                        }
                    }
                    if (unread == 0) {
                        holder.panding_seen.setVisibility(View.GONE);
                        holder.panding_seen.setText("");
                    } else {
                        holder.panding_seen.setVisibility(View.VISIBLE);
                        holder.panding_seen.setText("" + unread);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(model.getAge());
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Comment user = dataSnapshot.getValue(Comment.class);
                    if (dataSnapshot.child("imageurl").exists()) {

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(model.getAge()).child("imageInformatio");
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot data) {

                                if (data.child(email).exists()) {

                                } else {
                                    String image = dataSnapshot.child("imageurl").getValue().toString();
                                    String bio = dataSnapshot.child("bio").getValue().toString();
                                    dbHelper.updatePersonImage(model.getId(), GroupInfo.this, image,bio);
                                    populaterecyclerView(filter);
                                    adapter.notifyDataSetChanged();

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


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        private void goToUpdateActivity(long personId,String occupation){
            Intent goToUpdate = new Intent(mContext, UpdateRecordActivity.class);
            goToUpdate.putExtra("USER_ID", personId);
            goToUpdate.putExtra("id",occupation);

            mContext.startActivity(goToUpdate);
        }



        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }



    }



    public static class PersonEmailDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = groupName+".db";
        private static final int DATABASE_VERSION = 3 ;
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_AGE = "age";
        public static final String COLUMN_PERSON_OCCUPATION = "occupation";
        public static final String COLUMN_PERSON_IMAGE = "image";


        public PersonEmailDBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
        }

        public PersonEmailDBHelper(Context context,String database) {
            super(context, database , null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                    COLUMN_PERSON_AGE + " NUMBER NOT NULL, " +
                    COLUMN_PERSON_OCCUPATION + " TEXT NOT NULL, " +
                    COLUMN_PERSON_IMAGE + " BLOB NOT NULL);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // you can implement here migration process
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }
        /**create record**/
        public void saveNewPerson(Person person) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PERSON_NAME, person.getName());
            values.put(COLUMN_PERSON_AGE, person.getAge());
            values.put(COLUMN_PERSON_OCCUPATION, person.getOccupation());
            values.put(COLUMN_PERSON_IMAGE, person.getImage());

            // insert
            db.insert(TABLE_NAME,null, values);
            db.close();
        }

        /**Query records, give options to filter results**/
        public List<Person> peopleList(String filter) {
            String query;
            if(filter.equals("")){
                //regular query
                query = "SELECT  * FROM " + TABLE_NAME;
            }else{
                //filter results by filter option provided
                query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY "+ filter;
            }

            List<Person> personLinkedList = new LinkedList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            Person person;

            if (cursor.moveToFirst()) {
                do {
                    person = new Person();

                    person.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    person.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                    person.setAge(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_AGE)));
                    person.setOccupation(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_OCCUPATION)));
                    person.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGE)));
                    personLinkedList.add(person);
                } while (cursor.moveToNext());
            }


            return personLinkedList;
        }

        /**Query only 1 record**/
        public Person getPerson(long id){
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT  * FROM " + TABLE_NAME + " WHERE _id="+ id;
            Cursor cursor = db.rawQuery(query, null);

            Person receivedPerson = new Person();
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                receivedPerson.setName(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_NAME)));
                receivedPerson.setAge(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_AGE)));
                receivedPerson.setOccupation(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_OCCUPATION)));
                receivedPerson.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_PERSON_IMAGE)));
            }

            return receivedPerson;
        }


        /**delete record**/
        public void deletePersonRecord(long id, Context context) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE _id='"+id+"'");
            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
        }

        /**update record**/
        public void updatePersonRecord(long personId, Context context, Person updatedperson) {
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  "+TABLE_NAME+" SET name ='"+ updatedperson.getName() + "', age ='" + updatedperson.getAge()+ "', occupation ='"+ updatedperson.getOccupation() + "', image ='"+ updatedperson.getImage() + "'  WHERE _id='" + personId + "'");
            Toast.makeText(context, "Updated successfully.", Toast.LENGTH_SHORT).show();
        }

        public  void updatePersonImage(long personId, Context context, String image,String bio){
            SQLiteDatabase db = this.getWritableDatabase();
            //you can use the constants above instead of typing the column names
            db.execSQL("UPDATE  "+TABLE_NAME+" SET   occupation ='"+ bio + "', image ='"+ image + "'  WHERE _id='" + personId + "'");
            Toast.makeText(context, "Updated successfully.", Toast.LENGTH_SHORT).show();

        }
    }


}
