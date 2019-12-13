package com.example.go4lunchjava;

import android.content.Context;


import androidx.test.core.app.ApplicationProvider;

import com.example.go4lunchjava.places_api.pojo.details.hours.Close;
import com.example.go4lunchjava.places_api.pojo.details.hours.Open;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningPeriod;
import com.example.go4lunchjava.utils.RestaurantDataFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class RestaurantDataFormatTest {

    @Test
    public void convertPlacesApiRatingToStarImages(){
        //GIVEN
        float[] rates = {1.6f, 5, 2.4f, 3, -1};
        //WHEN
        int[] result = new int[5];
        for (int i = 0; i < rates.length; i++){
            result[i] = RestaurantDataFormat.getRatingResource(rates[i]);
        }

        int[] expected = {R.drawable.ic_star, R.drawable.ic_star_three, R.drawable.ic_star, R.drawable.ic_star_two, 0};

        //THEN
        assertArrayEquals(expected, result);
    }

    @Test
    public void formatDistanceBetweenPlacesWhenMoreThan100Km(){
        //GIVEN
        float distance = 2389980f;
        //WHEN
        String result = RestaurantDataFormat.formatDistanceAsString(distance);
        //THEN
        assertEquals(">100km", result);
    }

    @Test
    public void formatDistanceBetweenPlacesWhenMoreBetween1KmAnd100Km(){
        //GIVEN
        float distance = 2389f;
        //WHEN
        String result = RestaurantDataFormat.formatDistanceAsString(distance);
        //THEN
        assertEquals("2.4km", result);
    }

    @Test
    public void formatDistanceBetweenPlacesWhenLessThan1Km(){
        //GIVEN
        float distance = 298f;
        //WHEN
        String result = RestaurantDataFormat.formatDistanceAsString(distance);
        //THEN
        assertEquals("298m", result);
    }

    @Test
    public void returnFormattedHourStringFromPlacesAPiResponse(){
        //GIVEN
        int day = 2; //Monday

        Calendar calendar = Mockito.mock(Calendar.class);
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(day + 1); //Monday
        Mockito.when(calendar.get(Calendar.HOUR_OF_DAY)).thenReturn(12); //12 O'clock

        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getString(R.string.hours_not_communicated)).thenReturn("Opening hours not communicated");
        Mockito.when(context.getString(R.string.closed_today)).thenReturn("Closed today");
        Mockito.when(context.getString(R.string.open_until)).thenReturn("Open until ");
        Mockito.when(context.getString(R.string.closing_soon)).thenReturn("Closing soon");
        Mockito.when(context.getString(R.string.closed_until)).thenReturn("Closed until ");

        List<String> hoursDescriptions = new ArrayList<>();

        //WHEN
        OpeningHoursDetails h1 = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(day, "1100"), new Close(day, "2200"))});
        OpeningHoursDetails h2 = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(day, "0000"), null)});
        OpeningHoursDetails h3 = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(day, "1300"), new Close(day, "2200"))});
        OpeningHoursDetails h4 = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(6, "1100"), new Close(day, "2200"))});
        OpeningHoursDetails h5 = null;
        OpeningHoursDetails h6 = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(day, "1100"), new Close(day, "1215"))});

        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h1, calendar, context));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h2, calendar, context));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h3, calendar, context));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h4, calendar, context));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h5, calendar, context));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h6, calendar, context));

        //THEN
        String[] expected = {"Open until 10:00 PM", "24/7", "Closed until 01:00 PM", "Closed today",
                "Opening hours not communicated", "Closing soon"};

        assertArrayEquals(expected, hoursDescriptions.toArray());
    }

}