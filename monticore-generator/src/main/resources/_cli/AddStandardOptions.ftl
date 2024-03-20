<#-- (c) https://github.com/MontiCore/monticore -->
//help
options.addOption(org.apache.commons.cli.Option.builder("h")
    .longOpt("help")
    .desc("Prints this help dialog")
    .build());

//version
options.addOption(org.apache.commons.cli.Option.builder("v")
    .longOpt("version")
    .desc("Prints version information")
    .build());

//parse input file
options.addOption(org.apache.commons.cli.Option.builder("i")
    .longOpt("input")
    .argName("file")
    .hasArg()
    .desc("Reads the source file (mandatory) and parses the contents")
    .build());

//pretty print runner
options.addOption(org.apache.commons.cli.Option.builder("pp")
    .longOpt("prettyprint")
    .argName("file")
    .optionalArg(true)
    .numberOfArgs(1)
    .desc("Prints the AST to stdout or the specified file (optional)")
    .build());

// pretty print SC
options.addOption(org.apache.commons.cli.Option.builder("s")
    .longOpt("symboltable")
    .argName("file")
    .hasArg()
    .desc("Serializes the symbol table of the given artifact.")
    .build());

//reports about the runner
options.addOption(org.apache.commons.cli.Option.builder("r")
    .longOpt("report")
    .argName("dir")
    .hasArg(true)
    .desc("Prints reports of the artifact to the specified directory.")
    .build());

// model paths
options.addOption(org.apache.commons.cli.Option.builder("path")
    .hasArgs()
    .desc("Sets the artifact path for imported symbols, space separated.")
    .build());

return options;
