/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class ConstructorNoDuplicateModifierTest extends JavaLightCocoTest{
  private final String fileName = "de.monticore.javalight.cocos.invalid.A0808.A0808";

  @BeforeEach
  public void initCoCo() {
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ConstructorNoDuplicateModifier());
  }

  @Test
  public void testInvalid() {
    testInvalid(fileName, "constructor", ConstructorNoDuplicateModifier.ERROR_CODE,
        String.format(ConstructorNoDuplicateModifier.ERROR_MSG_FORMAT, "public", "constructor"), checker);
  }

  @Test
  public void testCorrect() {
    testValid("de.monticore.javalight.cocos.valid.A0808", "constructor", checker);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
