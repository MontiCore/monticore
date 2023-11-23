<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millname")}
while (!(s instanceof de.monticore.symboltable.IArtifactScope)) {
  s = s.getEnclosingScope();
}
return s;
