/*(c) https://github.com/MontiCore/monticore*/

package de.monticore.umlmodifier;

import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ModifierBuilderTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void checkAllOptions() {
    assertTrue(new ASTModifierBuilder().PUBLIC().build().isPublic());
    assertTrue(new ASTModifierBuilder().PRIVATE().build().isPrivate());
    assertTrue(new ASTModifierBuilder().PROTECTED().build().isProtected());
    assertTrue(new ASTModifierBuilder().FINAL().build().isFinal());
    assertTrue(new ASTModifierBuilder().ABSTRACT().build().isAbstract());
    assertTrue(new ASTModifierBuilder().LOCAL().build().isLocal());
    assertTrue(new ASTModifierBuilder().DERIVED().build().isDerived());
    assertTrue(new ASTModifierBuilder().READONLY().build().isReadonly());
    assertTrue(new ASTModifierBuilder().STATIC().build().isStatic());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
