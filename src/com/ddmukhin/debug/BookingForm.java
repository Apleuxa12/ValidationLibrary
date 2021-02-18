package com.ddmukhin.debug;

import com.ddmukhin.annotations.AnyOf;
import com.ddmukhin.annotations.Constrained;
import com.ddmukhin.annotations.NotNull;
import com.ddmukhin.annotations.Size;

import java.util.List;

@Constrained
public class BookingForm {

    @NotNull
    @Size(min = 1, max = 5)
    private List<@NotNull GuestForm> guests;

    @NotNull
    private List<@AnyOf({"TV", "Kitchen"}) String> amenities;

    @NotNull
    @AnyOf({"House", "Hostel"})
    private String propertyType;

    private List<List<@AnyOf({"A"}) String>> values;

    @NotNull
    private Unrelated unrelated;
    public BookingForm(List<GuestForm> guests, List<String> amenities, String
            propertyType, Unrelated unrelated, List<List<String>> values) {
        this.guests = guests;
        this.amenities = amenities;
        this.propertyType = propertyType;
        this.unrelated = unrelated;
        this.values = values;
    }

}
