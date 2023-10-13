<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millname")}
// While we could use instanceof, we instead use the semantic knowledge, that artifact scopes are subscopes of a global scope
List globalChildren = ${millname}.globalScope().getSubScopes();
while (!(globalChildren.contains(s))) {
  s = s.getEnclosingScope();
}
return s;
