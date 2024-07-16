/* (c) https://github.com/MontiCore/monticore */
package mc.feature.optionalname;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.optionalname.optionalkeyword.OptionalKeywordMill;
import mc.feature.optionalname.optionalkeyword._parser.OptionalKeywordParser;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesArtifactScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
public class OptionalKeywordTest {

  private IScopeAttributesArtifactScope scope;
  private ASTStartProd startProd;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParsing() throws IOException {
    OptionalKeywordParser parser = OptionalKeywordMill.parser();
    parser.parse_String("foo Foo");
    Assertions.assertFalse(parser.hasErrors());

    parser.parse_String("Foo");
    Assertions.assertFalse(parser.hasErrors());
  }

}
