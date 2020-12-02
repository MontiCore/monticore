<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("generatedErrorCode","globalScope")}
if(!(enclosingScope instanceof globalScope)){
  Log.warn("0xA1039${generatedErrorCode} The artifact scope " + (isPresentName() ? getName() : "") + " should have a global scope as enclosing scope or no "
    + "enclosing scope at all.");
}
super.setEnclosingScope(enclosingScope);
