/* (c) Monticore license: https://github.com/MontiCore/monticore */

import de.monticore.ast.ASTNode;
import de.monticore.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.mccommonliterals._ast.ASTNatLiteral;
import questionnaire._ast.*;
import questionnaire._visitor.*;

import java.util.Optional;

/**
 * Small pretty printer for questionnaires
 */
public class QuestionnaireTreePrinter implements
	QuestionnaireInheritanceVisitor
	/* see alternate behavior, then use: QuestionnaireVisitor */
{

  // These methods are only called because of the InheritanceVisitor
  //
  // interface ASTNode  (for all nodes)
  public void visit(ASTNode node) {
    depth++;
    print("[Nd"+depth+"|");
  }

  public void endVisit(ASTNode node) {
    print("|Nd"+depth+"]");
    depth--;
  }

  // interface ASTQuestionnaireNode  (for all nodes of Questionnaire grammar)
  public void visit(ASTQuestionnaireNode node) {
    print("[QN"+depth+"|");
  }

  public void endVisit(ASTQuestionnaireNode node) {
    print("|QN"+depth+"]");
  }

  // interface ASTLiteralsNode (for all nodes of Literals grammar)
  public void visit(ASTMCCommonLiteralsNode node) {
    print("[LN"+depth+"|");
  }

  public void endVisit(ASTMCCommonLiteralsNode node) {
    print("|LN"+depth+"]");
  }

  // interface ASTScaleType
  public void visit(ASTScaleType node) {
    print("[ST"+depth+"|");
  }

  public void endVisit(ASTScaleType node) {
    print("|ST"+depth+"]");
  }
      

  // additional function: helps the others ...
  public void print(String s) {
    System.out.print(s);
  }

  int depth = 0;
      
  public void visit(ASTQDefinition node) {
    print("<Q"+depth+">" +"questionnaire " + node.getName() + " {\n\n");
  }
      
  public void endVisit(ASTQDefinition node) {
    print("\n} </Q"+depth+">\n");
  }
      
  public void visit(ASTItem node) {
    print("<I"+depth+">" +"  item " + node.getName() +" \""+ node.getQuestion() + "\" ");
    Optional<String> scale = node.getScaleOpt();
    if(scale.isPresent()) {
      print(scale.get());
    }
  }
      
  public void endVisit(ASTItem node) {
    print("\n </I"+depth+">\n");
  }
      
  public void visit(ASTScale node) {
    print("<S"+depth+">" +"  scale " + node.getName() + " ");
  }
      
  public void endVisit(ASTScale node) {
    print("</S"+depth+">");
  }

  public void visit(ASTRange node) {
    String minTitle = node.getMinTitleOpt().orElse("");
    String maxTitle = node.getMaxTitleOpt().orElse("");
    print("<R"+depth+">" +"range  [" + minTitle +" "+ node.getMin().getValue() + " .. "
          + node.getMax().getValue() +" "+ maxTitle +"]\n" );
  }
      
  public void endVisit(ASTRange node) {
    print("</R"+depth+">");
  }

  public void visit(ASTNumber node) {
    print("<N"+depth+">" +"number");
  }
      
  public void endVisit(ASTNumber node) {
    print("</N"+depth+">");
  }
      
  public void visit(ASTText node) {
    if(node.isPresentMaxCharacters()) {
      print("<T"+depth+">" +"text " + node.getMaxCharacters().getValue());
    } else {
      print("text");
    }
  }
      
  public void endVisit(ASTText node) {
    print("</T"+depth+">");
  }

  public void visit(ASTSelect node) {
    print("<E"+depth+">" +"select { ");
  }
      
  public void endVisit(ASTSelect node) {
    print("} </E"+depth+">");
  }
      
  public void visit(ASTSelectOption node) {
    print("<O"+depth+">" +node.getId()+":"+node.getTitle()+" ");
  }

  public void endVisit(ASTSelectOption node) {
    print("</O"+depth+">");
  }

  public void visit(ASTNatLiteral node) {
    print("<L"+depth+"!"+node.getValue()+">");
  }

  public void endVisit(ASTNatLiteral node) {
    print("</L"+depth+">");
  }

} 
