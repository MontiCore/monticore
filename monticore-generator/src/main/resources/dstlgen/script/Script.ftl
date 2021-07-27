<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "fileExtension", "noCoCoGen", "package")}
package ${package}.script;

import com.google.common.collect.Lists;
import de.monticore.io.paths.MCPath;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._symboltable.ODRulesGlobalScope;
import de.monticore.tf.odrules._symboltable.ODRulesScopesGenitorDelegator;
import de.monticore.tf.ruletranslation.Rule2ODState;
import de.monticore.tf.rule2od.Variable2AttributeMap;

import ${package}.${grammarNameLower}tr.${grammarName}TRMill;
import ${package}.${grammarNameLower}tr._visitor.${grammarName}TRTraverser;
import ${package}.${grammarNameLower}tr._ast.AST${grammarName}TFRule;
import ${package}.${grammarNameLower}tr._cocos.*;
import ${package}.${grammarNameLower}tr._parser.${ast.getName()}TRParser;
import ${package}.translation.${ast.getName()}Rule2OD;
import ${package}.translation.${ast.getName()}RuleCollectVariables;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.logging.Log;
import de.monticore.tf.odrules.ODRuleCodeGenerator;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.runtime.matching.ModelTraversal;
import de.monticore.tf.runtime.matching.ModelTraversalFactory;
import de.monticore.tf.script.DSTLConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.FileSystems;

public class ${className} {

  static final String LOG_ID = "${ast.getName()}TR";


 public static List<AST${grammarName}TFRule> parseRules(MCPath models) {
    ${ast.getName()}TRMill.init();

    List<AST${grammarName}TFRule> result = Lists.newArrayList();

    Iterator<Path> modelsIt = models.getEntries().iterator();
    while (modelsIt.hasNext()) {
      Path it = modelsIt.next();
      Optional<AST${grammarName}TFRule> ast = parseRule(it);
      if (!ast.isPresent()) {
        Log.error("Failed to parse " + it.toString());
      }
      else {
        if(!ast.get().getTFRule().isPresentName()) {
          String name = it.toString().replace(".${fileExtension}", "");
          ast.get().getTFRule().setName(name.substring(it.toString().lastIndexOf(File.separator) + 1, name.length()));
        }
        result.add(ast.get());

      }
    }

    return result;

  }

  public static ASTODRule createODRule(AST${grammarName}TFRule ast, MCPath modelPath) {
    Log.info("Starting odrule generation ", LOG_ID);
    ModelTraversal <${ast.getName()}TRTraverser> mt = ModelTraversalFactory.getInstance().create((java.util.function.Supplier)${ast.getName()}TRMill::inheritanceTraverser);
    ast.accept(mt.getTraverser());
    Log.info("Starting rule2odstate ", LOG_ID);
    Rule2ODState state = new Rule2ODState(new Variable2AttributeMap(), mt.getParents());
    state.getGenRule().setGrammarPackageList(de.se_rwth.commons.Splitters.DOT.splitToList("${ast.getPackageList()?join(".")}"));
    state.getGenRule().setGrammarName("${grammarName}");
    ${ast.getName()}RuleCollectVariables variables = new ${ast.getName()}RuleCollectVariables(state);
    ${ast.getName()}Rule2OD rule2OD = new ${ast.getName()}Rule2OD(state);

    Log.info("Switching to OD ", LOG_ID);
    // Switch language to OD
    ODRulesMill.init();

    ast.accept(variables.getTraverser());

    ast.accept(rule2OD.getTraverser());
    ASTODRule astod = rule2OD.getOD();
    astod.setVariables(variables.getCollectedVariables());


    // create symbol table
    ODRulesScopesGenitorDelegator symbolTable = ODRulesMill.scopesGenitorDelegator();
    symbolTable.createFromAST(astod);


    if(!ast.getTFRule().getPackageList().isEmpty()) {
        astod.setPackageList(new ArrayList<>(ast.getTFRule().getPackageList()));
    }
    astod.setMCImportStatementList(Lists.newArrayList(ast.getTFRule().getMCImportStatementList()));
    if(ast.getTFRule().isPresentName()){
      astod.setName(ast.getTFRule().getName());
    }

    return astod;
  }

  public static void checkCoCos(AST${grammarName}TFRule ast) {
   Log.info("Starting coco checking ", LOG_ID);
  <#if !noCoCoGen>
    TransCoCos.getCheckerForAllCoCos().checkAll(ast);
  </#if>
    Log.info("Finished coco checking ", LOG_ID);
  }

  public static Optional<AST${grammarName}TFRule> parseRule(Path model) {
    ${ast.getName()}TRMill.init();
    Log.info("Start parsing of the model " + model, LOG_ID);
    try {
      ${ast.getName()}TRParser parser = new ${ast.getName()}TRParser();
      Optional<AST${grammarName}TFRule> ast = parser.parse${grammarName}TFRule(model.toString());
      if (!parser.hasErrors() && ast.isPresent()) {
        Log.info("Model " + model + " parsed successfully", LOG_ID);
        String name = model.toString().replace(".${fileExtension}", "");
        ast.get().getTFRule().setName(name.substring(model.toString().lastIndexOf(File.separator) + 1, name.length()));
      }
      else {
        Log.info(
            "There are parsing errors while parsing of the model " + model, LOG_ID);
      }
      return ast;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void generate(ASTODRule ast, File outputDirectory) {
    Log.info("Generate Transformation for " + ast.getName(), LOG_ID);
    ODRuleCodeGenerator.generate(ast, outputDirectory);
  }


  // Note: this logic was previously done in a groovy script
  public void run(Configuration configuration) {
    ${ast.getName()}TRMill.init();
    DSTLConfiguration config = DSTLConfiguration.withConfiguration(configuration);

    Log.info("----- Starting Groovy Script -----", LOG_ID);
    Log.info("--------------------------------", LOG_ID);
    Log.info("DSTL to Java", LOG_ID);
    Log.info("--------------------------------", LOG_ID);
    Log.info("Input files   : " + config.getModels("mtr"), LOG_ID);
    Log.info("Model path    : " + config.getModelPath(), LOG_ID);
    Log.info("Output dir    : " + config.getOut(), LOG_ID);

    Iterator<Path> modelIterator = config.getModels("mtr").getResolvedPaths();
      while (modelIterator.hasNext()) {
      Path input = FileSystems.getDefault().getPath(modelIterator.next().toString()).normalize().toAbsolutePath();

      // Parse rule
      AST${grammarName}TFRule rule = parseRule(input).get();

      // check CoCos
      checkCoCos(rule);

      //create od rule
      ASTODRule odrule = createODRule(rule, config.getModelPath());

      // generate
      generate(odrule, config.getOut());
    }
  }
}
