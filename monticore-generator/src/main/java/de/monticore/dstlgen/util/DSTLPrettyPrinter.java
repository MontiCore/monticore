/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.util;

import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.prettyprint.GrammarPrettyPrinter;
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
    super(out);
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
      
      getPrinter().print(a.getName());
      
      if (!a.getSuperRuleList().isEmpty()) {
        getPrinter().print(" extends ");
        printList(a.getSuperRuleList().iterator(), ", ");
      }
      
      if (!a.getSuperInterfaceRuleList().isEmpty()) {
        getPrinter().print(" implements ");
        printList(a.getSuperInterfaceRuleList().iterator(), ", ");
      }
      
      if (!a.getASTSuperClassList().isEmpty()) {
        getPrinter().print(" astextends ");
        printMCSimpleGenericList(a.getASTSuperClassList().iterator(), "");
      }
      
      if (!a.getASTSuperInterfaceList().isEmpty()) {
        getPrinter().print(" astimplements ");
        printMCSimpleGenericList(a.getASTSuperInterfaceList().iterator(), ", ");
      }
      
      if (a.isPresentAction()) {
        print(" {");
        getPrinter().println();
        getPrinter().indent();
        a.getAction().accept(getTraverser());
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
      print(" min = " + a.getCard().getMin());
    }
    if (a.isPresentCard() && a.getCard().isPresentMax()) {
      print(" max = " + a.getCard().getMax());
    }
    println();
  }

  @Override
  public void handle(ASTLexProd a) {
    if (a.isFragment()) {
      this.print("fragment ");
    }
    
    CommentPrettyPrinter.printPreComments(a, this.getPrinter());
    this.print("token ");
    this.println(a.getName());
    this.getPrinter().indent();
    if (a.isPresentLexOption()) {
      a.getLexOption().accept(this.getTraverser());
    }
    
    if (a.isPresentInitAction()) {
      this.print(" {");
      this.getPrinter().println();
      this.getPrinter().indent();
      a.getInitAction().accept(this.getTraverser());
      this.getPrinter().unindent();
      this.print("}");
    }
    
    this.getPrinter().print("=");
    this.printList(a.getAltList().iterator(), "|");
    if (a.isPresentVariable()) {
      this.getPrinter().print(" : ");
      this.getPrinter().print(a.getVariable());
      if (!a.getTypeList().isEmpty()) {
        this.getPrinter().print("->");
        this.getPrinter().print(Names.getQualifiedName(a.getTypeList()));
        if (a.isPresentBlock() || a.isPresentEndAction()) {
          this.getPrinter().print(":");
          if (a.isPresentEndAction()) {
            this.print(" {");
            this.getPrinter().println();
            this.getPrinter().indent();
            a.getEndAction().accept(this.getTraverser());
            this.getPrinter().unindent();
            this.print("}");
          }
          
          if (a.isPresentBlock()) {
            a.getBlock().accept(this.getTraverser());
          }
        }
      }
    }
    
    this.print(";");
    CommentPrettyPrinter.printPostComments(a, this.getPrinter());
    this.println();
    this.getPrinter().unindent();
    this.println();
  }
  private void print(String o) {
    getPrinter().print(o);
  }

  
  private void println(String o) {
    this.getPrinter().println(o);
  }
  
  private void println() {
    this.getPrinter().println();
  }


}
