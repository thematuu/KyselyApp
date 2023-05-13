package com.example.kyselyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questions;
    private int selectedPosition = -1;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_list_item, parent, false);
        return new ViewHolder(view);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.questionTextView.setText(questions.get(position).getText());

        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorItemSelected2));
            holder.questionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selectedTextColor));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorItemDefault2));
            holder.questionTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.defaultTextColor));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == holder.getAdapterPosition()) {
                    selectedPosition = -1;
                } else {
                    selectedPosition = holder.getAdapterPosition();
                }
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void updateQuestions(List<Question> updatedQuestions) {
        this.questions = updatedQuestions;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTextView;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questionTextView = itemView.findViewById(R.id.questionTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
