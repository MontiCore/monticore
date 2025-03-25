/* (c) https://github.com/MontiCore/monticore */


import de.se_rwth.commons.logging.Log;
import g1.G1Mill;
import g2.G2Mill;
import g3.G3Mill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MillTest {

  @BeforeEach
  public void before() {
    G2Mill.globalScope().clear();
    G2Mill.reset();
    Log.init();
  }

  @Test
  public void testCDClassMillDelegation()  {
    G3Mill.init();
    // All Mills should return a builder of the g2.Foo (overriding the g1.Foo)
    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
      G2Mill.fooBuilder().uncheckedBuild().getClass());

    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
        G2Mill.fooBuilder().uncheckedBuild().getClass());

    assertEquals(G3Mill.fooBuilder().uncheckedBuild().getClass(),
        G1Mill.fooBuilder().uncheckedBuild().getClass());
  }

}
