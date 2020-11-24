<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("doTraverse")}
  getTraverser().visit(node);
<#if doTraverse>
  getTraverser().traverse(node);
<#else>
  // no traverse() for abstract classes, interfaces and enums, only concrete classes are traversed
</#if>
  getTraverser().endVisit(node);
