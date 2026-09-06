/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractgrammar;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseAbstract;
import mc.feature.abstractgrammar.abstractgrammar._ast.ASTUseUnterface;
import mc.feature.abstractgrammar.implementation.ImplementationMill;
import mc.feature.abstractgrammar.implementation._ast.ASTB;
import mc.feature.abstractgrammar.implementation._ast.ASTC;
import mc.feature.abstractgrammar.implementation._parser.ImplementationParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(ImplementationMill.class)
public class AbstractGrammarTest {

  @Test
  public void testRefInterface() throws IOException {
    ImplementationParser p = ImplementationMill.parser();
    Optional<ASTUseUnterface> ast = p.parse_StringUseUnterface("use impl myimplinterface");
        
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertInstanceOf(ASTB.class, ast.get().getII());
  }
  
  @Test
  public void testRefAbstractRule() throws IOException {
    ImplementationParser p = ImplementationMill.parser();
    Optional<ASTUseAbstract> ast = p.parse_StringUseAbstract("use ext myextabstract");
    
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertInstanceOf(ASTC.class, ast.get().getAA());
  }
}
