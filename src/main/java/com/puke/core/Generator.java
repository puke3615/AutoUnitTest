package com.puke.core;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generator {

    private static final String SRC_PATH = "G:/workSpace/IDEA/JavaDemo/src/main/java";
    private static final String[] PACKAGES = new String[]{"com.puke.core", "com.puke"};

    public static void main(String[] args) {
        List<String> classNames = parseClassNameFromPackages(PACKAGES);
        System.out.println(classNames);

        classNames.stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(cls -> !cls.isEnum() && !cls.isInterface())
                .flatMap(cls -> {
                    Set<Method> allMethods = new HashSet<>();
                    allMethods.addAll(Arrays.asList(cls.getDeclaredMethods()));
                    allMethods.addAll(Arrays.asList(cls.getMethods()));
                    return allMethods.stream().filter(Objects::nonNull);
                })
                .filter(method -> method.isAnnotationPresent(Case.class))
                .forEach(method -> {
                    Case caseAnno = method.getAnnotation(Case.class);
                    String jsonStr = caseAnno.value();
                    Class<?> parameterType = method.getParameterTypes()[0];
                    Object param = JSON.parseObject(jsonStr, parameterType);
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    try {
                        Class<?> cls = method.getDeclaringClass();
                        Object instance = cls.newInstance();
                        Object result = method.invoke(instance, param);
                        System.out.println("=====================================");
                        System.out.println(String.format("Type: %s\nMethod: %s\nParam: %s\nResult: %s",
                                cls.getName(), method.getName(), param, result));
                        System.out.println("=====================================");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    private static List<String> parseClassNameFromPackages(String... packages) {
        return Arrays.stream(packages)
                .map(pkg -> pkg.replace(".", "/"))
                .map(pkgRelativePath -> {
                    File srcPath = new File(SRC_PATH);
                    return pkgRelativePath.isEmpty() ? srcPath : new File(srcPath, pkgRelativePath);
                })
                .filter(File::exists)
                .flatMap(pkgPath -> findAllJavaFiles(pkgPath).stream())
                .collect(Collectors.toList());
    }

    private static List<String> findAllJavaFiles(File file) {
        return Optional.ofNullable(file)
                .filter(File::exists)
                .map(f -> {
                    List<String> classInDir = new ArrayList<>();
                    if (f.isDirectory()) {
                        Optional.ofNullable(f.listFiles())
                                .ifPresent(files -> Arrays.stream(files)
                                        .map(Generator::findAllJavaFiles)
                                        .forEach(classInDir::addAll)
                                );
                    } else if (f.isFile() && f.getName().endsWith(".java")) {
                        String className = f.getAbsolutePath().replace("\\", ".").split("src.main.java.")[1];
                        className = className.substring(0, className.length() - 5);
                        classInDir.add(className);
                    }
                    return classInDir;
                })
                .orElse(Collections.emptyList());
    }
}
