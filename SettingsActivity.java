package com.some.notes;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.some.notes.Model.Person;
import com.some.notes.Model.SqlMessage;
import com.some.notes.Model.User;
import com.some.notes.sillicompresser.SiliCompressor;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    StorageTask mUploadTask;
    private Button UpdateAccountSettings;
    private EditText  userStatus;
    private TextView userName;
    private CircleImageView userProfileImage;
    Uri compressUri = null;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;

    private Toolbar SettingsToolBar;
    private String myUrl = "";

    Window window;

    PersonEmailDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        if (Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        InitializeFields();




        userName.setVisibility(View.VISIBLE);


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             //   UpdateSettings();
            }
        });


        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }


    public static  byte[] imageViewToBitmap(ImageView image){
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bytearray = stream.toByteArray();
        return bytearray;
    }



    private void InitializeFields()
    {
        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName =  findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            final Uri imageUri = data.getData();

            Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();

            Picasso.get().load(imageUri).into(userProfileImage);

            new ImageCompressionAsyncTask(this).execute(imageUri.toString(),
                    Environment.getExternalStorageDirectory() + "/Study Chats/images");



//            if (imageUri != null) {
//
//
//                final StorageReference filePath = UserProfileImagesRef.child("compress" + ".jpg");
//                loadingBar.setMessage("Please wait, your profile image is updating...");
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();
//
//
//                mUploadTask = filePath.putFile(imageUri);
//           //     mUploadTask = filePath.putFile(imageUri);
//                mUploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//                        return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
//
//                            Uri downloadUrl = task.getResult();
//                            myUrl = downloadUrl.toString();
//
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("imageurl", myUrl);
//
//
//                            reference.updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful()) {
//
//                                        loadingBar.dismiss();
//                                        Toast.makeText(SettingsActivity.this, "Update Successfully..!!", Toast.LENGTH_SHORT).show();
//
//                                    } else {
//
//                                        loadingBar.dismiss();
//                                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                }
//                            });
//
//                        }
//                    }
//                });
//
//            }

        }
    }


    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        Context mContext;
        int position;
        int totalItemSelected;

        public ImageCompressionAsyncTask(Context context) {
            mContext = context;

        }

        @Override
        protected String doInBackground(String... params) {
            return SiliCompressor.with(mContext).compress(params[0], new File(params[1]));
        }

        @Override
        protected void onPostExecute(String s) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                compressUri = Uri.parse(s);

                Cursor c = getContentResolver().query(compressUri, null, null, null, null);
                c.moveToFirst();
            } else {
                File imageFile = new File(s);
                compressUri = Uri.fromFile(imageFile);
            }
            if (compressUri != null) {


                final StorageReference filePath = UserProfileImagesRef.child("compress" + ".jpg");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                mUploadTask = filePath.putFile(compressUri);
                //     mUploadTask = filePath.putFile(imageUri);
                mUploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);

                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("imageurl", myUrl);


                            reference.updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        loadingBar.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Update Successfully..!!", Toast.LENGTH_SHORT).show();

                                    } else {

                                        loadingBar.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                        }
                    }
                });

            }
        }

    }


    private void UpdateSettings ()
        {
            String setUserName = userName.getText().toString();
            String setStatus = userStatus.getText().toString();

            if (TextUtils.isEmpty(setUserName)) {
                Toast.makeText(this, "Please write your user name first....", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(setStatus)) {
                Toast.makeText(this, "Please write your status....", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, Object> profileMap = new HashMap<>();

                profileMap.put("bio", setStatus);
                RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingsActivity.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = task.getException().toString();
                                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("fullname") && (dataSnapshot.hasChild("imageurl"))))
                        {
                            String retrieveUserName = dataSnapshot.child("fullname").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("bio").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("imageurl").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("fullname")))
                        {
                            String retrieveUserName = dataSnapshot.child("fullname").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("bio").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                        }
                        else
                        {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public class PersonEmailDBHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "skrathore.db";
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
//            db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
//                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
//                    COLUMN_PERSON_AGE + " NUMBER NOT NULL, " +
//                    COLUMN_PERSON_OCCUPATION + " TEXT NOT NULL, " +
//                    COLUMN_PERSON_IMAGE + " BLOB NOT NULL);"
//            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // you can implement here migration process
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            this.onCreate(db);
        }
        /**create record**/
        public void saveNewPerson(Person person,byte[] imageview) {

            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "INSERT INTO RECORD VALUES(NULL,?,?,?,?)";
            SQLiteStatement statement = db.compileStatement(sql);

            statement.bindString(1,person.getName());
            statement.bindString(1,person.getAge());
            statement.bindString(1,person.getOccupation());
            statement.bindBlob(1,imageview);

            statement.executeInsert();
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
