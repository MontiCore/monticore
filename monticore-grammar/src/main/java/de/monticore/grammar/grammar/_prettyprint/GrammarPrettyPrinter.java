// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.grammar._prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.Names;

import java.util.Iterator;

public class GrammarPrettyPrinter extends GrammarPrettyPrinterTOP {
  public GrammarPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTMCGrammar node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (!node.getPackageList().isEmpty()) {
      getPrinter().print("package ");
      getPrinter().print(Names.constructQualifiedName(node.getPackageList()));
      getPrinter().print(";");
    }

    for (ASTMCImportStatement importStatement : node.getImportStatementList()) {
      importStatement.accept(getTraverser());
    }

    if (node.isPresentGrammarAnnotation()) {
      node.getGrammarAnnotation().accept(getTraverser());
    }

    if (node.isComponent()) {
      getPrinter().print("component ");
    }
    getPrinter().print("grammar ");
    getPrinter().print(node.getName());
    getPrinter().print(" ");

    if (!node.getSupergrammarList().isEmpty()) {
      getPrinter().print("extends ");
      printList(node.iteratorSupergrammar(), ",");
    }

    getPrinter().println("{");
    getPrinter().indent();

    if (node.isPresentGrammarOption()) {
      node.getGrammarOption().accept(getTraverser());
    }
    printList(node.iteratorLexProds(), "");
    printList(node.iteratorClassProds(), "");
    printList(node.iteratorEnumProds(), "");
    printList(node.iteratorExternalProds(), "");
    printList(node.iteratorInterfaceProds(), "");
    printList(node.iteratorAbstractProds(), "");
    printList(node.iteratorASTRules(), "");
    printList(node.iteratorSymbolRules(), "");
    if (node.isPresentScopeRule()) {
      node.getScopeRule().accept(getTraverser());
    }
    printList(node.iteratorConcepts(), "");
    if (node.isPresentStartRule()) {
      node.getStartRule().accept(getTraverser());
    }
    printList(node.iteratorSplitRules(), "");
    printList(node.iteratorKeywordRules(), "");
    printList(node.iteratorReplaceRules(), "");


    getPrinter().unindent();
    getPrinter().print("}");


    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
    getPrinter().println();
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTInterfaceProd node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    printList(node.iteratorGrammarAnnotations(), " ");
    getPrinter().print("interface ");
    printList(node.iteratorSymbolDefinitions(), " ");
    getPrinter().print(node.getName());
    getPrinter().print(" ");

    if (!node.isEmptySuperInterfaceRule()) {
      getPrinter().print("extends ");
      printList(node.iteratorSuperInterfaceRule(),", ");
    }
    if (!node.isEmptyASTSuperInterface()) {
      getPrinter().print("astextends ");
      printList(node.iteratorASTSuperInterface(),", ");
    }
    if (!node.isEmptyAlt()) {
      getPrinter().print("= ");
      printList(node.iteratorAlt(),"|");
    }
    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTAbstractProd node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    printList(node.iteratorGrammarAnnotations(), " ");
    getPrinter().print("abstract ");
    printList(node.iteratorSymbolDefinitions(), " ");
    getPrinter().print(node.getName());
    getPrinter().print(" ");

    if (!node.isEmptySuperRule()) {
      getPrinter().print("extends ");
      printList(node.iteratorSuperRule(),", ");
    }
    if (!node.isEmptySuperInterfaceRule()) {
      getPrinter().print("implements ");
      printList(node.iteratorSuperInterfaceRule(),", ");
    }
    if (!node.isEmptyASTSuperClass()) {
      getPrinter().print("astextends ");
      printList(node.iteratorASTSuperClass(),", ");
    }
    if (!node.isEmptyASTSuperInterface()) {
      getPrinter().print("astimplements ");
      printList(node.iteratorASTSuperInterface(),", ");
    }
    if (!node.isEmptyAlt()) {
      getPrinter().print("= ");
      printList(node.iteratorAlt(),"|");
    }
    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTClassProd node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    printList(node.iteratorGrammarAnnotations(), " ");
    printList(node.iteratorSymbolDefinitions(), " ");
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    getPrinter().print(" ");

    if (!node.isEmptySuperRule()) {
      getPrinter().print("extends ");
      printList(node.iteratorSuperRule(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptySuperInterfaceRule()) {
      getPrinter().print("implements ");
      printList(node.iteratorSuperInterfaceRule(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptyASTSuperClass()) {
      getPrinter().print("astextends ");
      printList(node.iteratorASTSuperClass(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptyASTSuperInterface()) {
      getPrinter().print("astimplements ");
      printList(node.iteratorASTSuperInterface(),", ");
      getPrinter().print(" ");
    }

    if (node.isPresentAction()) {
      getPrinter().print("{");
      node.getAction().accept(getTraverser());
      getPrinter().print("} ");
    }

    if (!node.isEmptyAlts()) {
      getPrinter().print("= ");
      printList(node.iteratorAlts(),"|");
    }
    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTASTRule node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("astrule ");
    getPrinter().print(node.getType());
    getPrinter().print(" ");

    if (!node.isEmptyASTSuperClass()) {
      getPrinter().print("astextends ");
      printList(node.iteratorASTSuperClass(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptyASTSuperInterface()) {
      getPrinter().print("astimplements ");
      printList(node.iteratorASTSuperInterface(),", ");
      getPrinter().print(" ");
    }

    if (!node.isEmptyGrammarMethods() || !node.isEmptyAdditionalAttributes()) {
      getPrinter().print("= ");
      printList(node.iteratorGrammarMethods(), " ");
      printList(node.iteratorAdditionalAttributes(), " ");
    }

    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTSymbolRule node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("symbolrule ");
    getPrinter().print(node.getType());
    getPrinter().print(" ");

    if (!node.isEmptySuperClass()) {
      getPrinter().print("extends  ");
      printList(node.iteratorSuperClass(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptySuperInterface()) {
      getPrinter().print("implements  ");
      printList(node.iteratorSuperInterface(),", ");
      getPrinter().print(" ");
    }

    if (!node.isEmptyGrammarMethods() || !node.isEmptyAdditionalAttributes()) {
      getPrinter().print("= ");
      printList(node.iteratorGrammarMethods(), " ");
      printList(node.iteratorAdditionalAttributes(), " ");
    }

    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.grammar.grammar._ast.ASTScopeRule node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("scoperule ");

    if (!node.isEmptySuperClass()) {
      getPrinter().print("extends  ");
      printList(node.iteratorSuperClass(),", ");
      getPrinter().print(" ");
    }
    if (!node.isEmptySuperInterface()) {
      getPrinter().print("implements  ");
      printList(node.iteratorSuperInterface(),", ");
      getPrinter().print(" ");
    }

    if (!node.isEmptyGrammarMethods() || !node.isEmptyAdditionalAttributes()) {
      getPrinter().print("= ");
      printList(node.iteratorGrammarMethods(), " ");
      printList(node.iteratorAdditionalAttributes(), " ");
    }

    getPrinter().println(";");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }

  }

  /**
   * Prints a list
   *
   * @param iter      iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = seperator;
    }
  }


}
