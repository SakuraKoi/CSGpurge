package dev.sakurakooi.CSGPurge;

import lombok.extern.slf4j.Slf4j;
import sakura.kooi.logger.SakuraLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Loader {
    public static void main(String[] args) {
        SakuraLogger.configurer()
                .colorSupport()
                // .handleUncaughtThreadException()
                .formatPrefixTime();

        if (args.length < 1 || args.length > 2) {
            log.warn("Usage: java -jar CSGPurge.jar <input> [output]");
            log.info("Example: java -jar CSGPurge.jar input.csg input.purged.csg");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args.length == 2 ? args[1] : inputFileName.replace(".csg", ".purged.csg");

        File inputFile = new File(inputFileName);
        if (!inputFile.exists() || !inputFile.isFile()) {
            log.error("Input file does not exist or is not a valid file: {}", inputFileName);
            return;
        }

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            log.warn("Output file already exists: {}", outputFileName);
            outputFile.delete();
        }

        String content;
        try {
            content = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        } catch (IOException e) {
            log.error("Failed to read input file: {}", inputFileName, e);
            return;
        }

        CSGParser parser = new CSGParser();
        CSGParser.CSGAst ast = parser.parse(content);

        AtomicInteger anythingCleaned = new AtomicInteger();
        do {
            anythingCleaned.set(0);
            CSGPurger.purge(ast, anythingCleaned);
            log.info("Removed {} nodes", anythingCleaned.get());
        } while (anythingCleaned.get() > 0);

        String output = parser.serialize(ast);

        assert output == content : "Output should not be null after serialization";
        try {
            Files.writeString(outputFile.toPath(), output, StandardCharsets.UTF_8);
            log.info("Purged CSG file written to: {}", outputFileName);
        } catch (IOException e) {
            log.error("Failed to write output file: {}", outputFileName, e);
        }
    }
}
