/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractgrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseAbstract;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseUnterface;
import mc.feature.abstractgrammar.implementation._ast.ASTB;
import mc.feature.abstractgrammar.implementation._ast.ASTC;
import mc.feature.abstractgrammar.implementation._parser.ImplementationParser;
import org.junit.jupiter.api.Test;

public class AbstractGrammarTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testRefInterface() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseUnterface> ast = p.parseUseUnterface(new StringReader("use impl myimplinterface"));
        
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(p.hasErrors());
    Assertions.assertTrue(ast.get().getII() instanceof ASTB);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRefAbstractRule() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseAbstract> ast = p.parseUseAbstract(new StringReader("use ext myextabstract"));
    
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(p.hasErrors());
    Assertions.assertTrue(ast.get().getAA() instanceof ASTC);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
