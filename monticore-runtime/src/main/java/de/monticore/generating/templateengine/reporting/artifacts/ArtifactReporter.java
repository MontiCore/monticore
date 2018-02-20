/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.artifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.artifacts.formatter.AFormatter;
import de.monticore.generating.templateengine.reporting.artifacts.model.Element;
import de.monticore.generating.templateengine.reporting.artifacts.model.ElementFactory;
import de.monticore.generating.templateengine.reporting.artifacts.model.ElementType;
import de.monticore.generating.templateengine.reporting.artifacts.model.RootPkg;
import de.monticore.generating.templateengine.reporting.commons.AReporter;

/**
 * Creates a dependency Graph based on a generator run. Dependency Graph can be printed via various
 * printers.
 *
 */
public class ArtifactReporter extends AReporter {

  ElementFactory factory = new ElementFactory();
  
  Stack<Element> elementStack = new Stack<Element>();
  
  /**
   * Base of the generated dependency graph
   */
  protected RootPkg rootPkg = new RootPkg();
  
  // Nam of the dotgraph file
  private AFormatter formatter;
  
  // Filters to use
  private List<ElementType> filters = new ArrayList<ElementType>();
  
  final static String SIMPLE_FILE_NAME = "Artifacts";  
  
  public ArtifactReporter(String path, String qualifiedFileName, String fileextension, AFormatter formatter, ElementType... filters) {
    super(path, qualifiedFileName, fileextension);
    this.formatter = formatter;
    this.addFilters(filters);
  }
  
  /**
   * Allow elements of type filter to be displayed. First invocation disables all other types
   */
  public void addFilter(ElementType filter) {
    this.filters.add(filter);
  }
  
  /**
   * Add multiple filters at once
   * 
   * @see addFilter
   */
  public void addFilters(ElementType... filters) {
    for (ElementType filter : filters) {
      this.addFilter(filter);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see mc.codegen.logging.GenLoggerDefaultClient#logModelStart(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void reportModelStart(ASTNode ast, String modelName, String fileName) {
    if (this.filters.isEmpty() || this.filters.contains(ElementType.MODEL)) {
      String extension = ReportingNameHelper.getSimpleName(fileName);
      Element e = rootPkg.resolve(ReportingNameHelper.getPath(modelName),
          ReportingNameHelper.getSimpleName(modelName), extension);
      if (e == null) {
        e = factory.createModel(rootPkg, modelName, extension);
      }
      count(e);
      elementStack.push(e);      
    }
  }
  
  /*
   * (non-Javadoc)
   * @see mc.codegen.logging.GenLoggerDefaultClient#logTemplateStart(java.lang.String ,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    if (this.filters.isEmpty() || this.filters.contains(ElementType.TEMPLATE)) {
      elementStack.pop();
    }
  }
  
  /*
   * (non-Javadoc)
   * @see mc.codegen.logging.GenLoggerDefaultClient#logTemplateStart(java.lang.String ,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    if (this.filters.isEmpty() || this.filters.contains(ElementType.TEMPLATE)) {
      Element e = handleTemplate(templatename);
      count(e);
      elementStack.push(e);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see mc.codegen.logging.GenLoggerDefaultClient#logFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename, String qualifiedfilename, String fileextension,
      ASTNode ast) {
    if (this.filters.isEmpty() || this.filters.contains(ElementType.FILE)) {
      /* this method is called when the parent template of the template with name templatename is on
       * top of the stack. Therefore the template with name templatename has to be handled first
       */
      Element template = handleTemplate(templatename);
      elementStack.push(template);
      Element e = rootPkg.resolve(ReportingNameHelper.getPath(qualifiedfilename),
          ReportingNameHelper.getSimpleName(qualifiedfilename), fileextension);
      if (e == null) {
        e = factory.createFile(rootPkg, qualifiedfilename, fileextension);
      }      
      createElementLink(e);
      count(e);
      elementStack.pop();      
    }
  }
  
  /**
   * @param templatename
   * @return 
   */
  private Element handleTemplate(String templatename) {
    String extension = "ftl";
    Element e = rootPkg.resolve(ReportingNameHelper.getPath(templatename),
        ReportingNameHelper.getSimpleName(templatename), extension);
    if (e == null) {
      e = factory.createTemplate(rootPkg, templatename, extension);
    }
    createElementLink(e);    
    return e;
  }
  
  /**
   * @see mc.codegen.logging.GenLoggerDefaultClient#logInstantiateStart(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    if (this.filters.isEmpty() || this.filters.contains(ElementType.HELPER)) {
      String extension = "java";
      Element e = rootPkg.resolve(ReportingNameHelper.getPath(className),
          ReportingNameHelper.getSimpleName(className), extension);
      if (e == null) {
        e = factory.createHelper(rootPkg, className, extension);
      }
      createElementLink(e);
      count(e);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see mc.codegen.logging.GenLoggerDefaultClient#finish()
   */
  @Override
  public void flush(ASTNode node) {    
    writeContent();
    resetVariables();
    super.flush(node);
  }
  
  private void resetVariables() {
    rootPkg = new RootPkg();
    elementStack.clear();
  }

  private void writeContent() {
    List<String> lines = formatter.getContent(rootPkg);
    for (String l : lines) {
      writeLine(l);
    }
  }

  /**
   * Creates a link from the top element of the stack to the given element. This only works if the
   * stack is not empty.
   * 
   * @param element
   */
  private void createElementLink(Element element) {
    if (!elementStack.isEmpty()) {
      elementStack.peek().addlink(element);
    }
  }
  
  public void count(Element element) {
    element.incCalls();
    if (!elementStack.isEmpty()) {
      Element source = elementStack.peek();
      source.incLinkCalls(element);
    }
  }

  /**
   * @see mc.codegen.reporting.commons.AReporter#writeHeader()
   */
  @Override
  protected void writeHeader() {
    // Write empty header
  }
  
}
