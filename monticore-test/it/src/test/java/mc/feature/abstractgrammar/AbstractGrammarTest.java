/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractgrammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseAbstract;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseUnterface;
import mc.feature.abstractgrammar.implementation._ast.ASTB;
import mc.feature.abstractgrammar.implementation._ast.ASTC;
import mc.feature.abstractgrammar.implementation._parser.ImplementationParser;

public class AbstractGrammarTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testRefInterface() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseUnterface> ast = p.parseUseUnterface(new StringReader("use impl myimplinterface"));
        
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertTrue(ast.get().getII() instanceof ASTB);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRefAbstractRule() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseAbstract> ast = p.parseUseAbstract(new StringReader("use ext myextabstract"));
    
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertTrue(ast.get().getAA() instanceof ASTC);
    assertTrue(Log.getFindings().isEmpty());
  }
}
