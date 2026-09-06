/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.grammartransformation;


import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.tr.genericdsltr._ast.ASTNewClassProd;
import mc.testcases.tr.genericdsltr._parser.GenericDSLTRParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TFLanguageOverrideTest {

    @BeforeEach
    public void disableFailQuick() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testTFLanguageOverride() throws IOException {
        GenericDSLTRParser parser = new GenericDSLTRParser();

        Optional<ASTNewClassProd> ast = parser.parse_StringNewClassProd("dummy");
        assertTrue(ast.isPresent());
        assertEquals("", ast.get().toString());

        // should not result in any errors
        assertTrue(Log.getFindings().isEmpty());
    }
}
