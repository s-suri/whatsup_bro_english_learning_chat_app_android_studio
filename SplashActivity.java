package com.some.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.some.notes.Adapter.PersonEmailDBHelper;
import com.some.notes.Model.Person;
import com.squareup.picasso.Picasso;
import java.util.LinkedList;
import java.util.List;



public class SplashActivity extends AppCompatActivity {

    ProgressBar splashProgress;
    int SPLASH_TIME = 1500; //This is 3 seconds
    Window window;
    private RecyclerView emailRecyclerview;
    private LinearLayoutManager emailsLayoutManager;
    private PersonEmailDBHelper emailHelper;
    private PersonEmailAdapter emailAdapter;
    private String emailfilter = "";
    private String filter = "";
    TextView last_email_user;
    private FirebaseUser currentUser;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);





        if (Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.blue_300));
        }

        splashProgress = findViewById(R.id.splashProgress);
        last_email_user = findViewById(R.id.last_email_user);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivityInstagram.class);
            startActivity(intent);
            finish();

        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do any action here. Now we are moving to next page
                    Intent mySuperIntent = new Intent(SplashActivity.this, MainActivity.class);
                   mySuperIntent.putExtra("name",last_email_user.getText().toString());
                    startActivity(mySuperIntent);


                    //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                    finish();

                }
            }, SPLASH_TIME);

        }



        emailRecyclerview = (RecyclerView)findViewById(R.id.emails_users);
        emailRecyclerview.setHasFixedSize(true);

        emailsLayoutManager = new LinearLayoutManager(this);

        emailRecyclerview.setLayoutManager(emailsLayoutManager);


        populaterEmailsecyclerView(filter);


        playProgress();

        //Code to start timer and take action after the timer ends

    }



    private void populaterEmailsecyclerView(String filter){
        emailHelper = new PersonEmailDBHelper(this);
        emailAdapter = new PersonEmailAdapter(emailHelper.peopleList(filter), this, emailRecyclerview);
        emailRecyclerview.setAdapter(emailAdapter);

    }




    public class PersonEmailAdapter extends RecyclerView.Adapter<PersonEmailAdapter.ViewHolder> {
        private List<Person> mPeopleList;
        private Context mContext;
        private RecyclerView mRecyclerV;


        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView personNameTxtV;
            public TextView personAgeTxtV;
            public TextView personOccupationTxtV;
            public ImageView personImageImgV;


            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                personNameTxtV = (TextView) v.findViewById(R.id.name);
                personAgeTxtV = (TextView) v.findViewById(R.id.age);
                personOccupationTxtV = (TextView) v.findViewById(R.id.occupation);
                personImageImgV = (ImageView) v.findViewById(R.id.image);

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
        public PersonEmailAdapter(List<Person> myDataset, Context context, RecyclerView recyclerView) {
            mPeopleList = myDataset;
            mContext = context;
            mRecyclerV = recyclerView;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(
                    parent.getContext());
            View v = inflater.inflate(R.layout.email_layout, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final Person person = mPeopleList.get(position);
            holder.personNameTxtV.setText("Name: " + person.getName());
            holder.personAgeTxtV.setText("Age: " + person.getAge());
            holder.personOccupationTxtV.setText("Occupation: " + person.getOccupation());
            Picasso.get().load(person.getImage()).placeholder(R.mipmap.ic_launcher).into(holder.personImageImgV);

            last_email_user.setText(person.getName());

            //listen to single view layout click
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Choose option");
                    builder.setMessage("Update or delete user?");
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //go to update activity
                            goToUpdateActivity(person.getId());

                        }
                    });
                    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
        }

        private void goToUpdateActivity(long personId){
        }


        @Override
        public int getItemCount() {
            return mPeopleList.size();
        }

    }


    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 50)
                .setDuration(1500)
                .start();
    }
}
