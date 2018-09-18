/* (c)  https://github.com/MontiCore/monticore */
package src.test.java.mc.feature.deepclone;

import mc.feature.deepclone.deepclone._ast.ASTCloneBasic;
import mc.feature.deepclone.deepclone._ast.ASTCloneAST;
import mc.feature.deepclone.deepclone._ast.DeepCloneNodeFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeepCloneOverriddingTest {
  
  @Test
  public void TestBasic() throws IOException {
    DeepCloneNodeFactory.
    mc.feature.deepclone.deepclone2._parser.DeepClone2Parser parser = new mc.feature.deepclone.deepclone2._parser.DeepClone2Parser();
    Optional<ASTCloneAST> ast = parser.parse_StringCloneAST("clone cloneList bla");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneAST astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  

}
