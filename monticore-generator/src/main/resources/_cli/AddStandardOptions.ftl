<#-- (c) https://github.com/MontiCore/monticore -->
//help
options.addOption(org.apache.commons.cli.Option.builder("h")
    .longOpt("help")
    .desc("Prints this help dialog")
    .get());

//version
options.addOption(org.apache.commons.cli.Option.builder("v")
    .longOpt("version")
    .desc("Prints version information")
    .get());

//stacktrace option
options.addOption(org.apache.commons.cli.Option.builder()
    .longOpt("stacktrace")
    .argName("loglevel")
    .hasArgs()
    .optionalArg(true)
    .desc("Enables stacktraces, such as --stacktrace or --stacktrace=ERROR,WARN or --stacktrace=ERROR:stderr,warn:stderr (optional)")
    .get());

//parse input file
options.addOption(org.apache.commons.cli.Option.builder("i")
    .longOpt("input")
    .argName("file")
    .hasArg()
    .desc("Reads the source file (mandatory) and parses the contents")
    .get());

//pretty print model
options.addOption(org.apache.commons.cli.Option.builder("pp")
    .longOpt("prettyprint")
    .argName("file")
    .optionalArg(true)
    .numberOfArgs(1)
    .desc("Prints the AST to stdout or the specified file (optional)")
    .get());

//output symbol table
options.addOption(org.apache.commons.cli.Option.builder("s")
    .longOpt("symboltable")
    .argName("file")
    .hasArg()
    .desc("Serializes the symbol table of the given artifact.")
    .get());

//reports about the model
options.addOption(org.apache.commons.cli.Option.builder("r")
    .longOpt("report")
    .argName("dir")
    .hasArg(true)
    .desc("Prints reports of the artifact to the specified directory.")
    .get());

// model paths
options.addOption(org.apache.commons.cli.Option.builder("path")
    .hasArgs()
    .desc("Sets the artifact path for imported symbols, space separated.")
    .get());

return options;
