<#-- (c) https://github.com/MontiCore/monticore -->
  if(!de.se_rwth.commons.Names.getQualifier(name).isEmpty()){
    fullName = name;
  }
    if (fullName == null) {
    fullName = determineFullName();
  }
  return fullName;