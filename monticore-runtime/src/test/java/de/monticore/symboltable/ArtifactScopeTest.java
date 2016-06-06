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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import org.junit.Test;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class ArtifactScopeTest {

  @Test
  public void testArtifactScopeWithoutImportStatements() {
    final CommonResolvingFilter<EntitySymbol> classResolver = new CommonResolvingFilter<>(EntitySymbol.class, EntitySymbol.KIND);

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new LinkedHashSet<>(), new ResolverConfiguration());
    globalScope.addResolver(classResolver);

    ArtifactScope artifactScope1 = new ArtifactScope(Optional.of(globalScope), "p", new ArrayList<>());
    artifactScope1.addResolver(classResolver);
    EntitySymbol classA = new EntitySymbol("A");
    artifactScope1.add(classA);

    Scope classAScope = classA.getSpannedScope();

    ArtifactScope artifactScope2 = new ArtifactScope(Optional.of(globalScope), "q", new ArrayList<>());
    artifactScope2.addResolver(classResolver);
    EntitySymbol classQB = new EntitySymbol("B");
    artifactScope2.add(classQB);

    // resolve by qualified name
    assertSame(classQB, classAScope.resolve("q.B", EntitySymbol.KIND).get());

    // no imports defined, hence, resolving by simple name fails
    assertNull(classAScope.resolve("B", EntitySymbol.KIND).orElse(null));

    ArtifactScope artifactScope3 = new ArtifactScope(Optional.of(globalScope), "r", new ArrayList<>());
    artifactScope3.addResolver(classResolver);
    EntitySymbol classRB = new EntitySymbol("B");
    artifactScope3.add(classRB);

    // Now, besides q.B the symbol r.B is defines in the global scope (resp. in the compilation scopes)
    // => Nothing changes

    // resolve by qualified name
    assertSame(classQB, classAScope.resolve("q.B", EntitySymbol.KIND).get());

    // no imports defined, hence, resolving by simple name fails
    assertNull(classAScope.resolve("B", EntitySymbol.KIND).orElse(null));

  }

  @Test
  public void testArtifactScopeWithImportStatements() {
    final CommonResolvingFilter<EntitySymbol> classResolver = new CommonResolvingFilter<>(EntitySymbol.class, EntitySymbol.KIND);

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new LinkedHashSet<>(), new ResolverConfiguration());
    globalScope.addResolver(classResolver);

    ArtifactScope artifactScope1 = new ArtifactScope(Optional.of(globalScope), "p", Arrays.asList(
        new ImportStatement("q", true), new ImportStatement("r.B", false)));
    artifactScope1.addResolver(classResolver);
    EntitySymbol classA = new EntitySymbol("A");
    artifactScope1.add(classA);

    MutableScope classAScope = classA.getMutableSpannedScope();
    classAScope.addResolver(classResolver);

    ArtifactScope artifactScope2 = new ArtifactScope(Optional.of(globalScope), "q", new ArrayList<>());
    artifactScope2.addResolver(classResolver);
    EntitySymbol classQB = new EntitySymbol("B");
    classQB.getMutableSpannedScope().addResolver(classResolver);
    artifactScope2.add(classQB);

    // resolve by qualified name
    assertSame(classQB, classAScope.resolve("q.B", EntitySymbol.KIND).get());

    // q.B is imported, hence, resolving by simple succeeds.
    assertSame(classQB, classAScope.resolve("B", EntitySymbol.KIND).orElse(null));

    ArtifactScope artifactScope3 = new ArtifactScope(Optional.of(globalScope), "r", new ArrayList<>());
    artifactScope3.addResolver(classResolver);
    EntitySymbol classRB = new EntitySymbol("B");
    classRB.getMutableSpannedScope().addResolver(classResolver);
    artifactScope3.add(classRB);

    // Now, besides q.B the symbol r.B is defined in the global scope
    // (resp. in the artifact scope scopes)

    // resolve by qualified name
    assertSame(classQB, classAScope.resolve("q.B", EntitySymbol.KIND).get());

    // resolve by simple name
    try {
      classAScope.resolve("B", EntitySymbol.KIND);
      fail();
    }
    catch (ResolvedSeveralEntriesException e) {
      assertEquals(2, e.getSymbols().size());
      assertTrue(e.getSymbols().contains(classQB));
      assertTrue(e.getSymbols().contains(classRB));
    }

  }

  @Test
  public void testResolveUnqualifiedSymbolInSamePackage() {
    final CommonResolvingFilter<EntitySymbol> classResolver = new CommonResolvingFilter<>(EntitySymbol.class, EntitySymbol.KIND);

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new LinkedHashSet<>(), new ResolverConfiguration());
    globalScope.addResolver(classResolver);

    ArtifactScope artifactScope1 = new ArtifactScope(Optional.of(globalScope), "p", new ArrayList<>());
    artifactScope1.addResolver(classResolver);
    EntitySymbol classA = new EntitySymbol("A");
    artifactScope1.add(classA);
    Scope classAScope = classA.getSpannedScope();


    ArtifactScope artifactScope2 = new ArtifactScope(Optional.of(globalScope), "p", new ArrayList<>());
    artifactScope2.addResolver(classResolver);
    EntitySymbol classB = new EntitySymbol("B");
    artifactScope2.add(classB);

    // resolve B with unqualified name, because it is in the same package as A.
    assertSame(classB, classAScope.resolve("B", EntitySymbol.KIND).get());

    assertSame(classA, classAScope.resolve("A", EntitySymbol.KIND).get());
  }

  @Test
  public void testPackageNameMustBePrefixOfQualifiedSymbolName() {
    final CommonResolvingFilter<EntitySymbol> classResolver = new CommonResolvingFilter<>(EntitySymbol.class, EntitySymbol.KIND);

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new LinkedHashSet<>(), new ResolverConfiguration());
    globalScope.addResolver(classResolver);

    ArtifactScope artifactScope1 = new ArtifactScope(Optional.of(globalScope), "p", new ArrayList<>());
    artifactScope1.addResolver(classResolver);
    EntitySymbol classA = new EntitySymbol("A");
    artifactScope1.add(classA);


    ArtifactScope artifactScope2 = new ArtifactScope(Optional.of(globalScope), "p2", new ArrayList<>());
    artifactScope2.addResolver(classResolver);
    EntitySymbol classA2 = new EntitySymbol("A");
    artifactScope2.add(classA2);

    assertSame(classA, globalScope.resolve("p.A", EntitySymbol.KIND).get());
    assertSame(classA2, globalScope.resolve("p2.A", EntitySymbol.KIND).get());
    assertNotSame(classA, classA2);
  }

}
