package com.some.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.some.notes.Adapter.PersonAdapter;
import com.some.notes.Model.Person;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PdfImages extends AppCompatActivity {
    private RecyclerView.LayoutManager mLayoutManager;
    private PersonAdapter adapter;
    private String filter = "";


    private RecyclerView recyclerView,mRecyclerView;
    private CommentAdapter commentAdapter;
    private List<WebItems> commentList;
    private String id;
    private String userid,messageId,subject_name;
     public static String random;
    TextView title,up;
    RelativeLayout relativeLayout;
    AdView adView;
    private PersonDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_images);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        title = findViewById(R.id.title);
        up = findViewById(R.id.up);
        relativeLayout = findViewById(R.id.relative);




        Intent intent;
        intent = getIntent();
        userid = intent.getStringExtra("university_name");
        messageId = intent.getStringExtra("messageId");
        subject_name = intent.getStringExtra("subject_name");
        random = intent.getStringExtra("random");
        dbHelper = new PersonDBHelper(this);


        title.setText(subject_name);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.GONE);

            }
        });

        id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);


        mRecyclerView = (RecyclerView)findViewById(R.id.downloded_images);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        populaterecyclerView(filter);
        readComments();

    }

    private void populaterecyclerView(String filter){
        dbHelper = new PersonDBHelper(this);
        adapter = new PersonAdapter(dbHelper.peopleList(filter), this, mRecyclerView);
        mRecyclerView.setAdapter(adapter);

    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pdfs").child(userid).child("suggestion").child(messageId).child("Images");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("pawan").exists()) {

                        WebItems comment = snapshot.getValue(WebItems.class);

                        Toast.makeText(PdfImages.this, "download", Toast.LENGTH_SHORT).show();


                    } else {

                        String image = snapshot.child("postimage").getValue().toString();
                        String postid = snapshot.child("postid").getValue().toString();


                        Person person = new Person("surender", "10", "kemar", image);
                        dbHelper.saveNewPerson(person);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("pawan", true);
                        reference.child(postid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(PdfImages.this, "Download...", Toast.LENGTH_SHORT).show();
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


    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ImageViewHolder> {

        private Context mContext;
        private List<WebItems> mComment;
        private String postid;
        private LayoutInflater inflater;
        private FirebaseUser firebaseUser;

        public CommentAdapter(Context context, List<WebItems> comments){
            mContext = context;
            mComment = comments;

        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pur_pdf_images, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

            WebItems model = mComment.get(position);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pdfs").child(userid).child("suggestion").child(messageId).child("Images");





            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("rajesh").exists()){

                            WebItems comment = snapshot.getValue(WebItems.class);

                            Toast.makeText(mContext, "download", Toast.LENGTH_SHORT).show();


                        }else {
                            Person person = new Person(model.getType(), model.getDescription(), model.getDescription(), model.getPostimage());
                            dbHelper.saveNewPerson(person);
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("rajesh",true);
                            reference.child(model.getPostid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(mContext, "Download...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            Picasso.get().load(model.getPostimage()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return mComment.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            public ImageView image;

            public ImageViewHolder(View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.pur_image_view);


            }
        }


    }


    public static class PersonDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = random+".db";
        private static final int DATABASE_VERSION = 3 ;
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_AGE = "age";
        public static final String COLUMN_PERSON_OCCUPATION = "occupation";
        public static final String COLUMN_PERSON_IMAGE = "image";


        public PersonDBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
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
    }



}
