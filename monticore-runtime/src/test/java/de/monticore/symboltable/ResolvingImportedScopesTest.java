/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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
import java.util.Arrays;
import java.util.Optional;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolReference;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import org.junit.Test;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingImportedScopesTest {

  @Test
  public void testOnlyConsiderExplicitlyImportedScopesWhenResolvingInImportedScope() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolverConfiguration);
    final ArtifactScope as = new ArtifactScope(Optional.empty(), "p", new ArrayList<>());
    gs.addSubScope(as);
    as.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol asProp = new PropertySymbol("asProp", new EntitySymbolReference("foo", as));
    as.add(asProp);

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    as.add(supEntity);
    supEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    assertTrue(supEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());

    final PropertySymbol supProp = new PropertySymbol("supProp", new EntitySymbolReference("bar", supEntity.getSpannedScope()));
    supEntity.addProperty(supProp);

    assertTrue(supEntity.getSpannedScope().resolve("supProp", PropertySymbol.KIND).isPresent());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    gs.add(subEntity);
    subEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    subEntity.setSuperClass(new EntitySymbolReference("p.SupEntity", gs));

    assertTrue(subEntity.getSuperClass().get().existsReferencedSymbol());
    // Resolving property that is defined in super entity should work
    assertTrue(subEntity.getSpannedScope().resolve("supProp", PropertySymbol.KIND).isPresent());
    // Resolving property that is defined in the enclosing scope of the super entity should not work
    assertFalse(subEntity.getSpannedScope().resolve("asProp", PropertySymbol.KIND).isPresent());
  }

  @Test
  public void testSymbolInImportedScopeHasHigherPriorityThanSymbolInEnclosingScope() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolverConfiguration);
    final ArtifactScope as = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
    gs.addSubScope(as);
    as.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol asProp = new PropertySymbol("prop", new EntitySymbolReference("foo", as));
    as.add(asProp);

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    as.add(subEntity);
    subEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    gs.add(supEntity);
    supEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol supProp = new PropertySymbol("prop", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supEntity.addProperty(supProp);

    // Resolving "prop" in sub enity should resolve to property symbol of super entity (instead of enclosing scope)
    final PropertySymbol resolvedProp = subEntity.getSpannedScope().<PropertySymbol>resolve("prop", PropertySymbol.KIND).get();
    assertSame(supProp, resolvedProp);
  }

  @Test
  public void testResolutionContinuesWIthPackageLocalModifierIfSuperTypeIsInSamePackage() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolverConfiguration);
    final ArtifactScope asSub = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());
    gs.addSubScope(asSub);
    asSub.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    asSub.add(subEntity);
    subEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    final ArtifactScope asSup = new ArtifactScope(Optional.empty(), "p.q", new ArrayList<>());
    gs.addSubScope(asSup);
    asSup.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    asSup.add(supEntity);
    supEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol supProp = new PropertySymbol("prop", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supProp.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);
    supEntity.addProperty(supProp);

    // SubEntity and SupEntity are in same package, hence, package-local field "prop" can be resolved.
    PropertySymbol resolvedProp = subEntity.getSpannedScope().<PropertySymbol>resolve("prop", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).get();
    assertSame(supProp, resolvedProp);
  }

  @Test
  public void testResolutionContinuesWithProtectedModifierIfSuperTypeIsInDifferentPackage() {
    final ModelingLanguage language = new EntityLanguage();
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(language.getResolvers());

    final GlobalScope gs = new GlobalScope(new ModelPath(), language, resolverConfiguration);
    final ArtifactScope asSub = new ArtifactScope(Optional.empty(), "p.q", Arrays.asList(new ImportStatement("x.y", true)));
    gs.addSubScope(asSub);
    asSub.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final EntitySymbol subEntity = new EntitySymbol("SubEntity");
    asSub.add(subEntity);
    subEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    subEntity.setSuperClass(new EntitySymbolReference("SupEntity", subEntity.getEnclosingScope()));

    // Super type is in another package
    final ArtifactScope asSup = new ArtifactScope(Optional.empty(), "x.y", new ArrayList<>());
    gs.addSubScope(asSup);
    asSup.setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final EntitySymbol supEntity = new EntitySymbol("SupEntity");
    asSup.add(supEntity);
    supEntity.getSpannedScope().setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());

    final PropertySymbol supPropPackageLocal = new PropertySymbol("propPL", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supPropPackageLocal.setAccessModifier(BasicAccessModifier.PACKAGE_LOCAL);
    supEntity.addProperty(supPropPackageLocal);

    final PropertySymbol supPropProtected = new PropertySymbol("propProtected", new EntitySymbolReference("bar", supEntity.getEnclosingScope()));
    supPropProtected.setAccessModifier(BasicAccessModifier.PROTECTED);
    supEntity.addProperty(supPropProtected);

    // SubEntity and SupEntity are not in same package, hence, package-local field "propPL" cannot be resolved...
    assertFalse(subEntity.getSpannedScope().<PropertySymbol>resolve("propPL", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).isPresent());
    // ...but the protected field "propProtected" can be resolved.
    assertTrue(subEntity.getSpannedScope().<PropertySymbol>resolve("propProtected", PropertySymbol.KIND, BasicAccessModifier.PRIVATE).isPresent());

  }

}
