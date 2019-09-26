<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("visitorName")}
  parents.push(node);
  ${visitorName}.super.traverse(node);
  parents.pop();
