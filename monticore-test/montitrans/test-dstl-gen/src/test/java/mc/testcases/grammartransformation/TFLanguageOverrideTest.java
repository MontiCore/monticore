/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.grammartransformation;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.tr.genericdsltr.GenericDSLTRMill;
import mc.testcases.tr.genericdsltr._ast.ASTNewClassProd;
import mc.testcases.tr.genericdsltr._parser.GenericDSLTRParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(GenericDSLTRMill.class)
public class TFLanguageOverrideTest {

    @Test
    public void testTFLanguageOverride() throws IOException {
        GenericDSLTRParser parser = GenericDSLTRMill.parser();

        Optional<ASTNewClassProd> ast = parser.parse_StringNewClassProd("dummy");
        assertTrue(ast.isPresent());
        assertEquals("", ast.get().toString());
    }
}
