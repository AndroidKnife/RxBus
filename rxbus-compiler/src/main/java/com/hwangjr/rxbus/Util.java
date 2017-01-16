package com.hwangjr.rxbus;

import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by trs on 16-11-25.
 */
final class Util {
    public static Types TypeUtils;//处理TypeMirror
    public static Elements ElementUtils;//处理Element
    public static Filer Filer;//一般用于生成文件、获取文件
    public static Messager Messager;//打印信息用

    private static final String TYPE_LIST = TypeName.get(List.class).toString();
    private static final String TYPE_MAP = TypeName.get(Map.class).toString();

    /**
     * 判断该元素的上层元素是否符合目标元素的上层元素
     *
     * @param element
     * @return
     */
    public static final boolean isStandardEncloseingClass(Element element) {
        //判断上层元素是否为类，而且是否为public修饰
        //然后判断包名，android和java开头的不行
        TypeElement encloseingElement = (TypeElement) element.getEnclosingElement();
        if (encloseingElement.getKind() != CLASS)
            return false;

        if (!encloseingElement.getModifiers().contains(PUBLIC))
            return false;

        String qualifiedName = encloseingElement.getQualifiedName().toString();
        if (qualifiedName.startsWith("android") || qualifiedName.startsWith("java"))
            return false;
        return true;
    }

    /**
     * 判断是否为目标方法
     *
     * @param element
     * @return
     */
    public static final boolean isStandardMethod(Element element) {
        //元素类型必须为method，必须public修饰，不能为static
        if (element.getKind() != METHOD)
            return false;

        if (!element.getModifiers().contains(PUBLIC) || element.getModifiers().contains(STATIC))
            return false;

        return true;
    }

    public static final boolean isListType(Element e) {
        return TYPE_LIST.equals(e.toString());
    }

    public static final boolean isMapType(Element e) {
        return TYPE_MAP.equals(e.toString());
    }
}
