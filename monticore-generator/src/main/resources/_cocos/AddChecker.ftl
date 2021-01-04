<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cds")}

<#list cds as cd>
  checker.getTraverser().get${cd}VisitorList().forEach(s -> traverser.add4${cd}(s));
</#list>