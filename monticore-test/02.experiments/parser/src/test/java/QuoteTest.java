import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quotetest.QuoteTestMill;
import quotetest._ast.ASTEscapedNameDoubleQuote;
import quotetest._ast.ASTEscapedNameSingleQuote;
import quotetest._parser.QuoteTestParser;

import java.util.Optional;

public class QuoteTest {
    QuoteTestParser parser;

    @BeforeEach
    public void setUp() {
        QuoteTestMill.init();
        parser = QuoteTestMill.parser();
        Log.enableFailQuick(false);
    }

    @AfterEach
    public void tearDown() {
        QuoteTestMill.reset();
        Log.clearFindings();
    }

    @Test
    public void testSingleQuoteValid() {
        ASTEscapedNameSingleQuote ast = Assertions.assertDoesNotThrow(() -> parser.parse_StringEscapedNameSingleQuote(
                "n'thisWorks'"
        ).orElseThrow());
        Assertions.assertEquals("thisWorks", ast.getName());
        Assertions.assertEquals(0, Log.getFindingsCount());
    }

    @Test
    public void testSingleQuoteInvalid() {
        Optional<ASTEscapedNameSingleQuote> ast = Assertions.assertDoesNotThrow(() -> parser.parse_StringEscapedNameSingleQuote(
                "nthisDoesNotWork"
        ));
        Assertions.assertFalse(ast.isPresent());
        Assertions.assertTrue(Log.getFindingsCount() > 0);
    }

    @Test
    public void testDoubleQuoteValid() {
        ASTEscapedNameDoubleQuote ast = Assertions.assertDoesNotThrow(() -> parser.parse_StringEscapedNameDoubleQuote(
                "n\"thisWorks\""
        ).orElseThrow());
        Assertions.assertEquals("thisWorks", ast.getName());
        Assertions.assertEquals(0, Log.getFindingsCount());
    }

    @Test
    public void testDoubleQuoteInvalid() {
        Optional<ASTEscapedNameDoubleQuote> ast = Assertions.assertDoesNotThrow(() -> parser.parse_StringEscapedNameDoubleQuote(
                "n\\\"thisDoesNotWork\\\""
        ));
        Assertions.assertFalse(ast.isPresent());
        Assertions.assertTrue(Log.getFindingsCount() > 0);
    }
}
