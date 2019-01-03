import com.intellij.psi.PsiClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ClassAnalyzer {
    private Class<?> aClass;
    private Map<String, List<String>> classInfo = new HashMap<>();

    ClassAnalyzer(PsiClass psiClass) {
        try {
            this.aClass = Class.forName(psiClass.getQualifiedName());
        } catch (ClassNotFoundException ex) {
            System.out.println("Class " + psiClass.getQualifiedName() + " not found.");
        }
    }

    private void analyzeMethods() {
        if (aClass.getMethods() != null) {
            Method[] methods = aClass.getMethods();
            classInfo.put("methods", Arrays.stream(methods)
                    .map(Method::getName)
                    .collect(Collectors.toList()));
            for (Method method : methods) {
                classInfo.put(method.getName() + "Modifiers", Arrays.asList(Modifier.toString(method.getModifiers()).split(" ")));
                classInfo.put(method.getName() + "ReturnType", Collections.singletonList(method.getReturnType().getCanonicalName()));
                if (method.getParameterTypes() != null) {
                    classInfo.put(method.getName() + "Parameters",
                            Arrays.stream(method.getParameters())
                                    .map(parameter -> parameter.getType().getCanonicalName() + " " + parameter.getName())
                                    .collect(Collectors.toList()));
                }
                if (method.getExceptionTypes() != null) {
                    classInfo.put(method.getName() + "Exceptions", Arrays.stream(method.getExceptionTypes())
                            .map(Class::getCanonicalName)
                            .collect(Collectors.toList()));
                }
                if (method.getDeclaredAnnotations() != null) {
                    classInfo.put(method.getName() + "Annotations", Arrays.stream(method.getDeclaredAnnotations())
                            .map(Annotation::toString)
                            .collect(Collectors.toList()));
                }
            }
        }
    }

    private void analyze() {
        classInfo.put("name", Collections.singletonList(aClass.getCanonicalName()));
        classInfo.put("modifiers", Arrays.asList(Modifier.toString(aClass.getModifiers()).split(" ")));
        if (aClass.getSuperclass() != null) {
            classInfo.put("extends", Collections.singletonList(aClass.getSuperclass().getName()));
        } else {
            classInfo.put("extends", Collections.singletonList("java.lang.Object"));
        }
        if (aClass.getInterfaces() != null) {
            classInfo.put("implements", Arrays.stream(aClass.getInterfaces())
                                                    .map(Class::getName)
                                                    .collect(Collectors.toList()));
        }
        analyzeMethods();
    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
