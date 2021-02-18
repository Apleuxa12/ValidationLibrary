package com.ddmukhin.debug;

import com.ddmukhin.validation.Validator;
import com.ddmukhin.validation.ValidatorImpl;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<GuestForm> guests = List.of(
                new GuestForm(/*firstName*/ null, /*lastName*/ "Def", /*age*/ 21),
                new GuestForm(/*firstName*/ "", /*lastName*/ "Ijk", /*age*/ -3)
        );
        Unrelated unrelated = new Unrelated(-1);

        List<List<String>> values = new ArrayList<>();
        values.add(new ArrayList<>() {{
            add("B");
        }});

        BookingForm bookingForm = new BookingForm(
                guests,
                /*amenities*/ List.of("TV", "Piano"),
                /*propertyType*/ "Apartment",
                unrelated,
                values
        );

        Validator validator = new ValidatorImpl();
        try {
            validator.validate(bookingForm);
            validator.printErrors();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
