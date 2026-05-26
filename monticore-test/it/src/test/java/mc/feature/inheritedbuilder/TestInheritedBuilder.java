/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritedbuilder;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.inheritedbuilder.buildertest.BuilderTestMill;
import mc.feature.inheritedbuilder.buildertest._ast.ASTSubBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInheritedBuilder {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test(){
    //test if the return type of the builder for the inherited attribute name of Sub is correct
    assertInstanceOf(ASTSubBuilder.class, BuilderTestMill.subBuilder().setName("Foo"));
    
    assertTrue(Log.getFindings().isEmpty());
  }
}
