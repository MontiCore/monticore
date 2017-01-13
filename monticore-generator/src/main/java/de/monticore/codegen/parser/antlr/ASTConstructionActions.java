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

package de.monticore.codegen.parser.antlr;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.mc2cd.EssentialMCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

public class ASTConstructionActions {
  
  private final static String DEFAULT_RETURN_PARAM = "ret";
  
  protected ParserGeneratorHelper parserGenHelper;
  
  protected EssentialMCGrammarSymbol symbolTable;
  
  public ASTConstructionActions(ParserGeneratorHelper parserGenHelper) {
    this.parserGenHelper = parserGenHelper;
    this.symbolTable = parserGenHelper.getGrammarSymbol();
  }
  
  public String getConstantInConstantGroupMultipleEntries(ASTConstant constant,
      ASTConstantGroup constgroup) {
    String tmp = "";
    if (constgroup.getUsageName().isPresent()) {
      String constfile;
      String constantname;
      Optional<MCProdSymbol> rule = EssentialMCGrammarSymbolTableHelper
          .getEnclosingRule(constgroup);
      Optional<EssentialMCGrammarSymbol> ruleGrammar = EssentialMCGrammarSymbolTableHelper
          .getMCGrammarSymbol(constgroup);
      if (ruleGrammar.isPresent()) {
        constfile = AstGeneratorHelper.getConstantClassName(ruleGrammar.get());
        constantname = parserGenHelper.getConstantNameForConstant(constant);
      }
      else {
        constfile = AstGeneratorHelper.getConstantClassName(symbolTable);
        constantname = parserGenHelper.getConstantNameForConstant(constant);
      }
      
      // Add as attribute to AST
      tmp = "_aNode.set%uname%(%constfile%.%constantname%);";
      
      tmp = tmp.replaceAll("%uname%",
          StringTransformations.capitalize(constgroup.getUsageName().get()));
      
      tmp = tmp.replaceAll("%constfile%", constfile);
      
      tmp = tmp.replaceAll("%constantname%", constantname);
    }
    
    return tmp;
  }
  
  public String getActionAfterConstantInEnumProdSingle(ASTConstant c) {
    return "ret = true ;";
  }
  
  public String getActionAfterConstantInEnumProdIterated(ASTConstant c) {
    String constfile;
    String constantname;
    Optional<MCProdSymbol> rule = EssentialMCGrammarSymbolTableHelper.getEnclosingRule(c);
    Optional<EssentialMCGrammarSymbol> ruleGrammar = EssentialMCGrammarSymbolTableHelper
        .getMCGrammarSymbol(c);
    if (ruleGrammar.isPresent()) {
      constfile = AstGeneratorHelper.getConstantClassName(ruleGrammar.get());
      constantname = parserGenHelper.getConstantNameForConstant(c);
    }
    else {
      constfile = AstGeneratorHelper.getConstantClassName(symbolTable);
      constantname = parserGenHelper.getConstantNameForConstant(c);
    }
    
    return "ret = " + constfile + "." + constantname + ";";
  }
  
  public String getConstantInConstantGroupSingleEntry(ASTConstant constant,
      ASTConstantGroup constgroup) {
    String tmp = "";
    
    if (constgroup.getUsageName().isPresent()) {
      // Add as attribute to AST
      tmp = "_aNode.set%uname%(true);";
      
      tmp = tmp.replaceAll("%uname%",
          StringTransformations.capitalize(constgroup.getUsageName().get()));
    }
    else {
      if (constgroup.getConstants().size() == 1) {
        // both == null and #constants == 1 -> use constant string as name
        tmp = "_aNode.set%cname%(true);";
        tmp = tmp.replaceAll("%cname%", StringTransformations.capitalize(HelperGrammar
            .getAttributeNameForConstant(constgroup.getConstants().get(0))));
      }
      else {
        // both == null and #constants > 1 -> user wants to ignore token in AST
      }
    }
    
    return tmp;
  }
  
  public String getActionForRuleBeforeRuleBody(ASTClassProd a) {
    StringBuilder b = new StringBuilder();
    String type = EssentialMCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).get());
    Optional<EssentialMCGrammarSymbol> grammar = EssentialMCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    String name = grammar.isPresent()
        ? grammar.get().getName()
        : symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).get().getName();
    // name =
    // symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).getDefinedType().getName();
    
    // Setup return value
    b.append(
        "// ret is normally returned, a is used to be compatible with rule using the return construct\n");
    b.append(type + " _aNode = null;\n");
    b.append("_aNode=" + Names.getQualifier(type) + "." + name + "NodeFactory.create"
        +
        Names.getSimpleName(type) + "();\n");
    b.append("$ret=_aNode;\n");
    
    // List of all used temporary variables
    // TODO GV: don't need it anymore?
    
    // b.append(getCodeForTmpVars(a));
    
    return b.toString();
  }
  
  public String getActionForRuleBeforeRuleBodyExplicitReturnClass(ASTClassProd a) {
    
    StringBuilder b = new StringBuilder();
    
    String type = EssentialMCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).get());
    
    // Setup return value
    b.append("// ret is normally returned, a can be instanciated later\n");
    b.append(type + " _aNode = null;\n");
    
    // List of all used temporary variables
    // TODO GV: don't need it anymore?
    // b.append(getCodeForTmpVars(a));
    
    return b.toString();
    
  }
  
  public String getActionForLexerRuleNotIteratedAttribute(ASTNonTerminal a) {
    
    String tmp = "_aNode.set%u_usage%(convert" + a.getName() + "($%tmp%));";
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
        StringTransformations.capitalize(HelperGrammar.getUsuageName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));
    
    return tmp;
    
  }
  
  public String getActionForLexerRuleNotIteratedHandedOn(ASTNonTerminal a) {
    
    String tmp = "%handontmp% = convert" + a.getName() + "$%tmp%);";
    
    // Replace templates
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));
    
    return tmp;
  }
  
  public String getActionForLexerRuleIteratedAttribute(ASTNonTerminal a) {
    
    String tmpname = parserGenHelper.getTmpVarNameForAntlrCode(a);
    String tmp = " addToIteratedAttributeIfNotNull(_aNode.get%u_usage%(), convert" + a.getName()
        + "($%tmp%));";
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
        StringTransformations.capitalize(HelperGrammar.getUsuageName(a)));
    tmp = tmp.replaceAll("%tmp%", tmpname);
    
    return tmp;
  }
  
  public String getActionForLexerRuleIteratedHandedOn(ASTNonTerminal a) {
    
    String tmp = "addToIteratedAttributeIfNotNull(%handontmp%, convert" + a.getName()
        + "($%tmp%));";
    
    // Replace templates
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));
    
    return tmp;
    
  }
  
  public String getActionForInternalRuleIteratedAttribute(ASTNonTerminal a) {
    
    String tmp = "addToIteratedAttributeIfNotNull(_aNode.get%u_usage%(), _localctx.%tmp%.ret);";
    // TODO GV: || isConst()
    if (symbolTable.getProdWithInherited(a.getName()).get().isEnum() ) {
      tmp = "addToIteratedAttributeIfNotNull(_aNode.get%u_usage%(), _localctx.%tmp%.ret);";
    }
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
        StringTransformations.capitalize(HelperGrammar.getUsuageName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));
    
    return tmp;
  }
  
  public String getActionForInternalRuleNotIteratedAttribute(ASTNonTerminal a) {
    
    String tmp = "_aNode.set%u_usage%(_localctx.%tmp%.ret);";
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
        StringTransformations.capitalize(HelperGrammar.getUsuageName(a)));
    tmp = tmp.replaceAll("%tmp%", parserGenHelper.getTmpVarNameForAntlrCode(a));
    
    return tmp;
  }
  
  public String getActionForInternalRuleNotIteratedLeftRecursiveAttribute(ASTNonTerminal a) {
    
    String type = EssentialMCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(a.getName()).get()); // TODO
                                                                                // GV:
                                                                                // getDefinedType().getQualifiedName()
    String name = EssentialMCGrammarSymbolTableHelper.getMCGrammarSymbol(a).get().getName();
    SourcePositionActions sourcePositionBuilder = new SourcePositionActions(parserGenHelper);
    StringBuilder b = new StringBuilder();
    b.append("// Action code for left recursive rule \n");
    b.append("_aNode=" + Names.getQualifier(type) + "." + name + "NodeFactory.create"
        +
        Names.getSimpleName(type) + "();\n");
    b.append(sourcePositionBuilder.startPositionForLeftRecursiveRule(a));
    b.append(sourcePositionBuilder.endPositionForLeftRecursiveRule(a));
    b.append("$ret=_aNode;\n");
    return b.toString();
  }
  
  /**
   * Nothing to do for ignore
   */
  public String getActionForTerminalIgnore(ASTTerminal a) {
    return "";
  }
  
  public String getActionForTerminalNotIteratedAttribute(ASTTerminal a) {
    
    String tmp = "_aNode.set%u_usage%(\"%text%\");";
    
    if (!a.getUsageName().isPresent()) {
      return "";
    }
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(a.getUsageName().get()));
    tmp = tmp.replaceAll("%text%", a.getName());
    
    return tmp;
    
  }
  
  public String getActionForTerminalIteratedAttribute(ASTTerminal a) {
    
    if (!a.getUsageName().isPresent()) {
      return "";
    }
    
    String tmp = "_aNode.get%u_usage%().add(\"%text%\");";
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(a.getUsageName().get()));
    tmp = tmp.replaceAll("%text%", a.getName());
    
    return tmp;
  }
  
  public String getActionForTerminalNotIteratedVariable(ASTTerminal a) {
    if (!a.getVariableName().isPresent()) {
      return "";
    }
    
    String tmp = "%handontmp%= \"" + a.getName() + "\";";
    
    // Replace templates
    tmp = tmp.replaceAll("%handontmp%", a.getVariableName().get());
    tmp = tmp.replaceAll("%tmp%", a.getName());
    
    return tmp;
  }
  
  public String getActionForTerminalIteratedVariable(ASTTerminal a) {
    if (!a.getVariableName().isPresent()) {
      return "";
    }
    
    String tmp = "%handontmp%.add(\"" + a.getName() + "\");";
    
    // Replace templates
    tmp = tmp.replaceAll("%handontmp%", a.getVariableName().get());
    
    return tmp;
  }
  
  /**
   * Create temporary variable for all parser and interface rules
   *
   * @param rule Current rule
   * @param element ASTNonTerminal to create var for
   * @param usedTmp
   * @return
   * @return
   */
  // TODO GV: don't need it anymore?
  
  // private String getTempVarDeclaration(MCRuleSymbol rule, ASTNonTerminal
  // element,
  // Set<String> usedTmp) {
  //
  // StringBuilder code = new StringBuilder();
  // Optional<MCProdSymbol> ruleByName =
  // symbolTable.getProdWithInherited(element.getName());
  // if (!ruleByName.isPresent()) {
  // Log.error("0xA0921 Error by parser generation: there is no rule for " +
  // element.getName());
  // return code.toString();
  // }
  //
  // return code.toString();
  // }
  
  // TODO GV: don't need it anymore?
  
  private String getCodeForTmpVars(ASTClassProd ast) {
    Optional<MCProdSymbol> rule = symbolTable.getProdWithInherited(ast.getName());
    StringBuilder code = new StringBuilder();
    Set<String> usedTmp = new LinkedHashSet<>();
    
    // Declare tmp-Variables for NonTerminals
    for (ASTNonTerminal element : ASTNodes.getSuccessors(ast, ASTNonTerminal.class)) {
      // LexerRule do not need temporary variables, but all parser rules,
      // interface rules and hole rules do (even when used as variable)
      // Pattern: '%Type% %tmp% = null;'
      // TODO GV: don't need it anymore?
      
      // code.append(getTempVarDeclaration(rule, element, usedTmp));
    }
    
    return code.toString();
    
  }
  
}
