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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolReference;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingTest {

  // TODO PN test some complex resolving scenarios.

  @Test
  public void testSameSymbolOccursOnlyOnce() {
    final EntitySymbol entity = new EntitySymbol("Entity");

    final MutableScope localScope = new CommonScope(false);
    localScope.addResolver(CommonResolvingFilter.create(EntitySymbol.class, EntitySymbol.KIND));

    localScope.add(entity);
    localScope.add(entity);

    assertEquals(2, localScope.getSymbols().size());

    try {
      // Although the same symbol is stored twice in the scope, it is
      // only returned once, hence, no ambiguity exists.
      localScope.resolve("Entity", EntitySymbol.KIND).get();
    }
    catch(ResolvedSeveralEntriesException e) {
      fail();
    }

  }

  @Test
  public void testSpecificResolversForScopes() {
    final EntitySymbol entity = new EntitySymbol("Entity");
    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    entity.addProperty(property);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final MutableScope localScope = new CommonScope(false);
    ((MutableScope)action.getSpannedScope()).addSubScope(localScope);

    final ResolvingFilter<PropertySymbol> propertyResolvingFilter = CommonResolvingFilter.create(
        PropertySymbol.class, PropertySymbol.KIND);

    // Only localScope is initialized with a resolver for properties
    localScope.addResolver(propertyResolvingFilter);

    // If resolving starts from the local scope, prop is found, because local scope is
    // initialized with a property resolvers.
    assertTrue(localScope.resolve("prop", PropertySymbol.KIND).isPresent());
    assertSame(property, localScope.resolve("prop", PropertySymbol.KIND).get());

    // Although prop is defined in entity, it cannot be resolved starting from action or entity,
    // because these scopes are not initialized with a property resolvers.
    assertFalse(action.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
    assertFalse(entity.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());


    entity.getSpannedScope().addResolver(propertyResolvingFilter);
    assertFalse(action.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
    assertTrue(entity.getSpannedScope().resolve("prop", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testResolveInnerSymbol() {
    final EntitySymbol entity = new EntitySymbol("Entity");

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    action.addVariable(property);


    final ModelingLanguage modelingLanguage = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(modelingLanguage.getResolvers());
    final ModelPath modelPath = new ModelPath(Paths.get(""));

    final GlobalScope globalScope = new GlobalScope(modelPath, modelingLanguage, resolverConfiguration);
    globalScope.add(entity);

    assertTrue(globalScope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("Entity.action", ActionSymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("Entity.action.prop", PropertySymbol.KIND).isPresent());

    assertFalse(globalScope.resolve("Entity.action2.prop", PropertySymbol.KIND).isPresent());
  }


  @Test
  public void testResolveInnerSymbolWithArtifactScope() {
    final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());

    final EntitySymbol entity = new EntitySymbol("Entity");
    artifactScope.add(entity);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    final PropertySymbol property = new PropertySymbol("prop", new CommonJTypeReference<>("int", JTypeSymbol.KIND, entity.getSpannedScope()));
    action.addVariable(property);


    final ModelingLanguage modelingLanguage = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(modelingLanguage.getResolvers());
    final ModelPath modelPath = new ModelPath(Paths.get(""));

    final GlobalScope globalScope = new GlobalScope(modelPath, modelingLanguage, resolverConfiguration);
    globalScope.addSubScope(artifactScope);

    assertTrue(globalScope.resolve("p.q.Entity", EntitySymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("p.q.Entity.action", ActionSymbol.KIND).isPresent());
    assertTrue(globalScope.resolve("p.q.Entity.action.prop", PropertySymbol.KIND).isPresent());

    // Cannot be resolved since names are not qualified
    assertFalse(globalScope.resolve("Entity", EntitySymbol.KIND).isPresent());
    assertFalse(globalScope.resolve("Entity.action", ActionSymbol.KIND).isPresent());
    assertFalse(globalScope.resolve("Entity.action.prop", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testCannotResolveInnerSymbolViaPartialName() {
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(new EntityLanguage().getResolvers());

    final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "p", new ArrayList<>());
    artifactScope.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final EntitySymbol entity = new EntitySymbol("Entity");
    entity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());
    artifactScope.add(entity);

    final ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    assertTrue(entity.getSpannedScope().resolve("action", ActionSymbol.KIND).isPresent());
    assertTrue(entity.getSpannedScope().resolve("p.Entity.action", ActionSymbol.KIND).isPresent());

    // Cannot resolve Entity.action, since it is not clear whether it is a qualified
    // or a partial name.
    assertFalse(entity.getSpannedScope().resolve("Entity.action", ActionSymbol.KIND).isPresent());

  }

  @Ignore
  @Test
  public void testOnlyConsiderExplicitlyImportedScopesWhenResolvingInImportedScope() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolverConfiguration);
    final ArtifactScope as = new ArtifactScope(Optional.empty(), "p", new ArrayList<>());
    gs.addSubScope(as);
    as.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol asProp = new PropertySymbol("asProp", new EntitySymbolReference("foo", gs));
    as.add(asProp);

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    as.add(supEntity);
    supEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    assertTrue(supEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());


    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    gs.add(subEntity);
    subEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    subEntity.setSuperClass(new EntitySymbolReference("p.SupEntity", gs));

    assertTrue(subEntity.getSuperClass().get().existsReferencedSymbol());
    assertFalse(subEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());



  }

}
