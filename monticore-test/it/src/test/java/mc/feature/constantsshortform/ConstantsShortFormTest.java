/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.constantsshortform.constantsshortform.ConstantsShortFormMill;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ConstantsShortFormMill.class)
public class ConstantsShortFormTest {

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
  }
  
}
