package com.ddmukhin.debug;

import com.ddmukhin.annotations.Positive;

public class Unrelated {

    @Positive
    private int x;
    
    public Unrelated(int x){
        this.x = x;
    }

}
