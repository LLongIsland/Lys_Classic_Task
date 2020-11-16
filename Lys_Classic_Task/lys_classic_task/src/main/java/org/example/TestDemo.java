package org.example;

import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @program: lys_classic_task
 * @description: this class just for demo\
 * @author: Li Yongshao
 * @create: 2020-11-16 16:20
 */
public class TestDemo {


    public static void main(String[] args) throws IOException, InvalidClassFileException, WalaException, CancelException {
        ClassLoader classLoader=TestDemo.class.getClassLoader();
        File exclusion=new File("exclusion.txt");
        File file=new File("C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target\\classes\\net\\mooctest\\CMD.class");
        AnalysisScope scope=AnalysisScopeReader.readJavaScope("scope.txt",exclusion,classLoader);
        scope.addClassFileToScope(ClassLoaderReference.Application,file);
        File file1=new File("C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target\\test-classes\\net\\mooctest\\CMDTest.class");
        File file2=new File("C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target\\test-classes\\net\\mooctest\\CMDTest1.class");
        File file3=new File("C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target\\test-classes\\net\\mooctest\\CMDTest2.class");
        File file4=new File("C:\\Users\\Kotori\\Desktop\\经典大作业\\ClassicAutomatedTesting\\0-CMD\\target\\test-classes\\net\\mooctest\\CMDTest3.class");
        scope.addClassFileToScope(ClassLoaderReference.Application,file1);
        scope.addClassFileToScope(ClassLoaderReference.Application,file2);
        scope.addClassFileToScope(ClassLoaderReference.Application,file3);
        scope.addClassFileToScope(ClassLoaderReference.Application,file4);


        ClassHierarchy cha= ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps=new AllApplicationEntrypoints(scope,cha);
        CHACallGraph cg=new CHACallGraph(cha);
        cg.init(eps);
        for(CGNode node: cg){
            if(node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method=(ShrikeBTMethod)node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    String classInnerName=method.getDeclaringClass().getName().toString();
                    String signature=method.getSignature();

                    System.out.println(classInnerName+" "+signature);

                    Iterator<CGNode>pred=cg.getPredNodes(node);
                    System.out.println("pred------------------------------");
                    while(pred.hasNext()){
                        CGNode nd=pred.next();
                        if(nd.getMethod() instanceof ShrikeBTMethod){
                            ShrikeBTMethod m=(ShrikeBTMethod)nd.getMethod();
                            String cIN=m.getDeclaringClass().getName().toString();
                            String sig=m.getSignature();
                            System.out.println(cIN+" "+sig);
                        }
                    }
                    System.out.println("----------------------------------");
                }
            }else{
//                System.out.println(String.format("'%s'不是一个ShrikeBTMethod: %s",node.getMethod(),node.getMethod().getClass()));
            }
        }
        String stats= CallGraphStats.getStats(cg);
    }
}




