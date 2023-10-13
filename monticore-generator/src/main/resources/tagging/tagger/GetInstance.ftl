<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}
if (INSTANCE == null) {
    INSTANCE = new ${clazz}();
}
return INSTANCE;
