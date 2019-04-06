package de.neemann.digital.analyse;

import junit.framework.TestCase;

public class LabelNumberingTest extends TestCase {

    public void testCreate() {
        assertEquals("test", new LabelNumbering("test").create(name -> false));
        assertEquals("test2", new LabelNumbering("test").create(new TestCheck()));
        assertEquals("test2n", new LabelNumbering("testn").create(new TestCheck()));
        assertEquals("test2_n", new LabelNumbering("test_n").create(new TestCheck()));
    }

    private class TestCheck implements LabelNumbering.Exists {
        private int n;

        @Override
        public boolean exits(String name) {
            return n++ < 2;
        }
    }
}