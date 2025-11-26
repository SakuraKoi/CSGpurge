package dev.sakurakooi.CSGPurge.purger;

import dev.sakurakooi.CSGPurge.CSGParser;
import dev.sakurakooi.CSGPurge.utils.NullableOptional;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public interface IPurger {
    /**
     * Check and possibly cleanup the given node.
     *
     * @param node The node to check.
     * @return The modified node, empty if no changes were made, or null if the node should be removed.
     */
    NullableOptional<CSGParser.CSGAst> checkChildren(CSGParser.CSGAst node);

    default void purge(CSGParser.CSGAst node, AtomicInteger cleaned) {
        for (var children : new ArrayList<>(node.childrens)) {
            var newChildren = checkChildren(children);
            if (newChildren.isPresent()) {
                if (newChildren.isNull()) {
                    node.removeChild(children);
                } else {
                    node.replaceAt(children, newChildren.get());
                }
                cleaned.incrementAndGet();
            }
        }
    }
}
