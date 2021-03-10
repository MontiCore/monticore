/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform.ConstantsShortFormMill;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() {
    ASTA a = ConstantsShortFormMill.aBuilder().build();
    assertEquals(a.isMyConst(), false);
    a.setMyConst(true);
    assertEquals(a.isMyConst(), true);
    
    ASTB b = ConstantsShortFormMill.bBuilder().build();
    assertEquals(b.isConst(), false);
    b.setConst(true);
    assertEquals(b.isConst(), true);
  }
  
}
