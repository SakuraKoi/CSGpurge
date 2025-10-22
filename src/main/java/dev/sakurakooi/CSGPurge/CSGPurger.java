package dev.sakurakooi.CSGPurge;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CSGPurger {

    public static CSGParser.CSGAst purge(CSGParser.CSGAst ast, AtomicInteger anythingCleaned) {
        for (var children : new ArrayList<>(ast.childrens)) {
            if (children.name.equals("group") || children.name.equals("multmatrix") || children.name.equals("difference") || children.name.equals("union")) {
                if (children.childrens.isEmpty()) {
                    ast.removeChild(children);
                    anythingCleaned.incrementAndGet();
                }
            }
            if (children.name.equals("group")) {
                if (children.childrens.size() == 1) {
                    ast.replaceAt(children, purge(children.childrens.get(0), anythingCleaned));
                    anythingCleaned.incrementAndGet();
                }
            }
        }
        ast.childrens.forEach(ast1 -> purge(ast1, anythingCleaned));
        return ast;
    }
}
