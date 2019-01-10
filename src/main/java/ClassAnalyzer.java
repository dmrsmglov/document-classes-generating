import com.intellij.psi.*;

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
                int methodId = casualMethods.size() + constructors.size();
                if (method.getReturnType() == null) {
                    constructors.add(method.getName() + '#' + Integer.toString(methodId));
                } else {
                    casualMethods.add(method.getName() + '#' + Integer.toString(methodId));
                    classInfo.put(method.getName() + '#' + Integer.toString(methodId) + "ReturnType", Collections.singletonList(method.getReturnType().getCanonicalText()));
                }
                classInfo.put(method.getName() + '#' + Integer.toString(methodId) + "Modifiers", Arrays.asList(method.getModifierList().getText().split(" ")));
                classInfo.put(method.getName() + '#' + Integer.toString(methodId) + "Parameters",
                        Arrays.stream(method.getParameters())
                                .map(parameter -> parameter.getType().toString().substring(8) + " " + parameter.getName())
                                .collect(Collectors.toList()));
                classInfo.put(method.getName() + '#' + Integer.toString(methodId) + "Exceptions", Arrays.stream(method.getThrowsList().getReferencedTypes())
                        .map(PsiClassType::getClassName)
                        .collect(Collectors.toList()));
            }
            classInfo.put("constructors", constructors);
            classInfo.put("methods", casualMethods);
    }

    private void analyzeFields() {
        PsiField[] fields = aClass.getFields();
        classInfo.put("fields", Arrays.stream(aClass.getFields())
                                        .map(PsiField::getName)
                                        .collect(Collectors.toList()));
        for (PsiField field : fields) {
            classInfo.put(field.getName() + "Modifiers", Arrays.asList(field.getModifierList().getText().split(" ")));
            classInfo.put(field.getName() + "Type", Collections.singletonList(field.getType().toString().substring(8)));
        }
    }

    private void analyze() {
        classInfo.put("name", Collections.singletonList(aClass.getQualifiedName()));
        PsiClass containingClass = aClass.getContainingClass();
        if (containingClass != null) {
            classInfo.put("containClass", Collections.singletonList(containingClass.getQualifiedName()));
        }
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
        analyzeFields();

        if (aClass.getAllInnerClasses().length != 0) {
            classInfo.put("innerClasses", Arrays.stream(aClass.getAllInnerClasses())
                                                    .map(PsiClass::getQualifiedName)
                                                    .collect(Collectors.toList()));
        }
    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
