package dev.sakurakooi.CSGPurge.purger;

import dev.sakurakooi.CSGPurge.CSGElements;
import dev.sakurakooi.CSGPurge.CSGParser;
import dev.sakurakooi.CSGPurge.utils.NullableOptional;

// These operator directive that only has 1 shape child can be removed as it effectively does nothing
public class CascadeOperatorMerger implements IPurger {
    @Override
    public NullableOptional<CSGParser.CSGAst> checkChildren(CSGParser.CSGAst node) {
        if (CSGElements.booleanDirective.contains(node.name)) {
            if (node.childrens.size() == 1 && checkGroupHasShapeObject(node)) {
                return NullableOptional.of(node.childrens.get(0));
            }
        }
        return NullableOptional.empty();
    }

    /*
    * Handle edge case like:
    * intersection() {
    *   mulmatrix(...)  {
    *       cube(...);
    *       sphere(...);
    *   }
    * }
    * */
    private static boolean checkGroupHasShapeObject(CSGParser.CSGAst children) {
        int found = 0;
        for (CSGParser.CSGAst csgAst : children.childrens) {
            if (CSGElements.transformationDirective.contains(csgAst.name)) {
                if (checkGroupHasShapeObject(csgAst))
                    found++;
            }
            if (CSGElements.shapeDirective.contains(csgAst.name) || CSGElements.booleanDirective.contains(csgAst.name)) {
                found++;
            }

            if (found > 1)
                return false;
        }
        return found == 1;
    }
}
