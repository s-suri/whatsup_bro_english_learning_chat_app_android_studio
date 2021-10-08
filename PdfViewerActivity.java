package com.some.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class PdfViewerActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private CommentAdapter commentAdapter;
        private List<WebItems> commentList;
        private String id;
        private String userid;
        Window window;
        AdView adView;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pdf_viewer);

            id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


            if (Build.VERSION.SDK_INT>=21){
                window = this.getWindow();
                window.setStatusBarColor(this.getResources().getColor(R.color.blue_300));

            }


            Intent intent;
            intent = getIntent();
            userid = intent.getStringExtra("university_name");

            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            commentList = new ArrayList<>();

            commentAdapter = new CommentAdapter(this, commentList);
            recyclerView.setAdapter(commentAdapter);


            readComments();

        }



        private void readComments(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pdfs").child(userid).child("suggestion");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child(id).exists()){
                            WebItems comment = snapshot.getValue(WebItems.class);
                            commentList.add(comment);

                        }


                    }
                    Collections.reverse(commentList);
                    commentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchesed_row, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

            final WebItems model = mComment.get(position);

            holder.portal_course_name.setText(model.getCourseName());
            holder.portal_semester_name.setText(model.getSemesterName());
            holder.portal_subject_name.setText(model.getSubjectName());
            holder.portal_test_name.setText(model.getTestName());
            holder.poratl_branch_name.setText(model.getBranchName());

            Picasso.get().load(model.getUrl()).into(holder.portal_image);

            holder.portal_download_pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(PdfViewerActivity.this, PdfImages.class);
                        intent.putExtra("university_name",userid);

                         intent.putExtra("messageId",model.getMainId());
                         intent.putExtra("subject_name",model.getSubjectName());
                         intent.putExtra("random","pawan");

                        holder.itemView.getContext().startActivity(intent);

                    }
                });



        }

        @Override
        public int getItemCount() {
            return mComment.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            TextView portal_course_name, portal_semester_name,portal_subject_name,poratl_branch_name,portal_test_name;

            Button portal_download_pdf;
            ImageView portal_image;

            public ImageViewHolder(View itemView) {
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
