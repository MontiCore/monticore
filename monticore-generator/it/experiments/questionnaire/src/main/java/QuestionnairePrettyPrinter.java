/* (c) Monticore license: https://github.com/MontiCore/monticore */

import java.util.*;
import questionnaire._ast.*;
import questionnaire._visitor.*;
import de.monticore.ast.ASTNode;

import java.io.IOException;
import org.antlr.v4.runtime.RecognitionException;

/**
 * Small pretty printer for questionnaires
 */
public class QuestionnairePrettyPrinter implements QuestionnaireVisitor {

  // additional function: helps the others ...
  public void print(String s) {
    System.out.print(s);
  }

  int count = 0;
  int countItem = 0;
      
  public void visit(ASTQDefinition node) {
    print("questionnaire " + node.getName() + " {\n\n");
    count++;
  }
      
  public void endVisit(ASTQDefinition node) {
    print("\n}\n");
    print("/* "+ count     + " nodes have been visited */}\n");
    print("/* "+ countItem + " items have been defined */}\n");
  }
      
  public void visit(ASTItem node) {
    print("  item " + node.getName() +" \""+ node.getQuestion() + "\" ");
    Optional<String> scale = node.getScaleOpt();
    if(scale.isPresent()) {
      print(scale.get());
    }
    count++;
    countItem++;
  }
      
  public void endVisit(ASTItem node) {
    print("\n");
  }
      
  public void visit(ASTScale node) {
    print("  scale " + node.getName() + " ");
    count++;
  }
      
  public void visit(ASTRange node) {
    String minTitle = node.getMinTitleOpt().orElse("");
    String maxTitle = node.getMaxTitleOpt().orElse("");
    print("range  [" + minTitle +" "+ node.getMin().getValue() + " .. "
          + node.getMax().getValue() +" "+ maxTitle +"]\n" );
    count++;
  }
      
  public void visit(ASTNumber node) {
    print("number");
    count++;
  }
      
  public void visit(ASTText node) {
    if(node.isPresentMaxCharacters()) {
      print("text " + node.getMaxCharacters().getValue());
    } else {
      print("text");
    }
    count++;
  }
      
  public void visit(ASTSelect node) {
    print("select { ");
    count++;
  }
      
  public void endVisit(ASTSelect node) {
    print("}");
  }
      
  public void visit(ASTSelectOption node) {
    print(node.getId()+":"+node.getTitle()+" ");
    count++;
  }

} 
