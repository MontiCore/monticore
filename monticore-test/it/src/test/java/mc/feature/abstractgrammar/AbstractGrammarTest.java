/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractgrammar;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseAbstract;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseUnterface;
import mc.feature.abstractgrammar.implementation._ast.ASTB;
import mc.feature.abstractgrammar.implementation._ast.ASTC;
import mc.feature.abstractgrammar.implementation._parser.ImplementationParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractGrammarTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testRefInterface() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseUnterface> ast = p.parseUseUnterface(new StringReader("use impl myimplinterface"));
        
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertInstanceOf(ASTB.class, ast.get().getII());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRefAbstractRule() throws IOException {
    
    ImplementationParser p = new ImplementationParser();
    java.util.Optional<ASTUseAbstract> ast = p.parseUseAbstract(new StringReader("use ext myextabstract"));
    
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertInstanceOf(ASTC.class, ast.get().getAA());
    assertTrue(Log.getFindings().isEmpty());
  }
}
