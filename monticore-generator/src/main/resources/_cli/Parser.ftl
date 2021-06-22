<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startprod", "millFullName", "parserFullName", "generatedError1", "generatedError2")}


try {
      ${parserFullName} parser = ${millFullName}.parser() ;
      Optional<${startprod}> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050${generatedError1} Model could not be parsed.");
    }
    catch (NullPointerException | java.io.IOException e) {
      Log.error("0xA1051${generatedError2} Failed to parse " + model, e);
    }
    // should never be reached (unless failquick is off)
    return null;
