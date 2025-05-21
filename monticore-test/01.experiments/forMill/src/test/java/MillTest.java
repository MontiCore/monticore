/* (c) https://github.com/MontiCore/monticore */


import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.TestWithMCLanguage;
import g1.G1Mill;
import g2.G2Mill;
import g3.G3Mill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(G2Mill.class)
public class MillTest extends AbstractMCTest {

  @Test
  public void testCDClassMillDelegation()  {
    // All Mills should return a builder of the g2.Foo (overriding the g1.Foo)
    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
      G2Mill.fooBuilder().uncheckedBuild().getClass());

    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
        G2Mill.fooBuilder().uncheckedBuild().getClass());

    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
        G1Mill.fooBuilder().uncheckedBuild().getClass());
  }

}
