/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.mocks.CommonScopeMock;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ShadowingTest {

  @Test
  public void testLocalScopeDoesNotShadowEnclosingScope() {
    CommonScope enclosingScope = new CommonScope(false);
    CommonScope scope = new CommonScope(Optional.of(enclosingScope), false);

    JTypeReference intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, scope);
    JTypeReference stringReference = new CommonJTypeReference<>("String", JTypeSymbol.KIND, scope);

    PropertySymbol v1 = new PropertySymbol("var", intReference);
    enclosingScope.add(v1);
    PropertySymbol x = new PropertySymbol("x", intReference);
    enclosingScope.add(x);

    PropertySymbol v2 = new PropertySymbol("var", stringReference);
    scope.add(v2);
    PropertySymbol y = new PropertySymbol("y", stringReference);
    scope.add(y);

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create(PropertySymbol.KIND);
    enclosingScope.addResolver(variableResolvingFilter);
    scope.addResolver(variableResolvingFilter);

    assertSame(v1, enclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(x, enclosingScope.resolve("x", PropertySymbol.KIND).get());
    assertSame(x, scope.resolve("x", PropertySymbol.KIND).get());
    assertSame(y, scope.resolve("y", PropertySymbol.KIND).get());

    try {
      scope.resolve("var", PropertySymbol.KIND);
      fail();
    }
    catch (ResolvedSeveralEntriesException e) {
      assertEquals(2, e.getSymbols().size());
      assertTrue(e.getSymbols().contains(v2));
      assertTrue(e.getSymbols().contains(v1));
    }
  }

  @Test
  public void testShadowingScopeShadowsEnclosingScope() {
    CommonScope enclosingScope = new CommonScope(false);
    // is shadowing scope
    CommonScope scope = new CommonScope(Optional.of(enclosingScope), true);

    JTypeReference intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, scope);
    JTypeReference stringReference = new CommonJTypeReference<>("String", JTypeSymbol.KIND, scope);

    PropertySymbol v1 = new PropertySymbol("var", intReference);
    enclosingScope.add(v1);
    PropertySymbol x = new PropertySymbol("x", intReference);
    enclosingScope.add(x);

    PropertySymbol v2 = new PropertySymbol("var", stringReference);
    scope.add(v2);
    PropertySymbol y = new PropertySymbol("y", stringReference);
    scope.add(y);

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create(PropertySymbol.KIND);
    enclosingScope.addResolver(variableResolvingFilter);
    scope.addResolver(variableResolvingFilter);

    assertSame(v1, enclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(x, enclosingScope.resolve("x", PropertySymbol.KIND).get());
    assertSame(x, scope.resolve("x", PropertySymbol.KIND).get());
    assertSame(y, scope.resolve("y", PropertySymbol.KIND).get());

    // shadows enclosing var
    assertSame(v2, scope.resolve("var", PropertySymbol.KIND).get());
  }

  @Test
  public void testLocalScopeShadowsGrandEnclosingScopeIfEnclosingIsShadowingScope() {
    CommonScopeMock grandGrandEnclosingScope = new CommonScopeMock(false); // true would have the same impact
    CommonScopeMock grandEnclosing = new CommonScopeMock(Optional.of(grandGrandEnclosingScope), true);
    CommonScopeMock enclosingScope = new CommonScopeMock(Optional.of(grandEnclosing), false);
    CommonScopeMock scope = new CommonScopeMock(Optional.of(enclosingScope), false);

    // Set names to simplify testing (see below)
    grandGrandEnclosingScope.setName("grandGrand");
    grandEnclosing.setName("grand");
    enclosingScope.setName("enclosing");
    scope.setName("scope");

    JTypeReference intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, scope);

    PropertySymbol gpVariable = new PropertySymbol("var", intReference);
    grandGrandEnclosingScope.add(gpVariable);

    PropertySymbol variable = new PropertySymbol("var", intReference);
    scope.add(variable);

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create(PropertySymbol.KIND);
    grandGrandEnclosingScope.addResolver(variableResolvingFilter);
    enclosingScope.addResolver(variableResolvingFilter);
    scope.addResolver(variableResolvingFilter);

    assertSame(gpVariable, grandGrandEnclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(gpVariable, enclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(variable, scope.resolve("var", PropertySymbol.KIND).get());

    final List<MutableScope> involvedScopes = scope.getResolvingInfo().getInvolvedScopes();

    // Resolution must stop after the third scope (i.e., grandEnclosing)
    assertEquals(3, involvedScopes.size());
    assertEquals("scope", involvedScopes.get(0).getName().get());
    assertEquals("enclosing", involvedScopes.get(1).getName().get());
    assertEquals("grand", involvedScopes.get(2).getName().get());
  }
}
