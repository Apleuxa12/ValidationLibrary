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
    private List<@AnyOf({"TV", "Kitchen"}) String> amentities;

    @NotNull
    @AnyOf({"House", "Hostel"})
    private String propertyType;

    public BookingForm(List<GuestForm> guests, List<String> amentities, String propertyType) {
        this.guests = guests;
        this.amentities = amentities;
        this.propertyType = propertyType;
    }
}
