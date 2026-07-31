/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform.ConstantsShortFormMill;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test() {
    ASTA a = ConstantsShortFormMill.aBuilder().build();
    assertFalse(a.isMyConst());
    a.setMyConst(true);
    assertTrue(a.isMyConst());
    
    ASTB b = ConstantsShortFormMill.bBuilder().build();
    assertFalse(b.isConst());
    b.setConst(true);
    assertTrue(b.isConst());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
