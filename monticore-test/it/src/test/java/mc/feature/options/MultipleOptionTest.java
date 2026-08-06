/* (c) https://github.com/MontiCore/monticore */

package mc.feature.options;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTTestOptions;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(FeatureDSLMill.class)
public class MultipleOptionTest {

  @Test
  public void test() throws IOException {
    
    StringReader r = new StringReader("constants constants");
    
    FeatureDSLParser p = FeatureDSLMill.parser();
    
    Optional<ASTTestOptions> ast = p.parseTestOptions(r);
    
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isA());
  }
}
