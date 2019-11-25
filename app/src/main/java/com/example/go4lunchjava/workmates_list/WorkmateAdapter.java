package com.example.go4lunchjava.workmates_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunchjava.R;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    private List<Workmate> mWorkmates;

    private WorkmateClickListener mWorkmateClickListener;

    //Constructor
    WorkmateAdapter(WorkmateClickListener listener){
        this.mWorkmateClickListener = listener;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.wormate_item, parent, false);
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        Workmate currentUser = mWorkmates.get(position);

        String userSentence;
        if (currentUser.getRestaurantName() != null && !currentUser.getRestaurantName().isEmpty()) {
            userSentence = holder.itemView.getContext().getString(R.string.is_eating_at) + currentUser.getRestaurantName();
        } else {
            userSentence = holder.itemView.getContext().getString(R.string.has_not_decided_yet);
        }

        holder.textView.setText(String.format("%s%s", currentUser.getDisplayName(), userSentence));

        Glide.with(holder.imageView.getContext())
                .load(currentUser.getAvatarUri())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mWorkmates != null ? mWorkmates.size() : 0;
    }

    void updateData(List<Workmate> workmates){
        this.mWorkmates = workmates;
        this.notifyDataSetChanged();
    }


    class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_workmate_list);
            textView = itemView.findViewById(R.id.tv_workmates_list);

            itemView.setOnClickListener(view ->
                    mWorkmateClickListener.onWorkmateClick(mWorkmates.get(getAdapterPosition()).getUid()));

        }
    }

    interface WorkmateClickListener{
        void onWorkmateClick(String workmateId);
    }
}
