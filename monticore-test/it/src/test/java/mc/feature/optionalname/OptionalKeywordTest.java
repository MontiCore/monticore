/* (c) https://github.com/MontiCore/monticore */
package mc.feature.optionalname;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.optionalname.optionalkeyword.OptionalKeywordMill;
import mc.feature.optionalname.optionalkeyword._parser.OptionalKeywordParser;
import mc.feature.scopes.scopeattributes.ScopeAttributesMill;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._parser.ScopeAttributesParser;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesArtifactScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesGlobalScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesScope;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
public class OptionalKeywordTest {

  private IScopeAttributesArtifactScope scope;
  private ASTStartProd startProd;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParsing() throws IOException {
    OptionalKeywordParser parser = OptionalKeywordMill.parser();
    parser.parse_String("foo Foo");
    assertFalse(parser.hasErrors());

    parser.parse_String("Foo");
    assertFalse(parser.hasErrors());
  }

}
