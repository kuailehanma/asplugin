package com.skio.myplugins;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Created by 2820.
 * Desc: "description info"
 * Date: 2019/10/17
 * Time: 15:30
 */
public class LocalTransformUtil extends Transform  implements Plugin<Project>{

    @Override
    public String getName() {
        return "TransformUtil";
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

    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        System.out.println("=====>begin mytransform task..");
        System.out.println("=====>super.transform");
        super.transform(transformInvocation);
        for(int i=0;i<50;i++){
            System.out.println("=====>"+i);
        }
        System.out.println("=====>Mytransform task end");
    }


    @Override
    public void apply(Project project) {
        System.out.println("=====>Mytransform apply<=======");
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");
        appExtension.registerTransform( this, Collections.EMPTY_LIST);
    }
}
