/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import static org.junit.Assert.assertEquals;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import mc.feature.constantsshortform.constantsshortform._ast.ConstantsShortFormNodeFactory;

import org.junit.Test;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() {
    ASTA a = ConstantsShortFormNodeFactory.createASTA();
    assertEquals(a.isMyConst(), false);
    a.setMyConst(true);
    assertEquals(a.isMyConst(), true);
    
    ASTB b = ConstantsShortFormNodeFactory.createASTB();
    assertEquals(b.isConst(), false);
    b.setConst(true);
    assertEquals(b.isConst(), true);
  }
  
}
