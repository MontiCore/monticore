/*(c) https://github.com/MontiCore/monticore*/

package de.monticore.umlmodifier;

import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class ModifierBuilderTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void checkAllOptions() {
    Assertions.assertTrue(new ASTModifierBuilder().PUBLIC().build().isPublic());
    Assertions.assertTrue(new ASTModifierBuilder().PRIVATE().build().isPrivate());
    Assertions.assertTrue(new ASTModifierBuilder().PROTECTED().build().isProtected());
    Assertions.assertTrue(new ASTModifierBuilder().FINAL().build().isFinal());
    Assertions.assertTrue(new ASTModifierBuilder().ABSTRACT().build().isAbstract());
    Assertions.assertTrue(new ASTModifierBuilder().LOCAL().build().isLocal());
    Assertions.assertTrue(new ASTModifierBuilder().DERIVED().build().isDerived());
    Assertions.assertTrue(new ASTModifierBuilder().READONLY().build().isReadonly());
    Assertions.assertTrue(new ASTModifierBuilder().STATIC().build().isStatic());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
