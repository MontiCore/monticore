/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.AmbiguityException;
import de.monticore.cli.MontiCoreTool;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.mcbasics.MCBasicsMill;
import de.se_rwth.commons.io.SyncDeIsolated;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;

/**
 * This class contains a method which actually calls the {@link MontiCoreTool}.
 * It will be called by either the {@link MCToolAction} or the {@link MCGenTask}
 * itself, if debugging is enabled.
 * We isolate the method into its own class, to enable classloader isolation.
 */
public class MCToolInvoker {

  /**
   * Run the MontiCore Tool with arguments,
   *  and clean up afterward.
   * @param args CLI arguments passed to MontiCore
   */
  public static void run(String[] args) {
    Log.info("Starting MontiCoreTool: " +
            "\t  java -jar MontiCoreTool.jar " + Arrays.toString(args), MCToolInvoker.class.getName());
    SyncDeIsolated.run(() -> {
      // The MCTool is expensive with its the class loading (time)
      // individual class loading is blocking parallel execution at ZipFile$Source.readFullyAt,
      // resulting in blocked mc-tool-"main" threads, inducing extra overhead somewhere in the JVM
      // due to switching threads all trying to access the same .jar file.
      // We thus preload some classes in a synchronized block (its monitor is being shared across isolation borders)
      MontiCoreTool.preLoad();
    });
    try {
      MontiCoreTool.main(args);
    }catch (final AmbiguityException | MontiCoreFreeMarkerException e) {
      RuntimeException newThrow = e;
      if (e.getCause() instanceof AmbiguityException) { // Freemarker adds special Freemarker Exceptions
        newThrow = new RuntimeException("This might be a gradle-setup problem. Are the MontiCore-Plugin and UMLP-Plugin loaded in the same project? \n" +
                "Gradle doesn't isolate plugins, so resources are found twice. \n\n", e);
      }
      throw newThrow;
    } finally {
      MCGrammarSymbolTableHelper.cleanUp();
      MCBasicsMill.globalScope().clear();
      MCBasicsMill.globalScope().clearLoadedFiles();
      MCBasicsMill.globalScope().getSymbolPath().close();
      Grammar_WithConceptsMill.reset();
    }
  }
}
