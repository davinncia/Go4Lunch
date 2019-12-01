package com.example.go4lunchjava;

import com.example.go4lunchjava.places_api.pojo.details.hours.Close;
import com.example.go4lunchjava.places_api.pojo.details.hours.Open;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningPeriod;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class RestaurantItemFormatUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

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
    public void calculateDistanceBetweenTwoLatLng(){
        //GIVEN
        LatLng[] pair1 = {new LatLng(22, 22), new LatLng(22, 22)};

        //WHEN
        //TODO NINO: "Location not mocked". Testable ? unitTests.returnDefaultValues = true ?
        String[] results = {RestaurantDataFormat.getDistanceFromRestaurant(pair1[0], pair1[1])};
        String[] expected = {"0m"};

        //THEN
        assertArrayEquals(expected, results);
    }

    @Test
    public void returnFormattedHourStringFromPlacesAPiResponse(){
        //TODO NINO: had to implement constructors, good practice ? & inject calendar to mock it
        //GIVEN
        int day = 2; //Monday

        Calendar calendar = Mockito.mock(Calendar.class);
        Mockito.when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(day + 1); //Monday
        Mockito.when(calendar.get(Calendar.HOUR_OF_DAY)).thenReturn(12); //12 O'clock

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

        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h1, calendar));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h2, calendar));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h3, calendar));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h4, calendar));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h5, calendar));
        hoursDescriptions.add(RestaurantDataFormat.getHoursFromOpeningHours(h6, calendar));

        //THEN
        String[] expected = {"Open until 10:00 PM", "24/7", "Closed until 01:00 PM", "Closed today",
                "Opening hours not communicated", "Closing soon"};

        assertArrayEquals(expected, hoursDescriptions.toArray());
    }

}