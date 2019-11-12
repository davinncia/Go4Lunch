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
import com.example.go4lunchjava.auth.User;

import java.util.List;

public class WorkmateAdapter extends RecyclerView.Adapter<WorkmateAdapter.WorkmateViewHolder> {

    private List<User> mUsers;

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.wormate_item, parent, false);
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        User currentUser = mUsers.get(position);

        holder.textView.setText(currentUser.getDisplayName());

        Glide.with(holder.imageView.getContext())
                .load(currentUser.getAvatarUri())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUsers != null ? mUsers.size() : 0;
    }

    public void updateData(List<User> users){
        this.mUsers = users;
        this.notifyDataSetChanged();
    }


    class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_workmate_list);
            textView = itemView.findViewById(R.id.tv_workmates_list);

        }
    }
}
