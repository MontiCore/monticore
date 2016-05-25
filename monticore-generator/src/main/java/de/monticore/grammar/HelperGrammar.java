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

package de.monticore.grammar;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Some helper methods for GrammarDSL
 * 
 * @author krahn
 */
public class HelperGrammar {
  
  /**
   * The result is true iff ASTTerminal is iterated
   * 
   * @param a ASTConstantGroup to be evaluated
   * @return true iff ASTConstantGroup is iterated
   */
  public static boolean isIterated(ASTTerminal a) {
    return ((a.getIteration() == ASTConstantsGrammar.PLUS) || (a.getIteration() == ASTConstantsGrammar.STAR));
    
  }
  
  /**
   * The result is true iff ASTOrGroup is iterated
   * 
   * @param a ASTOrGroup to be evaluated
   * @return true iff ASTOrGroup is iterated
   */
  public static boolean isIterated(ASTBlock a) {
    return ((a.getIteration() == ASTConstantsGrammar.PLUS) || (a.getIteration() == ASTConstantsGrammar.STAR));
  }
    
  /**
   * Returns the name of a rule
   * 
   * @param a rule
   * @return Name of a rule
   */
  public static String getRuleName(ASTClassProd a) {
    return a.getName();
  }
  
  /**
   * Creates usuage name from a NtSym usually from its attribute or creates name
   * 
   * @param a
   * @return
   */
  
  public static String getUsuageName(ASTNonTerminal a) {
    
    String name;
    if (a.getUsageName().isPresent()) {
      name =a.getUsageName().get();
    }
    else {
      // Use Nonterminal name as attribute name starting with lower case
      // latter
      name = StringTransformations.uncapitalize(a.getName());
    }
     return name;
  }
  
  public static boolean isIterated(ASTNonTerminal a) {
    return ((a.getIteration() == ASTConstantsGrammar.PLUS) || (a.getIteration() == ASTConstantsGrammar.STAR));
  }
    
  public static String getTypeNameForEnum(String surroundtype, ASTConstantGroup a) {
    return "[enum." + surroundtype + "." + a.getUsageName();
  }
  
  /**
   * Creates the convert function for a lexrule
   *
   * @param a
   * @return
   */
  public static String createConvertFunction(ASTLexProd a, Grammar_WithConceptsPrettyPrinter prettyPrinter) {

    String name = a.getName();
    // simple String
    if (!a.getVariable().isPresent()) {
      return createStringConvertFunction(name);
    }

    // default functions
    else if (a.getType() == null || a.getType().isEmpty()) {
      String variable = a.getVariable().get();

      if ("int".equals(variable)) {
        String function = "private int convert%name%(Token t) {\n"

          + "  return Integer.parseInt(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("boolean".equals(variable)) {
        return createConvertFunction(
            name,
            "private boolean convert"
            + name
            + "(Token t) {\n"
            + "    if (t.getText().equals(\"1\")||t.getText().equals(\"start\")||t.getText().equals(\"on\")||t.getText().equals(\"true\")){return true;}else{return false;} \n"
            + "}\n");
      }
      else if ("byte".equals(variable)) {
        String function = "private byte convert%name%(Token t) {\n"
          + "  return Byte.parseByte(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("char".equals(variable)) {
        return createConvertFunction(name, "private char convert" + name + "(Token t) " + "{\n" + "  return t.getText().charAt(0); \n" + "}\n");
      }
      else if ("float".equals(variable)) {
        String function = "private float convert%name%(Token t) {\n"
          + "  return Float.parseFloat(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("double".equals(variable)) {
        String function = "private double convert%name%(Token t) {\n"
          + "  return Double.parseDouble(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("long".equals(variable)) {
        String function = "private long convert%name%(Token t) {\n"
          + "  return Long.parseLong(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("short".equals(variable)) {
        String function = "private short convert%name%(Token t) {\n"
          + "return Short.parseShort(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else if ("card".equals(variable)) {
        String function = "private int convert%name%(Token t) {\n"
          + "   if (t.getText().equals(\"*\")) return -1; else return Integer.parseInt(t.getText());\n"
          + " }\n";
        return createConvertFunction(name, function);
      }
      else {
        Log.warn("0xA1061 No function for " + a.getVariable() + " registered, will treat it as string!");
        return createStringConvertFunction(name);
      }

    }
    // specific function
    else {
      if (a.getBlock().isPresent()) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(prettyPrinter.prettyprint(a.getBlock().get()));
        String createConvertFunction = createConvertFunction(name, "private " + Names.getQualifiedName(a.getType()) + " convert" + name
         + "(Token " + a.getVariable().get() + ")" + " {\n" + buffer.toString() + "}\n");
        return createConvertFunction;
      }
    }
    return "";

  }



  private static String createConvertFunction(String name, String function) {
    String f = function.replaceAll("%name%", name);
    
    return "// convert function for " + name + "\n" + f;
    
  }
  
  public static String createStringConvertFunction(String name) {
    
    String t = "private String convert" + name + "(Token t)  {\n" + "    return t.getText();\n" + "}\n";
    
    return "// convert function for " + name + "\n" + t;
  }
  
  public static String createConvertType(ASTLexProd a) {
    
    if (!a.getVariable().isPresent()) {
      return "String";
    }
    String variable = a.getVariable().get();
    
    String name = a.getName();
    // simple String
    
    // default functions
    if (a.getType() == null || a.getType().isEmpty()) {
      
      if ("int".equals(variable) || "boolean".equals(variable) || "char".equals(variable) || "float".equals(variable) || "double".equals(variable)
          || "long".equals(variable) || "byte".equals(variable) || "short".equals(variable)) {
        return variable;
      }
      else if (variable.equals("card")) {
        return "int";
      }
      else {
        Log.warn("0xA1032 No function for " + a.getVariable() + " registered, will treat it as string!");
        return createStringConvertFunction(name);
      }
      
    }
    // specific function
    else {
      
      return Names.getQualifiedName(a.getType());
    }
  }
  
  /**
   * Printable representation of iteration
   * 
   * @param i Value from AST
   * @return String representing value i
   */
  public static String printIteration(int i) {
    switch (i) {
      case ASTConstantsGrammar.PLUS:
        return "+";
      case ASTConstantsGrammar.STAR:
        return "*";
      case ASTConstantsGrammar.QUESTION:
        return "?";
      default:
        return "";
    }
  }

  public static String printGenericType(ASTGenericType genericType) {
    
    StringBuilder b = new StringBuilder();
    
    b.append(Names.getQualifiedName(genericType.getNames()));
    
    boolean first = true;
    for (ASTGenericType t : genericType.getGenericTypes()) {
      if (first) {
        b.append("<");
        first = false;
      }
      else {
        b.append(",");
        
      }
      
      b.append(printGenericType(t));
    }
    
    if (!first) {
      b.append(">");
    }
    
    int dimension = genericType.getDimension();
    for (int i = dimension; i > 0; i--) {
      b.append("[]");
    }
    
    return b.toString();
  }
  
  public static boolean hasValidName(ASTConstant astConstant) {
    if (astConstant.getHumanName().isPresent()) {
      return true;
    }
    String constName = astConstant.getName();
    if (constName == null || constName.isEmpty()) {
      return false;
    }
    if (!matchesJavaIdentifier(constName) && LexNamer.createGoodName(constName) == null) {
      return false;
    }
    return true;
  }
  
  public static boolean matchesJavaIdentifier(String checkedString) {
    
    if (checkedString == null || checkedString.length() == 0) {
      return false;
    }
    char[] stringAsChars = checkedString.toCharArray();
    if (!Character.isJavaIdentifierStart(stringAsChars[0])) {
      return false;
    }
    for (int i = 1; i < stringAsChars.length; i++) {
      if (!Character.isJavaIdentifierPart(stringAsChars[i])) {
        return false;
      }
    }
    return true;
  }
  
  public static String getAttributeNameForConstant(ASTConstant astConstant) {
    String name;
    
    if (astConstant.getHumanName().isPresent()) {
      name = astConstant.getHumanName().get();
    }
    else {
      String constName = astConstant.getName();
      if (matchesJavaIdentifier(constName)) {
        name = constName;
      }
      else {
        name = LexNamer.createGoodName(constName);
        if (name == null) {
          name = constName;
        }
      }
    }
    return name;
    
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @param rulename rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(String rulename) {
    
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(rulename));
    
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(ASTClassProd rule) {
    
    String s = getRuleNameForAntlr(HelperGrammar.getRuleName(rule));
    
    return s;
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(ASTNonTerminal ntsym) {
    
    String s = getRuleNameForAntlr(ntsym.getName());
    
    return s;
  }

  public static List<String> findImplicitTypes(ASTLexActionOrPredicate action, Grammar_WithConceptsPrettyPrinter prettyPrinter) {
    List<String> ret = Lists.newArrayList();
    StringBuilder buffer = new StringBuilder();
    buffer.append(prettyPrinter.prettyprint(action.getExpressionPredicate()));
    
    String actionText = buffer.toString();
    if (actionText.contains("_ttype")) {
      String[] split = actionText.split("_ttype");
      
      for (int i = 1; i < split.length; i++) {
        String rest = split[i].trim();
        if (rest.length() > 1 && rest.startsWith("=")) {
          rest = rest.substring(1).trim();
          if (!rest.startsWith("Token")) {
            String string = rest.split("[ ;]")[0];
            ret.add(string);
          }
        }
      }
    }
    if (actionText.contains("$setType(")) {
      String[] split = actionText.split("[$]setType[(]");
      
      for (int i = 1; i < split.length; i++) {
        String rest = split[i].trim();
        if (rest.length() > 0) {
          
          if (!rest.startsWith("Token")) {
            String string = rest.split("[ )]")[0];
            ret.add(string);
          }
        }
      }
      
    }
    return ret;
  }
  
  
}
