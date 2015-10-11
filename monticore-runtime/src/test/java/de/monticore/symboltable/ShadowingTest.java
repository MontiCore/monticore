/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

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

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create
        (PropertySymbol.class, PropertySymbol.KIND);
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

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create
        (PropertySymbol.class, PropertySymbol.KIND);
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
    CommonScope grandEnclosingScope = new CommonScope(false); // true would have the same impact
    CommonScope enclosingScope = new CommonScope(Optional.of(grandEnclosingScope), true);
    CommonScope scope = new CommonScope(Optional.of(enclosingScope), false);

    JTypeReference intReference = new CommonJTypeReference<>("int", JTypeSymbol.KIND, scope);

    PropertySymbol gpVariable = new PropertySymbol("var", intReference);
    grandEnclosingScope.add(gpVariable);

    PropertySymbol variable = new PropertySymbol("var", intReference);
    scope.add(variable);

    ResolvingFilter<PropertySymbol> variableResolvingFilter = CommonResolvingFilter.create
        (PropertySymbol.class, PropertySymbol.KIND);
    grandEnclosingScope.addResolver(variableResolvingFilter);
    enclosingScope.addResolver(variableResolvingFilter);
    scope.addResolver(variableResolvingFilter);

    assertSame(gpVariable, grandEnclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(gpVariable, enclosingScope.resolve("var", PropertySymbol.KIND).get());
    assertSame(variable, scope.resolve("var", PropertySymbol.KIND).get());
  }
}
