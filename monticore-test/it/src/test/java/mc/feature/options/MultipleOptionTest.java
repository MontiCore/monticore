/* (c) https://github.com/MontiCore/monticore */

package mc.feature.options;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTTestOptions;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import de.se_rwth.commons.logging.Log;

import static org.junit.jupiter.api.Assertions.*;

public class MultipleOptionTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test() throws IOException {
    
    StringReader r = new StringReader("constants constants");
    
    FeatureDSLParser p = new FeatureDSLParser();
    
    Optional<ASTTestOptions> ast = p.parseTestOptions(r);
    
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isA());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
