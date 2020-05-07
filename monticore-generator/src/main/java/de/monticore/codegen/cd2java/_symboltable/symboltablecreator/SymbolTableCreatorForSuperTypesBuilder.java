/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolReference class from a grammar
 */
public class SymbolTableCreatorForSuperTypesBuilder extends AbstractCreator<List<ASTCDClass>, List<ASTCDClass>> {

  protected static final String TEMPLATE_PATH = "_symboltable.symboltablecreatordelegator.";

  protected final BuilderDecorator builderDecorator;

  protected final SymbolTableService symbolTableService;

  public SymbolTableCreatorForSuperTypesBuilder(final GlobalExtensionManagement glex,
                                                final BuilderDecorator builderDecorator,
                                                final SymbolTableService symbolTableService) {
    super(glex);
    this.builderDecorator = builderDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDClass> decorate(List<ASTCDClass> inputList) {
    List<ASTCDClass> builderList = new ArrayList<>();
    for (ASTCDClass input : inputList) {
      ASTCDClass superST = input.deepClone();
      ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, symbolTableService.getScopeInterfaceFullName());
      superST.addCDAttribute(createScopeStackAttribute(dequeType));

      superST.getCDMethodList().clear();

      ASTCDClass superSTBuilder = builderDecorator.decorate(superST);

      // search for build method
      Optional<ASTCDMethod> buildMethod = superSTBuilder.getCDMethodList()
          .stream()
          .filter(m -> BUILD_METHOD.equals(m.getName()))
          .findFirst();
      // replace init part of build method
      buildMethod.ifPresent(b -> this.replaceTemplate("_ast.builder.BuildInit", b, new StringHookPoint(
          "value= new " + superST.getName() + "(scopeStack);")));

      builderList.add(superSTBuilder);
    }
    return builderList;
  }


  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }
}
