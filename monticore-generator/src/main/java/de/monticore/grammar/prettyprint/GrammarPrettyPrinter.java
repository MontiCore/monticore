/* (c) https://github.com/MontiCore/monticore */

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
import de.monticore.grammar.grammar._ast.ASTLexAnyChar;
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
import de.monticore.grammar.grammar._ast.ASTStartRule;
import de.monticore.grammar.grammar._ast.ASTSymbolDefinition;
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
    
    if (a.isPresentExpressionPredicate()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getExpressionPredicate().accept(getRealThis());
      getPrinter().unindent();
      print("}");
      print(" ?");
    }
    if (a.isPresentAction()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getAction().accept(getRealThis());
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
    
    printList(a.getSymbolDefinitionList().iterator(), " ");
    
    if (a.isPresentGenericType()) {
      getPrinter().print(" " + a.getGenericType().getTypeName());
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
    
    printList(a.getFollowOptionList().iterator(), "");
    
    getPrinter().unindent();
    print("}");
    
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    
    println();
    println();
    
  }
  
  @Override
  public void handle(ASTNonTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    
    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
    }
    
    print(a.getName());
    if (a.isPresentReferencedSymbol()) {
      print("@");
      print(a.getReferencedSymbol());
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
    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
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
    
    if (a.isPresentOption()) {
      print("options {");
      
      for (ASTOptionValue x : a.getOption().getOptionValueList()) {
        print(x.getKey() + "=" + x.getValue() + ";");
      }
      
      print("} ");
    }
    
    if (a.isPresentInitAction()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }
    
    if (a.isPresentInitAction() || a.isPresentOption()) {
      print(": ");
    }
    
    printList(a.getAltList().iterator(), "| ");
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
    if (a.isPresentHumanName()) {
      print(a.getHumanName() + ":");
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
    if (a.isPresentUsageName()) {
      print(a.getUsageName());
      print(":");
    }
    print("[");
    printList(a.getConstantList().iterator(), " | ");
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
    if (a.isRightAssoc()) {
      getPrinter().print(" <rightassoc> ");
    }
    printList(a.getComponentList().iterator(), " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  @Override
  public void handle(ASTInterfaceProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    
    print("interface ");
    print(a.getName());
    printList(a.getSymbolDefinitionList().iterator(), " ");
    
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      getPrinter().print(" extends ");
      String comma = "";
      for (ASTRuleReference x : a.getSuperInterfaceRuleList()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }
    
    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperInterfaceList()) {
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
    for (ASTConstant ref : a.getConstantList()) {
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
    print("astrule ");
    
    print(a.getType());
    
    if (!a.getASTSuperClassList().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperClassList()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }
    
    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astimplements ");
      String comma = "";
      for (ASTGenericType x : a.getASTSuperInterfaceList()) {
        getPrinter().print(comma);
        x.accept(getRealThis());
        comma = ", ";
      }
    }
    
    if (!a.getMethodList().isEmpty() || !a.getAttributeInASTList().isEmpty()) {
      
      println(" = ");
      getPrinter().indent();
      printList(a.getAttributeInASTList().iterator(), "");
      printList(a.getMethodList().iterator(), "");
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
    for (ASTMethodParameter x : a.getMethodParameterList()) {
      getPrinter().print(comma);
      getPrinter().print(x.getType() + " " + x.getName());
      comma = ", ";
    }
    
    print(")");
    
    if (!a.getExceptionList().isEmpty()) {
      
      print("throws ");
      comma = "";
      for (ASTGenericType x : a.getExceptionList()) {
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
    if (node.isPresentUsageName()) {
      getPrinter().print(node.getUsageName());
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
    
    if (a.isPresentName()) {
      getPrinter().print(a.getName());
    }
    getPrinter().print(":");
    a.getGenericType().accept(getRealThis());
    if (a.isPresentCard() && a.getCard().isPresentMin()) {
      print(" min = " + a.getCard().getMin());
    }
    if (a.isPresentCard() && a.getCard().isPresentMax()) {
      print(" max = " + a.getCard().getMax());
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
      
      if (!a.getSuperRuleList().isEmpty()) {
        getPrinter().print(" extends ");
        printList(a.getSuperRuleList().iterator(), " ");
      }
      
      if (!a.getSuperInterfaceRuleList().isEmpty()) {
        getPrinter().print(" implements ");
        printList(a.getSuperInterfaceRuleList().iterator(), ", ");
      }
      
      if (!a.getASTSuperClassList().isEmpty()) {
        getPrinter().print(" astextends ");
        printList(a.getASTSuperClassList().iterator(), "");
      }
      
      if (!a.getASTSuperInterfaceList().isEmpty()) {
        getPrinter().print(" astimplements ");
        printList(a.getASTSuperInterfaceList().iterator(), ", ");
      }
      
      if (a.isPresentAction()) {
        print(" {");
        getPrinter().println();
        getPrinter().indent();
        a.getAction().accept(getRealThis());
        getPrinter().unindent();
        print("}");
      }
      
      if (!a.getAltList().isEmpty()) {
        println(" =");
        
        getPrinter().indent();
        printList(a.getAltList().iterator(), " | ");
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
    
    if (a.isPresentLexOption()) {
      a.getLexOption().accept(getRealThis());
    }
    if (a.isPresentInitAction()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }
    
    getPrinter().print("=");
    
    printList(a.getAltList().iterator(), "");
    
    if (a.isPresentVariable()) {
      
      getPrinter().print(" : ");
      getPrinter().print(a.getVariable());
      
      if (!a.getTypeList().isEmpty()) {
        getPrinter().print("->");
        getPrinter().print(Names.getQualifiedName(a.getTypeList()));
        
        if (a.isPresentBlock() || a.isPresentEndAction()) {
          getPrinter().print(":");
          if (a.isPresentEndAction()) {
            print(" {");
            getPrinter().println();
            getPrinter().indent();
            a.getEndAction().accept(getRealThis());
            getPrinter().unindent();
            print("}");
          }
          if (a.isPresentBlock()) {
            a.getBlock().accept(getRealThis());
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
    if (a.isPresentOption()) {
      print("options {");
      print(a.getOption().getID() + "=" + a.getOption().getValue() + ";");
      print("} ");
    }
    
    if (a.isPresentInitAction()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getRealThis());
      getPrinter().unindent();
      print("}");
    }
    
    if (a.isPresentInitAction() || a.isPresentOption()) {
      print(": ");
    }
    
    printList(a.getLexAltList().iterator(), " | ");
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
    
    if (!a.getPackageList().isEmpty()) {
      print("package ");
      print(Names.getQualifiedName(a.getPackageList()));
      println(";");
    }
    
    println();
    if (a.isComponent()) {
      print("component ");
    }
    print("grammar " + a.getName());
    
    if (!a.getSupergrammarList().isEmpty()) {
      print(" extends ");
      String comma = "";
      for (ASTGrammarReference sgrammar : a.getSupergrammarList()) {
        print(comma + Names.getQualifiedName(sgrammar.getNameList()));
        comma = ", ";
      }
    }
    println(" {");
    getPrinter().indent();
    
    printList(a.getGrammarOptionList().iterator(), "");
    printList(a.getLexProdList().iterator(), "");
    printList(a.getClassProdList().iterator(), "");
    printList(a.getExternalProdList().iterator(), "");
    printList(a.getEnumProdList().iterator(), "");
    printList(a.getInterfaceProdList().iterator(), "");
    printList(a.getAbstractProdList().iterator(), "");
    printList(a.getASTRuleList().iterator(), "");
    printList(a.getConceptList().iterator(), "");
    
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
    for (ASTLexComponent c : a.getLexComponentList()) {
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
  public void handle(ASTLexAnyChar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    
    getPrinter().print(".");
    
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
    if (a.isPresentSemanticpredicateOrAction()) {
      a.getSemanticpredicateOrAction().accept(getRealThis());
    }
    getPrinter().print(a.getName());
    if (a.isPresentPrio()) {
      getPrinter().print(" <" + a.getPrio() + "> ");
    }
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
    printList(a.getSymbolDefinitionList().iterator(), " ");
    if (!a.getSuperRuleList().isEmpty()) {
      getPrinter().print("extends ");
      printList(a.getSuperRuleList().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      getPrinter().print("implements ");
      printList(a.getSuperInterfaceRuleList().iterator(), ", ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperClassList().isEmpty()) {
      getPrinter().print("astextends ");
      printList(a.getASTSuperClassList().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print("astimplements ");
      printList(a.getASTSuperInterfaceList().iterator(), ", ");
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
    for (String name : a.getNameList()) {
      print(sep);
      print(name);
      sep = ".";
    }
    if (!a.getGenericTypeList().isEmpty()) {
      print("<<");
      sep = "";
      for (ASTGenericType type : a.getGenericTypeList()) {
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
  
  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#handle(de.monticore.grammar.grammar._ast.ASTSymbolDefinition)
   */
  @Override
  public void handle(ASTSymbolDefinition node) {
    if (node.isGenSymbol()) {
      getPrinter().print(" symbol ");
      if (node.isPresentSymbolKind()) {
        getPrinter().print(node.getSymbolKind() + " ");
      }
    }
    if (node.isGenScope()) {
      getPrinter().print(" scope ");
    }
  }
  
  /**
   * @see de.monticore.grammar.grammar._visitor.GrammarVisitor#handle(de.monticore.grammar.grammar._ast.ASTStartRule)
   */
  @Override
  public void handle(ASTStartRule node) {
    getPrinter().println(" start " + node.getName() + ";");
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
