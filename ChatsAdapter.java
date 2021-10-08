package com.some.notes;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.some.notes.Model.Chat;
import com.some.notes.Model.Comment;
import com.some.notes.Notifications.Token;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

  public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    List<Comment> mChats;
      String retImage;
    Context context;
      private String currentUserID = "";
      private String theLastMessage;
      String AES = "AES";
      String outputString;
      private DatabaseReference ChatsRef, UsersRef;
      private FirebaseAuth mAuth;
      private LayoutInflater inflater;


      public ChatsAdapter(Context context, List<Comment> mChats) {
        this.mChats = mChats;
        this.context = context;
          inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_chat_fragment,parent,false);
        ChatsViewHolder viewHolder = new ChatsViewHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference(currentUserID);



        Comment model = mChats.get(position);


        int count = 0;

        Picasso.get().load(model.getImageurl()).into(holder.profileImage);


        String messageid = model.getMessageID();

        holder.userName.setText(model.getFullname());




       holder.last_msg.setVisibility(View.VISIBLE);






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(holder.itemView.getContext(), MessageActivity.class);
                chatIntent.putExtra("userid", model.getReceiver());
                chatIntent.putExtra("messageid",model.getMessageID());
                holder.itemView.getContext().startActivity(chatIntent);

            }

        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                Intent chatIntent = new Intent(holder.itemView.getContext(), UserImage.class);
                chatIntent.putExtra("url", retImage);


                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(holder.profileImage, "imageTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                holder.itemView.getContext().startActivity(chatIntent, options.toBundle());




                                /*
                                Intent chatIntent = new Intent(getContext(), ImageViewActivity.class);
                                chatIntent.putExtra("url", retImage[0]);


                                Pair[] pairs = new Pair[1];
                                pairs[0] = new Pair<View, String>(holder.profileImage, "imageTransition");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);


                                startActivity(chatIntent, options.toBundle());

                                 */

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                holder.user_delete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.user_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(currentUserID).child(model.getMessageID());
                rootRef

                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {


                            Toast.makeText(holder.itemView.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Error occurred..", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }


        });

    }


        @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView last_msg, userName, user_delete,password_null;
        private TextView userStatus, unseen, sender_unseen;




        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);


            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            user_delete = itemView.findViewById(R.id.user_delete);
            unseen = itemView.findViewById(R.id.unseen);
            last_msg = itemView.findViewById(R.id.last_msg);
            password_null = itemView.findViewById(R.id.password_null);

        }
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

  }
