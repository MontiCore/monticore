/* (c) https://github.com/MontiCore/monticore */

package mc.feature.compilationunit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.compilationunit.compunit._ast.ASTCu;
import mc.feature.compilationunit.compunit._ast.ASTCuBar;
import mc.feature.compilationunit.compunit._ast.ASTCuFoo;
import mc.feature.compilationunit.compunit._parser.CompunitParser;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testFoo() throws IOException {
    CompunitParser p = new CompunitParser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("foo a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertTrue(cUnit.get() instanceof ASTCuFoo);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBar() throws IOException {
    CompunitParser p = new CompunitParser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("bar a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertTrue(cUnit.get() instanceof ASTCuBar);
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
