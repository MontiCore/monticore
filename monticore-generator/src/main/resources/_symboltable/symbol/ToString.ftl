 <#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolName")}
return "${symbolName}{" +
  "fullName='" + this.getFullName() + "'" +
  ", sourcePosition=" + this.getSourcePosition() +
'}';
