import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;

import java.util.*;
import java.util.stream.Collectors;

public class ClassAnalyzer {
    private PsiClass aClass;
    private Map<String, List<String>> classInfo = new HashMap<>();

    ClassAnalyzer(PsiClass psiClass) {
        this.aClass = psiClass;
    }

    private void analyzeMethods() {
            PsiMethod[] methods = aClass.getMethods();
            List<String> casualMethods = new ArrayList<>();
            List<String> constructors = new ArrayList<>();
            for (PsiMethod method : methods) {
                if (method.getReturnType() == null) {
                    constructors.add(method.getName());
                } else {
                    casualMethods.add(method.getName());
                    classInfo.put(method.getName() + "ReturnType", Collections.singletonList(method.getReturnType().getCanonicalText()));
                }
                classInfo.put(method.getName() + "Modifiers", Arrays.asList(method.getModifierList().getText().split(" ")));
                classInfo.put(method.getName() + "Parameters",
                        Arrays.stream(method.getParameters())
                                .map(parameter -> parameter.getType().toString().substring(8) + " " + parameter.getName())
                                .collect(Collectors.toList()));
                classInfo.put(method.getName() + "Exceptions", Arrays.stream(method.getThrowsList().getReferencedTypes())
                        .map(PsiClassType::getClassName)
                        .collect(Collectors.toList()));
                classInfo.put(method.getName() + "Annotations", Arrays.stream(method.getAnnotations())
                        .map(PsiAnnotation::getQualifiedName)
                        .collect(Collectors.toList()));
            }
            classInfo.put("constructors", constructors);
            classInfo.put("methods", casualMethods);
    }

    private void analyze() {
        classInfo.put("name", Collections.singletonList(aClass.getQualifiedName()));
        if (aClass.isInterface()) {
            classInfo.put("modifiers", Arrays.asList((aClass.getModifierList().getText() + " interface").split(" ")));
        } else {
            classInfo.put("modifiers", Arrays.asList((aClass.getModifierList().getText() + " class").split(" ")));
        }
        if (aClass.getExtendsList() != null) {
            classInfo.put("extends", Arrays.stream(aClass.getExtendsListTypes())
                    .map(PsiClassType::getName)
                    .collect(Collectors.toList()));
        }
        if (aClass.getImplementsList() != null) {
            classInfo.put("implements", Arrays.stream(aClass.getImplementsListTypes())
                    .map(PsiClassType::getName)
                    .collect(Collectors.toList()));
        }
        analyzeMethods();
    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
