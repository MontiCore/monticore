/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.util;

import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._prettyprint.GrammarPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

/**
 * Created by
 *
 */
public class DSTLPrettyPrinter extends GrammarPrettyPrinter {

  IndentPrinter out;

  public DSTLPrettyPrinter(IndentPrinter out) {
    super(out, true);
    this.out = out;
  }

  /**
   * Visiting an ASTRule #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTClassProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    
    printList(a.iteratorGrammarAnnotations(), " ");
    getPrinter().println();
    getPrinter().print(a.getName());
    getPrinter().print(" ");
    
    if (!a.isEmptySuperRule()) {
      getPrinter().print("extends ");
      printList(a.iteratorSuperRule(), ", ");
    }
    if (!a.isEmptySuperInterfaceRule()) {
      getPrinter().print("implements ");
      printList(a.iteratorSuperInterfaceRule(), ", ");
    }
    if (!a.isEmptyASTSuperClass()) {
      getPrinter().print("astextends ");
      printList(a.iteratorASTSuperClass(), ", ");
    }
    if (!a.isEmptyASTSuperInterface()) {
      getPrinter().print("astimplements ");
      printList(a.iteratorASTSuperInterface(), ", ");
    }
    
    if (a.isPresentAction()) {
      getPrinter().print("{");
      getPrinter().println();
      getPrinter().indent();
      a.getAction().accept(getTraverser());
      getPrinter().unindent();
      getPrinter().print("} ");
    }
    
    if (!a.isEmptyAlts()) {
      getPrinter().print("=");
      getPrinter().println();
      getPrinter().indent();
      printList(a.iteratorAlts(), "| ");
    }
    getPrinter().println(";");
    
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().unindent();
    getPrinter().println();
  }

  @Override
  public void handle(ASTAdditionalAttribute a) {

    if (a.isPresentName()) {
      getPrinter().print(a.getName());
    }
    getPrinter().print(":");
    /*if (a.isUnordered()) {
      getPrinter().print("<<unordered>> ");
    }*/
    a.getMCType().accept(getTraverser());
//    if(a.isPresentCard() && a.getCard().isUnbounded()){
//      print("*");
//    }
    if (a.isPresentCard() && a.getCard().isPresentMin()) {
      getPrinter().print(" min = " + a.getCard().getMin());
    }
    if (a.isPresentCard() && a.getCard().isPresentMax()) {
      getPrinter().print(" max = " + a.getCard().getMax());
    }
    getPrinter().println();
  }

  @Override
  public void handle(ASTLexProd a) {
    if (a.isFragment()) {
      getPrinter().print("fragment ");
    }
    
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("token ");
    getPrinter().println(a.getName());
    getPrinter().indent();
    if (a.isPresentLexOption()) {
      a.getLexOption().accept(this.getTraverser());
    }
    
    if (a.isPresentInitAction()) {
      getPrinter().print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(this.getTraverser());
      getPrinter().unindent();
      getPrinter().print("}");
    }
    
    getPrinter().print("=");
    printList(a.getAltList().iterator(), "|");
    if (a.isPresentVariable() || a.isPresentEndAction()) {
      getPrinter().print(" : ");
      if (a.isPresentEndAction()) {
        getPrinter().print(" { ");
        a.getEndAction().accept(getTraverser());
        getPrinter().print(" } ");
      }
      if (a.isPresentVariable()) {
        getPrinter().print(a.getVariable());
        if (!a.getTypeList().isEmpty()) {
          getPrinter().print("->");
          getPrinter().print(Names.getQualifiedName(a.getTypeList()));
          if (a.isPresentBlock() || a.isPresentEndAction()) {
            getPrinter().print(":");
            if (a.isPresentEndAction()) {
              getPrinter().print(" {");
              getPrinter().println();
              getPrinter().indent();
              a.getEndAction().accept(this.getTraverser());
              getPrinter().unindent();
              getPrinter().print("}");
            }

            if (a.isPresentBlock()) {
              a.getBlock().accept(this.getTraverser());
            }
          }
        }
      }
    }
    
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().unindent();
    getPrinter().println();
  }
}
