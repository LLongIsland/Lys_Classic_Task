package org.example;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import org.example.aug.AugUtil;

import java.io.File;
import java.io.IOException;

/**
 * @program: lys_classic_task
 * @description: 处理路径与域的构建
 * @author: Li Yongshao
 * @create: 2020-11-16 23:09
 */
public class InitUtil {
    public static AnalysisScope scope;
    public static CHACallGraph cg;
    private static int selectType; //0 class; 1 method
    private static String target, change_info;


    /**
     * @Description: 初始化域
     * @Author: Li Yongshao
     * @date: 2020/11/16
     */
    private static void init() throws IOException {
        File exclusion = new File("exclusion.txt");
        ClassLoader classLoader = InitUtil.class.getClassLoader();
        scope = AnalysisScopeReader.readJavaScope("scope.txt", exclusion, classLoader);
    }

    /**
     * @Description: 解析输入的参数
     * @Params: 命令行后的 -m|c <project_target> <change_info>
     * @Author: Li Yongshao
     * @date: 2020/11/16
     */
    private static void parseArgs(String[] args) {
        //根据参数选择类级或方法级
        if (args[0].equals("-c"))
            selectType = 0;
        else if (args[0].equals("-m"))
            selectType = 1;
        //加载路径参数
        target = args[1];
        change_info = args[2];
    }

    /**
     * @Description: 将target目录中的class文件加入域
     * @Author: Li Yongshao
     * @date: 2020/11/16
     */
    private static void initScope() throws InvalidClassFileException {
        File targetFolder = new File(target);
        File[] files = targetFolder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals("classes")) {  //加入classes文件夹内的class文件
                getDirEx(file);
            } else if (file.isDirectory() && file.getName().equals("test-classes")) {  //加入test-classes文件夹内的class文件
                getDirEx(file);
            }
        }
    }

    private static void getDirEx(File file) throws InvalidClassFileException {
        File[] testFiles = file.listFiles();
        assert testFiles != null;
        testFiles=testFiles[0].listFiles();
        assert testFiles != null;
        testFiles=testFiles[0].listFiles();
        assert testFiles != null;
        for (File testFile : testFiles) {
            scope.addClassFileToScope(ClassLoaderReference.Application, testFile);
        }
    }

    /**
     * @Description: 初始化载入点，初始化图
     * @Author: Li Yongshao
     * @date: 2020/11/16
     */
    private static void initGraph() throws ClassHierarchyException, CancelException {
        ClassHierarchy cha= ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps=new AllApplicationEntrypoints(scope,cha);
        cg=new CHACallGraph(cha);
        cg.init(eps);
    }

    public static void main(String[] args) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        init();
        if(args.length==0){
            parseArgs(new String[]{"-c",
                    "C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target",
                    "C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\data\\change_info.txt"});
        }
        else parseArgs(args);
        initScope();
        initGraph();
        if(selectType==0)
            new AugUtil(cg,change_info).classLevelSelect();
        else if(selectType==1)
            new AugUtil(cg,change_info).methodLevelSelect();
    }
}