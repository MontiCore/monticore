/* (c) https://github.com/MontiCore/monticore */

package ${package};

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import ${package}.cocos.AtLeastOneMyField;
import ${package}.cocos.ExistingMyFieldType;
import ${package}.cocos.MyDSLCoCos;
import ${package}.cocos.MyElementNameStartsWithCapitalLetter;
import ${package}.lang.MyDSLLanguage;
import ${package}.mydsl._ast.ASTMyModel;
import ${package}.mydsl._cocos.MyDSLCoCoChecker;
import ${package}.mydsl._parser.MyDSLParser;
import ${package}.prettyprint.PrettyPrinter;
import ${package}.symboltable.MyDSLSymbolTableCreator;
import ${package}.symboltable.MyElementSymbol;
import ${package}.visitors.CountMyElements;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Main class for the MyDSL tool.
 *
 */
public class MyDSLTool {
  
  /**
   * Use the single argument for specifying the single input model file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xC0004 Please specify only one single path to the input model.");
      return;
    }
    Log.enableFailQuick(false);
    Log.info("MyDSL Tool", MyDSLTool.class.getName());
    Log.info("----------", MyDSLTool.class.getName());
    String model = args[0];
    
    // setup the language infrastructure
    final MyDSLLanguage lang = new MyDSLLanguage();
    
    // parse the model and create the AST representation
    final ASTMyModel ast = parse(model, lang.getParser());
    Log.info(model + " parsed successfully!", MyDSLTool.class.getName());
    
    // setup the symbol table
    Scope modelTopScope = createSymbolTable(lang, ast);
    // can be used for resolving things in the model
    Optional<Symbol> aSymbol = modelTopScope.resolve("Car", MyElementSymbol.KIND);
    if (aSymbol.isPresent()) {
      Log.info("Resolved element symbol \"Car\"; FQN = " + aSymbol.get().toString(),
          MyDSLTool.class.getName());
    }
    
    // execute default context conditions
    runDefaultCoCos(ast);
    
    // execute a custom set of context conditions
    Log.info("Running customized set of context conditions", MyDSLTool.class.getName());
    MyDSLCoCoChecker customCoCos = new MyDSLCoCoChecker();
    customCoCos.addCoCo(new MyElementNameStartsWithCapitalLetter());
    customCoCos.checkAll(ast);
    
    // analyze the model with a visitor
    CountMyElements cs = new CountMyElements();
    cs.handle(ast);
    Log.info("The model contains " + cs.getCount() + " elements.", MyDSLTool.class.getName());
    
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed model into console:", MyDSLTool.class.getName());
    System.out.println(pp.getResult());
  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @param parser
   * @return
   */
  public static ASTMyModel parse(String model, MyDSLParser parser) {
    try {
      Optional<ASTMyModel> optModel = parser.parse(model);
      
      if (!parser.hasErrors() && optModel.isPresent()) {
        return optModel.get();
      }
      Log.error("0xC0005 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xC0006 Failed to parse " + model, e);
    }
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   * 
   * @param lang
   * @param ast
   * @return
   */
  public static Scope createSymbolTable(MyDSLLanguage lang, ASTMyModel ast) {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addTopScopeResolvers(lang.getResolvers());
    
    GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, resolvingConfiguration);
    
    Optional<MyDSLSymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
        resolvingConfiguration, globalScope);
    return symbolTable.get().createFromAST(ast);
  }
  
  /**
   * Run the default context conditions {@link AtLeastOneMyField},
   * {@link ExistingMyFieldType}, and {@link MyElementNameStartsWithCapitalLetter}.
   * 
   * @param ast
   */
  public static void runDefaultCoCos(ASTMyModel ast) {
    new MyDSLCoCos().getCheckerForAllCoCos().checkAll(ast);
  }
  
}
