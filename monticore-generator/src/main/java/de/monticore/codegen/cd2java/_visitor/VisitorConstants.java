/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

public final class VisitorConstants {

  public static final String VISITOR_PACKAGE = "_visitor";

  public static final String VISITOR_SUFFIX = "Visitor";

  public static final String INHERITANCE_SUFFIX = "Inheritance";

  public static final String TRAVERSER_SUFFIX = "Traverser";
  
  public static final String TRAVERSER_CLASS_SUFFIX = "TraverserImplementation";
  
  public static final String VISITOR2_SUFFIX = "Visitor2";
  
  public static final String HANDLER_SUFFIX = "Handler";

  public static final String VISITOR_PREFIX = "visitor";

  public static final String TRAVERSER = "traverser";

  public static final String INHERITANCE_TRAVERSER = "inheritanceTraverser";

  public static final String GET_TRAVERSER = "getTraverser";

  public static final String SET_TRAVERSER = "setTraverser";
  
  public static final String REAL_THIS = "realThis";

  public static final String VISIT = "visit";

  public static final String END_VISIT = "endVisit";

  public static final String HANDLE = "handle";

  public static final String TRAVERSE = "traverse";

  public static final String TRAVERSED_ELEMS_NAME = "traversedElements";

  public static final String TRAVERSED_ELEMS_TYPE = "Set<Object>";

  public static final String TRAVERSED_ELEMS_INSTANCE_TYPE = "HashSet<Object>";

  public static final String IVISITOR_FULL_NAME = "de.monticore.visitor.IVisitor";

  public static final String ITRAVERSER_FULL_NAME = "de.monticore.visitor.ITraverser";

  public static final String IHANDLER_FULL_NAME = "de.monticore.visitor.IHandler";

  public static final String HANDLE_AST_INHERITANCE_TEMPLATE = "_visitor.handler.HandleASTInheritance";

  public static final String HANDLE_AST_NODE_INHERITANCE_TEMPLATE = "_visitor.handler.HandleASTNodeInheritance";

  public static final String HANDLE_SYMTAB_INHERITANCE_TEMPLATE = "_visitor.handler.HandleSymTabInheritance";

  public static final String TRAVERSER_HANDLE_TEMPLATE = "_visitor.traverser.Handle";
  
  public static final String TRAVERSER_TRAVERSE_TEMPLATE = "_visitor.traverser.Traverse";
  
  public static final String TRAVERSER_TRAVERSE_SCOPE_TEMPLATE = "_visitor.traverser.TraverseScope";
  
  public static final String HANDLER_HANDLE_TEMPLATE = "_visitor.handler.Handle";
  
  public static final String HANDLER_TRAVERSE_TEMPLATE = "_visitor.handler.Traverse";
  
  public static final String HANDLER_TRAVERSE_SCOPE_TEMPLATE = "_visitor.handler.TraverseScope";

  public static final String TRAVERSER_ADD_VISITOR_TEMPLATE = "_visitor.traverser.AddVisitor";
  
  public static final String TRAVERSER_SET_HANDLER_TEMPLATE = "_visitor.traverser.SetHandler";

  public static final String VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE = "_visitor.traverser.DelegatingMethods";


  private VisitorConstants() {}
}
