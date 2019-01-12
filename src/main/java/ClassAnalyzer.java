import com.intellij.psi.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ClassAnalyzer {

    private List<String> getMethods(PsiClass psiClass) {
        PsiMethod[] methods = psiClass.getMethods();
        List<String> casualMethods = new ArrayList<>();
        for (PsiMethod method : methods) {
            String signature = "";
            if (method.getReturnType() != null) {
                Map<Boolean, List<String>> modifiers =  Arrays.stream(method.getModifierList().getText().split(" "))
                                    .filter(modifier -> !modifier.equals(""))
                                    .collect(Collectors.groupingBy(x -> x.startsWith("@")));
                signature += String.join("", Optional.ofNullable(modifiers.get(true)).orElse(Collections.emptyList()));
                signature += String.join(" ", Optional.ofNullable(modifiers.get(false)).orElse(Collections.emptyList()));
                if (modifiers.get(false) != null) {
                    signature += " ";
                }
                signature += method.getReturnType().getCanonicalText();
                signature += " " + method.getName();
                signature += Arrays.stream(method.getParameters())
                                    .map(parameter -> parameter.getType().toString().substring(8) + " " + parameter.getName())
                                    .collect(Collectors.joining(", ", "(", ")"));
                if (method.getThrowsList().getReferencedTypes().length != 0) {
                    signature += Arrays.stream(method.getThrowsList().getReferencedTypes())
                            .map(PsiClassType::getClassName)
                            .collect(Collectors.joining(", ", " throws ", ""));
                }
                casualMethods.add(signature);
            }
        }
        return casualMethods;
    }


    public PdfClass getClassInfo(PsiClass aClass) {

        String name = aClass.getQualifiedName();
        String outerClass = getOuterClass(aClass);
        String signature = getClassSignature(name, aClass);
        String classAnnotations = getClassAnnotations(aClass);
        List<String> fields = getFields(aClass);
        List<String> constructors = getConstructors(aClass);
        List<String> methods = getMethods(aClass);
        List<String> extending = getExtending(aClass);
        List<String> implementing = getImplementing(aClass);
        List<String> innerClasses = getInnerClasses(aClass);

        return new PdfClass(name, outerClass, signature, classAnnotations, extending, implementing, fields, constructors, methods, innerClasses);
    }

    private String getClassAnnotations(PsiClass psiClass) {
        return Arrays.stream(psiClass.getModifierList().getText().split(" "))
                        .filter(modifier -> modifier.startsWith("@"))
                        .collect(Collectors.joining(""));
    }

    private List<String> getConstructors(PsiClass psiClass) {
        PsiMethod[] methods = psiClass.getMethods();
        List<String> constructors = new ArrayList<>();
        for (PsiMethod method : methods) {
            String signature = "";
            if (method.getReturnType() == null) {
                Map<Boolean, List<String>> modifiers =  Arrays.stream(method.getModifierList().getText().split(" "))
                        .filter(modifier -> !modifier.equals(""))
                        .collect(Collectors.groupingBy(x -> x.startsWith("@")));
                signature += String.join("", Optional.ofNullable(modifiers.get(true)).orElse(Collections.emptyList()));
                signature += String.join(" ", Optional.ofNullable(modifiers.get(false)).orElse(Collections.emptyList()));
                if (modifiers.get(false) != null) {
                    signature += " ";
                }
                signature += method.getName();
                signature += Arrays.stream(method.getParameters())
                                    .map(parameter -> parameter.getType().toString().substring(8) + " " + parameter.getName())
                                    .collect(Collectors.joining(", ", "(", ")"));
                if (method.getThrowsList().getReferencedTypes().length != 0) {
                    signature += Arrays.stream(method.getThrowsList().getReferencedTypes())
                                        .map(PsiClassType::getClassName)
                                        .collect(Collectors.joining(", ", " throws ", ""));
                }
                constructors.add(signature);
            }
        }
        return constructors;
    }

    @Nullable
    private String getOuterClass(PsiClass aClass) {
        PsiClass containingClass = aClass.getContainingClass();
        if (containingClass != null) {
            return containingClass.getQualifiedName();
        }
        return null;
    }

    private List<String> getFields(PsiClass aClass) {
        List<String> fieldsSignature = new ArrayList<>();
        PsiField[] fields = aClass.getFields();
        for (PsiField field : fields) {
            Map<Boolean, List<String>> modifiers = Arrays.stream(field.getModifierList().getText().split(" "))
                                                            .filter(modifier -> !modifier.isEmpty())
                                                            .collect(Collectors.groupingBy(modifier -> modifier.startsWith("@")));
            String signature = "";
            signature += String.join("", Optional.ofNullable(modifiers.get(true)).orElse(Collections.emptyList()));
            signature += String.join(" ", Optional.ofNullable(modifiers.get(false)).orElse(Collections.emptyList()));
            if (modifiers.get(false) != null) {
                signature += " ";
            }
            signature += field.getType().getCanonicalText() + " " + field.getName();
            fieldsSignature.add(signature);
        }
        return fieldsSignature;
    }

    private String getClassSignature(String name, PsiClass aClass) {
        String signature = Arrays.stream(aClass.getModifierList().getText().split(" "))
                                    .filter(modifier -> !modifier.startsWith("@") && !modifier.equals(""))
                                    .collect(Collectors.joining(" "));
        if (!signature.isEmpty()) {
            signature += " ";
        }
        if (aClass.isInterface()) {
            signature += "interface ";
        } else {
            signature += "class ";
        }
        return signature + name;
    }

    private List<String> getExtending(PsiClass aClass) {
        if (aClass.getExtendsList() != null) {
            return Arrays.stream(aClass.getExtendsListTypes())
                    .map(PsiClassType::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> getImplementing(PsiClass aClass) {
        if (aClass.getImplementsList() != null) {
            return Arrays.stream(aClass.getImplementsListTypes())
                    .map(PsiClassType::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> getInnerClasses(PsiClass aClass) {
        if (aClass.getAllInnerClasses().length != 0) {
            return Arrays.stream(aClass.getAllInnerClasses())
                    .map(PsiClass::getQualifiedName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
