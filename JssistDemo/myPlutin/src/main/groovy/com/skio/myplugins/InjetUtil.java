package com.skio.myplugins;

import org.gradle.api.Project;

import java.io.File;

import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * Created by 2820.
 * Desc: "description info"
 * Date: 2019/10/22
 * Time: 16:06
 */
public class InjetUtil {

    private static final ClassPool mPool = ClassPool.getDefault();
    public static void inject(File inputFile, Project mProject) {
        String path = inputFile.getAbsolutePath();
        try {
            mPool.appendClassPath(path);
            eacheFileinject(inputFile);
        } catch (NotFoundException e) {
            e.printStackTrace();
            System.out.println(e+"");
        }

    }

    private static void eacheFileinject(File inputFile) {
        String fileName = inputFile.getAbsolutePath();
        if(inputFile.isDirectory()){
            File[] files = inputFile.listFiles();
            for(File file:files){
                eacheFileinject(file);
            }
        }else{
            System.out.println("-----injectClass:"+fileName+"-------");
        }
    }
}
