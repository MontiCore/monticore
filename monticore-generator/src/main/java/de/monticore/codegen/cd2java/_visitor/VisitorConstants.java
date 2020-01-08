/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

public final class VisitorConstants {

  public static final String VISITOR_PACKAGE = "_visitor";

  public static final String VISITOR_SUFFIX = "Visitor";

  public static final String INHERITANCE_SUFFIX = "Inheritance";

  public static final String PARENT_AWARE_SUFFIX = "ParentAware";

  public static final String DELEGATOR_SUFFIX = "Delegator";

  public static final String PARENTS_ATTRIBUTE = "parents";

  public static final String GET_PARENT_METHOD = "getParent";

  public static final String GET_PARENTS_METHOD = "getParents";

  public static final String VISITOR_PREFIX = "visitor";

  public static final String GET_REAL_THIS = "getRealThis";

  public static final String SET_REAL_THIS = "setRealThis";

  public static final String REAL_THIS = "realThis";

  public static final String VISIT = "visit";

  public static final String END_VISIT = "endVisit";

  public static final String HANDLE = "handle";

  public static final String TRAVERSE = "traverse";

  public static final String HANDLE_INHERITANCE_TEMPLATE = "_visitor.inheritance.HandleInheritance";

  public static final String HANDLE_TEMPLATE = "_visitor.Handle";

  public static final String TRAVERSE_TEMPLATE = "_visitor.Traverse";

  public static final String GET_PARENT_PAREENTAWARE_TEMPLATE = "_visitor.parentaware.GetParent";

  public static final String TRAVERSE_PAREENTAWARE_TEMPLATE = "_visitor.parentaware.Travers";

  public static final String SET_REAL_THIS_DELEGATOR_TEMPLATE = "_visitor.delegator.SetRealThis";

  public static final String SET_VISITOR_DELEGATOR_TEMPLATE = "_visitor.delegator.SetVisitor";

  public static final String VISITOR_METHODS_DELEGATOR_TEMPLATE = "_visitor.delegator.VisitorMethods";

  public static final String  TRAVERSE_SCOPE_TEMPLATE= "_visitor.scope.Traverse";


  private VisitorConstants() {}
}
