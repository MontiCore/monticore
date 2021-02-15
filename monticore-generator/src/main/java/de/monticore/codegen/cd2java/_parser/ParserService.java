/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;

import javax.print.DocFlavor;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.*;
import static de.monticore.codegen.cd2java._parser.ParserConstants.ANTLR_SUFFIX;

public class ParserService extends AbstractService<ParserService> {

  public ParserService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ParserService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_parser' package for Parser generation
   */

  @Override
  public String getSubPackage() {
    return PARSER_PACKAGE;
  }

  @Override
  protected ParserService createService(DiagramSymbol cdSymbol) {
    return createParserService(cdSymbol);
  }

  public static ParserService createParserService(DiagramSymbol cdSymbol) {
    return new ParserService(cdSymbol);
  }

  /**
   * parser class names e.g. AutomataParser
   */

  public String getParserClassFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getParserClassSimpleName(cdSymbol);
  }

  public String getParserClassFullName() {
    return getParserClassFullName(getCDSymbol());
  }

  public String getParserClassSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + PARSER_SUFFIX;
  }

  public String getParserClassSimpleName() {
    return getParserClassSimpleName(getCDSymbol());
  }

  public String getAntlrParserSimpleName(){
    return getAntlrParserSimpleName(getCDSymbol());
  }

  public String getAntlrParserSimpleName(DiagramSymbol cdSymbol){
    return cdSymbol.getName() + ANTLR_SUFFIX + PARSER_SUFFIX;
  }

  public String getParseRuleNameJavaCompatible(ASTCDType rule) {
    return getParseRuleNameJavaCompatible(rule.getName());
  }

  public String getParseRuleNameJavaCompatible(String ruleName){
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(removeASTPrefix(ruleName)));
  }

  public String removeASTPrefix(String name){
    if(name.startsWith(AST_PREFIX)){
      name = name.substring(3);
    }
    return name;
  }

  public Optional<String> getStartProd(ASTCDDefinition astcdDefinition){
    if (astcdDefinition.isPresentModifier() && hasStartProdStereotype(astcdDefinition.getModifier())) {
      return getStartProdValue(astcdDefinition.getModifier());
    }
    for (ASTCDClass prod : astcdDefinition.getCDClassesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(astcdDefinition.getSymbol().getPackageName() + "." + astcdDefinition.getSymbol().getName() + "." + prod.getName());
      }
    }
    for (ASTCDInterface prod : astcdDefinition.getCDInterfacesList()) {
      if (hasStereotype(prod.getModifier(), MC2CDStereotypes.START_PROD)) {
        return Optional.of(astcdDefinition.getSymbol().getPackageName() + "." + astcdDefinition.getSymbol().getName() + "." + prod.getName());
      }
    }
    //look for a start prod in super grammars
    for(DiagramSymbol def: getSuperCDsDirect(astcdDefinition.getSymbol())){
      return getStartProd((ASTCDDefinition) def.getAstNode());
    }
    return Optional.empty();
  }

  public Optional<String> getStartProd(){
    return getStartProd((ASTCDDefinition) getCDSymbol().getAstNode());
  }


}
