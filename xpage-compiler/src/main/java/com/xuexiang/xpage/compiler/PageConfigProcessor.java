package com.xuexiang.xpage.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.enums.PageCategory;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xpage.util.Consts;
import com.xuexiang.xpage.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.xuexiang.xpage.util.Consts.KEY_MODULE_NAME;


@AutoService(Processor.class)
@SupportedOptions(KEY_MODULE_NAME)
public class PageConfigProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Types mTypes;
    private Elements mElements;
    private Logger mLogger;

    /**
     * Module name, maybe its 'app' or others
     */
    private String moduleName = null;

    private static final String PACKAGE_NAME = "cn.zmmax.scm.page";

    private static final String PAGE_CONFIG_CLASS_NAME = "PageConfig";

    private TypeMirror mFragment = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();              // Generate class.
        mTypes = processingEnv.getTypeUtils();            // Get type utils.
        mElements = processingEnv.getElementUtils();      // Get class meta.
        mLogger = new Logger(processingEnv.getMessager());

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            mLogger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            mLogger.info("These no module name, at 'build.gradle', like :\n" +
                    "javaCompileOptions {\n" +
                    "    annotationProcessorOptions {\n" +
                    "        arguments = [ moduleName : project.getName() ]\n" +
                    "    }\n" +
                    "}\n");
            //默认是app
            moduleName = "app";
        }

        mFragment = mElements.getTypeElement(Consts.FRAGMENT).asType();

        mLogger.info(">>> PageConfigProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> pageElements = roundEnvironment.getElementsAnnotatedWith(Page.class);
            try {
                mLogger.info(">>> Found Pages, start... <<<");
                parsePages(pageElements);

            } catch (Exception e) {
                mLogger.error(e);
            }
            return true;
        }

        return false;
    }

    private void parsePages(Set<? extends Element> pageElements) throws IOException {
        if (CollectionUtils.isNotEmpty(pageElements)) {
            mLogger.info(">>> Found Pages, size is " + pageElements.size() + " <<<");

            ClassName pageConfigClassName = ClassName.get(PACKAGE_NAME, upperFirstLetter(moduleName) + PAGE_CONFIG_CLASS_NAME);
            TypeSpec.Builder pageConfigBuilder = TypeSpec.classBuilder(pageConfigClassName);

             /*
               private static PageConfig sInstance;
             */
            FieldSpec instanceField = FieldSpec.builder(pageConfigClassName, "sInstance")
                    .addModifiers(Modifier.PRIVATE)
                    .addModifiers(Modifier.STATIC)
                    .build();

            /*

              ``List<PageInfo>```
             */
            ParameterizedTypeName inputListTypeOfPage = ParameterizedTypeName.get(
                    ClassName.get(List.class),
                    ClassName.get(PageInfo.class)
            );

             /*
               private List<PageInfo> mPages = new ArrayList<>();
             */
            FieldSpec pagesField = FieldSpec.builder(inputListTypeOfPage, "mPages")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

             /*
               private List<PageInfo> purchaseList = new ArrayList<>();
             */
            FieldSpec purchaseField = FieldSpec.builder(inputListTypeOfPage, "purchaseList")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

             /*
               private List<PageInfo> warehouseList = new ArrayList<>();
             */
            FieldSpec warehouseField = FieldSpec.builder(inputListTypeOfPage, "warehouseList")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            /*
               private List<PageInfo> workList = new ArrayList<>();
             */
            FieldSpec workField = FieldSpec.builder(inputListTypeOfPage, "workList")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

             /*
               private List<PageInfo> workList = new ArrayList<>();
             */
            FieldSpec reportField = FieldSpec.builder(inputListTypeOfPage, "reportList")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("mPages = new $T<>()", ClassName.get(ArrayList.class))
                    .addStatement("purchaseList = new $T<>()", ClassName.get(ArrayList.class))
                    .addStatement("warehouseList = new $T<>()", ClassName.get(ArrayList.class))
                    .addStatement("workList = new $T<>()", ClassName.get(ArrayList.class))
                    .addStatement("reportList = new $T<>()", ClassName.get(ArrayList.class));


            TypeMirror tm;
            String name;
            for (Element element : pageElements) {
                tm = element.asType();
                // Fragment
                if (mTypes.isSubtype(tm, mFragment)) {
                    mLogger.info(">>> Found Fragment Page: " + tm.toString() + " <<<");
                    Page page = element.getAnnotation(Page.class);
                    name = StringUtils.isEmpty(page.name()) ? element.getSimpleName().toString() : page.name();
                    int extra = page.extra();
                    int sort = page.sort();
                    PageCategory pageCategory = page.category();
                    switch (pageCategory) {
                        case purchase:
                            constructorBuilder.addStatement("purchaseList.add(new $T($S, $S, $S, $T.$L, $L, $L))",
                                    PageInfo.class,
                                    name,
                                    tm.toString(),
                                    PageInfo.getParams(page.params()),
                                    ClassName.get(CoreAnim.class),
                                    page.anim(),
                                    extra, sort);
                            break;
                        case warehouse:
                            constructorBuilder.addStatement("warehouseList.add(new $T($S, $S, $S, $T.$L, $L, $L))",
                                    PageInfo.class,
                                    name,
                                    tm.toString(),
                                    PageInfo.getParams(page.params()),
                                    ClassName.get(CoreAnim.class),
                                    page.anim(),
                                    extra, sort);
                            break;
                        case work:
                            constructorBuilder.addStatement("workList.add(new $T($S, $S, $S, $T.$L, $L, $L))",
                                    PageInfo.class,
                                    name,
                                    tm.toString(),
                                    PageInfo.getParams(page.params()),
                                    ClassName.get(CoreAnim.class),
                                    page.anim(),
                                    extra, sort);
                            break;
                        case report:
                            constructorBuilder.addStatement("reportList.add(new $T($S, $S, $S, $T.$L, $L, $L))",
                                    PageInfo.class,
                                    name,
                                    tm.toString(),
                                    PageInfo.getParams(page.params()),
                                    ClassName.get(CoreAnim.class),
                                    page.anim(),
                                    extra, sort);
                            break;
                    }
                    constructorBuilder.addStatement("mPages.add(new $T($S, $S, $S, $T.$L, $L))",
                            PageInfo.class,
                            name,
                            tm.toString(),
                            PageInfo.getParams(page.params()),
                            ClassName.get(CoreAnim.class),
                            page.anim(),
                            page.extra());
                }
            }

            MethodSpec constructorMethod = constructorBuilder.build();

            MethodSpec instanceMethod = MethodSpec.methodBuilder("getInstance")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(pageConfigClassName)
                    .addCode("if (sInstance == null) {\n" +
                            "    synchronized ($T.class) {\n" +
                            "        if (sInstance == null) {\n" +
                            "            sInstance = new $T();\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n", pageConfigClassName, pageConfigClassName)
                    .addStatement("return sInstance")
                    .build();

            MethodSpec getPagesMethod = MethodSpec.methodBuilder("getPages")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(inputListTypeOfPage)
                    .addStatement("return mPages")
                    .build();

            MethodSpec getPurchaseMethod = MethodSpec.methodBuilder("getPurchaseList")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(inputListTypeOfPage)
                    .addStatement("return purchaseList")
                    .build();

            MethodSpec getWarehouseMethod = MethodSpec.methodBuilder("getWarehouseList")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(inputListTypeOfPage)
                    .addStatement("return warehouseList")
                    .build();

            MethodSpec getWorkMethod = MethodSpec.methodBuilder("getWorkList")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(inputListTypeOfPage)
                    .addStatement("return workList")
                    .build();

            MethodSpec getReportMethod = MethodSpec.methodBuilder("getReportList")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(inputListTypeOfPage)
                    .addStatement("return reportList")
                    .build();

            CodeBlock javaDoc = CodeBlock.builder()
                    .add("<p></p>\n")
                    .add("\n")
                    .add("@author yh \n")
                    .build();

            pageConfigBuilder
                    .addJavadoc(javaDoc)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(instanceField)
                    .addField(pagesField)
                    .addField(purchaseField)
                    .addField(warehouseField)
                    .addField(workField)
                    .addField(reportField)
                    .addMethod(constructorMethod)
                    .addMethod(instanceMethod)
                    .addMethod(getPagesMethod)
                    .addMethod(getPurchaseMethod)
                    .addMethod(getWarehouseMethod)
                    .addMethod(getWorkMethod)
                    .addMethod(getReportMethod);
            ;
            JavaFile.builder(PACKAGE_NAME, pageConfigBuilder.build()).build().writeTo(mFiler);
        }
    }


    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Page.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }



    public static String upperFirstLetter(final String s) {
        if (StringUtils.isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }
}
