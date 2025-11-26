package dev.sakurakooi.CSGPurge.purger;

import dev.sakurakooi.CSGPurge.CSGParser;
import dev.sakurakooi.CSGPurge.utils.NullableOptional;

public class IntersectionFix implements IPurger{
    @Override
    public NullableOptional<CSGParser.CSGAst> checkChildren(CSGParser.CSGAst node) {
        if ("intersection".equalsIgnoreCase(node.name)) {
            if (node.childrens.size() == 2) {
                node.childrens.add(new CSGParser.CSGAst("cube", "[100000, 100000, 100000]", false));
                return NullableOptional.of(node);
            }
        }
        return NullableOptional.empty();
    }
}
