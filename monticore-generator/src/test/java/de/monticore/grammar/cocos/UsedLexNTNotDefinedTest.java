/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

import static de.monticore.grammar.cocos.UsedLexNTNotDefined.ERROR_CODE;
import static de.se_rwth.commons.logging.Log.enableFailQuick;

public class UsedLexNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The lexical production A must not" +
          " use the nonterminal B because there exists no lexical production defining B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4016.A4016";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new UsedLexNTNotDefined());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testCorrect() {
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void test2() {
    test();
  }

  public Stringc test() {
    Stringc a;
    String y="y",x="j",c="e";

    y = x = c = 1+y +1   ;

    int i = 1;

    double h =  i + 1;
    System.out.println(h);
    Stringb b = new Stringb();

//    while( (a=readLine(file))!=null) {
//      print a;
//    }

      if((a=b) instanceof Stringb) {
        System.out.println("b");
      }

    if((a=b) instanceof Stringc) {
      System.out.println("c");
    }

    return (a=b);
  }

  private class Stringb extends Stringc {

  }


  private class Stringc {

  }

}
