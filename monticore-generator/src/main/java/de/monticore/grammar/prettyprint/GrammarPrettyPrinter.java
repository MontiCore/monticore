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

package de.monticore.grammar.prettyprint;

import java.util.Iterator;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTAnything;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConcept;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTEof;
import de.monticore.grammar.grammar._ast.ASTExternalProd;
import de.monticore.grammar.grammar._ast.ASTFollowOption;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._ast.ASTGrammarOption;
import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTLexActionOrPredicate;
import de.monticore.grammar.grammar._ast.ASTLexAlt;
import de.monticore.grammar.grammar._ast.ASTLexBlock;
import de.monticore.grammar.grammar._ast.ASTLexChar;
import de.monticore.grammar.grammar._ast.ASTLexCharRange;
import de.monticore.grammar.grammar._ast.ASTLexComponent;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexOption;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTLexSimpleIteration;
import de.monticore.grammar.grammar._ast.ASTLexString;
import de.monticore.grammar.grammar._ast.ASTMCAnything;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTOptionValue;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._ast.ASTSemanticpredicateOrAction;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._visitor.GrammarVisitor;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

public class GrammarPrettyPrinter extends LiteralsPrettyPrinterConcreteVisitor
    implements GrammarVisitor {

  private final String QUOTE = "\"";

  private GrammarVisitor realThis = this;

  public GrammarPrettyPrinter(IndentPrinter out) {
    super(out);
    out.setIndentLength(2);
  }

  @Override
  public void handle(ASTEof a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    print("EOF");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSemanticpredicateOrAction a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.getExpressionPredicate().isPresent()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getExpressionPredicate().get().accept(getRealThis());
      getPrinter().unindent();
      print("}");
      print(" ?");
    }
    if (a.getAction().isPresent()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getAction().get().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }

    getPrinter().print(" ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTExternalProd a) {

    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("external ");

    getPrinter().print(a.getName());
    if (a.getSymbolDefinition().isPresent()) {
      getPrinter().print("@!");
    }

    if (a.getGenericType().isPresent()) {
      getPrinter().print(" " + a.getGenericType().get().getTypeName());
    }
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();

  }

  @Override
  public void handle(ASTGrammarOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    println("options {");
    getPrinter().indent();

    printList(a.getFollowOptions().iterator(), "");

    getPrinter().unindent();
    print("}");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
    println();

  }

  @Override
  public void handle(ASTNonTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.getUsageName().isPresent()) {
      print("" + a.getUsageName().get() + ":");
    }

    print(a.getName());
    if (a.getReferencedSymbol().isPresent()) {
      print("@");
      print(a.getReferencedSymbol().get());
    }

    if (a.isPlusKeywords()) {
      print("& ");
    }

    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    // output("ASTTerminal Iteration " + a.getIteration());
    if (a.getUsageName().isPresent()) {
      print("" + a.getUsageName().get() + ":");
    }
    if (a.getVariableName().isPresent()) {
      print("" + a.getVariableName().get() + "=");
    }
    /* if (a.isKeyword()) { output("!" + QUOTE + a.getName() + QUOTE + " "); } else { */

    // Transfering to new version
    print("\"" + a.getName() + "\"");
    // }
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("(");

    if (a.getOption().isPresent()) {
      print("options {");

      for (ASTOptionValue x : a.getOption().get().getOptionValues()) {
        print(x.getKey() + "=" + x.getValue() + ";");
      }

      print("} ");
    }

    if (a.getInitAction().isPresent()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().get().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }

    if (a.getInitAction().isPresent() || a.getOption().isPresent()) {
      print(": ");
    }

    printList(a.getAlts().iterator(), "| ");
    print(")");
    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    getPrinter().optionalBreak();

  }

  /**
   * Visiting an ASTConcept #not sure for complete children methods
   *
   * @param a
   */
  @Override
  public void handle(ASTConcept a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    println("concept " + a.getName() + "{ ");

    a.getConcept().accept(getRealThis());

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    println("}");
  }

  /**
   * #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTConstant a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.getHumanName().isPresent()) {
      print(a.getHumanName().get() + ":");
    }

    print(QUOTE + a.getName() + QUOTE);

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * #complete children calls is usagename ever used??
   *
   * @param a
   */
  @Override
  public void handle(ASTConstantGroup a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.getUsageName().isPresent()) {
      print(a.getUsageName().get());
      print(":");
    }
    print("[");
    printList(a.getConstants().iterator(), " | ");
    print("]");
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTAlt a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printList(a.getComponents().iterator(), " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTInterfaceProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    print("interface ");
    print(a.getName());
    if (a.getSymbolDefinition().isPresent()) {
      getPrinter().print("@!");
    }

    if (!a.getSuperInterfaceRule().isEmpty()) {
      getPrinter().print(" extends ");
      String comma = "";
      for (ASTRuleReference x : a.getSuperInterfaceRule()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }

    if (!a.getASTSuperInterface().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperInterface()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }

    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTEnumProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("enum ");
    print(a.getName());

    getPrinter().print(" = ");
    String sep = "";
    for (ASTConstant ref : a.getConstants()) {
      print(sep);
      ref.accept(getRealThis());
      sep = " | ";
    }
    getPrinter().print(" ");

    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTASTRule a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("ast ");

    print(a.getType());

    if (!a.getASTSuperClass().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperClass()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }

    if (!a.getASTSuperInterface().isEmpty()) {
      getPrinter().print(" astimplements ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperInterface()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }

    if (!a.getMethods().isEmpty() || !a.getAttributeInASTs().isEmpty()) {

      println(" = ");
      getPrinter().indent();
      printList(a.getAttributeInASTs().iterator(), "");
      printList(a.getMethods().iterator(), "");
    }

    getPrinter().print(";");
    getPrinter().unindent();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTMethod a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("method ");

    if (a.isFinal()) {
      print("final ");
    }
    if (a.isStatic()) {
      print("static ");
    }
    if (a.isPrivate()) {
      print("private ");
    }
    if (a.isPublic()) {
      print("public ");
    }
    if (a.isProtected()) {
      print("protected ");
    }

    a.getReturnType().accept(getRealThis());

    print(" " + a.getName() + "(");

    String comma = "";
    for (ASTMethodParameter x : a.getMethodParameters()) {
      getPrinter().print(comma);
      getPrinter().print(x.getType() + " " + x.getName());
      comma = ", ";
    }

    print(")");

    if (!a.getExceptions().isEmpty()) {

      print("throws ");
      comma = "";
      for (ASTGenericType x : a.getExceptions()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }

    }

    // a.getBody());
    print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getBody().accept(getRealThis());
    getPrinter().unindent();
    print("}");


    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();

  }

  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#handle(de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator)
   */
  @Override
  public void handle(ASTNonTerminalSeparator node) {
    if (node.getUsageName().isPresent()) {
      getPrinter().print(node.getUsageName().get());
      getPrinter().print(":");
    }
    getPrinter().print(" (");
    getPrinter().print(node.getName());
    if (node.isPlusKeywords()) {
      getPrinter().print("&");
    }
    getPrinter().print(" || \"");
    getPrinter().print(node.getSeparator());
    getPrinter().print("\" )");
    outputIteration(node.getIteration());
  }

  @Override
  public void visit(ASTMethodParameter a) {
    a.accept(getRealThis());
    print(a.getName());
  }

  @Override
  public void handle(ASTAttributeInAST a) {

    if (a.getName().isPresent()) {
      getPrinter().print(a.getName().get());
    }
    getPrinter().print(":");
    if (a.isUnordered()) {
      getPrinter().print("<<unordered>> ");
    }
    a.getGenericType().accept(getRealThis());
    if (a.getCard().isPresent() && a.getCard().get().getMin().isPresent()) {
      print(" min = " + a.getCard().get().getMin().get());
    }
    if (a.getCard().isPresent() && a.getCard().get().getMax().isPresent()) {
      print(" max = " + a.getCard().get().getMax().get());
    }
    println();
  }

  /**
   * Visiting an ASTRule #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTClassProd a) {

    // Rules with names that start with MC are created by the symboltable
    // and are not pretty printeds
    if (!a.getName().startsWith("MC")) {

      CommentPrettyPrinter.printPreComments(a, getPrinter());

      getPrinter().print(a.getName());

      if (!a.getSuperRule().isEmpty()) {
        getPrinter().print(" extends ");
        printList(a.getSuperRule().iterator(), " ");
      }

      if (!a.getSuperInterfaceRule().isEmpty()) {
        getPrinter().print(" implements ");
        printList(a.getSuperInterfaceRule().iterator(), ", ");
      }

      if (!a.getASTSuperClass().isEmpty()) {
        getPrinter().print(" astextends ");
        printList(a.getASTSuperClass().iterator(), "");
      }

      if (!a.getASTSuperInterface().isEmpty()) {
        getPrinter().print(" astimplements ");
        printList(a.getASTSuperInterface().iterator(), ", ");
      }

      if (a.getAction().isPresent()) {
        print(" {");
        getPrinter().println();
        getPrinter().indent();
        a.getAction().get().accept(getRealThis());
        getPrinter().unindent();
        print("}");
      }

      if (!a.getAlts().isEmpty()) {
        println(" =");

        getPrinter().indent();
        printList(a.getAlts().iterator(), " | ");
      }
      println(";");

      CommentPrettyPrinter.printPostComments(a, getPrinter());
      getPrinter().unindent();
      getPrinter().println();
    }
  }

  /**
   * Visiting a LexRule #complete children calls
   *
   * @param a the LexRule
   */
  @Override
  public void handle(ASTLexProd a) {

    if (a.isFragment()) {
      print("fragment ");
    }
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("token ");

    println(a.getName());
    getPrinter().indent();

    if (a.getLexOption().isPresent()) {
      a.getLexOption().get().accept(getRealThis());
    }
    if (a.getInitAction().isPresent()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().get().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }

    getPrinter().print("=");

    printList(a.getAlts().iterator(), "");

    if (a.getVariable().isPresent()) {

      getPrinter().print(" : ");
      getPrinter().print(a.getVariable().get());

      if (!a.getType().isEmpty()) {
        getPrinter().print("->");
        getPrinter().print(Names.getQualifiedName(a.getType()));

        if (a.getBlock().isPresent() || a.getEndAction().isPresent()) {
          getPrinter().print(":");
          if (a.getEndAction().isPresent()) {
            print(" {");
            getPrinter().println();
            getPrinter().indent();
            a.getEndAction().get().accept(getRealThis());
            getPrinter().unindent();
            print("}");
          }
          if (a.getBlock().isPresent()) {
            a.getBlock().get().accept(getRealThis());
          }
        }

      }

    }
    print(";");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
    getPrinter().unindent();
    println();
  }

  @Override
  public void handle(ASTLexBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isNegate()) {
      getPrinter().print("~ ");
    }

    print("(");
    if (a.getOption().isPresent()) {
      print("options {");
      print(a.getOption().get().getID() + "=" + a.getOption().get().getValue() + ";");
      print("} ");
    }

    if (a.getInitAction().isPresent()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().get().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }

    if (a.getInitAction().isPresent() || a.getOption().isPresent()) {
      print(": ");
    }

    printList(a.getLexAlts().iterator(), " | ");
    print(")");
    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    getPrinter().optionalBreak();

  }

  /**
   * Visit method for the ASTGrammar (the root object) we have to use the handle method because
   * neither the visit/endVisit nor the traverseOrder merhods allow us to visit Packagename before
   * the AstGrammar itself #complete children calls
   *
   * @param a The ASTGrammar
   */
  @Override
  public void handle(ASTMCGrammar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (!a.getPackage().isEmpty()) {
      print("package ");
      print(Names.getQualifiedName(a.getPackage()));
      println(";");
    }

    println();
    if (a.isComponent()) {
      print("component ");
    }
    print("grammar " + a.getName());

    if (!a.getSupergrammar().isEmpty()) {
      print(" extends ");
      String comma = "";
      for (ASTGrammarReference sgrammar : a.getSupergrammar()) {
        print(comma + Names.getQualifiedName(sgrammar.getNames()));
        comma = ", ";
      }
    }
    println(" {");
    getPrinter().indent();

    if (a.getGrammarOptions().isPresent()) {
      a.getGrammarOptions().get().accept(getRealThis());
    }
    printList(a.getLexProds().iterator(), "");
    printList(a.getClassProds().iterator(), "");
    printList(a.getExternalProds().iterator(), "");
    printList(a.getEnumProds().iterator(), "");
    printList(a.getInterfaceProds().iterator(), "");
    printList(a.getAbstractProds().iterator(), "");
    printList(a.getASTRules().iterator(), "");
    printList(a.getConcepts().iterator(), "");

    getPrinter().unindent();
    print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
  }

  // helper fuctions


  /**
   * returns the right String for the Iteration value
   *
   * @param i .getIteration() value
   */
  private void outputIteration(int i) {
    if (i == ASTConstantsGrammar.QUESTION) {
      print("?");
    }
    else if (i == ASTConstantsGrammar.STAR) {
      print("*");
    }
    else if (i == ASTConstantsGrammar.PLUS) {
      print("+");
    }
    else {
      print("");
    }
  }

  private void print(String o) {
    getPrinter().print(o);
  }

  private void println(String o) {
    getPrinter().println(o);
  }

  private void println() {
    getPrinter().println();
  }

  @Override
  public void handle(ASTLexAlt a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    String sep = "";
    for (ASTLexComponent c : a.getLexComponents()) {
      print(sep);
      c.accept(getRealThis());
      sep = " ";
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLexChar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isNegate()) {
      getPrinter().print("~");
    }

    getPrinter().print("'" + a.getChar() + "'");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLexCharRange a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isNegate()) {
      getPrinter().print("~");
    }

    getPrinter().print("'" + a.getLowerChar() + "'..");
    getPrinter().print("'" + a.getUpperChar() + "' ");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTRuleReference a) {
    getPrinter().print(a.getName());
  }

  public void handle(ASTLexString a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print("\"" + a.getString() + "\"");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void endVisit(ASTLexSimpleIteration a) {
    outputIteration(a.getIteration());
  }

  @Override
  public void handle(ASTLexActionOrPredicate a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getExpressionPredicate().accept(getRealThis());
    getPrinter().unindent();
    print("}");

    if (a.isPredicate()) {
      print("?");
    }

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTLexNonTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print(a.getName());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTLexOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print("options ");

    getPrinter().print("{" + a.getID() + "=" + a.getValue() + ";}");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTAbstractProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print("abstract ");
    getPrinter().print(a.getName() + " ");
    if (a.getSymbolDefinition().isPresent()) {
      getPrinter().print("@!");
    }
    if (!a.getSuperRule().isEmpty()) {
      getPrinter().print("extends ");
      printList(a.getSuperRule().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getSuperInterfaceRule().isEmpty()) {
      getPrinter().print("implements ");
      printList(a.getSuperInterfaceRule().iterator(), ", ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperClass().isEmpty()) {
      getPrinter().print("astextends ");
      printList(a.getASTSuperClass().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperInterface().isEmpty()) {
      getPrinter().print("astimplements ");
      printList(a.getASTSuperInterface().iterator(), ", ");
      getPrinter().print(" ");
    }

    getPrinter().println(";");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
  }

  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#handle(de.monticore.grammar.grammar._ast.ASTGenericType)
   */
  @Override
  public void handle(ASTGenericType a) {
    String sep = "";
    for (String name : a.getNames()) {
      print(sep);
      print(name);
      sep = ".";
    }
    if (!a.getGenericTypes().isEmpty()) {
      print("<<");
      sep = "";
      for (ASTGenericType type : a.getGenericTypes()) {
        print(sep);
        type.accept(getRealThis());
        sep = ",";
      }
      print(">>");
    }
    for (int i = 0; i < a.getDimension(); i++) {
      print("[]");
    }
  }

  @Override
  public void handle(ASTFollowOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("follow " + a.getProdName() + " ");
    a.getAlt().accept(getRealThis());
    println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTAnything a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print(". ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTMCAnything a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("MCA ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  public String prettyprint(ASTGrammarNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#setRealThis(de.monticore.grammar.grammar._visitor.GrammarVisitor)
   */
  @Override
  public void setRealThis(GrammarVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#getRealThis()
   */
  @Override
  public GrammarVisitor getRealThis() {
    return realThis;
  }

  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTGrammarNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }



}
