/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

/**
 * created mill class for a grammar
 */
public class RunnerDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  public static final String TEMPLATE_PATH = "_cli.";
  protected final AbstractService abstractService;


  public RunnerDecorator(final GlobalExtensionManagement glex,
                         final AbstractService abstractService) {
    super(glex);
    this.abstractService = abstractService;

  }


  public Optional<ASTCDClass> decorate(final ASTCDCompilationUnit cd) {
    Optional<ASTCDClass>  cliClass = Optional.empty();

    ASTCDDefinition cdDefinition = cd.getCDDefinition();
    if (!cdDefinition.isPresentModifier() || !abstractService.hasComponentStereotype(cdDefinition.getModifier())) {
      String cliClassName = abstractService.getCliSimpleName();
      cliClass = Optional.of(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(cliClassName)
          .addCDMember(createMainMethod(abstractService.getCDSymbol()))
          .addCDMember(createCreateSymbolTableMethod(abstractService.getCDSymbol()))
          .addCDMember(createParseMethod(abstractService.getCDSymbol()))
          .build());
    }

    return cliClass;

  }

  protected ASTCDMethod createMainMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String", 1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "main", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main", grammarname, startprod.get()));
    return addCheckerMethod;
  }

  protected ASTCDMethod createParseMethod(DiagramSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    Optional<String> startprod = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "model");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "parse", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Parser", grammarname, startprod.get()));
    return addCheckerMethod;
  }

  protected ASTCDMethod createCreateSymbolTableMethod(DiagramSymbol cdDefinitionSymbol) {
    String grammarname = cdDefinitionSymbol.getName();
    Optional<String> str = abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(str.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), "createSymbolTable", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "SymbolTable", grammarname));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createAttribute() {
    ASTMCType type = getMCTypeFacade().createStringType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE.build(), type, "mill");
    return attribute;
  }

  protected ASTCDAttribute createMillASTCliAttributeII() {
    ASTMCType type = getMCTypeFacade().createIntType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE.build(), type, "in");
    return attribute;
  }

}
