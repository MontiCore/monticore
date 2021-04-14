org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

//help
options.addOption(org.apache.commons.cli.Options.builder("h")
    .longOpt("help")
    .desc("Prints this help dialog")
    .build());

//parse input file
options.addOption(org.apache.commons.cli.Options.builder("i")
    .longOpt("input")
    .argName("file")
    .desc("Prints this help dialog")
    .build());

//pretty print runner
options.addOption(org.apache.commons.cli.Options.builder("pp")
    .longOpt("input")
    .argName("file")
    .desc("Prints this help dialog")
    .build());

return options;

