/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform.ConstantsShortFormMill;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() {
    ASTA a = ConstantsShortFormMill.aBuilder().build();
    Assertions.assertEquals(a.isMyConst(), false);
    a.setMyConst(true);
    Assertions.assertEquals(a.isMyConst(), true);
    
    ASTB b = ConstantsShortFormMill.bBuilder().build();
    Assertions.assertEquals(b.isConst(), false);
    b.setConst(true);
    Assertions.assertEquals(b.isConst(), true);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
