/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

public class ASTConstructionActions {

  protected ParserGeneratorHelper parserGenHelper;
  
  protected MCGrammarSymbol symbolTable;
  
  public ASTConstructionActions(ParserGeneratorHelper parserGenHelper) {
    this.parserGenHelper = parserGenHelper;
    this.symbolTable = parserGenHelper.getGrammarSymbol();
  }

  public String getConstantInConstantGroupMultipleEntries(ASTConstant constant,
      ASTConstantGroup constgroup) {
    String tmp = "";
    if (constgroup.isPresentUsageName()) {
      String constfile;
      String constantname;
      Optional<MCGrammarSymbol> ruleGrammar = MCGrammarSymbolTableHelper
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
      tmp = "_aNode.set%uname%(true);";
      
      tmp = tmp.replaceAll("%uname%",
          StringTransformations.capitalize(constgroup.getUsageName()));
    }
    else {
      if (constgroup.getConstantList().size() == 1) {
        // both == null and #constants == 1 -> use constant string as name
        tmp = "_aNode.set%cname%(true);";
        tmp = tmp.replaceAll("%cname%", StringTransformations.capitalize(HelperGrammar
            .getAttributeNameForConstant(constgroup.getConstantList().get(0))));
      }
      else {
        // both == null and #constants > 1 -> user wants to ignore token in AST
      }
    }
    
    return tmp;
  }
  
  public String getActionForRuleBeforeRuleBody(ASTClassProd a) {
    StringBuilder b = new StringBuilder();
    String type = MCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).get());
    Optional<MCGrammarSymbol> grammar = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    String name = grammar.isPresent()
        ? grammar.get().getName()
        : symbolTable.getProdWithInherited(HelperGrammar.getRuleName(a)).get().getName();
    
        // Setup return value
        b.append(
            "// ret is normally returned, a is used to be compatible with rule using the return construct\n");
        b.append(type + " _aNode = null;\n");
        b.append("_aNode=" + Names.getQualifier(type) + "." + name + "NodeFactory.create"
            +
            Names.getSimpleName(type) + "();\n");
        b.append("$ret=_aNode;\n");
        
        return b.toString();
      }

  public String getActionForAltBeforeRuleBody(String className, ASTAlt a) {
    StringBuilder b = new StringBuilder();
    String type = MCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(className).get());
    Optional<MCGrammarSymbol> grammar = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    String name = grammar.isPresent()
        ? grammar.get().getName()
        : symbolTable.getProdWithInherited(className).get().getName();
    
        // Setup return value
        b.append(type + " _aNode = null;\n");
        b.append("_aNode=" + Names.getQualifier(type) + "." + name + "NodeFactory.create"
            +
            Names.getSimpleName(type) + "();\n");
        b.append("$ret=_aNode;\n");
        
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
  
  public String getActionForLexerRuleIteratedAttribute(ASTNonTerminal a) {

    String tmpname = parserGenHelper.getTmpVarNameForAntlrCode(a);
    String tmp = " addToIteratedAttributeIfNotNull(_aNode.get%u_usage%(), convert" + a.getName()
        + "($%tmp%));";
    
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%",
        StringTransformations.capitalize(HelperGrammar.getListName(a)));
    tmp = tmp.replaceAll("%tmp%", tmpname);
    
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
        StringTransformations.capitalize(HelperGrammar.getListName(a)));
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
    
    String type = MCGrammarSymbolTableHelper
        .getQualifiedName(symbolTable.getProdWithInherited(a.getName()).get()); // TODO
                                                                                // GV:
                                                                                // getDefinedType().getQualifiedName()
    String name = MCGrammarSymbolTableHelper.getMCGrammarSymbol(a).get().getName();
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

    if (!a.isPresentUsageName()) {
      return "";
    }
    // Replace templates
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(a.getUsageName()));
    tmp = tmp.replaceAll("%text%", a.getName());

    return tmp;

  }

  public String getActionForTerminalIteratedAttribute(ASTTerminal a) {

    if (!a.isPresentUsageName()) {
      return "";
    }

    String tmp = "_aNode.get%u_usage%().add(\"%text%\");";

    // Replace templates
    // TODO MB : Find better solution
    String usageName = StringTransformations.capitalize(a.getUsageName());
    if (usageName.endsWith(TransformationHelper.LIST_SUFFIX)) {
      usageName = usageName.substring(0, usageName.length()-TransformationHelper.LIST_SUFFIX.length());    
    }
    tmp = tmp.replaceAll("%u_usage%", StringTransformations.capitalize(usageName+ GeneratorHelper.GET_SUFFIX_LIST));
    tmp = tmp.replaceAll("%text%", a.getName());

    return tmp;
  }
  
}
