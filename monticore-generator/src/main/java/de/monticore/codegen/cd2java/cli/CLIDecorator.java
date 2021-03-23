/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._cocos.CoCoConstants.ADD_CHECKER;
import static de.monticore.codegen.cd2java._cocos.CoCoConstants.CHECKER_SIMPLE_NAME;

/**
 * created mill class for a grammar
 */
public class CLIDecorator extends AbstractCreator<List<ASTCDCompilationUnit> , ASTCDClass> {

  public static final String TEMPLATE_PATH = "_cli.";
  protected final AbstractService abstractService;


  public CLIDecorator(final GlobalExtensionManagement glex,
                      final AbstractService abstractService) {
    super(glex);
    this.abstractService = abstractService;
  }

  public ASTCDClass decorate(final List<ASTCDCompilationUnit> cds) {
    String cliClassName = abstractService.getCliSimpleName();

    ASTCDClass cliClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(cliClassName)
        .addCDMethod(creatMainMethod(abstractService.getCDSymbol()))
        .addCDMethod(creatCreateSymbolTableMethod())
        .addCDMethod(creatParseMethod())
        .build();

    return cliClass;
  }

  protected ASTCDMethod creatMainMethod(CDDefinitionSymbol cdSymbol) {
    String grammarname = cdSymbol.getName();
    String startprod = abstractService.getStartProdASTFullName().get();
    ASTMCType checkerType = getMCTypeFacade().createArrayType("String",1);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "args");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "main" , parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Main" , grammarname, startprod));
    return addCheckerMethod;
  }

  protected ASTCDMethod creatParseMethod() {
    ASTMCType checkerType = getMCTypeFacade().createStringType();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "model");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "parse", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Addmaker"));
    return addCheckerMethod;
  }

  protected ASTCDMethod creatCreateSymbolTableMethod() {
    Optional<String> str =  abstractService.getStartProdASTFullName();
    ASTMCType checkerType = getMCTypeFacade().createQualifiedType(str.get());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(checkerType, "ast");
    ASTCDMethod addCheckerMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, "createSymbolTable", parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new TemplateHookPoint(TEMPLATE_PATH + "Addmaker"));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createAttribute(){
    ASTMCType type = getMCTypeFacade().createStringType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE , type, "mill");
    return attribute;
  }

  protected ASTCDAttribute createMillASTCliAttributeII(){
    ASTMCType type = getMCTypeFacade().createIntType();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PRIVATE , type, "in");
    return attribute;
  }

}
