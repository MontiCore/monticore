/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.asts.ASTSymbolReference;
import de.monticore.symboltable.mocks.languages.entity.*;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTProperty;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

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

    
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(new EntityLanguage().getResolvingFilters());

    final Scope globalScope = new GlobalScope(new ModelPath(), new ArrayList<>(), resolvingConfiguration);

    EntityLanguageSymbolTableCreator creator = new CommonEntityLanguageSymbolTableCreator(resolvingConfiguration, globalScope);

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
