package com.bonnag.ukcointax;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class CommandLineOptions extends OptionsBase {
    @Option(
            name = "input",
            abbrev = 'i',
            help = "Input directory from which to read trades.",
            defaultValue = ""
    )
    public String input;

    @Option(
            name = "output",
            abbrev = 'o',
            help = "Output directory to which to write report.",
            defaultValue = ""
    )
    public String output;
}
