package dev.sakurakooi.CSGPurge.purger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import dev.sakurakooi.CSGPurge.CSGParser;
import dev.sakurakooi.CSGPurge.utils.NullableOptional;

import java.util.Arrays;
import java.util.stream.Collectors;

// Merge cascaded multmatrix directives into one
// BOSL2 often generates cascaded multmatrix directives and spam all the screen
public class MultmatrixMerger implements IPurger {
    @Override
    public NullableOptional<CSGParser.CSGAst> checkChildren(CSGParser.CSGAst node) {
        if ("multmatrix".equalsIgnoreCase(node.name)) {
            if (node.childrens.size() == 1) {
                var firstChild = node.childrens.get(0);
                if ("multmatrix".equalsIgnoreCase(firstChild.name)) {
                    var matrix1 = json2Matrix(JSON.parseArray(node.arguments));
                    var matrix2 = json2Matrix(JSON.parseArray(firstChild.arguments));
                    var newMatrix = mergeMultmatrix(matrix1, matrix2);
                    firstChild.arguments = matrix2Str(newMatrix);
                    return NullableOptional.of(firstChild);
                }
            }
        }
        return NullableOptional.empty();
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
