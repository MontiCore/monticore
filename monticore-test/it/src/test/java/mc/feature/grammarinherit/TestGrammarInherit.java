/* (c) https://github.com/MontiCore/monticore */

package mc.feature.grammarinherit;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.grammarinherit.sub.subfeaturedslgrammarinherit._parser.SubFeatureDSLgrammarinheritParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGrammarInherit extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1() throws IOException {
    
    StringReader s = new StringReader("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    SubFeatureDSLgrammarinheritParser p = new SubFeatureDSLgrammarinheritParser();
    p.parseFile(s);
    
    Assertions.assertEquals(false, p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
