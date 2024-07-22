/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

public abstract class TranslationTestCase {

  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUpTranslationTestCase() {
    Grammar_WithConceptsMill.reset();
    CD4CodeMill.reset();

    Grammar_WithConceptsMill.init();
    CD4CodeMill.init();
  }

}
