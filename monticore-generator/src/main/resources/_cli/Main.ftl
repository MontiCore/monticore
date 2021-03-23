<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "startprod")}


if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    String model = args[0];

// setup the deser infrastructure
    final ${grammarname}ScopeDeSer deser = new ${grammarname}ScopeDeSer();

// parse the model and create the AST representation
       final ${startprod} ast = parse(model);
        Log.info(model + " parsed successfully!", AutomataTool.class.getName());