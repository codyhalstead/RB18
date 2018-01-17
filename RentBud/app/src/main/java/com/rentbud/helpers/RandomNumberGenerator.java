package com.rentbud.helpers;

import java.util.Random;

/**
 * Created by Cody on 12/13/2017.
 */

public class RandomNumberGenerator {
    Random random;

    public RandomNumberGenerator(){
        random = new Random();
    }

    public String gererateVerificationNumber(int numberOfDigits){
        String theNumber = "";
        for (int i = 0; i < numberOfDigits; i++){
            theNumber += random.nextInt(10);
        }
        return theNumber;
    }
}
