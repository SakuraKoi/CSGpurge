package dev.sakurakooi.CSGPurge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CSGElements {
    public static final List<String> shape2dDirective = Arrays.asList(
            "circle",
            "square",
            "polygon",
            "text",
            "projection"
    );

    public static final List<String> shape3dDirective = Arrays.asList(
            "sphere",
            "cube",
            "cylinder",
            "polyhedron",
            "linear_extrude",
            "rotate_extrude",
            "surface"
    );

    public static final List<String> shapeDirective = new ArrayList<>(Stream.concat(shape2dDirective.stream(), shape3dDirective.stream()).distinct().toList());

    public static final List<String> booleanDirective = Arrays.asList(
            "group",
            "difference",
            "intersection",
            "union"
    );

    public static final List<String> transformationDirective = Arrays.asList(
            "multmatrix",
            "color",
            "render"
    );

    // I'm not gonna handle it, at least for now
    public static final List<String> unsupportedDirective = Arrays.asList(
            "hull",
            "minkowski",
            "polyhedron"
    );

    public static final List<String> allDirectives = new ArrayList<>(Stream.of(shapeDirective.stream(), booleanDirective.stream(), transformationDirective.stream(), unsupportedDirective.stream()).flatMap(Function.identity()).distinct().toList());
}
