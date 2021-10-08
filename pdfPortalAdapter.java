package com.some.notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;
/*

public class pdfPortalAdapter extends RecyclerView.Adapter<pdfPortalAdapter.PdfHolder> {

    List<WebItems> mChats;
    String retImage;
    Context context;


    public pdfPortalAdapter(Context context, List<WebItems> mChats) {
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

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/watsapp-6d8e6.appspot.com/o/pdf_image.png?alt=media&token=82269ec7-e22f-49c2-88c5-2cdcdd241128").into(holder.portal_image);


        holder.portal_download_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(),PdfTransaction.class);
                intent.putExtra("user_id",model.getSender());
                intent.putExtra("download_id",model.getUrl());
                intent.putExtra("payment_id",model.getPaymentId());
                intent.putExtra("PaymentHolderName",model.getUpiHolderName());
                intent.putExtra("print_message",userid   +"  "+ model.getCourseName()+"  "+ model.getBranchName() +"  "+ model.getSemesterName() + "  " +model.getSubjectName() +"  "+model.getTestName());
                holder.itemView.getContext().startActivity(intent);



            }
        });

    }


    @Override
    public int getItemCount() {
        return 0;
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

 */
