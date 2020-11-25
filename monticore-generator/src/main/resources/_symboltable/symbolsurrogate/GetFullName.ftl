<#-- (c) https://github.com/MontiCore/monticore -->
  if(!de.monticore.utils.Names.getQualifier(name).isEmpty()){
    fullName = name;
  }
    if (fullName == null) {
    fullName = determineFullName();
  }
  return fullName;