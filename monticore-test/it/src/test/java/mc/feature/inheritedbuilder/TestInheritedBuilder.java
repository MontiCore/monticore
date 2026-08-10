/* (c) https://github.com/MontiCore/monticore */
package mc.feature.inheritedbuilder;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.inheritedbuilder.buildertest.BuilderTestMill;
import mc.feature.inheritedbuilder.buildertest._ast.ASTSubBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(BuilderTestMill.class)
public class TestInheritedBuilder {

  @Test
  public void test(){
    //test if the return type of the builder for the inherited attribute name of Sub is correct
    assertInstanceOf(ASTSubBuilder.class, BuilderTestMill.subBuilder().setName("Foo"));
  }
}
