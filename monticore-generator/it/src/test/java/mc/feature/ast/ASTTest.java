/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.monticore.MontiCoreScript;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.delete.deletetest._ast.ASTChild;
import mc.feature.delete.deletetest._ast.ASTParent;
import mc.feature.delete.deletetest._ast.DeleteTestNodeFactory;
import mc.feature.featuredsl._ast.ASTA;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;
import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGet_ChildNodes1() {
    List<ASTA> aList = new ArrayList<>();
    ASTA a = FeatureDSLNodeFactory.createASTA();
    assertEquals(0, aList.size());
    aList.add(a);
    assertEquals(1, aList.size());
  }
  
  @Test
  public void testGet_ChildNodes2() {
    ASTParent p = DeleteTestNodeFactory.createASTParent();
    ASTChild s = DeleteTestNodeFactory.createASTChild();
    p.addChild(s);
    p.setSon(s);
    assertEquals(1, p.getChildList().size());
    assertTrue(p.containsChild(s));
  }
  
  @Test
  public void testFileNameInSourcePosition() {
    String grammarToTest = "src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4";
    
    Path model = Paths.get(new File(
        grammarToTest).getAbsolutePath());
    
    MontiCoreScript mc = new MontiCoreScript();
    Optional<ASTMCGrammar> ast = mc.parseGrammar(model);
    assertTrue(ast.isPresent());
    ASTMCGrammar clonedAst = ast.get().deepClone();
    assertTrue(clonedAst.get_SourcePositionStart().getFileName().isPresent());
    assertEquals("SimpleGrammarWithConcept.mc4", FilenameUtils.getName(clonedAst.get_SourcePositionStart().getFileName().get()));
  }
  
}
