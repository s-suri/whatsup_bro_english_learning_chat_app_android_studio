package com.some.notes.Uploads;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.some.notes.LoginActivityInstagram;
import com.some.notes.R;
import com.some.notes.UserInfo;
import com.some.notes.WebItems;

public class MainActivityUpload extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFreindrecyclerList;
    DatabaseReference UsersRef;
    SearchView searchView;
    EditText search_users;
    FirebaseAuth mAuth;
    String currentUserID;
    private FirebaseUser currentUser;
    Window window;
    private String saveCurrentTime, saveCurrentDate,userType;
    DatabaseReference reference,uploaderUserInformation;
    ImageView dataupload;
    RelativeLayout relativeLayout;
    CardView card_1,card_2,card_3,card_4,card_5,card_6;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crome);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        start();


        if (Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.blue_300));

        }

        dataupload = findViewById(R.id.dataUpload);
        dataupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityUpload.this,UserInfo.class);
                startActivity(intent);
            }
        });


        uploaderUserInformation = FirebaseDatabase.getInstance().getReference("Users");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Weburls");
        FindFreindrecyclerList = (RecyclerView)findViewById(R.id.chats_list);
        FindFreindrecyclerList.setLayoutManager(new LinearLayoutManager(this));


        relativeLayout = findViewById(R.id.relative_layout);

        card_1 = findViewById(R.id.card_1);
        card_2 = findViewById(R.id.card_2);
        card_3 = findViewById(R.id.card_3);
        card_4 = findViewById(R.id.card_4);
        card_5 = findViewById(R.id.card_5);
        card_6 = findViewById(R.id.card_6);


        card_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","10th Class");
                startActivity(intent);

            }
        });


        card_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","12th Class");
                startActivity(intent);

            }
        });



        card_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","Bank Exam");
                startActivity(intent);

            }
        });



        card_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","Railway Exam");
                startActivity(intent);

            }
        });

        card_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","Artificial Intelligence");
                startActivity(intent);

            }
        });

        card_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                intent.putExtra("university_name","Java Programing");
                startActivity(intent);

            }
        });


        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    FindFreindrecyclerList.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);

                } else {
                    FindFreindrecyclerList.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    firebaseSearch(charSequence.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }



    private void start(){
        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            Toast.makeText(this, "Welcome  !!!", Toast.LENGTH_SHORT).show();
        }
    }



    private void firebaseSearch(String newText) {

        String query = newText.toLowerCase();

        Query ChatsRef = UsersRef.orderByChild("search").startAt(newText).endAt(newText + "\uf8ff");

        FirebaseRecyclerOptions<WebItems> options=
                new FirebaseRecyclerOptions.Builder<WebItems>()
                        .setQuery(ChatsRef, WebItems.class).build();


        final FirebaseRecyclerAdapter<WebItems, ContactViewHolder> adapter =
                new FirebaseRecyclerAdapter<WebItems, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position, @NonNull final WebItems model) {

                        holder.web_title.setText(model.getTitle());
                        holder.web_des.setText(model.getDescription());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (model.getType().equals("web_pdf")){
                                    Intent intent = new Intent(MainActivityUpload.this, ViewPdfPosts.class);
                                   intent.putExtra("university_name",model.getUniversityName());
                                    startActivity(intent);
                                }

                                else if(model.getType().equals("web_url")){
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(model.getUrl()));
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_open_website,viewGroup,false);
                        ContactViewHolder viewHolder = new ContactViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFreindrecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    /*
    @Override
    protected void onStart() {
        super.onStart();




        FirebaseRecyclerOptions<WebItems> options=
                new FirebaseRecyclerOptions.Builder<WebItems>()
                        .setQuery(UsersRef, WebItems.class).build();


        final FirebaseRecyclerAdapter<WebItems, ContactViewHolder>  adapter =
                new FirebaseRecyclerAdapter<WebItems, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position, @NonNull final WebItems model) {

                        holder.web_title.setText(model.getTitle());
                        holder.web_des.setText(model.getDescription());



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(model.getUrl()));
                                startActivity(intent);    }
                        });



                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_open_website,viewGroup,false);
                        ContactViewHolder viewHolder = new ContactViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFreindrecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

     */



    public static  class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView web_title, web_des;


        public ContactViewHolder(@NonNull View itemView) {

            super(itemView);

            web_title = itemView.findViewById(R.id.web_title);
            web_des = itemView.findViewById(R.id.web_description);



        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivityUpload.this, LoginActivityInstagram.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }


}



//  309125433833883_309128810500212