package com.hwangjr.rxbus;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by trs on 16-11-24.
 */
@AutoService(Processor.class)
public class RxBusProProcessor extends AbstractProcessor {

    private static final Map<TypeElement, ProxyBuilder> PROXYS = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Util.TypeUtils = processingEnv.getTypeUtils();
        Util.ElementUtils = processingEnv.getElementUtils();
        Util.Filer = processingEnv.getFiler();
        Util.Messager = processingEnv.getMessager();
    }

    /**
     * @param annotations 该处理器声明支持并已经在源码中使用了的注解
     * @param roundEnv    注解处理的上下文环境妈的
     * @return true:支持的注解处理完毕；false:支持的注解在该处理器处理完毕后，接着给其他处理器处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //获取所有被目标注解标记的元素
        Set<Element> targetElements = (Set<Element>) roundEnv.getElementsAnnotatedWith(Subscribe.class);
        for (Element e : targetElements) {
            try {
                if (!SuperficialValidation.validateElement(e))
                    continue;
                if (!Util.isStandardEncloseingClass(e) || !Util.isStandardMethod(e))
                    continue;
//                Printer.SamplePrint2(e);
                Printer.SamplePrint3(e);
                addProxy(e);
            } catch (Exception ee) {
                ee.printStackTrace();
                Printer.PrintError(e, ee.getMessage());
            }
        }
        createProxy();
        return true;
    }

    /**
     * 替代@SupportedAnnotationTypes
     *
     * @return 支持的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(Subscribe.class.getCanonicalName());
        types.add(RxThread.class.getCanonicalName());
        return types;
    }

    /**
     * 替代@SupportedSourceVersion
     *
     * @return 支持的java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //默认返回1.6，改为支持最新版本
        return SourceVersion.latestSupported();
    }

    private void addProxy(Element e) {
        TypeElement clazz = (TypeElement) e.getEnclosingElement();
        ProxyBuilder proxyBuilder = PROXYS.get(clazz);
        if (proxyBuilder == null) {
            proxyBuilder = new ProxyBuilder(ClassName.get(clazz));
            PROXYS.put(clazz, proxyBuilder);
        }

        ThreadType threadType = ThreadType.Immediate;
//        List<? extends AnnotationMirror> annoList = e.getAnnotationMirrors();
//        for (AnnotationMirror mirror : annoList) {
//            Element annoElement = mirror.getAnnotationType().asElement();
//            if (annoElement.getKind().equals(ANNOTATION_TYPE)) {
//                if (RxThread.class.getCanonicalName().equals(annoElement.toString())) {
//                    Printer.PrintNote(mMessager, e, "Element is %s", e.getSimpleName().toString());
//                    Printer.PrintNote(mMessager, e, "annoElement is %s", annoElement.getSimpleName().toString());
//                    threadType = annoElement.getAnnotation(RxThread.class).value();
//                }
//            }
//        }
        RxThread rxThread = e.getAnnotation(RxThread.class);
        if (rxThread != null)
            threadType = rxThread.value();

        MethodBinder methodBinder = new MethodBinder();
        methodBinder.setMethodName(e.getSimpleName().toString());
        methodBinder.setThreadType(threadType);

        String[] tags = e.getAnnotation(Subscribe.class).value();
        for (String tag : tags)
            methodBinder.addTag(tag);

        ExecutableElement executableElement = (ExecutableElement) e;
        int size = executableElement.getParameters().size();
        if (size > 1) {
            Printer.PrintError(executableElement, "%s paramters size can't more than 1!", executableElement.getSimpleName().toString());
        } else if (size == 1) {
            VariableElement ve = executableElement.getParameters().get(0);
            methodBinder.setParamType(ve.asType());
        } else {

        }
        proxyBuilder.addMethod(methodBinder);
    }

    private void createProxy() {
        for (ProxyBuilder pb : PROXYS.values()) {
            pb.build(Util.Filer);
//            Printer.testPrint(pb.getClassName(), pb.toString());
        }
    }
}
