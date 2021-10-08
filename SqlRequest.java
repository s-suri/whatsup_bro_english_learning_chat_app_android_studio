package com.some.notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.some.notes.Model.Contacts;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SqlRequest extends AppCompatActivity {

    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef,contect_saved;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Uri fileUri;
    DatabaseReference RootRef;
    FirebaseUser fuser;
    private String saveCurrentTime, saveCurrentDate;
    ProgressBar progress_circular;
    Window window;
    TextView search, ic_more,password_null;

    public static String email;
    Intent intent;
    float x1,x2,y1,y2;
    TextView study_chats,chats,requests,line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_requests);


        intent = getIntent();
        email = intent.getStringExtra("name");


        search = findViewById(R.id.search);
        ic_more = findViewById(R.id.ic_more);
        study_chats = findViewById(R.id.study_chats);
        chats= findViewById(R.id.chats);
        line = findViewById(R.id.line);
        requests = findViewById(R.id.request);


        if (Build.VERSION.SDK_INT >= 21) {
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }



        progress_circular = findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contect_saved= FirebaseDatabase.getInstance().getReference().child("ContactSaved");


        myRequestsList = (RecyclerView) findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(this));

        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SqlRequest.this,SqlChatsActivity.class);
                intent.putExtra("name",email);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(study_chats, "studychats");
                pairs[1] = new Pair<View, String>(search, "search");
                pairs[2] = new Pair<View, String>(ic_more, "icmore");
                pairs[3] = new Pair<View, String>(chats, "chats");
                pairs[4] = new Pair<View, String>(line, "line");
                pairs[5] = new Pair<View, String>(requests, "requests");


                @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)SqlRequest.this, pairs);


                startActivity(intent, options.toBundle());
                finish();

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
                if (y1 > y2){
                    Intent intent = new Intent(SqlRequest.this,SqlChatsActivity.class);
                    intent.putExtra("name",email);
                    Pair[] pairs = new Pair[6];
                    pairs[0] = new Pair<View, String>(study_chats, "studychats");
                    pairs[1] = new Pair<View, String>(search, "search");
                    pairs[2] = new Pair<View, String>(ic_more, "icmore");
                    pairs[3] = new Pair<View, String>(chats, "chats");
                    pairs[4] = new Pair<View, String>(line, "line");
                    pairs[5] = new Pair<View, String>(requests, "requests");


                    @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) SqlRequest.this, pairs);


                    startActivity(intent, options.toBundle());
                    finish();


                }
                break;
        }
        return false;
    }



    @Override
    public void onStart()
    {
        super.onStart();



        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, RequestsFragment.RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsFragment.RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsFragment.RequestsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);


                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received"))
                                    {
                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.hasChild("imageurl"))
                                                {
                                                    final String requestProfileImage= dataSnapshot.child("imageurl").getValue().toString();

                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }

                                                final String requestUserName = dataSnapshot.child("fullname").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("bio").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText("wants to connect with you.");



                                                UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                                        if (dataSnapshot.hasChild("imageurl"))
                                                        {
                                                            final String senderRequestImage= dataSnapshot.child("imageurl").getValue().toString();


                                                        }


                                                        final String senderRequestName = dataSnapshot.child("fullname").getValue().toString();
                                                        final String senderRequestStatus = dataSnapshot.child("bio").getValue().toString();



                                                        holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {


                                                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        Toast.makeText(SqlRequest.this, "New Contact Saved", Toast.LENGTH_SHORT).show();


                                                                                                        RootRef = FirebaseDatabase.getInstance().getReference();
                                                                                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");


                                                                                                        DatabaseReference userMessageKeyRef = RootRef
                                                                                                                .child(currentUserID).push();

                                                                                                        final String messagePushID = generateString(12);
                                                                                                        String random = generateString(12);


                                                                                                        Calendar calForDate = Calendar.getInstance();
                                                                                                        SimpleDateFormat currendateFormat = new SimpleDateFormat("MMM dd");
                                                                                                        saveCurrentDate = currendateFormat.format(calForDate.getTime());


                                                                                                        Calendar calForTime = Calendar.getInstance();
                                                                                                        SimpleDateFormat currenTimeFormat = new SimpleDateFormat("hh:mm a");
                                                                                                        saveCurrentTime = currenTimeFormat.format(calForTime.getTime());


                                                                                                        HashMap<String, Object> groupMessageKey = new HashMap<>();
                                                                                                        RootRef.updateChildren(groupMessageKey);


                                                                                                        final String requestProfileImage= dataSnapshot.child("imageurl").getValue().toString();

                                                                                                        Map messageTextBody = new HashMap();
                                                                                                        messageTextBody.put("bio", requestUserStatus);
                                                                                                        messageTextBody.put("fullname", requestUserName);
                                                                                                        messageTextBody.put("search", requestUserName.toLowerCase());
                                                                                                        messageTextBody.put("sender", currentUserID);
                                                                                                        messageTextBody.put("receiver", list_user_id);
                                                                                                        messageTextBody.put("imageurl", requestProfileImage);
                                                                                                        messageTextBody.put("messageID", messagePushID);
                                                                                                        messageTextBody.put("time", saveCurrentTime);
                                                                                                        messageTextBody.put("date", saveCurrentDate);
                                                                                                        messageTextBody.put("id", "no");
                                                                                                        messageTextBody.put("randomId",random);

                                                                                                        Map messageBodyDetails = new HashMap();

                                                                                                        final String senderRequestImage= dataSnapshot.child("imageurl").getValue().toString();

                                                                                                        Map senderTextBody = new HashMap();
                                                                                                        senderTextBody.put("bio", senderRequestStatus);
                                                                                                        senderTextBody.put("fullname", senderRequestName);
                                                                                                        senderTextBody.put("search", senderRequestName.toLowerCase());
                                                                                                        senderTextBody.put("sender", list_user_id);
                                                                                                        senderTextBody.put("receiver", currentUserID);
                                                                                                        senderTextBody.put("imageurl", senderRequestImage);
                                                                                                        senderTextBody.put("messageID", messagePushID);
                                                                                                        senderTextBody.put("time", saveCurrentTime);
                                                                                                        senderTextBody.put("date", saveCurrentDate);
                                                                                                        senderTextBody.put("id", "no");
                                                                                                        senderTextBody.put("randomId",random);


                                                                                                        Map senderBodyDetails = new HashMap();
                                                                                                        senderBodyDetails.put(list_user_id + "/" + messagePushID, senderTextBody);
                                                                                                        messageBodyDetails.put(currentUserID + "/" + messagePushID, messageTextBody);



                                                                                                        RootRef.updateChildren(messageBodyDetails);
                                                                                                        RootRef.updateChildren(senderBodyDetails);


                                                                                                        ContactsRef.child(currentUserID).child(messagePushID).child("messageID")
                                                                                                                .setValue(messagePushID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {

                                                                                                                    ContactsRef.child(list_user_id).child(messagePushID).child("Contact")
                                                                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {

                                                                                                                                ContactsRef.child(currentUserID).child(messagePushID).child("Contact")
                                                                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()) {

                                                                                                                                            contect_saved.child(currentUserID).child(list_user_id).child("saved").setValue("yes")
                                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                            if (task.isSuccessful()){


                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    });

                                                                                                                                        }

                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }

                                                                                                                        }
                                                                                                                    });

                                                                                                                }

                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                }
                                                                            }
                                                                        });


                                                            }
                                                        });










                                                        holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if (task.isSuccessful())
                                                                                                    {
                                                                                                        Toast.makeText(SqlRequest.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (type.equals("sent"))
                                    {
                                        Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                                        request_sent_btn.setText("Req Sent");

                                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.hasChild("fullname"))
                                                {
                                                    final String requestProfileImage = dataSnapshot.child("imageurl").getValue().toString();

                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }

                                                final String requestUserName = dataSnapshot.child("fullname").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("bio").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText("you have sent a request to " + requestUserName);


                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view)
                                                    {
                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Cancel Chat Request"
                                                                };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SqlRequest.this);
                                                        builder.setTitle("Already Sent Request");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i)
                                                            {
                                                                if (i == 0)
                                                                {
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(SqlRequest.this, "you have cancelled the chat request.", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        RequestsFragment.RequestsViewHolder holder = new RequestsFragment.RequestsViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();
        progress_circular.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SqlRequest.this,SqlChatsActivity.class);
        intent.putExtra("name",email);
        Pair[] pairs = new Pair[6];
        pairs[0] = new Pair<View, String>(study_chats, "studychats");
        pairs[1] = new Pair<View, String>(search, "search");
        pairs[2] = new Pair<View, String>(ic_more, "icmore");
        pairs[3] = new Pair<View, String>(chats, "chats");
        pairs[4] = new Pair<View, String>(line, "line");
        pairs[5] = new Pair<View, String>(requests, "requests");


        @SuppressLint("NewApi") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)SqlRequest.this, pairs);


        startActivity(intent, options.toBundle());
        finish();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;


        public RequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);


            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }

    private String generateString(int lenth){
        char[] chasr= "QWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <lenth ; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }
}
