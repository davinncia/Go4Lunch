package com.example.go4lunchjava.restaurant_list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunchjava.R;

public class RestaurantAdapter extends ListAdapter<RestaurantItem, RestaurantAdapter.RestaurantViewHolder> {

    //CLICKS
    private OnRestaurantClickListener mListener;

    RestaurantAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.restaurant_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int i) {

        RestaurantItem restaurant = getItem(i);

        holder.restaurantNameView.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());

        holder.openingHoursView.setText(restaurant.getHours());
        if (restaurant.getHours().contains("Close"))
            holder.openingHoursView.setTextColor(Color.RED);
        else
            holder.openingHoursView.setTextColor(Color.DKGRAY);

        holder.distanceView.setText(restaurant.getDistance());

        holder.nbrOfWorkmatesView.setText(String.format("%s", "(" + restaurant.getWorkmatesNbr() + ")"));

        holder.starsView.setImageResource(restaurant.getRatingResource());

        if (!restaurant.getPictureUrl().isEmpty())
            Glide.with(holder.pictureView.getContext())
                .load(restaurant.getPictureUrl())
                .centerCrop()
                .into(holder.pictureView);

    }

    /////////////////
    //DIFF CALLBACK//
    /////////////////
    private static final DiffUtil.ItemCallback<RestaurantItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RestaurantItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull RestaurantItem oldItem, @NonNull RestaurantItem newItem) {
                    return oldItem.getPlaceId().equals(newItem.getPlaceId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull RestaurantItem oldItem, @NonNull RestaurantItem newItem) {
                    return oldItem.equals(newItem);
                }
            };

    /////////////////
    ///VIEW HOLDER///
    /////////////////
    class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private TextView restaurantNameView;
        private TextView address;
        private TextView openingHoursView;
        private TextView distanceView;
        private TextView nbrOfWorkmatesView;
        private ImageView starsView;
        private ImageView pictureView;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantNameView = itemView.findViewById(R.id.tv_name_restaurant_item);
            address = itemView.findViewById(R.id.tv_address_restaurant_item);
            openingHoursView = itemView.findViewById(R.id.tv_hours_restaurant_item);
            distanceView = itemView.findViewById(R.id.tv_distance_restaurant_item);
            nbrOfWorkmatesView = itemView.findViewById(R.id.tv_workmates_number_restaurant_item);
            starsView = itemView.findViewById(R.id.iv_stars_restaurant_item);
            pictureView = itemView.findViewById(R.id.iv_picture_restraurant_item);


            itemView.setOnClickListener(view -> mListener.onRestaurantClick(getItem(getAdapterPosition())));
        }
    }

    interface OnRestaurantClickListener{
        void onRestaurantClick(RestaurantItem restaurant);
    }

    void setOnRestaurantClickListener(OnRestaurantClickListener listener){
        this.mListener = listener;
    }

}
