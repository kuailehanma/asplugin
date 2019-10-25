package com.skio.myplugins;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;

/**
 * Created by 2820.
 * Desc: "description info"
 * Date: 2019/10/17
 * Time: 15:30
 */
public class FirstPlutin extends Transform  implements Plugin<Project>{
    Project mProject;


    @Override
    public String getName() {
        return "FirstPlutin";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    /**
     * transformInvocation.getInputs() 中是传过来的输入流，其中有两种格式，一种是jar包格式一种是目录格式。
     * transformInvocation.getOutputProvider() 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
     *
     */
    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        System.out.println("=====>begin FirstPlutin transform task..");
        transfromImpl1(transformInvocation);
        System.out.println("=====>FirstPlutin transform task end");
    }

    private void transfromImpl2(TransformInvocation transformInvocation) throws IOException  {
        //采用流式处理
        //https://github.com/alfredxl/PluginDemo
    }

    private void transfromImpl1(TransformInvocation transformInvocation) throws IOException {
        Context context = transformInvocation.getContext();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        boolean isIncremental = transformInvocation.isIncremental();
        outputProvider.deleteAll();//删除上次编译记录
        for(TransformInput input: inputs){
            System.out.println("transform dir:");
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs();
            for(DirectoryInput dirInput:dirInputs){
                File inputFile = dirInput.getFile();
                //注入代码
                InjetUtil.inject(inputFile,mProject);

                File outputFile = outputProvider.getContentLocation(dirInput.getName(),
                        dirInput.getContentTypes(),
                        dirInput.getScopes(),
                        Format.DIRECTORY);
                FileUtils.copyDirectory(inputFile,outputFile);
                System.out.println(String.format("===>copy dir from:%s to:%s",inputFile.getAbsolutePath(),outputFile.getAbsolutePath()));
            }
            System.out.println("transform jar(third party):");
            Collection<JarInput> jarInputs = input.getJarInputs();
            for(JarInput jarInput:jarInputs){
                String jarName = jarInput.getName();
                String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
                if(jarName.endsWith(".jar")){
                    jarName = jarName.substring(0,jarName.length()-4);
                }
                File jarOutPut = outputProvider.getContentLocation(jarName+md5Name,
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                FileUtils.copyDirectory(jarInput.getFile(),jarOutPut);
                System.out.println(String.format("===>copy jar form:%s to:%s",jarInput.getFile().getAbsolutePath(),jarOutPut.getAbsolutePath()));
            }
        }

    }


    @Override
    public void apply(Project project) {
        System.out.println("=====>FirstPlutin apply<=======");
        this.mProject = project;
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");
        appExtension.registerTransform( this, Collections.EMPTY_LIST);
    }
}
