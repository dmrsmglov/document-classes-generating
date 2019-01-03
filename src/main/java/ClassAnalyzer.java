import com.intellij.psi.PsiClass;

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

    private void analyze() {
        classInfo.put("name", Collections.singletonList(aClass.getCanonicalName()));
        classInfo.put("modifiers", Arrays.asList(Modifier.toString(aClass.getModifiers()).split(" ")));
        if (aClass.getSuperclass() != null) {
            classInfo.put("extends", Collections.singletonList(aClass.getSuperclass().getName()));
        } else {
            classInfo.put("extends", Collections.singletonList("java.lang.Object"));
        }
        if (aClass.getInterfaces() != null) {
            classInfo.put("implements", Arrays.stream(aClass.getInterfaces()).map(Class::getName).collect(Collectors.toList()));
        }
    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
