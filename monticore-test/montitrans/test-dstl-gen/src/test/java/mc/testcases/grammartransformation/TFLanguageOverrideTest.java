/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.grammartransformation;


import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.tr.genericdsltr._ast.ASTNewClassProd;
import mc.testcases.tr.genericdsltr._parser.GenericDSLTRParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TFLanguageOverrideTest {

    @Before
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
