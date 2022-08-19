/* (c) https://github.com/MontiCore/monticore */

package mc.feature.grammarinherit;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.grammarinherit.sub.subfeaturedslgrammarinherit._parser.SubFeatureDSLgrammarinheritParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGrammarInherit extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1() throws IOException {
    
    StringReader s = new StringReader("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    SubFeatureDSLgrammarinheritParser p = new SubFeatureDSLgrammarinheritParser();
    p.parseFile(s);
    
    assertEquals(false, p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
