package com.example.go4lunchjava.restaurants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunchjava.R;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    //DATA
    private List<RestaurantItem> mRestaurants;

    //CLICKS
    private OnRestaurantClickListener mListener;

    public RestaurantAdapter(){
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

        RestaurantItem restaurant = mRestaurants.get(i);

        holder.restaurantNameView.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.openingHoursView.setText(restaurant.isOpen());
        holder.distanceView.setText(restaurant.getDistance());

        //Might be to much intelligence for an adapter
        if (restaurant.getRating() > 2) holder.starsView.setImageResource(R.drawable.ic_star_three);
        else if (restaurant.getRating() > 1) holder.starsView.setImageResource(R.drawable.ic_star_two);
        else if (restaurant.getRating() > 0 && restaurant.getRating() < 1) holder.starsView.setImageResource(R.drawable.ic_star);

        Glide.with(holder.pictureView.getContext())
                .load(restaurant.getPictureUrl())
                .centerCrop()
                .into(holder.pictureView);

    }

    @Override
    public int getItemCount() {
        return mRestaurants != null ? mRestaurants.size() : 0;
    }

    void populateRecyclerView(List<RestaurantItem> restaurants){
        this.mRestaurants = restaurants;
        notifyDataSetChanged();
    }

    interface OnRestaurantClickListener{
        void onRestaurantClick(RestaurantItem restaurant);
    }

    void setOnRestaurantClickListener(OnRestaurantClickListener listener){
        this.mListener = listener;
    }

    /////////////////
    ///VIEW HOLDER///
    /////////////////
    class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private TextView restaurantNameView;
        private TextView address;
        private TextView openingHoursView;
        private TextView distanceView;
        private ImageView starsView;
        private ImageView pictureView;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantNameView = itemView.findViewById(R.id.tv_name_restaurant_item);
            address = itemView.findViewById(R.id.tv_address_restaurant_item);
            openingHoursView = itemView.findViewById(R.id.tv_hours_restaurant_item);
            distanceView = itemView.findViewById(R.id.tv_distance_restaurant_item);
            starsView = itemView.findViewById(R.id.iv_stars_restaurant_item);
            pictureView = itemView.findViewById(R.id.iv_picture_restraurant_item);

            itemView.setOnClickListener(view -> mListener.onRestaurantClick(mRestaurants.get(getAdapterPosition())));
        }
    }
}
