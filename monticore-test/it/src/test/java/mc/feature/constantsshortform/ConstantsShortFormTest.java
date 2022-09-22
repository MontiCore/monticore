/* (c) https://github.com/MontiCore/monticore */

package mc.feature.constantsshortform;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.constantsshortform.constantsshortform.ConstantsShortFormMill;
import mc.feature.constantsshortform.constantsshortform._ast.ASTA;
import mc.feature.constantsshortform.constantsshortform._ast.ASTB;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConstantsShortFormTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
