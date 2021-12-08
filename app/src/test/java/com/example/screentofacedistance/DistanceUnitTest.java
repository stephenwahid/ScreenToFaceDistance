package com.example.screentofacedistance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito.*;
public class DistanceUnitTest {

    private MainActivity mainActivity = mock(MainActivity.class);

    @Test
    public void checkIfWhite() {
        String val = mainActivity.getColorOnDistance(24);
        assertEquals("#0000FF", val);
    }

}
