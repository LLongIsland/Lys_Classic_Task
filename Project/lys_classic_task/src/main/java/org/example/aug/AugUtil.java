package org.example.aug;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @program: lys_classic_task
 * @description: 辅助工具类
 * @author: Li Yongshao
 * @create: 2020-11-17 00:29
 */
public class AugUtil {
    private String path;
    private CHACallGraph cg;
    private ArrayList<MethodEdge> methodEdgePairs;
    private ArrayList<ClassEdge> classEdgePairs;
    private ArrayList<AugEntry> classRecord;

    public void getDAG(){
        for(CGNode node:cg){
            if(node.getMethod() instanceof ShrikeBTMethod){
                ShrikeBTMethod method=(ShrikeBTMethod)node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())){
                    String classInnerName=method.getDeclaringClass().getName().toString(); //类的内部表示
                    String signature=method.getSignature(); //方法签名
                    //记录所有类与各自的方法
                    boolean existFlag=false;
                    for(AugEntry augEntry:classRecord){
                        if(augEntry.iClass.equals(method.getDeclaringClass())){
                            existFlag=true;
                            augEntry.tryAddMethod(method);
                        }
                    }
                    if(!existFlag) {
                        AugEntry augEntry = new AugEntry(method.getDeclaringClass());
                        augEntry.tryAddMethod(method);
                        classRecord.add(augEntry);
                    }

//                    System.out.println(classInnerName+" "+signature);

                    //分别记录类、方法等级的边
                    Iterator<CGNode> pred=cg.getPredNodes(node); //pred记录了当前方法调用了哪些方法
                    while(pred.hasNext()){
                        CGNode nd=pred.next();
                        if(nd.getMethod() instanceof ShrikeBTMethod){
                            ShrikeBTMethod methodEnd=(ShrikeBTMethod)nd.getMethod();
                            //记录类级的边
                            boolean classEdgeExistFlag=false;
                            for(ClassEdge classEdge:classEdgePairs){
                                if(classEdge.begin.equals(method.getDeclaringClass())&&
                                classEdge.end.equals(methodEnd.getDeclaringClass()))
                                    classEdgeExistFlag=true;
                            }
                            if(!classEdgeExistFlag){
                                classEdgePairs.add(new ClassEdge(method.getDeclaringClass(),methodEnd.getDeclaringClass()));
                            }
                            //记录方法级的边
                            boolean methodEdgeExistFlag=false;
                            for(MethodEdge methodEdge:methodEdgePairs){
                                if(methodEdge.begin.equals(method)&&methodEdge.end.equals(methodEnd))
                                    methodEdgeExistFlag=true;
                            }
                            if(!methodEdgeExistFlag){
                                methodEdgePairs.add(new MethodEdge(method,methodEnd));
                            }
                        }
                    }

                }
            }
        }
    }
    public void classLevelSelect(){

    }
    public void methodLevelSelect(){

    }
    //TODO
    private void outputDotFile(){}

    public AugUtil(CHACallGraph cg,String change_info){
        this.cg=cg;
        this.path=change_info;
        this.classEdgePairs=new ArrayList<>();
        this.methodEdgePairs=new ArrayList<>();
        this.classRecord=new ArrayList<>();
        getDAG();
        System.out.println("Test");
    }
}