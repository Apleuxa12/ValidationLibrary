package com.ddmukhin.test;

import com.ddmukhin.library.annotations.*;
import com.ddmukhin.library.validation.Validator;
import com.ddmukhin.library.validation.ValidatorImpl;
import com.ddmukhin.library.validation.errors.*;
import com.ddmukhin.library.validation.errors.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorImplTest {

    private final Validator validator = new ValidatorImpl();

    static class EmptyObject{

    }

    @Constrained
    static class ConstrainedEmptyObject{

    }

    @Constrained
    static class PrimitiveObjects{
        @NotNull
        @NotBlank
        public String value1;

        @NotNull
        @AnyOf({"a", "b", "c"})
        public String value2;

        @NotNull
        @Positive
        public Integer value3;

        @NotNull
        @Negative
        public Integer value4;

        @NotNull
        @InRange(min = 0, max = 20)
        public Integer value5;

        @NotNull
        public Boolean value6;

        public PrimitiveObjects(String value1, String value2, Integer value3, Integer value4, Integer value5, Boolean value6){
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
            this.value4 = value4;
            this.value5 = value5;
            this.value6 = value6;
        }
    }

    @Test
    void validateEmptyObject() throws IllegalAccessException {
        Set<ValidationError> errors = validator.validate(new EmptyObject());
        assertEquals(errors.size(), 0);
    }

    @Test
    void validateConstrainedEmptyObject() throws IllegalAccessException {
        Set<ValidationError> errors = validator.validate(new ConstrainedEmptyObject());
        assertEquals(errors.size(), 0);
    }

    @Test
    void validatePrimitiveObjectsNoErrors() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("value1", "a", 1, -1, 1, true);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 0);
    }

    @Test
    void validatePrimitiveObjectsNotNullErrors() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects(null, null, null, null, null, null);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 6);
        for(var error : errors){
            assertTrue(error instanceof NotNullError);

            assertEquals(error.getMessage(), "must not be null");
            assertTrue(error.getPath().contains("value"));
            assertNull(error.getFailedValue());
        }
    }

    @Test
    void validatePrimitiveObjectsBlankError() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("  ", "a", 1, -1, 1, true);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);
        for(var error : errors) {
            assertTrue(error instanceof NotBlankError);

            assertEquals(error.getMessage(), "must not be blank");
            assertEquals(error.getPath(), "value1");
            assertEquals(error.getFailedValue(), "  ");
        }
    }

    @Test
    void validatePrimitiveObjectsAnyOfError() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("value1", "d", 1, -1, 1, true);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);
        for(var error : errors) {
            assertTrue(error instanceof AnyOfError);

            assertEquals(error.getMessage(), "must be one of 'a', 'b', 'c'");
            assertEquals(error.getPath(), "value2");
            assertEquals(error.getFailedValue(), "d");
        }
    }

    @Test
    void validatePrimitiveObjectsPositiveError() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("value1", "a", -1, -1, 1, true);
        Set<ValidationError> errors = validator.validate(objects);
        System.out.println(validator.toString());
        assertEquals(errors.size(), 1);
        for(var error : errors){
            assertTrue(error instanceof PositiveError);

            assertEquals(error.getMessage(), "must be greater than zero");
            assertEquals(error.getPath(), "value3");
            assertEquals(error.getFailedValue(), -1);
        }
    }

    @Test
    void validatePrimitiveObjectsNegativeError() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("value1", "a", 1, 1, 1, true);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);
        for(var error : errors){
            assertTrue(error instanceof NegativeError);

            assertEquals(error.getMessage(), "must be less than zero");
            assertEquals(error.getPath(), "value4");
            assertEquals(error.getFailedValue(), 1);
        }
    }

    @Test
    void validatePrimitiveObjectsInRangeError() throws IllegalAccessException{
        PrimitiveObjects objects = new PrimitiveObjects("value1", "a", 1, -1, 21, true);
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);
        for(var error : errors){
            assertTrue(error instanceof InRangeError);

            assertEquals(error.getMessage(), "must be in range between 0 and 20");
            assertEquals(error.getPath(), "value5");
            assertEquals(error.getFailedValue(), 21);
        }
    }

    @Constrained
    static class CollectionObjects{
        @NotEmpty
        public List<String> value1;

        @Size(min = 1, max = 3)
        public List<String> value2;

        public List<@NotBlank String> value3;

        public CollectionObjects(List<String> value1, List<String> value2, List<String> value3){
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }
    }

    @Test
    void validateCollectionObjectsNotEmptyError() throws IllegalAccessException{
        CollectionObjects objects = new CollectionObjects(List.of(), List.of("a", "b"), List.of("a", "b"));
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof NotEmptyError);

            assertEquals(error.getMessage(), "must not be empty");
            assertEquals(error.getPath(), "value1");
            assertEquals(error.getFailedValue(), List.of());
        }
    }

    @Test
    void validateCollectionObjectsSizeError() throws IllegalAccessException{
        CollectionObjects objects = new CollectionObjects(List.of("a"), List.of("a", "b", "c", "d"), List.of("a", "b"));
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof SizeError);

            assertEquals(error.getMessage(), "must be in range between 1 and 3");
            assertEquals(error.getPath(), "value2");
            assertEquals(error.getFailedValue(), 4);
        }
    }

    @Test
    void validateCollectionObjectsNotBlankError() throws IllegalAccessException{
        CollectionObjects objects = new CollectionObjects(List.of("a"), List.of("a", "b", "c"), List.of("  ", "b"));
        Set<ValidationError> errors = validator.validate(objects);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof NotBlankError);

            assertEquals(error.getMessage(), "must not be blank");
            assertEquals(error.getPath(), "value3[0]");
            assertEquals(error.getFailedValue(), "  ");
        }
    }

    @Constrained
    static class ConstrainedObject{

        @NotNull
        public CollectionObjects collectionObjects;

        @NotNull
        public PrimitiveObjects primitiveObjects;

        public ConstrainedObject(CollectionObjects collectionObjects, PrimitiveObjects primitiveObjects){
            this.collectionObjects = collectionObjects;
            this.primitiveObjects = primitiveObjects;
        }
    }

    @Test
    void validateConstrainedObjectNotNullErrors() throws IllegalAccessException{
        ConstrainedObject object = new ConstrainedObject(null, null);
        Set<ValidationError> errors = validator.validate(object);
        assertEquals(errors.size(), 2);

        for(var error : errors){
            assertTrue(error instanceof NotNullError);

            assertEquals(error.getMessage(), "must not be null");
            assertTrue(error.getPath().contains("Objects"));
            assertNull(error.getFailedValue());
        }
    }

    @Test
    void validateConstrainedObjectPrimitiveObjectsAnyOfError() throws IllegalAccessException{
        PrimitiveObjects primitiveObjects = new PrimitiveObjects("value1", "d", 1, -1, 1, true);
        CollectionObjects collectionObjects = new CollectionObjects(List.of("a"), List.of("a", "b"), List.of("a", "b", "c"));
        ConstrainedObject object = new ConstrainedObject(collectionObjects, primitiveObjects);
        Set<ValidationError> errors = validator.validate(object);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof AnyOfError);

            assertEquals(error.getMessage(), "must be one of 'a', 'b', 'c'");
            assertEquals(error.getPath(), "primitiveObjects.value2");
            assertEquals(error.getFailedValue(), "d");
        }
    }

    @Test
    void validateConstrainedObjectCollectionObjectsNotEmptyError() throws IllegalAccessException{
        PrimitiveObjects primitiveObjects = new PrimitiveObjects("value1", "a", 1, -1, 1, true);
        CollectionObjects collectionObjects = new CollectionObjects(List.of(), List.of("a", "b"), List.of("a", "b"));
        ConstrainedObject object = new ConstrainedObject(collectionObjects, primitiveObjects);
        Set<ValidationError> errors = validator.validate(object);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof NotEmptyError);

            assertEquals(error.getMessage(), "must not be empty");
            assertEquals(error.getPath(), "collectionObjects.value1");
            assertEquals(error.getFailedValue(), List.of());
        }
    }

    @Test
    void validateConstrainedObjectCollectionObjectsNotBlankError() throws IllegalAccessException{
        PrimitiveObjects primitiveObjects = new PrimitiveObjects("value1", "a", 1, -1, 1, true);
        CollectionObjects collectionObjects = new CollectionObjects(List.of("a"), List.of("a", "b"), List.of("  ", "b"));
        ConstrainedObject object = new ConstrainedObject(collectionObjects, primitiveObjects);
        Set<ValidationError> errors = validator.validate(object);
        assertEquals(errors.size(), 1);

        for(var error : errors){
            assertTrue(error instanceof NotBlankError);

            assertEquals(error.getMessage(), "must not be blank");
            assertEquals(error.getPath(), "collectionObjects.value3[0]");
            assertEquals(error.getFailedValue(), "  ");
        }
    }

    @Constrained
    static class GuestForm {
        @NotNull
        @NotBlank
        private String firstName;

        @NotBlank
        @NotNull
        private String lastName;

        @InRange(min = 0, max = 200)
        private int age;

        public GuestForm(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }

    static class Unrelated {
        @Positive
        private int x;

        public Unrelated(int x) {
            this.x = x;
        }
    }

    @Constrained
    static class BookingForm {
        @NotNull
        @Size(min = 1, max = 5)
        private List<@NotNull GuestForm> guests;

        @NotNull
        private List<@AnyOf({"TV", "Kitchen"}) String> amenities;

        @NotNull
        @AnyOf({"House", "Hostel"})
        private String propertyType;

        @NotNull
        private Unrelated unrelated;

        public BookingForm(List<GuestForm> guests, List<String> amenities, String propertyType, Unrelated unrelated) {
            this.guests = guests;
            this.amenities = amenities;
            this.propertyType = propertyType;
            this.unrelated = unrelated;
        }
    }

    @Test
    void validateExampleObject() throws IllegalAccessException{
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
        Set<ValidationError> errors = validator.validate(bookingForm);
        assertEquals(errors.size(), 5);
    }
}