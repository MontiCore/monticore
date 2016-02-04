/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf._ast.ASTENodePackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class ASTNodeTest {
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * TODO: Write me!
   * 
   * @param args
   */
  @Test
  public  void test() {
     
    AST2ModelFiles.get().serializeAST(ASTENodePackage.eINSTANCE);
      
//      String path1 = "de/monticore/emf/Automaton.mc4";
//      String path2 = "de/monticore/emf/Automaton2.mc4";
//      Optional<ASTMCGrammar> transB = new Grammar_WithConceptsParser().parse("src/test/resources/" + path1);
//      if (transB.isPresent()) {
//        MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/Automaton_mc4");
//      }
//      else {
//        System.err.println("Missed");
//      }
//      
//      Optional<ASTMCGrammar> transC = new Grammar_WithConceptsParser().parse("src/test/resources/" + path2);
//      if (transB.isPresent()) {
//        MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/Automaton2_mc4");
//      }
//      else {
//        System.err.println("Missed");
//      }
      
  }
  
}
