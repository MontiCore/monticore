/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.tuples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TuplesTest {

  Tuple2<Integer, String> tuple2 = Tuple2.of(2, "Antwort");
  Tuple3<Integer, String, Float> tuple3 = Tuple3.of(3, "Antwort", 3.0f);
  Tuple4<Integer, String, Float, Double> tuple4 = Tuple4.of(4, "Hund", 4.0f, 4.0);
  Tuple5<Integer, String, Float, Double, Character> tuple5 = Tuple5.of(5, "Hund", 5.0f, 4.0, 'e');
  Tuple6<Integer, String, Float, Double, Character, Boolean> tuple6 = Tuple6.of(6, "Hund", 6.0f, 4.0, 'f', true);
  Tuple7<Integer, String, Float, Double, Character, Boolean, Integer> tuple7 = Tuple7.of(7, "Hund", 7.0f, 4.0, 'g', true, 1);
  Tuple8<Integer, String, Float, Double, Character, Boolean, Integer, Integer> tuple8 = Tuple8.of(8, "Hund", 8.0f, 4.0, 'h', true, 1, 2);
  Tuple9<Integer, String, Float, Double, Character, Boolean, Integer, Integer, Integer> tuple9 = Tuple9.of(9, "Hund", 7.0f, 4.0, 'i', false, 1, 2, 3);

  @Test
  public void testTuple_getX() {
    assertEquals(2, tuple2.get0());
    assertEquals("Antwort", tuple2.get1());

    assertEquals(3, tuple3.get0());
    assertEquals("Antwort", tuple3.get1());
    assertEquals(3.0f, tuple3.get2());

    assertEquals(4, tuple4.get0());
    assertEquals("Hund", tuple4.get1());
    assertEquals(4.0f, tuple4.get2());
    assertEquals(4.0, tuple4.get3());

    assertEquals(5, tuple5.get0());
    assertEquals("Hund", tuple5.get1());
    assertEquals(5.0f, tuple5.get2());
    assertEquals(4.0, tuple5.get3());
    assertEquals('e', tuple5.get4());
  }

  @Test
  public void testTuple_toString() {
    assertEquals("(2, Antwort)", tuple2.toString());
    assertEquals("(3, Antwort, 3.0)", tuple3.toString());
    assertEquals("(4, Hund, 4.0, 4.0)", tuple4.toString());
    assertEquals("(5, Hund, 5.0, 4.0, e)", tuple5.toString());
  }

  @Test
  public void testTuple_equals() {
    Tuple2<Integer, String> t2a = Tuple2.of(2, "Antwort");
    Tuple2<Integer, String> t2b = Tuple2.of(2, "Antwort");
    Tuple2<Integer, String> t2c = Tuple2.of(3, "Nein");
    assertEquals(t2a, t2b);
    assertNotEquals(t2a, t2c);

    Tuple3<Integer, String, Float> t3a = Tuple3.of(3, "Antwort", 3.0f);
    Tuple3<Integer, String, Float> t3b = Tuple3.of(3, "Antwort", 3.0f);
    Tuple3<Integer, String, Float> t3c = Tuple3.of(3, "Antwort", 4.0f);
    assertEquals(t3a, t3b);
    assertNotEquals(t3a, t3c);

    Tuple4<Integer, String, Float, Double> t4a = Tuple4.of(4, "Hund", 4.0f, 4.0);
    Tuple4<Integer, String, Float, Double> t4b = Tuple4.of(4, "Hund", 4.0f, 4.0);
    Tuple4<Integer, String, Float, Double> t4c = Tuple4.of(4, "Katze", 4.0f, 4.0);
    assertEquals(t4a, t4b);
    assertNotEquals(t4a, t4c);

    Tuple5<Integer, String, Float, Double, Character> t5a = Tuple5.of(5, "Hund", 5.0f, 4.0, 'e');
    Tuple5<Integer, String, Float, Double, Character> t5b = Tuple5.of(5, "Hund", 5.0f, 4.0, 'e');
    Tuple5<Integer, String, Float, Double, Character> t5c = Tuple5.of(5, "Hund", 5.0f, 4.0, 'x');
    assertEquals(t5a, t5b);
    assertNotEquals(t5a, t5c);
  }

  @Test
  public void testTuple_equalsNegative() {
    Tuple2<Integer, String> t2 = Tuple2.of(2, "Antwort");

    assertNotEquals(null, t2);

    assertNotEquals("Antwort", t2);

    Tuple2<Integer, String> t2diff = Tuple2.of(2, "Nein");
    assertNotEquals(t2, t2diff);

    Tuple3<Integer, String, Float> t3 = Tuple3.of(2, "Antwort", 3.0f);
    assertNotEquals(t2, t3);
  }

}
