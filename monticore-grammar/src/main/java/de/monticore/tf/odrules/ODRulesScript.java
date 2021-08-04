/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.Lists;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._parser.ODRulesParser;
import de.monticore.tf.odrules._symboltable.ODRulesScopesGenitorDelegator;
import de.monticore.tf.odrules.util.NameASTPair;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by
 *
 */
public class ODRulesScript  {

  static final String LOG_ID = "ODRules";


  /**
   * The default (Java) imports for within Groovy scripts.
   */
  public static final String[] DEFAULT_IMPORTS = {
          "de.monticore.tf.odrules._ast" };

  private static final String[] DEFAULT_STATIC_IMPORTS = {
          "de.se_rwth.commons.logging.Log"};


  public static List<NameASTPair> parseODRules(MCPath models){
    List<NameASTPair> result = Lists.newArrayList();

    Iterator<Path> modelsIt = models.getEntries().iterator();
    while (modelsIt.hasNext()) {
      Path it = modelsIt.next();
      Optional<ASTODRule> ast = parseODRule(it);
      if (!ast.isPresent()) {
        Log.error("Failed to parse " + it.toString());
      }
      else {
        // create symbol table
        ODRulesScopesGenitorDelegator symbolTable = ODRulesMill.scopesGenitorDelegator();
        symbolTable.createFromAST(ast.get());
        
        String name = it.toString().replace(".mtod", "");
        NameASTPair pair = new NameASTPair(name.substring(it.toString().lastIndexOf(File.separator)+1, name.length()), ast.get());
        result.add(pair);
      }
    }

    return result;
  }

  public static Optional<ASTODRule> parseODRule(Path model) {
    Log.info("Start parsing of the model " + model, LOG_ID);
    try {
      ODRulesParser parser = new ODRulesParser();
      Optional<ASTODRule> ast = parser.parse(model.toString());
      if (!parser.hasErrors() && ast.isPresent()) {
        Log.info("Model " + model + " parsed successfully", LOG_ID);
      } else {
        Log.info(
                "There are parsing errors while parsing of the model " + model, LOG_ID);
      }
      return ast;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void generate(ASTODRule ast, GlobalExtensionManagement glex, File outputDirectory, String filename){
    Log.info("Generate Transformation for " + filename, LOG_ID);
    ast.setName(filename);
    ODRuleCodeGenerator.generate(ast, outputDirectory);
  }




  /**
   * Executes the given Groovy script with the given
   *
   * @see Configuration
   * @param configuration of MontiCore for this execution
   */
  public void run( Configuration configuration) {
    ODRulesMill.init();

    ODRulesConfiguration odRulesConfig = ODRulesConfiguration.withConfiguration(configuration);
    // we add the configuration object as property with a special property
    // name

    Log.info("----- Starting Groovy Script -----", LOG_ID);

    Log.info("--------------------------------", LOG_ID);
    Log.info("ODRules", LOG_ID);


    Log.info("--------------------------------", LOG_ID);
    Log.info("Input files   : " + odRulesConfig.getModels(), LOG_ID);
    Log.info("Model path    : " + odRulesConfig.getModelPath(), LOG_ID);
    Log.info("Output dir    : " + odRulesConfig.getOut(), LOG_ID);
    Log.info("Grammar dir    : " + odRulesConfig.getGrammar(), LOG_ID);

//Parse Grammar

// Parse OD-Rules
    List<NameASTPair> astRules = parseODRules(odRulesConfig.getModels());

    for (NameASTPair rule : astRules) {
      GlobalExtensionManagement glex = new GlobalExtensionManagement();
      generate(rule.getAst(), glex, odRulesConfig.getOut(), rule.getName());
    }

  }
}
