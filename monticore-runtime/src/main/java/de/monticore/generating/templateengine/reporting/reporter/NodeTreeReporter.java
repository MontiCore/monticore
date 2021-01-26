/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.commons.*;
import de.monticore.visitor.ITraverser;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 */
public class NodeTreeReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "10_NodeTree";
  
  private ReportingRepository repository;
  
  private Map<String, Integer> nodeVisits;
  
  private List<String> serializedTreeResult;

  private ITraverser traverser;

  private TreePrintVisitor tpv;
  
  public NodeTreeReporter(String outputDir,
      String modelName, ReportingRepository repository, ITraverser traverser) {
    super(outputDir + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
    nodeVisits = Maps.newHashMap();
    serializedTreeResult = Lists.newArrayList();
    this.traverser = traverser;
    tpv = new TreePrintVisitor();
    traverser.add4IVisitor(tpv);

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
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    String aident = compactStr(ast);
    MapUtil.incMapValue(nodeVisits, aident);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    for (HookPoint hp : afterHPs) {
      if (hp != null && hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    for (HookPoint hp : beforeHPs) {
      if (hp != null && hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      if (hp != null && hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    for (HookPoint hp : hps) {
      if (hp != null && hp instanceof TemplateHookPoint) {
        String aident = compactStr(ast);
        MapUtil.incMapValue(nodeVisits, aident);
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * de.monticore.generating.templateengine.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    if (hp != null && hp instanceof TemplateHookPoint) {
      String aident = compactStr(ast);
      MapUtil.incMapValue(nodeVisits, aident);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
    String aident = compactStr(ast);
    MapUtil.incMapValue(nodeVisits, aident);
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
  private void deriveTreeStructureAST(ASTNode ast) {
    
    // this is a decoration of the tree at the lineend
    Map<String, String> endLineDecoration = Maps.newHashMap();
    
    for (Entry<String, Integer> entry : nodeVisits.entrySet()) {
      endLineDecoration.put(entry.getKey(), "(" + entry.getValue() + "x)");
    }

    tpv.clear();
    tpv.setRepo(repository);
    tpv.setEndLineDecoration(endLineDecoration);
    ast.accept(traverser);
    serializedTreeResult = tpv.getTreeResult();
    
  }
  
  private void resetVariables() {
    nodeVisits.clear();
    serializedTreeResult.clear();
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
  
}
