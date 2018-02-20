/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.MapUtil;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.commons.TreePrintVisitor;
import de.se_rwth.commons.Names;

/**
 */
public class NodeTreeDecoratedReporter extends AReporter {
  
  final static String INSTANTIATE_JAVA_CLASS = "inst";
  
  final static String GENERATES_FILE = "generates";
  
  final static String USED_TEMPLATE = "template";
  
  final static String TEMPLATE_HOOKPOINT = "THP";
  
  final static String SPECIFIC_TEMPLATE_HOOKPOINT = "ATHP";
  
  final static String SPECIFIC_STRING_HOOKPOINT = "ASHP";
  
  final static String SPECIFIC_CODE_HOOKPOINT = "ACHP";
  
  final static String SIMPLE_FILE_NAME = "11_NodeTreeDecorated";
  
  private ReportingRepository repository;
  
  private Map<String, Integer> nodeVisits;
  
  private Map<String, List<String>> astNodeExtraInfos;
  
  private List<String> serializedTreeResult;
  
  public NodeTreeDecoratedReporter(String outputDir, String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
    nodeVisits = Maps.newHashMap();
    astNodeExtraInfos = Maps.newHashMap();
    serializedTreeResult = Lists.newArrayList();
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Node Tree + Extra Infos");
  }
  
  private void writeContent(ASTNode ast) {
    if (ast == null) {
      return;
    }
    
    deriveTreeStructureAST(ast);
    for (String s : serializedTreeResult) {
      writeLine(s);
    }
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Node Tree: this is the extended form: one with extra infos");
    writeLine("as sublines. The tree itself lists all AST nodes using their identifiers.");
    writeLine("Vertical line: list all the direct children of a node.");
    writeLine("Each node knows about:");
    writeLine("- #Visits through templates (in the raw version) looks like: (2x)");
    writeLine("- files generated originating from this node");
    writeLine("- templates called on this node");
    writeLine("Remark: a \"null\" as value in the tree means that the tree has been altered");
    writeLine("after parsing. --> this is not yet reflected in the protocol");
    writeLine("(EOF)");
  }
  
  // @Override
  // public void reportExecuteStandardTemplate(String templatename, ASTNode ast)
  // {
  // String aident = compactStr(ast);
  // MapUtil.incMapValue(nodeVisits, aident);
  // MapUtil.addToListMap(astNodeExtraInfos, aident, USED_TEMPLATE + " "
  // + ReportingHelper.getTemplateName(templatename));
  //
  // }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * de.monticore.generating.templateengine.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    callHP(hp, ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportTemplateStart(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    String aident = compactStr(ast);
    MapUtil.incMapValue(nodeVisits, aident);
    MapUtil.addToListMap(astNodeExtraInfos, aident, USED_TEMPLATE + " "
        + ReportingHelper.getTemplateName(templatename));
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String aident = compactStr(ast);
    if (qualifiedfilename != null) {
      qualifiedfilename = Names.getSimpleName(qualifiedfilename);
    }
    MapUtil.incMapValue(nodeVisits, aident);
    MapUtil.addToListMap(astNodeExtraInfos, aident, GENERATES_FILE + " \""
        + qualifiedfilename + "." + fileextension + "\"");
  }
  
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    callHPS(oldTemplate, afterHPs, ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    callHPS(oldTemplate, beforeHPs, ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    callHPS(oldTemplate, hps, ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    callSpecificHPS(oldTemplate, hps, ast);
  }
  
  /**
   * Uses ast2idents to print a compact version of the ASTNode
   * 
   * @param ast
   * @return
   */
  public String compactStr(ASTNode ast) {
    return repository.getASTNodeNameFormatted(ast);
  }
  
  /**
   * derive the tree structure of the AST using the ast idents + some decoration
   * coming from
   * 
   * @param ast
   * @param ast2idents
   * @return
   */
  private void deriveTreeStructureAST(ASTNode ast) {
    
    // this is a decoration of the tree at the lineend
    Map<String, String> endLineDecoration = Maps.newHashMap();
    
    for (Entry<String, Integer> entry : nodeVisits.entrySet()) {
      endLineDecoration.put(entry.getKey(), "(" + entry.getValue() + "x)");
    }
    
    TreePrintVisitor tpv = new TreePrintVisitor(repository,
        endLineDecoration, astNodeExtraInfos);
    tpv.handle(ast);
     
    serializedTreeResult = tpv.getTreeResult();
    
  }
  
  private void resetVariables() {
    nodeVisits.clear();
    astNodeExtraInfos.clear();
    serializedTreeResult.clear();
  }
  
  private void callSpecificHPS(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      callSpecificHP(hp, ast);
    }
    
  }
  
  private void callSpecificHP(HookPoint hp, ASTNode ast) {
    if (hp != null) {
//      if (hp instanceof StringHookPoint) {
//        String aident = compactStr(ast);
//        MapUtil.incMapValue(nodeVisits, aident);
//        MapUtil.addToListMap(astNodeExtraInfos, aident, SPECIFIC_STRING_HOOKPOINT + " "
//            + getHookPointValue(hp));
//      }
//      else if (hp instanceof CodeHookPoint) {
//        String aident = compactStr(ast);
//        MapUtil.incMapValue(nodeVisits, aident);
//        MapUtil.addToListMap(astNodeExtraInfos, aident, SPECIFIC_CODE_HOOKPOINT + " "
//            + getHookPointValue(hp));
//      }
//      else 
        if (hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
        MapUtil.addToListMap(astNodeExtraInfos, aident, SPECIFIC_TEMPLATE_HOOKPOINT + " "
            + getHookPointValue(hp));
      }
    }
  }
  
  private void callHPS(String oldTemplate, Collection<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      callHP(hp, ast);
    }
  }
  
  private void callHP(HookPoint hp, ASTNode ast) {
      if (hp != null) {
        if (hp instanceof TemplateHookPoint) {
          String aident = compactStr(ast);
          MapUtil.incMapValue(nodeVisits, aident);
          MapUtil.addToListMap(astNodeExtraInfos, aident, TEMPLATE_HOOKPOINT + " "
              + getHookPointValue(hp));
        }
      }
    
  }
  
  private String getHookPointValue(HookPoint hp) {
    String value = null;
    if (hp != null && hp instanceof TemplateHookPoint) {
      value = ((TemplateHookPoint) hp).getTemplateName();
      value = ReportingHelper.getTemplateName(value);
    }
    else if (hp != null && hp instanceof StringHookPoint) {
      value = ((StringHookPoint) hp).getValue();
      value = ReportingHelper.formatStringToReportingString(value, 50);
    }
    else if (hp != null && hp instanceof CodeHookPoint) {
      value = ((CodeHookPoint) hp).getClass().getName();
      value = Names.getSimpleName(value);
    }
    return value;
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
  
}
