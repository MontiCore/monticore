<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname")}

    try {
      ${grammarname}Tool tool = new  ${grammarname}Tool();
      tool.run(args);
    }
    catch (Exception exception) {
      // ensure a sane exit
      Log.ensureInitialization();
      Log.error("0xEEEEE an internal error occurred"
              + " during the execution of the ${grammarname}Tool."
              + System.lineSeparator() + "This error is unexpected"
              + " and does not indicate an issue with any provided models.",
          exception
      );
      System.exit(1);
    }
