/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ScopeTest {

  @Test
  public void testInitEnclosingAndSubScopesUsingConstructors() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(enclosing));
    MutableScope sub2 = new CommonScope(Optional.of(enclosing));

    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testInitEnclosingAndSubScopesUsingAddSubScopeMethod() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(false);
    MutableScope sub2 = new CommonScope(false);

    assertEquals(0, enclosing.getSubScopes().size());
    assertFalse(sub1.getEnclosingScope().isPresent());
    assertFalse(sub2.getEnclosingScope().isPresent());

    enclosing.addSubScope(sub1);
    enclosing.addSubScope(sub2);

    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    // the enclosing scope is also set in the sub scopes
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());

    enclosing.addSubScope(sub1);
    // Nothing changes, because sub1 had already been added.
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testInitEnclosingAndSubScopesUsingSetEnclosingScopeMethod() {
    MutableScope enclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(false);
    MutableScope sub2 = new CommonScope(false);

    assertEquals(0, enclosing.getSubScopes().size());
    assertFalse(sub1.getEnclosingScope().isPresent());
    assertFalse(sub2.getEnclosingScope().isPresent());

    sub1.setEnclosingScope(enclosing);
    sub2.setEnclosingScope(enclosing);

    // the sub scopes are also added to the enclosing scopes
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());

    sub1.setEnclosingScope(enclosing);
    // Nothing changes, because the enclosing scope had already been set.
    assertEquals(2, enclosing.getSubScopes().size());
    assertSame(sub1, enclosing.getSubScopes().get(0));
    assertSame(sub2, enclosing.getSubScopes().get(1));
    assertSame(enclosing, sub1.getEnclosingScope().get());
    assertSame(enclosing, sub2.getEnclosingScope().get());
  }

  @Test
  public void testChangingEnclosingAndSubScopesUsingSetEnclosingMethod() {
    MutableScope oldEnclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(oldEnclosing));
    MutableScope sub2 = new CommonScope(Optional.of(oldEnclosing));

    assertEquals(2, oldEnclosing.getSubScopes().size());
    assertSame(sub1, oldEnclosing.getSubScopes().get(0));
    assertSame(sub2, oldEnclosing.getSubScopes().get(1));
    assertSame(oldEnclosing, sub1.getEnclosingScope().get());
    assertSame(oldEnclosing, sub2.getEnclosingScope().get());

    MutableScope newEnclosing = new CommonScope(false);

    sub1.setEnclosingScope(newEnclosing);

    // sub1 is now sub of newEnclosing...
    assertEquals(1, newEnclosing.getSubScopes().size());
    assertSame(sub1, newEnclosing.getSubScopes().get(0));
    assertSame(newEnclosing, sub1.getEnclosingScope().get());
    // ...and removed from oldEnclosing
    assertEquals(1, oldEnclosing.getSubScopes().size());
    assertSame(sub2, oldEnclosing.getSubScopes().get(0));
  }

  @Test
  public void testChangingEnclosingAndSubScopesUsingAddSubScopeMethod() {
    MutableScope oldEnclosing = new CommonScope(false);
    MutableScope sub1 = new CommonScope(Optional.of(oldEnclosing));
    MutableScope sub2 = new CommonScope(Optional.of(oldEnclosing));

    assertEquals(2, oldEnclosing.getSubScopes().size());
    assertSame(sub1, oldEnclosing.getSubScopes().get(0));
    assertSame(sub2, oldEnclosing.getSubScopes().get(1));
    assertSame(oldEnclosing, sub1.getEnclosingScope().get());
    assertSame(oldEnclosing, sub2.getEnclosingScope().get());

    MutableScope newEnclosing = new CommonScope(false);

    newEnclosing.addSubScope(sub1);

    // sub1 is now sub of newEnclosing...
    assertEquals(1, newEnclosing.getSubScopes().size());
    assertSame(sub1, newEnclosing.getSubScopes().get(0));
    assertSame(newEnclosing, sub1.getEnclosingScope().get());
    // ...and removed from oldEnclosing
    assertEquals(1, oldEnclosing.getSubScopes().size());
    assertSame(sub2, oldEnclosing.getSubScopes().get(0));
  }

  @Test
  public void testGetAllEncapsulatedSymbols(){
    final MutableScope scope1 = new CommonScope(false);
    final StateSymbol symbol11 = new StateSymbol("s11");
    scope1.add(symbol11);

    final MutableScope scope2 = new CommonScope(false);
    final StateSymbol symbol21 = new StateSymbol("s21");
    final StateSymbol symbol22 = new StateSymbol("s22");
    scope2.add(symbol21);
    scope2.add(symbol22);

    final MutableScope scope3 = new CommonScope(false);
    final StateSymbol symbol31 = new StateSymbol("s31");
    scope3.add(symbol31);

    scope1.addResolver(CommonResolvingFilter.create(StateSymbol.KIND));
    scope2.addResolver(CommonResolvingFilter.create(StateSymbol.KIND));
    scope3.addResolver(CommonResolvingFilter.create(StateSymbol.KIND));

    scope1.addSubScope(scope2);
    scope2.addSubScope(scope3);

    final Collection<? extends Symbol> encapsulatedSymbolsScope1 = Scopes.getAllEncapsulatedSymbols(scope1);
    assertEquals(1, encapsulatedSymbolsScope1.size());
    assertTrue(encapsulatedSymbolsScope1.contains(symbol11));

    final Collection<? extends Symbol> encapsulatedSymbolsScope2 = Scopes.getAllEncapsulatedSymbols(scope2);
    assertEquals(3, encapsulatedSymbolsScope2.size());
    // symbols directly defined in scope2
    assertTrue(encapsulatedSymbolsScope2.contains(symbol21));
    assertTrue(encapsulatedSymbolsScope2.contains(symbol22));
    // symbols implicitly imported from scope3, and hence, encapsulated
    assertTrue(encapsulatedSymbolsScope2.contains(symbol11));

    final Collection<? extends Symbol> encapsulatedSymbolsScope3 = Scopes.getAllEncapsulatedSymbols(scope3);
    assertEquals(4, encapsulatedSymbolsScope3.size());
    // symbol directly defined in scope1
    assertTrue(encapsulatedSymbolsScope3.contains(symbol31));
    // symbols implicitly imported from scope2, and hence, encapsulated
    assertTrue(encapsulatedSymbolsScope3.contains(symbol21));
    assertTrue(encapsulatedSymbolsScope3.contains(symbol22));
    // symbols implicitly (and transitively) imported from scope3, and hence, encapsulated
    assertTrue(encapsulatedSymbolsScope3.contains(symbol11));
  }

}
