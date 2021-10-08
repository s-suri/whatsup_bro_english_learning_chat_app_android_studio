package com.some.notes;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PdfPortal extends AppCompatActivity {

    EditText search_users;
    TextView university_nameportal,cart;
    DatabaseReference UsersRef;
    private RecyclerView FindFreindrecyclerList,suggetionitems;
    Button click;
    private String userid;
    AutoCompleteTextView course_portal, branch_portal, subject_portal, test_portal;
    Button btn_search_portal;
    ProgressBar progress_circular;
    Window window;
    PdfPortalAdapter pdfPortalAdapter;
    AdView adView;

    ArrayList<WebItems> mChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_portal);


        Intent intent;
        intent = getIntent();
        userid = intent.getStringExtra("university_name");


        mChats = new ArrayList<>();
        progress_circular = findViewById(R.id.progress_circular);
        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PdfPortal.this,PdfViewerActivity.class);
                intent.putExtra("university_name",userid);
                startActivity(intent);
            }
        });


        university_nameportal = findViewById(R.id.university_name_portal);
        university_nameportal.setText(userid + " Pdfs");

        UsersRef = FirebaseDatabase.getInstance().getReference("Pdfs").child(userid);
        FindFreindrecyclerList = (RecyclerView) findViewById(R.id.recycler_portal);
        FindFreindrecyclerList.setLayoutManager(new LinearLayoutManager(this));


        suggetionitems = findViewById(R.id.recycler_suggetion_portal);
        suggetionitems.setLayoutManager(new LinearLayoutManager(this));

        pdfPortalAdapter = new PdfPortalAdapter(this,mChats);
        suggetionitems.setAdapter(pdfPortalAdapter);


        if (Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.blue_300));

        }

        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firebaseSearch(charSequence.toString().toLowerCase());


                if (charSequence.toString().trim().length() == 0) {

                    FindFreindrecyclerList.setVisibility(View.GONE);
                    suggetionitems.setVisibility(View.VISIBLE);

                } else {
                    FindFreindrecyclerList.setVisibility(View.VISIBLE);
                    suggetionitems.setVisibility(View.GONE);


                    firebaseSearch(charSequence.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readNotifications();


        }


    private void readNotifications(){

        UsersRef.child("suggestion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    WebItems notification = snapshot.getValue(WebItems.class);
                    mChats.add(notification);
                }


                Collections.reverse(mChats);
                pdfPortalAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void firebaseSearch(String newText) {

        String query = newText.toLowerCase();

        Query ChatsRef = UsersRef.child("suri").orderByChild("search").startAt(newText).endAt(newText + "\uf8ff");

        FirebaseRecyclerOptions<WebItems> options=
                new FirebaseRecyclerOptions.Builder<WebItems>()
                        .setQuery(ChatsRef, WebItems.class).build();


        final FirebaseRecyclerAdapter<WebItems, ContactViewHolder> adapter =
                new FirebaseRecyclerAdapter<WebItems, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position, @NonNull final WebItems model) {




                        holder.portal_course_name.setText(model.getCourseName());
                        holder.portal_semester_name.setText(model.getSemesterName());
                        holder.portal_subject_name.setText(model.getSubjectName());
                        holder.portal_test_name.setText(model.getTestName());
                        holder.poratl_branch_name.setText(model.getBranchName());
                        Picasso.get().load(model.getUrl()).into( holder.portal_image);


                  //      Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/pdf_image.png?alt=media&token=82269ec7-e22f-49c2-88c5-2cdcdd241128").into(holder.portal_image);


                        holder.portal_download_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(PdfPortal.this,PdfTransaction.class);
                                intent.putExtra("user_id",model.getSender());
                                intent.putExtra("download_id",model.getUrl());
                                intent.putExtra("payment_id",model.getPaymentId());
                                intent.putExtra("PaymentHolderName",model.getUpiHolderName());
                                intent.putExtra("amount",model.getAmount());
                                intent.putExtra("universityName",userid);
                                intent.putExtra("messageID",model.getMainId());
                                intent.putExtra("print_message",userid +"  "+ model.getCourseName()+"  "+ model.getBranchName() +"  "+ model.getSemesterName() + "  " +model.getSubjectName() +"  "+model.getTestName());
                                holder.itemView.getContext().startActivity(intent);



                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pdf_portal,viewGroup,false);
                        ContactViewHolder viewHolder = new ContactViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFreindrecyclerList.setAdapter(adapter);
        adapter.startListening();
    }
    class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView portal_course_name, portal_semester_name,portal_subject_name,poratl_branch_name,portal_test_name;

        Button portal_download_pdf;
        ImageView portal_image;


        public ContactViewHolder(@NonNull View itemView) {

            super(itemView);

            portal_course_name = itemView.findViewById(R.id.row_course);
            poratl_branch_name = itemView.findViewById(R.id.row_branch);
            portal_test_name = itemView.findViewById(R.id.row_test);
            portal_subject_name = itemView.findViewById(R.id.row_subject);
            portal_semester_name =itemView.findViewById(R.id.row_semester);
            portal_download_pdf = itemView.findViewById(R.id.download_pdf_portal);
            portal_image = itemView.findViewById(R.id.pdf_image);



        }
    }


    public  class PdfPortalAdapter extends RecyclerView.Adapter<PdfPortalAdapter.PdfHolder> {

        List<WebItems> mChats;
        String retImage;
        Context context;


        public PdfPortalAdapter(Context context, List<WebItems> mChats) {
            this.mChats = mChats;
            this.context = context;
        }

        @NonNull
        @Override
        public PdfHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_pdf_portal, viewGroup, false);
            return new PdfHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final PdfHolder holder, int position) {


            final WebItems model = mChats.get(position);

            holder.portal_course_name.setText(model.getCourseName());
            holder.portal_semester_name.setText(model.getSemesterName());
            holder.portal_subject_name.setText(model.getSubjectName());
            holder.portal_test_name.setText(model.getTestName());
            holder.poratl_branch_name.setText(model.getBranchName());

            Picasso.get().load(model.getUrl()).into(holder.portal_image);
        //    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/pdf_image.png?alt=media&token=82269ec7-e22f-49c2-88c5-2cdcdd241128").into(holder.portal_image);





            holder.portal_download_pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(holder.itemView.getContext(),PdfTransaction.class);
                    intent.putExtra("user_id",model.getSender());
                    intent.putExtra("download_id",model.getUrl());
                    intent.putExtra("payment_id",model.getPaymentId());
                    intent.putExtra("PaymentHolderName",model.getUpiHolderName());
                    intent.putExtra("amount",model.getAmount());
                    intent.putExtra("messageID",model.getMainId());
                    intent.putExtra("universityName",userid);
                    intent.putExtra("print_message",userid   +"  "+ model.getCourseName()+"  "+ model.getBranchName() +"  "+ model.getSemesterName() + "  " +model.getSubjectName() +"  "+model.getTestName());
                    holder.itemView.getContext().startActivity(intent);



                }
            });

        }


        @Override
        public int getItemCount() {
            return mChats.size();
        }

        public class PdfHolder extends RecyclerView.ViewHolder {

            TextView portal_course_name, portal_semester_name,portal_subject_name,poratl_branch_name,portal_test_name;

            Button portal_download_pdf;
            ImageView portal_image;


            public PdfHolder(@NonNull View itemView) {
                super(itemView);


                portal_course_name = itemView.findViewById(R.id.row_course);
                poratl_branch_name = itemView.findViewById(R.id.row_branch);
                portal_test_name = itemView.findViewById(R.id.row_test);
                portal_subject_name = itemView.findViewById(R.id.row_subject);
                portal_semester_name =itemView.findViewById(R.id.row_semester);
                portal_download_pdf = itemView.findViewById(R.id.download_pdf_portal);
                portal_image = itemView.findViewById(R.id.pdf_image);


            }
        }
    }
}


/*
ca-app-pub-2729927781686503/9030643871

Next, place the ad unit inside your app
Follow these instructions:
Complete the instructions in the Google Mobile Ads SDK guide using this app ID:
Study Chats Notesca-app-pub-2729927781686503~9675842839
Follow the banner implementation guide to integrate the SDK. You'll specify ad type, size, and placement when you integrate the code using this ad unit ID:
chat_1ca-app-pub-2729927781686503/2918862793
Review the AdMob policies to ensure your implementation complies.

 */

