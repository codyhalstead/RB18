package com.rba18.helpers;

import java.util.Random;

/**
 * Created by Cody on 12/13/2017.
 */

public class RandomNumberGenerator {
    private Random mRandom;

    public RandomNumberGenerator(){
        mRandom = new Random();
    }

    //Generate mRandom number with digits you specify
    public String gererateVerificationNumber(int numberOfDigits){
        String theNumber = "";
        for (int i = 0; i < numberOfDigits; i++){
            theNumber += mRandom.nextInt(10);
        }
        return theNumber;
    }
}
