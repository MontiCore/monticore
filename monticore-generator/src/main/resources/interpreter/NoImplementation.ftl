<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "errorCode")}

String errorMsg = "0x57086${errorCode} Interpreter was not implemented for ${name}";
de.se_rwth.commons.logging.Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
return new de.monticore.interpreter.values.ErrorMIValue(errorMsg);