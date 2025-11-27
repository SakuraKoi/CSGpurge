package dev.sakurakooi.CSGPurge;

import dev.sakurakooi.CSGPurge.purger.CascadeOperatorMerger;
import dev.sakurakooi.CSGPurge.purger.EmptyDirectivePurger;
import dev.sakurakooi.CSGPurge.purger.IPurger;
import dev.sakurakooi.CSGPurge.purger.MultmatrixMerger;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CSGPurger {
    private static final List<IPurger> purgers = Arrays.asList(
            new EmptyDirectivePurger(),
            new CascadeOperatorMerger(),
            new MultmatrixMerger()
    );

    public static void purge(CSGParser.CSGAst node, AtomicInteger cleaned, boolean showUnknownDirectiveWarning) {
        if (showUnknownDirectiveWarning && !"root".equals(node.name)) {
            if (!CSGElements.allDirectives.contains(node.name)) {
                log.warn("Unknown directive encountered during purge, thats a bug: {}", node.name);
            } else if (CSGElements.unsupportedDirective.contains(node.name)) {
                log.warn("Unsupported directive, might be a trouble: {}", node.name);
            }
        }

        for (IPurger purger : purgers) {
            purger.purge(node, cleaned);
        }

        node.childrens.forEach(children -> purge(children, cleaned, showUnknownDirectiveWarning));

    }
}
