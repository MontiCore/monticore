/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;

import java.util.Optional;

public class ASTConstructionActions {

  protected ParserGeneratorHelper parserGenHelper;

  protected MCGrammarSymbol symbolTable;

  public ASTConstructionActions(ParserGeneratorHelper parserGenHelper) {
    this.parserGenHelper = parserGenHelper;
    this.symbolTable = parserGenHelper.getGrammarSymbol();
  }

  protected String getConstantClassName(MCGrammarSymbol symbol) {
    return Joiners.DOT.join(symbol.getFullName().toLowerCase(),
            ASTConstants.AST_PACKAGE,
            ASTConstants.AST_CONSTANTS + symbol.getName());
  }

  public String getConstantInConstantGroupMultipleEntries(ASTConstant constant,
                                                          ASTConstantGroup constgroup) {
    String tmp = "";
    if (constgroup.isPresentUsageName()) {
      String constfile;
      String constantname;
      Optional<MCGrammarSymbol> ruleGrammar = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(constgroup.getEnclosingScope());
      if (ruleGrammar.isPresent()) {
        constfile = getConstantClassName(ruleGrammar.get());
        constantname = parserGenHelper.getConstantNameForConstant(constant);
      }
      else {
        constfile = getConstantClassName(symbolTable);
        constantname = parserGenHelper.getConstantNameForConstant(constant);
      }

      // Add as attribute to AST
      tmp = "_builder.set%uname%(%constfile%.%constantname%);";

      tmp = tmp.replaceAll("%uname%",
              StringTransformations.capitalize(constgroup.getUsageName()));

      tmp = tmp.replaceAll("%constfile%", constfile);

      tmp = tmp.replaceAll("%constantname%", constantname);
    }

    return tmp;
  }

  public String getActionAfterConstantInEnumProdSingle(ASTConstant c) {
    return "ret = true ;";
  }

  public String getConstantInConstantGroupSingleEntry(ASTConstant constant,
                                                      ASTConstantGroup constgroup) {
    String tmp = "";

    if (constgroup.isPresentUsageName()) {
      // Add as attribute to AST
      tmp = "_builder.set%uname%(true);";

      tmp = tmp.replaceAll("%uname%",
              StringTransformations.capitalize(constgroup.getUsageName()));
    } else {
      if (constgroup.getConstantList().size() == 1) {
        // both == null and #constants == 1 -> use constant string as name
        tmp = "_builder.set%cname%(true);";
        tmp = tmp.replaceAll("%cname%", StringTransformations.capitalize(HelperGrammar
                .getAttributeNameForConstant(constgroup.getConstantList().get(0))));
      } else {
        // both == null and #constants > 1 -> user wants to ignore token in AST
      }
    }

    return tmp;
  }

  public String getActionForRuleBeforeRuleBody(ASTClassProd a) {
    StringBuilder b = new StringBuilder();
    String type = MCGrammarSymbolTableHelper
            .getQualifiedName(a.getSymbol());
    Optional<MCGrammarSymbol> grammar = MCGrammarSymbolTableHelper
            .getMCGrammarSymbol(a.getEnclosingScope());

    // Setup builder
    b.append("// getActionForAltBeforeRuleBody\n");
    b.append(type + "Builder _builder = " + parserGenHelper.getQualifiedGrammarName().toLowerCase()
            + "." + parserGenHelper.getGrammarSymbol().getName() + "Mill."
            + StringTransformations.uncapitalize(a.getName()) + "Builder();\n");

    return b.toString();
  }

  public String getActionForAltBeforeRuleBody(String className, ASTAlt a) {
    StringBuilder b = new StringBuilder();
    String type = MCGrammarSymbolTableHelper
            .getQualifiedName(symbolTable.getProdWithInherited(className).get());
    Optional<MCGrammarSymbol> grammar = MCGrammarSymbolTableHelper
            .getMCGrammarSymbol(a.getEnclosingScope());
    String name = grammar.isPresent()
            ? grammar.get().getName()
            : symbolTable.getProdWithInherited(className).get().getName();

    // Setup builder
    b.append("// getActionForAltBeforeRuleBody\n");
    b.append(type + "Builder _builder = " + parserGenHelper.getQualifiedGrammarName().toLowerCase()
            + "." + parserGenHelper.getGrammarSymbol().getName() + "Mill."
            + StringTransformations.uncapitalize(className) + "Builder();\n");

    return b.toString();
  }


  public String getActionForLexerRuleNotIteratedAttribute(ASTNonTerminal a) {

    String tmp = "_builder.set%u_usage%(convert" + a.getName() + "($%tmp%));";

    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
            StringTransformations.capitalize(HelperGrammar.getUsageName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));

    return tmp;

  }

  public String getActionForLexerRuleIteratedAttribute(ASTNonTerminal a) {

    String tmpname = parserGenHelper.getTmpVarNameForAntlrCode(a);
    String tmp = " addToIteratedAttributeIfNotNull(_builder.get%u_usage%(), convert" + a.getName()
            + "($%tmp%));";

    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
            StringTransformations.capitalize(MCGrammarInfo.getListName(a)));
    tmp = tmp.replaceAll("%tmp%", tmpname);

    return tmp;
  }

  public String getActionForInternalRuleIteratedAttribute(ASTNonTerminal a) {

    String tmp = "addToIteratedAttributeIfNotNull(_builder.get%u_usage%(), _localctx.%tmp%.ret);";
    if (symbolTable.getProdWithInherited(a.getName()).get().isIsEnum()) {
      tmp = "addToIteratedAttributeIfNotNull(_builder.get%u_usage%(), _localctx.%tmp%.ret);";
    }

    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
            StringTransformations.capitalize(MCGrammarInfo.getListName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));

    return tmp;
  }

  public String getActionForInternalRuleNotIteratedAttribute(ASTNonTerminal a) {

    String tmp = "_builder.set%u_usage%(_localctx.%tmp%.ret);";

    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
            StringTransformations.capitalize(HelperGrammar.getUsageName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));

    return tmp;
  }

  public String getActionForInternalRuleNotIteratedLeftRecursiveAttribute(ASTNonTerminal a) {

    SourcePositionActions sourcePositionBuilder = new SourcePositionActions(parserGenHelper);
    StringBuilder b = new StringBuilder();
    b.append("// Action code for left recursive rule \n");
    // Setup builder
    b.append("// getActionForInternalRuleNotIteratedLeftRecursiveAttribute \n");
    b.append(sourcePositionBuilder.endPosition());
    b.append("\n_localctx." + parserGenHelper.getTmpVarName(a) + ".ret = _builder.uncheckedBuild();");
    b.append("\n_builder=" + parserGenHelper.getQualifiedGrammarName().toLowerCase()
            + "." + parserGenHelper.getGrammarSymbol().getName() + "Mill."
            +
            StringTransformations.uncapitalize(a.getName()) + "Builder();\n");
    b.append(sourcePositionBuilder.startPositionForLeftRecursiveRule(a));


    return b.toString();
  }

  /**
   * Nothing to do for ignore
   */
  public String getActionForTerminalIgnore(ASTTerminal a) {
    return "";
  }

  public String getActionForTerminalNotIteratedAttribute(ASTITerminal a) {

    String tmp = "_builder.set%u_usage%(\"%text%\");";

    if (!a.isPresentUsageName()) {
      return "";
    }
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(a.getUsageName()));
    tmp = tmp.replaceAll("%text%", a.getName());

    return tmp;

  }

  public String getActionForTerminalIteratedAttribute(ASTITerminal a) {

    if (!a.isPresentUsageName()) {
      return "";
    }

    String tmp = "_builder.get%u_usage%().add(\"%text%\");";

    // Replace templates
    String usageName = StringTransformations.capitalize(a.getUsageName());
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(usageName + DecorationHelper.GET_SUFFIX_LIST));
    tmp = tmp.replaceAll("%text%", a.getName());

    return tmp;
  }

  public String getActionForKeyTerminalNotIteratedAttribute(ASTKeyTerminal a) {

    String tmp = "_builder.set%u_usage%(_input.LT(-1).getText());";

    if (!a.isPresentUsageName()) {
      return "";
    }
    // Replace templates
    return tmp.replaceAll("%u_usage%", StringTransformations.capitalize(a.getUsageName()));
  }

  public String getActionForKeyTerminalIteratedAttribute(ASTKeyTerminal a) {

    if (!a.isPresentUsageName()) {
      return "";
    }

    String tmp = "_builder.get%u_usage%().add(_input.LT(-1).getText());";

    // Replace templates
    String usageName = StringTransformations.capitalize(a.getUsageName());
    return tmp.replaceAll("%u_usage%", StringTransformations.capitalize(usageName + DecorationHelper.GET_SUFFIX_LIST));
  }

  public String getBuildAction() {
    return "\n_localctx.ret = _builder.uncheckedBuild();";
  }

}
