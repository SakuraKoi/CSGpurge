package dev.sakurakooi.CSGPurge.purger;

import dev.sakurakooi.CSGPurge.CSGElements;
import dev.sakurakooi.CSGPurge.CSGParser;
import dev.sakurakooi.CSGPurge.utils.NullableOptional;

// These directive should be removed if they have no children, as it would cause null shape error
public class EmptyDirectivePurger implements IPurger {
    @Override
    public NullableOptional<CSGParser.CSGAst> checkChildren(CSGParser.CSGAst node) {
        if (CSGElements.booleanDirective.contains(node.name) || CSGElements.transformationDirective.contains(node.name)) {
            if (node.childrens.isEmpty()) {
                return NullableOptional.of(null);
            }
        }
        return NullableOptional.empty();
    }
}
