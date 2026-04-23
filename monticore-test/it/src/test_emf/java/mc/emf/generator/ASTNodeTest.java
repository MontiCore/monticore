/* (c) https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf._ast.ASTENodePackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ASTNodeTest {

    @BeforeAll
    public static void setup() {
      LogStub.init();
      Log.enableFailQuick(false);
    }

    /**
     *
     * @param args
     */
    @Test
    public void testSerializing() {

      try {
        AST2ModelFiles.get().serializeAST(ASTENodePackage.eINSTANCE);
      }
      catch (IOException e) {
        fail("Should not reach this, but: " + e);
      }

    }
}
