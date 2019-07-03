/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen;

import com.google.common.collect.Lists;
import de.monticore.MontiCoreConfiguration;
import de.monticore.codegen.cd2java.ast.AstGeneratorTest;

import java.util.List;

/**
 * The super class for all test classes that generate and compile code which
 * depends on the generated ast code
 *
 */
public abstract class AstDependentGeneratorTest extends GeneratorTest {
  
  protected AstGeneratorTest astTest;
  
  protected AstDependentGeneratorTest() {
    astTest = new AstGeneratorTest();
    astTest.setDoCompile(false);
  }
  
  protected String[] getCLIArguments(String grammar) {
    List<String> args = Lists.newArrayList(getGeneratorArguments());
    args.add(getConfigProperty(MontiCoreConfiguration.Options.GRAMMARS.toString()));
    args.add(grammar);
    return args.toArray(new String[0]);
  }

  @Override
  public void testCorrect(String model) {
    testCorrect(model, getPathToGeneratedCode(model));
  }
}
