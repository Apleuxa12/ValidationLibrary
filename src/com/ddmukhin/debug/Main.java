package com.ddmukhin.debug;

import com.ddmukhin.validation.Validator;
import com.ddmukhin.validation.ValidatorImpl;
import com.ddmukhin.validation.errors.ValidationError;

import java.util.*;

public class Main {

    public static void main(String[] args) throws IllegalAccessException {
        List<GuestForm> guests = List.of(
                new GuestForm(/*firstName*/ null, /*lastName*/ "Def", /*age*/ 21),
                new GuestForm(/*firstName*/ "", /*lastName*/ "Ijk", /*age*/ -3)
        );
        Unrelated unrelated = new Unrelated(-1);
        BookingForm bookingForm = new BookingForm(
                guests,
                /*amenities*/ List.of("TV", "Piano"),
                /*propertyType*/ "Apartment",
                unrelated
        );

        Validator validator = new ValidatorImpl();

        Set<ValidationError> validationErrors = validator.validate(bookingForm);
        for(ValidationError error : validationErrors){
            System.out.println(error.getPath() + "\t|\t" + error.getMessage() + "\t|\t" + error.getFailedValue());
        }
    }
}
