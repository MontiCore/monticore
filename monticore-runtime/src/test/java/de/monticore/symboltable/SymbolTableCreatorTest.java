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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.asts.ASTSymbolReference;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.CommonEntityLanguageSymbolTableCreator;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguageSymbolTableCreator;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolKind;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;
import org.junit.Test;

/**
 *
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolTableCreatorTest {
  
  @Test
  public void testEntitySymbolTableCreator() {
    /*
     * class Class {
     *   type field;
     *   
     *   void method() {
     *     type var;   
     *   }
     * } 
     * 
     */

    ASTEntityCompilationUnit classCompilationUnit = new ASTEntityCompilationUnit();

    ASTEntity astClass = new ASTEntity();
    astClass.setName("Class");
    classCompilationUnit.setClassNode(astClass);

    ASTProperty astField = new ASTProperty();
    astField.setName("field");
    astClass.addChild(astField);
    
    ASTSymbolReference astReference1 = new ASTSymbolReference(ASTEntity.class);
    astReference1.setSymbolName("type");
    astField.setReference(astReference1);
    
    ASTAction astAction = new ASTAction();
    astAction.setName("method");
    astClass.addChild(astAction);
    
    ASTProperty astProperty = new ASTProperty();
    astProperty.setName("var");
    astAction.addChild(astProperty);
    
    ASTSymbolReference astReference2 = new ASTSymbolReference(ASTEntity.class);
    astReference2.setSymbolName("type");
    astProperty.setReference(astReference2);

    
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(new EntityLanguage().getResolvers());

    final MutableScope globalScope = new GlobalScope(new ModelPath(), new ArrayList<>(), resolverConfiguration);

    EntityLanguageSymbolTableCreator creator = new CommonEntityLanguageSymbolTableCreator(resolverConfiguration, globalScope);

    Scope globals = creator.createFromAST(classCompilationUnit);

    EntitySymbol clazz = globals.<EntitySymbol>resolve("Class", EntitySymbolKind.KIND).get();
    assertNotNull(clazz);
    
    PropertySymbol field  = clazz.getProperty("field").get();
    assertNotNull(field);
    assertEquals("field", field.getName());
    assertSame(field, clazz.getSpannedScope().resolve("field", PropertySymbol.KIND).get());
    
    ActionSymbol method = clazz.getAction("method").get();
    assertNotNull(method);
    assertEquals("method", method.getName());
    assertSame(method, clazz.getSpannedScope().resolve("method", ActionSymbol.KIND).get());
    assertSame(field, method.getSpannedScope().resolve("field", PropertySymbol.KIND).get());
    
    PropertySymbol variable  = method.getVariable("var").get();
    assertNotNull(variable);
    assertEquals("var", variable.getName());
    assertSame(variable, method.getSpannedScope().resolve("var", PropertySymbol.KIND).get());
  }
  

}
