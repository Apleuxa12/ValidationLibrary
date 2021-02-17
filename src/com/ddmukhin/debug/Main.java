package com.ddmukhin.debug;

import com.ddmukhin.annotations.Constrained;
import com.ddmukhin.annotations.NotNull;
import com.ddmukhin.annotations.Size;
import com.ddmukhin.validation.Validator;
import com.ddmukhin.validation.ValidatorImpl;
import com.ddmukhin.validation.errors.SimpleValidationError;
import com.ddmukhin.validation.errors.ValidationError;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static Validator validator;

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

//        validator = new ValidatorImpl();
//
//        Set<ValidationError> validationErrors = validator.validate(bookingForm);


    }
}
