package de.monticore.codegen.cd2java._parser;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;

import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_PACKAGE;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_SUFFIX;

public class ParserService extends AbstractService<ParserService> {

  public ParserService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ParserService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return PARSER_PACKAGE;
  }

  @Override
  protected ParserService createService(CDDefinitionSymbol cdSymbol) {
    return createParserService(cdSymbol);
  }

  public static ParserService createParserService(CDDefinitionSymbol cdSymbol) {
    return new ParserService(cdSymbol);
  }

         /*
    parser class names e.g. AutomataParser
   */

  public String getParserClassFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getParserClassSimpleName(cdSymbol);
  }

  public String getParserClassFullName() {
    return getParserClassFullName(getCDSymbol());
  }

  public String getParserClassSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + PARSER_SUFFIX;
  }

  public String getParserClassSimpleName() {
    return getParserClassSimpleName(getCDSymbol());
  }


}
