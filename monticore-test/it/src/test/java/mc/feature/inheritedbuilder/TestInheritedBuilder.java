/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritedbuilder;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.inheritedbuilder.buildertest.BuilderTestMill;
import mc.feature.inheritedbuilder.buildertest._ast.ASTSubBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestInheritedBuilder {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void test(){
    //test if the return type of the builder for the inherited attribute name of Sub is correct
    assertTrue(BuilderTestMill.subBuilder().setName("Foo") instanceof ASTSubBuilder);
    
    assertTrue(Log.getFindings().isEmpty());
  }
}
