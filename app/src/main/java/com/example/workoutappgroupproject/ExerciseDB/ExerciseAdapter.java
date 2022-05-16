package com.example.workoutappgroupproject.ExerciseDB;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutappgroupproject.R;

public class ExerciseAdapter extends ListAdapter<Exercise,ExerciseAdapter.ExerciseViewHolder> {
    private onItemClickListener listener;
    public ExerciseAdapter() {super(DIFF_CALLBACK);}

    private static final DiffUtil.ItemCallback<Exercise> DIFF_CALLBACK = new DiffUtil.ItemCallback<Exercise>() {
        @Override
        public boolean areItemsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getQuantity()== newItem.getQuantity() &&
                    oldItem.getTime() == newItem.getTime();
        }
    };

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_train,parent,false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise viewableExercise = getItem(position);
        holder.txtName.setText(viewableExercise.getName());
        if((viewableExercise.getTime()==0)){
            holder.txtTime.setText("");
        }
        else{
            holder.txtTime.setText(String.format("%s sec",viewableExercise.getTime()));
        }
        if(viewableExercise.getQuantity()==0){
            holder.txtQuantity.setText("");
        }
        else{
            holder.txtQuantity.setText(String.format("%s x",viewableExercise.getQuantity()));
        }
    }

    public Exercise getExercisePosition(int position){
        return getItem(position);
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtName;
        private final TextView txtQuantity;
        private final TextView txtTime;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTime = itemView.findViewById(R.id.txtTime);

            itemView.setOnClickListener(view -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onItemClickListener(getItem(position));
                }
            });
        }
    }
    public interface onItemClickListener{
        void onItemClickListener(Exercise exercise);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}

