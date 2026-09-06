/* (c) https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf._ast.ASTENodePackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.runtime.junit.AbstractMCTest;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ASTNodeTest extends AbstractMCTest {

    @Test
    public void testSerializing() throws IOException {
        AST2ModelFiles.get().serializeAST(ASTENodePackage.eINSTANCE);
    }
}
