/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FCollectionTest {

  @Test
  public void testContainsAll() {
    Assertions.assertFalse(FSet.of().containsAll(FList.of("a")));
    Assertions.assertFalse(FSet.of("a", "b").containsAll(FList.of("a", "c")));
    Assertions.assertTrue(FSet.of("a", "b").containsAll(FList.of("a", "b")));
    Assertions.assertTrue(FSet.of("a", "b").containsAll(FList.of()));
  }

  @Test
  public void testToArray() {
    Assertions.assertArrayEquals(
        new String[] {},
        FSet.of().toArray()
    );
    Assertions.assertArrayEquals(
        new String[] { "a" },
        FSet.of("a").toArray()
    );

    Assertions.assertArrayEquals(
        new String[] {},
        FSet.of().toArray(String[]::new)
    );
    Assertions.assertArrayEquals(
        new String[] { "a" },
        FSet.of("a").toArray(String[]::new)
    );
  }

  @Test
  public void testCollect() {
    Assertions.assertEquals(
        List.of(1, 2, 3, 4, 5),
        FList.of(1, 2, 3, 4, 5).collect(new ArrayList<>(), ArrayList::add)
    );
    Assertions.assertEquals(
        List.of(1),
        FList.of(1).collect(new ArrayList<>(), ArrayList::add)
    );
    Assertions.assertEquals(
        List.of(),
        FList.of().collect(new ArrayList<>(), ArrayList::add)
    );
  }

  @Test
  public void testFold() {
    Assertions.assertEquals(
        FList.of(5, 4, 3, 2, 1),
        FList.of(1, 2, 3, 4, 5).fold(FList.of(), FList::withPrepended)
    );
    Assertions.assertEquals(
        FList.of(1),
        FList.of(1).fold(FList.of(), FList::withPrepended)
    );
    Assertions.assertEquals(
        FList.of(),
        FList.of().fold(FList.of(), FList::withPrepended)
    );
  }

}
