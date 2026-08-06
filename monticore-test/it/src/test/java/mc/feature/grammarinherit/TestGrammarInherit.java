/* (c) https://github.com/MontiCore/monticore */

package mc.feature.grammarinherit;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.grammarinherit.sub.subfeaturedslgrammarinherit.SubFeatureDSLgrammarinheritMill;
import mc.feature.grammarinherit.sub.subfeaturedslgrammarinherit._parser.SubFeatureDSLgrammarinheritParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(SubFeatureDSLgrammarinheritMill.class)
public class TestGrammarInherit {
 
  @Test
  public void test1() throws IOException {
    SubFeatureDSLgrammarinheritParser p = SubFeatureDSLgrammarinheritMill.parser();
    p.parse_StringFile("automaton ad {\n state all;\n state bss;\n ass -> bss; }");
    
    assertFalse(p.hasErrors());
  }
}
