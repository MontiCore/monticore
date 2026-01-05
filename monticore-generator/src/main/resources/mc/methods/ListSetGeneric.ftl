<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute","attributeType","errorCode")}
${defineHookPoint("Setter:Before")}
boolean allElementsValid = true;

if(${attribute.getName()} != null) {
  for(Object element: ${attribute.getName()}) {
    if(!(${attribute.getName()}.getClass() ==  ${attributeType}.class)){
      allElementsValid = false;
      break;
    }
  }
}

if(allElementsValid && ${attribute.getName()} != null) {
  <#-- this complicated name is done to ensure naming conflicts -->
  List<${attributeType}> castedList${attribute.getName()} = (List<${attributeType}>)${attribute.getName()};
  this.${attribute.getName()} = castedList${attribute.getName()};
}else{
  Log.error("${errorCode} a unexpected type was set in the set ${attribute.getName()?capitalize} List() method of the ListSetGeneric");
}
${defineHookPoint("Setter:After")}