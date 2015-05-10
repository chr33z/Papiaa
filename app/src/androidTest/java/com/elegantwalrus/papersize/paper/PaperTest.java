package com.elegantwalrus.papersize.paper;

import android.test.InstrumentationTestCase;

/**
 * Created by chris on 28.04.15.
 */
public class PaperTest extends InstrumentationTestCase {

    private Paper paper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        paper = new Paper("paper", "this is paper", "id", 200, 400);
    }

    public void testPaperFunctionality() throws Exception {
        assertEquals("paper", paper.getName());
        assertEquals("this is paper", paper.getDescription());
        assertEquals(200.0, paper.getWidth());
        assertEquals(400.0, paper.getHeight());


    }
}
