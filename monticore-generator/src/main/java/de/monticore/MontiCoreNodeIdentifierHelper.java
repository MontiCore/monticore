/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.types.MCSimpleGenericTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.Names;

import static de.monticore.generating.templateengine.reporting.commons.Layouter.nodeName;

public class MontiCoreNodeIdentifierHelper extends MCSimpleGenericTypesNodeIdentHelper {
  
  // ##############
  // Identifier helper for Literals (could be moved into the grammars module?)
  // TODO: discuss this (with BR?): what do we want identifiers for in the end?
  // until then they remain unused
  // ##############
  
  
    
  // ##############
  // Identifier helper for Grammar
  // TODO: incomplete by now; only those added here which "seem" to have a name
  // ##############
  
  public String getIdent(ASTAntlrOption ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTAdditionalAttribute ast) {
    if (ast.isPresentName()) {
      return format(ast.getName(), nodeName(ast));
    }
    return format(nodeName(ast));
  }
  
  public String getIdent(ASTConcept ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTConstantGroup ast) {
    if (ast.isPresentUsageName()) {
      return format(ast.getUsageName(), nodeName(ast));
    }
    return format(nodeName(ast));
  }
  
  public String getIdent(ASTFollowOption ast) {
    return format(ast.getProdName(), nodeName(ast));
  }
  
  public String getIdent(ASTMCBasicGenericType ast) {
    return format(Names.getQualifiedName(ast.getNameList()), nodeName(ast));
  }
  
  public String getIdent(ASTGrammarReference ast) {
    return format(Names.getSimpleName(ast.getNameList()), nodeName(ast));
  }
  
  public String getIdent(ASTITerminal ast) {
    // return a regular "Name"
    String name = ast.getName();
    if ((name.length()) < 4 && !name.matches("[a-zA-Z0-9_$]*")) {
      // Replace special character by the corresponding name (; -> SEMI)
      name = createGoodName(name);
    }
    else {
      // Replace all special characters by _
      name = name.replaceAll("[^a-zA-Z0-9_$\\-+]", "_");
      if (name.matches("[0-9].*")) {
        // if the name starts with a digit ...
        name = "_".concat(name);
      }
    }
    return format(name, nodeName(ast));
  }
  
  public String getIdent(ASTLexNonTerminal ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTMCGrammar ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTGrammarMethod ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTMethodParameter ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTNonTerminal ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTNonTerminalSeparator ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTProd ast) {
    return format(ast.getName(), nodeName(ast));
  }
  
  public String getIdent(ASTRuleReference ast) {
    return format(ast.getName(), nodeName(ast));
  }
  public String getIdent(ASTMCType a)
  {
    return format(a.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()), Layouter.nodeName(a));
  }
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IASTNodeIdentHelper#getIdent(de.monticore.ast.ASTNode)
   */
  @Override
  public String getIdent(ASTNode ast) {

    if (ast instanceof ASTMCQualifiedName) {
      return getIdent((ASTMCQualifiedName) ast);
    }
    else if (ast instanceof ASTMCType) {
      return getIdent((ASTMCType) ast);
    }
    else if (ast instanceof ASTAntlrOption) {
      return getIdent((ASTAntlrOption) ast);
    }
    else if (ast instanceof ASTAdditionalAttribute) {
      return getIdent((ASTAdditionalAttribute) ast);
    }
    else if (ast instanceof ASTConcept) {
      return getIdent((ASTConcept) ast);
    }
    else if (ast instanceof ASTConstantGroup) {
      return getIdent((ASTConstantGroup) ast);
    }
    else if (ast instanceof ASTFollowOption) {
      return getIdent((ASTFollowOption) ast);
    }
    else if (ast instanceof ASTGrammarReference) {
      return getIdent((ASTGrammarReference) ast);
    }
    else if (ast instanceof ASTITerminal) {
      return getIdent((ASTITerminal) ast);
    }
    else if (ast instanceof ASTLexNonTerminal) {
      return getIdent((ASTLexNonTerminal) ast);
    }
    else if (ast instanceof ASTMCGrammar) {
      return getIdent((ASTMCGrammar) ast);
    }
    else if (ast instanceof ASTGrammarMethod) {
      return getIdent((ASTGrammarMethod) ast);
    }
    else if (ast instanceof ASTMethodParameter) {
      return getIdent((ASTMethodParameter) ast);
    }
    else if (ast instanceof ASTNonTerminal) {
      return getIdent((ASTNonTerminal) ast);
    }
    else if (ast instanceof ASTNonTerminalSeparator) {
      return getIdent((ASTNonTerminalSeparator) ast);
    }
    else if (ast instanceof ASTProd) {
      return getIdent((ASTProd) ast);
    }
    else if (ast instanceof ASTRuleReference) {
      return getIdent((ASTRuleReference) ast);
    }
    return format(nodeName(ast));
  }
  
  public static String createGoodName(String x) {
    StringBuilder ret = new StringBuilder();
    
    for (int i = 0; i < x.length(); i++) {
      
      String substring = x.substring(i, i + 1);
      if (LexNamer.getGoodNames().containsKey(substring)) {
        ret.append(LexNamer.getGoodNames().get(substring));
      }
      else {
        ret.append(substring);
      }
    }
    
    return ret.toString();
    
  }
  
}
