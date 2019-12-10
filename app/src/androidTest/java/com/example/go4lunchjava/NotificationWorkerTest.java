package com.example.go4lunchjava;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class NotificationWorkerTest {


    @Before
    public void setup(){
        Context context = ApplicationProvider.getApplicationContext();
        Configuration config = new Configuration.Builder()
                // Use a SynchronousExecutor to make it easier to write tests
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                context, config);
    }

    @Test
    public void testNotifWorker() throws Exception {
        // Define input data
        Data input = new Data.Builder()
                .put(NotificationWorker.KEY_RESTAURANT_NAME, "L'alien")
                .put(NotificationWorker.KEY_ADDRESS, " - 2 rue Mars")
                .putStringArray(NotificationWorker.KEY_COWORKERS, new String[]{"R2D2", "Luke", "Leila"})
                .build();

        // Create request
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInputData(input)
                        .build();

        WorkManager workManager = WorkManager.getInstance(ApplicationProvider.getApplicationContext());
        // Enqueue and wait for result. This also runs the Worker synchronously because we are using a SynchronousExecutor.
        workManager.enqueue(request).getResult().get();
        // Get WorkInfo and outputData
        WorkInfo workInfo = workManager.getWorkInfoById(request.getId()).get();
        Data outputData = workInfo.getOutputData();
        // Assert
        //FAILS.... Seem to still be in the background
        assertThat(workInfo.getState(), is(WorkInfo.State.SUCCEEDED));
        //assertThat(outputData, is(input)); //TEST Notif
    }
}
