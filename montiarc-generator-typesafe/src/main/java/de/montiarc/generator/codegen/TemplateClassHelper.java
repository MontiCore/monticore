/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import java.time.LocalDateTime;
import java.util.List;

import de.monticore.ast.Comment;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParameter;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.TypeReference;

/**
 * Common helper methods for generator.
 *
 * @author Robert Heim
 */
public class TemplateClassHelper {
  
  public static String getMontiArcVersion() {
    return "TODOMAVersion";
  }
  
  public static String getTimeNow() {
    return LocalDateTime.now().toString();
  }
  
  /**
   * @param comments comments that should be converted to a string.
   * @return the given comments as a string. Comment start- and end-markers
   * (e.g. '/**') are removed. '*' at the begining of a line are kept.
   */
  public static String getCommentAsString(List<Comment> comments) {
    StringBuilder sb = new StringBuilder();
    if (comments != null) {
      for (Comment c : comments) {
        sb.append(c.getText());
      }
    }
    String result = sb.toString().replace("/**", "").replace("/*", "").replace("*/", "");
    return result;
  }
  
//  public static String printType(ArcTypeReference typeRef) {
//    // return ArcTypePrinter.printType((ASTType) typeRef.getAstNode().get());
//    if (typeRef.getReferencedSymbol().isFormalTypeParameter()) {
//      return typeRef.getName();
//    }
//    return typeRef.getFullName();
//  }
  
  /**
   * TODO is there a helper in MC that can do it?<br/>
   * Recursively prints the type + its generics and the corresponding actual
   * type arguments.
   * 
   * @param type the type reference to print
   */
  public static String printType(TypeReference<? extends TypeSymbol> type) {
    StringBuilder sb = new StringBuilder();
    sb.append(type.getName());
    if (!type.getActualTypeArguments().isEmpty()) {
      String sep = "<";
      for (ActualTypeArgument subParam : type.getActualTypeArguments()) {
        sb.append(sep);
        sep = ", ";
        // recursively print the types arguments
        sb.append(printType(subParam.getType()));
      }
      sb.append(">");
    }
    return sb.toString();
  }
  
  /**
   * Checks whether an additional in-port for time is required. This is the case
   * when there are no incoming ports in a decomposed component with exactly 1
   * subcomponent. TODO improve doc
   * 
   * @param symbol
   * @return
   */
//  public static boolean requiresPortTimeIn(ComponentSymbol symbol) {
//    return symbol.getAllIncomingPorts().isEmpty() && symbol.isDecomposed()
//        && symbol.getSubComponents().size() == 1;
//  }
  
//  public static String printTypeParameters(ComponentSymbol symbol) {
//    ASTComponent a = ((ASTComponent) symbol.getAstNode().get());
//    return TypesPrinter.printTypeParameters(a.getHead().getGenericTypeParameters().get());
//  }
  
//  public static String printTypeParameters(ArcTypeSymbol symbol) {
//    if (!symbol.getAstNode().isPresent()) {
//      // TODO ?!
//      return "";
//    }
//    ASTNode n = symbol.getAstNode().get();
//    if (n instanceof ASTComponent) {
//      ASTComponent a = (ASTComponent) n;
//      return TypesPrinter.printTypeParameters(a.getHead().getGenericTypeParameters().get());
//    }
//    // TODO non-component types?
//    Log.error("non-component types are not yet fully supported for generic-bindings");
//    return "";
//  }
  
  /**
   * TODO replace this method with something from MC and do not store the
   * imports in ComponentSymbol anymore (see
   * {@link ComponentSymbol#setImports(List)}).
   * 
   * @param symbol
   * @return
   */
//  public static List<String> getImports(ComponentSymbol symbol) {
//    // TODO this probably should be implemented somewhere in MC
//    List<String> statements = new ArrayList<>();
//    for (ImportStatement is : symbol.getImports()) {
//      String i = is.getStatement() + (is.isStar() ? ".*" : "");
//      statements.add(i);
//    }
//    return statements;
//  }
  
  public static String printParameters(List<ASTParameter> parameters) {
    String ret = "";
    for (ASTParameter p : parameters) {
      ret += p.getParamType() + " " + p.getName() + ", ";
    }
    if (ret.contains(",")) {
      return ret.substring(0, ret.lastIndexOf(","));
    }
    return ret;
  }
  
  public static String printParameterNames(List<ASTParameter> parameters) {
    String ret = "";
    for (ASTParameter p : parameters) {
      ret += p.getName() + ", ";
    }
    if (ret.contains(",")) {
      return ret.substring(0, ret.lastIndexOf(","));
    }
    return ret;
  }
  
  public static String printSimpleName(String fqn) {
    if (fqn.contains(".")) {
      return fqn.substring(fqn.lastIndexOf("."));
    }
    return fqn;
  }
}
