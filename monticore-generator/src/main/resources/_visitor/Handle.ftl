${tc.signature("doTraverse")}
getRealThis().visit(node);
<#if doTraverse>
  getRealThis().traverse(node);
<#else>
  // no traverse() for abstract classes, interfaces and enums, only concrete classes are traversed
</#if>
getRealThis().endVisit(node);
