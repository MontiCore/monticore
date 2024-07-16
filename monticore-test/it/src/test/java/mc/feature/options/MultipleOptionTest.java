/* (c) https://github.com/MontiCore/monticore */

package mc.feature.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTTestOptions;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import de.se_rwth.commons.logging.Log;

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
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(false, ast.get().isA());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
