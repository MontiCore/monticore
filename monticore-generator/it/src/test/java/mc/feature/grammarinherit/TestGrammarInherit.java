/* (c) https://github.com/MontiCore/monticore */

package mc.feature.grammarinherit;

import mc.GeneratorIntegrationsTest;
import mc.feature.grammarinherit.sub.subfeaturedslgrammarinherit._parser.SubFeatureDSLgrammarinheritParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class TestGrammarInherit extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    StringReader s = new StringReader("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    SubFeatureDSLgrammarinheritParser p = new SubFeatureDSLgrammarinheritParser();
    p.parseFile(s);
    
    assertEquals(false, p.hasErrors());
    
  }
  
}
