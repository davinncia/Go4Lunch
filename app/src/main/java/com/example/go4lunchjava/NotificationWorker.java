package com.example.go4lunchjava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationWorker extends Worker {

    public static final String KEY_RESTAURANT_NAME = "name_key";
    public static final String KEY_RESTAURANT_ID = "id_key";
    public static final String KEY_ADDRESS = "address_key";

    private Context mContext;
    private UsersFireStoreRepository mFireStoreRepository;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.mContext = context;
        this.mFireStoreRepository = UsersFireStoreRepository.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {

        String restaurant = getInputData().getString(KEY_RESTAURANT_NAME);
        String address = getInputData().getString(KEY_ADDRESS);
        String id = getInputData().getString(KEY_RESTAURANT_ID);

        try {
            createNotificationChannel();
            getWorkmatesJoining(restaurant, address, id);
            //sendNotification(restaurant, address, workmates);
        } catch (Exception e){
            return Result.retry();
        }

        return Result.success();
    }

    private void createNotificationChannel(){
        //This should always be executed on app start.

        // Create the NotificationChannel, but only on API 26+
        String CHANNEL_ID = "eating_time_notif_channel";
        String notifDescription = "Notification to alert on eating time";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = notifDescription;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getWorkmatesJoining(String restaurant, String address, String restaurantId){
        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            List<String> workmates = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots){

                if (document.get(Workmate.FIELD_RESTAURANT_ID) != null &&
                        document.get(Workmate.FIELD_RESTAURANT_ID).equals(restaurantId) &&
                        !document.getId().equals(FirebaseAuth.getInstance().getUid())){

                    workmates.add(String.valueOf(document.get(Workmate.FIELD_NAME)));

                }
            }

            sendNotification(restaurant, address, workmates.toArray(new String[0]));
        });
    }

    private void sendNotification(String restaurant, String address, String[] coworkers){
        String CHANNEL_ID = "eating_time_notif_channel";
        int NOTIF_ID = 44;

        StringBuilder workmateNames = new StringBuilder();
        for (String name : coworkers){
            workmateNames.append(name);
            workmateNames.append(" ");
        }

        String textTitle = mContext.getResources().getString(R.string.notif_title);
        String textContent = restaurant + address;

        // Create an explicit intent
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //putExtra
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_restaurant)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textContent + "\n" + workmateNames))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIF_ID, builder.build());
    }

}