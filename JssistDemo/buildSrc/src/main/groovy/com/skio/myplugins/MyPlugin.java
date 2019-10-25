package com.skio.myplugins;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Created by 2820.
 * Desc: "description info"
 * Date: 2019/10/17
 * Time: 16:14
 */
public class MyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger =  project.getLogger();
        logger.error("==========>MyPlugin is started");
        for(int i=0;i<50;i++) {
            logger.error("==========>MyPlugin:"+i);
        }
        logger.error("==========>MyPlugin is end");
        logger.error("==========>project.className = "+project.getClass().getName());


    }
}
