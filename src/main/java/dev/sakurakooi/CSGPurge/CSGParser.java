package dev.sakurakooi.CSGPurge;

import java.util.LinkedList;
import java.util.List;

public class CSGParser {
    public static class CSGAst {
        public String name;
        public String arguments;
        public boolean hasChildren;
        public CSGAst parent;
        public List<CSGAst> childrens = new LinkedList<>();

        public CSGAst(String name, String arguments, boolean hasChildren) {
            this.name = name;
            this.arguments = arguments;
            this.hasChildren = hasChildren;
        }

        public static CSGAst root() {
            return new CSGAst("root", null, true);
        }

        public void addChild(CSGAst child) {
            if (child == null) {
                throw new IllegalArgumentException("Child cannot be null");
            }
            childrens.add(child);
            child.parent = this;
        }

        public void removeChild(CSGAst child) {
            if (child == null) {
                throw new IllegalArgumentException("Child cannot be null");
            }
            if (!childrens.remove(child)) {
                throw new IllegalArgumentException("Child not found: " + child.name);
            }
            child.parent = null;
        }

        public void replaceAt(CSGAst oldChild, CSGAst newChild) {
            int index = this.childrens.indexOf(oldChild);
            if (index != -1) {
                this.childrens.set(index, newChild);
                newChild.parent = this;
            } else {
                throw new IllegalArgumentException("Child not found: " + oldChild.name);
            }
        }

        public String toString() {
            return "CSGParser.CSGAst(name=" + this.name + ", arguments=" + this.arguments + ", childrens=" + this.childrens.size() + ")";
        }
    }

    public CSGAst parse(String content) {
        String[] lines = content.split("\n");
        CSGAst root = CSGAst.root();

        CSGAst lastNode = root;
        int currentDepth = 0;

        for (String line : lines) {
            if (line.startsWith("#")) {
                line = line.substring(1);
            }
            if (line.trim().isEmpty()) {
                continue;
            }
            // format: name(arguements) {
            // or:     name(arguments);

            int identLevel = line.indexOf(line.trim());
            if (line.trim().equals("}")) {
                lastNode = lastNode.parent;
                currentDepth--;
                continue;
            }

            String trimmedLine = line.trim();
            String name = trimmedLine.substring(0, trimmedLine.indexOf('(')).trim();
            String arguments = trimmedLine.substring(trimmedLine.indexOf('(') + 1, trimmedLine.lastIndexOf(')')).trim();
            boolean hasChildren = !trimmedLine.endsWith(";");
            CSGAst newNode = new CSGAst(name, arguments, hasChildren);

            if (identLevel == currentDepth) {
                if (lastNode.name.equals("root")) {
                    lastNode.addChild(newNode);
                    if (hasChildren) {
                        lastNode = newNode;
                    }
                } else {
                    lastNode.parent.addChild(newNode);
                }
            } else if (identLevel > currentDepth) {
                lastNode.addChild(newNode);
                if (hasChildren) {
                    lastNode = newNode;
                    currentDepth = identLevel;
                }
            } else {
                int upperLevelCount = currentDepth - identLevel + 1;
                for (int i = 0; i < upperLevelCount; i++) {
                    if (lastNode.parent == null) {
                        throw new IllegalStateException("Cannot go up from root node");
                    }
                    lastNode = lastNode.parent;
                }
                lastNode.addChild(newNode);
                if (hasChildren) {
                    lastNode = newNode;
                    currentDepth = identLevel;
                }
            }
        }

        return root;
    }

    public void serializeInternal(StringBuilder output, CSGAst ast, int identLevel) {
        String ident = "\t".repeat(identLevel);
        if (!ast.name.equals("root")) {
            output.append(ident).append(ast.name).append("(").append(ast.arguments).append(")");
            if (ast.hasChildren) {
                output.append(" {\n");
            } else {
                output.append(";\n");
                return; // No children, so we can return early
            }
        }

        for (CSGAst child : ast.childrens) {
            serializeInternal(output, child, ast.name.equals("root") ? identLevel : identLevel + 1);
        }

        if (!ast.name.equals("root") && ast.hasChildren) {
            output.append(ident).append("}\n");
        }
    }

    public String serialize(CSGAst ast) {
        StringBuilder output = new StringBuilder();
        serializeInternal(output, ast, 0);
        return output.toString();
    }
}
