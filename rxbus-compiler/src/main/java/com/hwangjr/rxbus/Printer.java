package com.hwangjr.rxbus;

import com.google.auto.common.SuperficialValidation;
import com.squareup.javapoet.TypeName;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by trs on 16-11-25.
 */
final class Printer {

    public static void SamplePrint(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv, Messager messager) {
        final String fileLocation = "/home/trs/AndroidStudioProjects/RxBus/testprint" + System.currentTimeMillis() + ".txt";
        File file = new File(fileLocation);
        try {
            if (!file.exists())
                file.createNewFile();

            FileWriter writer = new FileWriter(file, false);
            writer.write("Annotations: " + annotations.size() + "\n");
            for (TypeElement e : annotations) {
                writer.write("SimpleName: " + e.getSimpleName() + "\n");
                writer.write("QualifiedName: " + e.getQualifiedName() + "\n");

                TypeMirror mirror = e.getSuperclass();
                NestingKind kind = e.getNestingKind();
                Element element = e.getEnclosingElement();
                List<Element> elementList = (List<Element>) e.getEnclosedElements();
                List<TypeMirror> interfaces = (List<TypeMirror>) e.getInterfaces();
                List<TypeParameterElement> parameterElements = (List<TypeParameterElement>) e.getTypeParameters();
                writer.write("TypeMirror: " + mirror + "\n");
                writer.write("NestingKind: " + kind + "\n");
                writer.write("EnclosingElement: " + element + "\n");
                writer.write("EnclosedElements: " + elementList + "\n");
                writer.write("Interfaces: " + interfaces + "\n");
                writer.write("TypeParameterElement: " + parameterElements + "\n\n");
            }

            writer.write("=================================================\n\n");

            Set<Element> elements = (Set<Element>) roundEnv.getRootElements();
            writer.write("Elements: " + elements.size() + "\n");
            for (Element e : elements) {
                writer.write("SimpleName: " + e.getSimpleName() + "\n");

                TypeMirror mirror = e.asType();
                ElementKind kind = e.getKind();
                Set<Modifier> modifiers = e.getModifiers();
                Element element = e.getEnclosingElement();
                List<Element> elementList = (List<Element>) e.getEnclosedElements();
                List<AnnotationMirror> annotationMirrors = (List<AnnotationMirror>) e.getAnnotationMirrors();
                writer.write("TypeMirror: " + mirror + "\n");
                writer.write("ElementKind: " + kind + "\n");
                writer.write("Modifiers: " + modifiers + "\n");
                writer.write("EnclosingElement: " + element + "\n");
                writer.write("EnclosedElements: " + elementList + "\n");
                writer.write("AnnotationMirrors: " + annotationMirrors + "\n\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    public static void SamplePrint2(Element targetElement, Messager messager) {
        final String fileLocation = "/home/trs/AndroidStudioProjects/RxBus/SamplePrint2_" + targetElement.getEnclosingElement() + "_" +
                targetElement.getSimpleName() + "_" + targetElement
                .asType() + ".txt";
        File file = new File(fileLocation);
        try {
            if (!file.exists())
                file.createNewFile();
            else {
                file.delete();
                file.createNewFile();
            }

            //方法element
            ExecutableElement executableElement = (ExecutableElement) targetElement;

            FileWriter writer = new FileWriter(file, false);
            writer.write("Elements: \n");
            writer.write("SimpleName: " + targetElement.getSimpleName() + "\n");
            writer.write("Util.isStandardEncloseingClass: " + Util.isStandardEncloseingClass(targetElement) + "\n");
            writer.write("Util.isStandardMethod: " + Util.isStandardMethod(targetElement) + "\n");
            writer.write("SuperficialValidation.validateElement: " + SuperficialValidation.validateElement(targetElement) + "\n");

            writer.write("\nExecutableElement :\n");
            writer.write("SimpleName :" + executableElement.getSimpleName() + "\n");
            writer.write("DefaultValue :" + executableElement.getDefaultValue() + "\n");
            writer.write("ReturnType :" + executableElement.getReturnType() + "\n");
            writer.write("\nParameters :" + executableElement.getParameters().size());
            for (VariableElement ve : executableElement.getParameters()) {
                writer.write("\nSimpleName :" + ve.getSimpleName() + "\n");
                writer.write("ConstantValue :" + ve.getConstantValue() + "\n");
                writer.write("EnclosingElement :" + ve.getEnclosingElement() + "\n");
            }
            writer.write("\nTypeParameters :" + executableElement.getTypeParameters().size());
            for (TypeParameterElement tpe : executableElement.getTypeParameters()) {
                writer.write("\nSimpleName :" + tpe.getSimpleName() + "\n");
                writer.write("Kind :" + tpe.getKind() + "\n");
                writer.write("GenericElement :" + tpe.getGenericElement() + "\n");
                writer.write("EnclosingElement :" + tpe.getEnclosingElement() + "\n");
            }

            TypeMirror mirror = targetElement.asType();
            ElementKind kind = targetElement.getKind();
            Set<Modifier> modifiers = targetElement.getModifiers();
            Element element = targetElement.getEnclosingElement();
            List<Element> elementList = (List<Element>) targetElement.getEnclosedElements();
            List<AnnotationMirror> annotationMirrors = (List<AnnotationMirror>) targetElement.getAnnotationMirrors();
            writer.write("\nTypeMirror: " + mirror + "\n");
            writer.write("ElementKind: " + kind + "\n");
            writer.write("Modifiers: " + modifiers + "\n");
            writer.write("EnclosingElement: " + element + "\n");
            writer.write("EnclosedElements: " + elementList + "\n");
            writer.write("AnnotationMirrors: " + annotationMirrors + "\n\n");

            writeElement(writer, element);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    public static void SamplePrint3(Element targetElement) {
        final String fileLocation = "/home/trs/AndroidStudioProjects/RxBus/SamplePrint3_" + targetElement.getEnclosingElement() + "_" +
                targetElement.getSimpleName() + "_" + targetElement
                .asType() + ".txt";
        File file = new File(fileLocation);
        try {
            if (!file.exists())
                file.createNewFile();
            else {
                file.delete();
                file.createNewFile();
            }

            //方法element
            ExecutableElement executableElement = (ExecutableElement) targetElement;

            FileWriter writer = new FileWriter(file, false);
            writer.write("Elements: \n");
            Util.ElementUtils.printElements(writer, targetElement);
            Util.ElementUtils.printElements(writer, executableElement);

            writer.write("\nParameters :" + executableElement.getParameters().size() + "\n");
            for (VariableElement ve : executableElement.getParameters()) {
                writer.write("asElement: " + Util.TypeUtils.asElement(ve.asType()) + "\n");
                TypeKind tKind = ve.asType().getKind();
                writer.write("tKind: " + tKind + "\n");
                if (tKind.isPrimitive()) {
                    writer.write("PrimitiveType: " + Util.TypeUtils.getPrimitiveType(ve.asType().getKind()) + "\n");
                } else {
                    writer.write("asType: " + ve.asType() + "\n");
                    if (tKind.equals(TypeKind.ARRAY)) {
                        //数组
                        ArrayType at = (ArrayType) ve.asType();
                        writer.write("at:" + at + "\n");
                        writer.write("ComponentType:" + at.getComponentType() + "\n");
                    } else if (tKind.equals(TypeKind.DECLARED)) {
                        //类或接口
                        DeclaredType dt = (DeclaredType) ve.asType();
                        writer.write("dt:" + dt + "\n");
                        TypeElement eee = (TypeElement) dt.asElement();
                        writer.write("eee:" + eee + "\n");
                        List<TypeMirror> interfaces = (List<TypeMirror>) eee.getInterfaces();
                        if (interfaces != null && interfaces.size() > 0) {
                            TypeMirror tm = interfaces.get(0);
                            writer.write("TypeName.get(List):" + TypeName.get(List.class) + "\n");
                            writer.write("TypeName.get(Map):" + TypeName.get(Map.class) + "\n");
                            writer.write("tm:" + Util.TypeUtils.asElement(tm).toString() + "\n");
                        }
                        writer.write("getInterfaces:" + eee.getInterfaces() + "\n");
                        for (TypeMirror tm : dt.getTypeArguments()) {
                            writer.write("tm:" + tm + "\n");
                        }
                    }
                }
            }
            writer.write("\nTypeParameters :" + executableElement.getTypeParameters().size());
            for (TypeParameterElement tpe : executableElement.getTypeParameters()) {
                Util.ElementUtils.printElements(writer, tpe);
            }

            TypeMirror mirror = targetElement.asType();
            ElementKind kind = targetElement.getKind();
            Set<Modifier> modifiers = targetElement.getModifiers();
            Element element = targetElement.getEnclosingElement();
            List<Element> elementList = (List<Element>) targetElement.getEnclosedElements();
            List<AnnotationMirror> annotationMirrors = (List<AnnotationMirror>) targetElement.getAnnotationMirrors();

            writer.write("\nTypeMirror: " + mirror + "\n");
            writer.write("ElementKind: " + kind + "\n");
            writer.write("Modifiers: " + modifiers + "\n");
            writer.write("EnclosingElement: " + element + "\n");
            writer.write("EnclosedElements: " + elementList + "\n");
            writer.write("AnnotationMirrors: " + annotationMirrors + "\n\n");

            writeElement(writer, element);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Util.Messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private static void writeElement(FileWriter writer, Element targetElement) {
        TypeMirror mirror = targetElement.asType();
        ElementKind kind = targetElement.getKind();
        Set<Modifier> modifiers = targetElement.getModifiers();
        Element element = targetElement.getEnclosingElement();
        List<Element> elementList = (List<Element>) targetElement.getEnclosedElements();
        List<AnnotationMirror> annotationMirrors = (List<AnnotationMirror>) targetElement.getAnnotationMirrors();
        try {
            writer.write("TypeMirror: " + mirror + "\n");
            writer.write("ElementKind: " + kind + "\n");
            writer.write("Modifiers: " + modifiers + "\n");
            writer.write("EnclosingElement: " + element + "\n");
            writer.write("EnclosedElements: " + elementList + "\n");
            writer.write("AnnotationMirrors: " + annotationMirrors + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testPrint(String name, String str) {
        final String fileLocation = "/home/trs/AndroidStudioProjects/RxBus/test_" + name + ".txt";
        File file = new File(fileLocation);
        try {
            if (!file.exists())
                file.createNewFile();
            else {
                file.delete();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PrintError(Element element, String format, Object... args) {
        Print(Diagnostic.Kind.ERROR, element, format, args);
    }

    public static void PrintNote(Element element, String format, Object... args) {
        Print(Diagnostic.Kind.NOTE, element, format, args);
    }

    private static void Print(Diagnostic.Kind kind, Element element, String format, Object... args) {
        if (args.length > 0)
            format = String.format(format, args);
        Util.Messager.printMessage(kind, format, element);
    }
}
