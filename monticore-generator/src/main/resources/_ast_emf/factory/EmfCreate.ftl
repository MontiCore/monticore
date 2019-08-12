${tc.signature("factoryClassName", "packageName", "classList")}
switch (eClass.getClassifierID()) {
<#list classList as clazz>
  case ${packageName}.${clazz.getName()}: return ${factoryClassName}.create${clazz.getName()}();
</#list>
default:
  throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
}
