/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest._ast.ASTTCCompilationUnit;
import mc.typechecktest._parser.TypeCheckTestParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class OOTest {

  @BeforeClass
  public static void init(){
    Log.init();
    Log.enableFailQuick(false);
    BasicSymbolsMill.initializePrimitives();
  }

  @Before
  public void setup() throws IOException {
    TypeCheckTestParser parser = TypeCheckTestMill.parser();
    Optional<ASTTCCompilationUnit> comp =  parser.parse("src/test/resources/mc/typescalculator/Check.tc");
    assertTrue(comp.isPresent());

  }

  @Test
  public void test(){

  }



}
