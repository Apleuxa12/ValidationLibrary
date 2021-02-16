package com.ddmukhin.debug;

import com.ddmukhin.annotations.Constrained;
import com.ddmukhin.annotations.InRange;
import com.ddmukhin.annotations.NotBlank;
import com.ddmukhin.annotations.NotNull;
@Constrained
public class GuestForm {

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
