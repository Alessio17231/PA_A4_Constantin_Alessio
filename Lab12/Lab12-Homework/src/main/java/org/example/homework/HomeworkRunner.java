package org.example.homework;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeworkRunner {

    private static final int MOCK_VALUE = 10;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java org.example.homework.HomeworkRunner <folder>");
            return;
        }

        File root = new File(args[0]);

        if (!root.exists() || !root.isDirectory()) {
            System.out.println("Folderul primit nu exista sau nu este director.");
            return;
        }

        List<File> classFiles = new ArrayList<>();
        findClassFiles(root, classFiles);

        URLClassLoader loader = new URLClassLoader(new URL[]{root.toURI().toURL()});

        List<Class<?>> classes = new ArrayList<>();
        Set<Class<?>> annotations = new HashSet<>();

        for (File file : classFiles) {
            String className = getClassName(root, file);

            try {
                Class<?> clazz = loader.loadClass(className);
                classes.add(clazz);

                if (clazz.isAnnotation()) {
                    annotations.add(clazz);
                }
            } catch (Throwable e) {
                System.out.println("Nu am putut incarca: " + className);
            }
        }

        System.out.println("=== Annotation types ===");
        for (Class<?> annotation : annotations) {
            System.out.println(annotation.getName());
        }

        System.out.println("\n=== Public classes ===");

        for (Class<?> clazz : classes) {
            if (!Modifier.isPublic(clazz.getModifiers()) || clazz.isAnnotation()) {
                continue;
            }

            System.out.println("\nClass: " + clazz.getName());
            System.out.println("Methods:");

            for (Method method : clazz.getDeclaredMethods()) {
                System.out.println("  " + method);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (!hasIdentifiedAnnotation(method, annotations)) {
                    continue;
                }

                Object instance = null;

                if (!Modifier.isStatic(method.getModifiers())) {
                    instance = clazz.getDeclaredConstructor().newInstance();
                }

                method.setAccessible(true);

                if (method.getParameterCount() == 0) {
                    System.out.println("Invoc metoda: " + clazz.getName() + "." + method.getName());
                    method.invoke(instance);
                } else if (method.getParameterCount() == 1
                        && method.getParameterTypes()[0].equals(int.class)) {
                    System.out.println("Invoc metoda: " + clazz.getName() + "." + method.getName());
                    method.invoke(instance, MOCK_VALUE);
                }
            }
        }
    }

    private static void findClassFiles(File folder, List<File> classFiles) {
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                findClassFiles(file, classFiles);
            } else if (file.getName().endsWith(".class")) {
                classFiles.add(file);
            }
        }
    }

    private static String getClassName(File root, File file) {
        String rootPath = root.getAbsolutePath();
        String filePath = file.getAbsolutePath();

        String relativePath = filePath.substring(rootPath.length() + 1);

        return relativePath
                .replace(File.separatorChar, '.')
                .replace(".class", "");
    }

    private static boolean hasIdentifiedAnnotation(Method method, Set<Class<?>> annotations) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotations.contains(annotation.annotationType())) {
                return true;
            }
        }

        return false;
    }
}