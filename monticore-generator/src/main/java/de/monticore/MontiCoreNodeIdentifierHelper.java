/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import static de.monticore.generating.templateengine.reporting.commons.Layouter.nodeName;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTAntlrOption;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTConcept;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTFollowOption;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTITerminal;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.types.TypesNodeIdentHelper;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.Names;

public class MontiCoreNodeIdentifierHelper extends TypesNodeIdentHelper {
  
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
  
  public String getIdent(ASTAttributeInAST ast) {
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
  
  public String getIdent(ASTGenericType ast) {
    return format(Names.getQualifiedName(ast.getNameList()), nodeName(ast));
  }
  
  public String getIdent(ASTGrammarReference ast) {
    return format(Names.getSimpleName(ast.getNameList()), nodeName(ast));
  }
  
  public String getIdent(ASTITerminal ast) {
    // return a regular "Name"
    String name = ast.getName();
    if ((name.length()) < 4 && !name.matches("[a-zA-Z0-9_$\\-+]*")) {
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
  
  public String getIdent(ASTMethod ast) {
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
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IASTNodeIdentHelper#getIdent(de.monticore.ast.ASTNode)
   */
  @Override
  public String getIdent(ASTNode ast) {
    if (ast instanceof ASTBooleanLiteral) {
      return getIdent((ASTBooleanLiteral) ast);
    }
    else if (ast instanceof ASTCharLiteral) {
      return getIdent((ASTCharLiteral) ast);
    }
    else if (ast instanceof ASTDoubleLiteral) {
      return getIdent((ASTDoubleLiteral) ast);
    }
    else if (ast instanceof ASTFloatLiteral) {
      return getIdent((ASTFloatLiteral) ast);
    }
    else if (ast instanceof ASTIntLiteral) {
      return getIdent((ASTIntLiteral) ast);
    }
    else if (ast instanceof ASTLongLiteral) {
      return getIdent((ASTLongLiteral) ast);
    }
    else if (ast instanceof ASTNullLiteral) {
      return getIdent((ASTNullLiteral) ast);
    }
    else if (ast instanceof ASTStringLiteral) {
      return getIdent((ASTStringLiteral) ast);
    }
    else if (ast instanceof ASTQualifiedName) {
      return getIdent((ASTQualifiedName) ast);
    }
    else if (ast instanceof ASTSimpleReferenceType) {
      return getIdent((ASTSimpleReferenceType) ast);
    }
    else if (ast instanceof ASTTypeVariableDeclaration) {
      return getIdent((ASTTypeVariableDeclaration) ast);
    }
    else if (ast instanceof ASTAntlrOption) {
      return getIdent((ASTAntlrOption) ast);
    }
    else if (ast instanceof ASTAttributeInAST) {
      return getIdent((ASTAttributeInAST) ast);
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
    else if (ast instanceof ASTGenericType) {
      return getIdent((ASTGenericType) ast);
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
    else if (ast instanceof ASTMethod) {
      return getIdent((ASTMethod) ast);
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
