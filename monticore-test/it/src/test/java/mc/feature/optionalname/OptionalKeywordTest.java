/* (c) https://github.com/MontiCore/monticore */
package mc.feature.optionalname;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.optionalname.optionalkeyword.OptionalKeywordMill;
import mc.feature.optionalname.optionalkeyword._parser.OptionalKeywordParser;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesArtifactScope;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
@TestWithMCLanguage(OptionalKeywordMill.class)
public class OptionalKeywordTest {

  private IScopeAttributesArtifactScope scope;
  private ASTStartProd startProd;

  @Test
  public void testParsing() throws IOException {
    OptionalKeywordParser parser = OptionalKeywordMill.parser();
    parser.parse_String("foo Foo");
    assertFalse(parser.hasErrors());

    parser.parse_String("Foo");
    assertFalse(parser.hasErrors());
  }

}
