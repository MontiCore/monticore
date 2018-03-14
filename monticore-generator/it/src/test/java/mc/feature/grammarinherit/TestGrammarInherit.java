/* (c) https://github.com/MontiCore/monticore */

package mc.feature.grammarinherit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.grammarinherit.sub.featuredslgrammarinherit._parser.FeatureDSLgrammarinheritParser;

public class TestGrammarInherit extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    StringReader s = new StringReader("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    FeatureDSLgrammarinheritParser p = new FeatureDSLgrammarinheritParser();
    p.parseFile(s);
    
    assertEquals(false, p.hasErrors());
    
  }
  
}
