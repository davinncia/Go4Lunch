package com.example.go4lunchjava.restaurant_details;

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
import com.example.go4lunchjava.workmates_list.Workmate;

import java.util.List;

public class RestaurantDetailsAdapter extends RecyclerView.Adapter<RestaurantDetailsAdapter.DetailsViewHolder>{

    private List<Workmate> mWorkmates;

    @NonNull
    @Override
    public RestaurantDetailsAdapter.DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.wormate_item, parent, false);
        return new RestaurantDetailsAdapter.DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailsAdapter.DetailsViewHolder holder, int position) {
        Workmate currentUser = mWorkmates.get(position);

        String userSentence = holder.textView.getContext().getString(R.string.is_joining);

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


    class DetailsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_workmate_list);
            textView = itemView.findViewById(R.id.tv_workmates_list);

        }
    }
}
