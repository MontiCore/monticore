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

import java.util.Optional;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.JTypeSymbolMock;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.references.PropertySymbolReference;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertSame;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class ReferencesTest {

  @Test
  public void testVariableReference() {
    /**
     * class C {
     * ... D.fieldOfD // reference to fieldOfD
     * }
     *
     * class D {
     *   int fieldOfD;
     * }
     *
     */

    EntitySymbol c = new EntitySymbol("C");
    PropertySymbolReference ref = new PropertySymbolReference("fieldOfD", Optional.of("D"), c.getSpannedScope());

    EntitySymbol d = new EntitySymbol("D");

    PropertySymbol fieldOfD = new PropertySymbol("fieldOfD", new CommonJTypeReference<>("int", JTypeSymbolMock.KIND, c.getSpannedScope()));
    d.addProperty(fieldOfD);

    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolver(CommonResolvingFilter.create(CommonJTypeSymbol.class,
        CommonJTypeSymbol.KIND));
    resolverConfiguration.addTopScopeResolver(CommonResolvingFilter.create(PropertySymbol
            .class,
        PropertySymbol.KIND));

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new ArrayList<>(), resolverConfiguration);

    globalScope.add(c);
    globalScope.add(d);

    ((MutableScope)d.getSpannedScope()).setResolvingFilters(globalScope.getResolvingFilters());

    assertSame(c, globalScope.resolve("C", CommonJTypeSymbol.KIND).orElse(null));
    assertSame(d, globalScope.resolve("D", CommonJTypeSymbol.KIND).orElse(null));

    assertSame(fieldOfD, d.getProperty("fieldOfD").orElse(null));

    assertSame(fieldOfD, ref.getReferencedSymbol());
  }

}
