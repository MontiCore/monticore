/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.grammar.concepts.antlr.antlr._ast.AntlrResourceController;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.GrammarResourceController;
import de.monticore.grammar.grammar_withconcepts._ast.Grammar_WithConceptsPackage;
import de.monticore.grammar.grammar_withconcepts._ast.Grammar_WithConceptsResourceController;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.java.javadsl._ast.JavaDSLResourceController;
import de.monticore.lexicals.lexicals._ast.LexicalsResourceController;
import de.monticore.literals.literals._ast.LiteralsResourceController;
import de.monticore.types.types._ast.TypesResourceController;
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
public class TestGrammarEcore {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMCGrammar() {
     try {
       
       AST2ModelFiles res = AST2ModelFiles.get();
     
   
      LexicalsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      LiteralsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      TypesResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      JavaDSLResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      AntlrResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      GrammarResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      res.serializeAST(Grammar_WithConceptsPackage.eINSTANCE);
      
      
      String path1 = "de/monticore/emf/Automaton.mc4";
      Optional<ASTMCGrammar> transB = new Grammar_WithConceptsParser().parse("src/test/resources/" + path1);
      assertTrue(transB.isPresent());
      MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/Automaton_mc4");
    }
    catch (RecognitionException | IOException e) {
      fail(e.getMessage());
    }
  }
}
