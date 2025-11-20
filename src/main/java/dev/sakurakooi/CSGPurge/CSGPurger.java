package dev.sakurakooi.CSGPurge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CSGPurger {
    // Can be removed if no children
    private static final List<String> nonObjectTag = Arrays.asList(
            "group",
            "multmatrix",
            "difference",
            "union"
    );

    // Can be replaced with children, if only one
    private static final List<String> collapsableTag = Arrays.asList(
            "group"
    );

    public static CSGParser.CSGAst purge(CSGParser.CSGAst ast, AtomicInteger anythingCleaned) {
        for (var children : new ArrayList<>(ast.childrens)) {
            if (nonObjectTag.contains(children.name)) {
                if (children.childrens.isEmpty()) {
                    ast.removeChild(children);
                    anythingCleaned.incrementAndGet();
                    continue;
                }
            }
            if (collapsableTag.contains(children.name)) {
                if (children.childrens.size() == 1 && checkGroupHasOnlyOneObject(children)) {
                    ast.replaceAt(children, children.childrens.get(0));
                    anythingCleaned.incrementAndGet();
                    continue;
                }
            }
            if ("multmatrix".equalsIgnoreCase(children.name)) {
                if (children.childrens.size() == 1) {
                    var firstChild = children.childrens.get(0);
                    if ("multmatrix".equalsIgnoreCase(firstChild.name)) {
                        var matrix1 = json2Matrix(JSON.parseArray(children.arguments));
                        var matrix2 = json2Matrix(JSON.parseArray(firstChild.arguments));
                        var newMatrix = mergeMultmatrix(matrix1, matrix2);
                        firstChild.arguments = matrix2Str(newMatrix);
                        ast.replaceAt(children, firstChild);
                        anythingCleaned.incrementAndGet();
                        continue;
                    }
                }
            }
            if ("intersection".equalsIgnoreCase(children.name)) {
                if (children.childrens.size() == 2) {
                    children.childrens.add(new CSGParser.CSGAst("cube", "[100000, 100000, 100000]", false));
                    anythingCleaned.incrementAndGet();
                }

            }
        }
        ast.childrens.forEach(ast1 -> purge(ast1, anythingCleaned));
        return ast;
    }

    private static final List<String> realObjectList = Arrays.asList(
            "group",
            "union",
            "difference",
            "intersection",
            // 2d
            "circle",
            "square",
            "polygon",
            "text",
            "projection",
            // 3d
            "sphere",
            "cube",
            "cylinder",
            "polyhedron",
            "linear_extrude",
            "rotate_extrude",
            "surface"
    );

    private static boolean checkGroupHasOnlyOneObject(CSGParser.CSGAst children) {
        int found = 0;
        for (CSGParser.CSGAst csgAst : children.childrens) {
            if (Objects.equals(csgAst.name, "multmatrix")) {
                if (checkGroupHasOnlyOneObject(csgAst))
                    found++;
            }
            if (realObjectList.contains(csgAst.name)) {
                found++;
            }

            if (found > 1)
                return false;
        }
        return found == 1;
    }

    public static double[][] json2Matrix(JSONArray array) {
        double[][] result = new double[4][4];
        for (int x = 0; x < 4; x++) {
            JSONArray row = array.getJSONArray(x);
            for (int y = 0; y < 4; y++) {
                result[x][y] = row.getDoubleValue(y);
            }
        }
        return result;
    }

    public static String matrix2Str(double[][] matrix) {
        return Arrays.stream(matrix)
                .map(row -> Arrays.stream(row)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", ", "[", "]")))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static double[][] mergeMultmatrix(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != 4 || matrix2.length != 4)
            throw new IllegalArgumentException("Both matrices must be 4x4.");

        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += matrix1[i][k] * matrix2[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }
}
