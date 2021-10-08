package com.some.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.some.notes.Model.Contacts;
import com.some.notes.Notifications.APIService;
import com.some.notes.Notifications.Client;
import com.some.notes.Notifications.Data;
import com.some.notes.Notifications.MyResponse;
import com.some.notes.Notifications.Sender;
import com.some.notes.Notifications.Token;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView myRequestsList;
    APIService apiService;
    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef,contect_saved;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Uri fileUri;
    DatabaseReference RootRef;
    FirebaseUser fuser;
    private String saveCurrentTime, saveCurrentDate;
    ProgressBar progress_circular;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);


        progress_circular = RequestsFragmentView.findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contect_saved= FirebaseDatabase.getInstance().getReference().child("ContactSaved");


        myRequestsList = (RecyclerView) RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        return RequestsFragmentView;
    }


    @Override
    public void onStart()
    {
        super.onStart();



        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model)
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
                                                                                                                        Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();


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
                                                                                                                        messageTextBody.put("randomId",random);


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
                                                                                                Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
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

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                                                                                            Toast.makeText(getContext(), "you have cancelled the chat request.", Toast.LENGTH_SHORT).show();
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
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();
        progress_circular.setVisibility(View.GONE);
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
        char[] chasr= "1234567890".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <lenth ; i++) {
            char c = chasr[random.nextInt(chasr.length)];
            stringBuilder.append(c);


        }
        return stringBuilder.toString();
    }


}
