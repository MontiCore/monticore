/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.commons.*;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.Names;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 */
public class NodeTreeDecoratedReporter extends AReporter {
  
  static final String INSTANTIATE_JAVA_CLASS = "inst";
  
  static final String GENERATES_FILE = "generates";
  
  static final String USED_TEMPLATE = "template";
  
  static final String TEMPLATE_HOOKPOINT = "THP";
  
  static final String SPECIFIC_TEMPLATE_HOOKPOINT = "ATHP";
  
  static final String SPECIFIC_STRING_HOOKPOINT = "ASHP";
  
  static final String SPECIFIC_CODE_HOOKPOINT = "ACHP";
  
  static final String SIMPLE_FILE_NAME = "11_NodeTreeDecorated";
  
  protected ReportingRepository repository;
  
  protected Map<String, Integer> nodeVisits;
  
  protected Map<String, List<String>> astNodeExtraInfos;
  
  protected List<String> serializedTreeResult;

  protected ITraverser traverser;

  protected TreePrintVisitor tpv;
  
  public NodeTreeDecoratedReporter(String outputDir, String modelName,
      ReportingRepository repository, ITraverser traverser) {
    super(outputDir
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
    nodeVisits = Maps.newHashMap();
    astNodeExtraInfos = Maps.newHashMap();
    serializedTreeResult = Lists.newArrayList();
    this.traverser = traverser;
    this.tpv = new TreePrintVisitor();
    tpv.setRepo(repository);
    traverser.add4IVisitor(tpv);
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Node Tree + Extra Infos");
  }
  
  protected void writeContent(ASTNode ast) {
    if (ast == null) {
      return;
    }
    
    deriveTreeStructureAST(ast);
    for (String s : serializedTreeResult) {
      writeLine(s);
    }
  }
  
  protected void writeFooter() {
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
   * @return
   */
  protected void deriveTreeStructureAST(ASTNode ast) {
    
    // this is a decoration of the tree at the lineend
    Map<String, String> endLineDecoration = Maps.newHashMap();
    
    for (Entry<String, Integer> entry : nodeVisits.entrySet()) {
      endLineDecoration.put(entry.getKey(), "(" + entry.getValue() + "x)");
    }

    tpv.clear();
    tpv.setRepo(repository);
    tpv.setEndLineDecoration(endLineDecoration);
    tpv.setAstNodeExtraInfos(astNodeExtraInfos);
    ast.accept(traverser);
     
    serializedTreeResult = tpv.getTreeResult();
    
  }
  
  protected void resetVariables() {
    nodeVisits.clear();
    astNodeExtraInfos.clear();
    serializedTreeResult.clear();
  }
  
  protected void callSpecificHPS(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      callSpecificHP(hp, ast);
    }
    
  }

  protected void callSpecificHP(HookPoint hp, ASTNode ast) {
    if (hp != null) {
      if (hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
        MapUtil.addToListMap(astNodeExtraInfos, aident, SPECIFIC_TEMPLATE_HOOKPOINT + " "
                + getHookPointValue(hp));
      }
    }
  }
  
  protected void callHPS(String oldTemplate, Collection<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      callHP(hp, ast);
    }
  }
  
  protected void callHP(HookPoint hp, ASTNode ast) {
      if (hp != null) {
        if (hp instanceof TemplateHookPoint) {
          String aident = compactStr(ast);
          MapUtil.incMapValue(nodeVisits, aident);
          MapUtil.addToListMap(astNodeExtraInfos, aident, TEMPLATE_HOOKPOINT + " "
              + getHookPointValue(hp));
        }
      }
    
  }
  
  protected String getHookPointValue(HookPoint hp) {
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
