自定义gradle插件

 一、创建仅在本项目中使用的插件

    注:Gradle 插件是使用 Groovy 语言进行开发的，而 Groovy 是可以兼容 Java 的。

    1、新建插件module，module名必须是“buildSrc”（任意种类的module，因为后续一定会修改）
    2、建立文件结构：buildSrc/src/main/groovy。 和 buildSrc/src/main/resources/META-INF/gradle-plugins
    3、删除其他文件结构，仅留下必要的文件目录如下：
        buildSrc/src/main/groovy:放插件代码文件的目录（groovy文件或者java文件）
        buildSrc/src/main/resources/META-INF/gradle-plugins:放*.properties文件
        buildSrc/build.gradle：编译脚本
    4、编译脚本：
        apply plugin: 'java'
        apply plugin: 'groovy'
        apply plugin: 'maven'

        repositories {
            google()
            mavenCentral()
            jcenter()
        }

        dependencies {
            implementation gradleApi()
            implementation localGroovy()
            //implementation 'org.javassist:javassist:3.18.+'
            implementation 'com.android.tools.build:gradle:3.4.2'
        }
    5、插件代码:
        在插件目录src/main/groovy中构建自己的包名结构如：com.skio.plugin,并在其中建立代码实现Plugin<Project>接口
            public class MyPlugin implements Plugin<Project> {
                @Override
                public void apply(Project project) {
                    Logger logger =  project.getLogger();
                    logger.error("==========>MyPlugin is started");
                }
             }
    6、配置properties文件：
        在src/main/resources/META-INF/gradle-plugins目录建立文件xx.properties,并配置
        implementation-class=com.skio.localtools.LocalTransformUtil（完整的插件类名）

    7、在主app中引用插件：
        在settings.gradle中应用buildSrc模块：include ':app', ':buildSrc'
        在app中依赖buildSrc模块（仅编译期依赖）： compileOnly project(':buildSrc')
        在编译脚本中引用插件：apply plugin: com.skio.localtools.LocalTransformUtil（properties文件中配置的插件 路径）
    8、执行app编译，观察编译器输出日志："==========>MyPlugin is started"

 二、将插件发布到本地仓库

    注：插件开发见“一、创建仅在本项目中使用的插件” 。区别是：此时插件module命名没有特殊要求
    1、在插件目录配置仓库本地地址
        //发插件到本地
        uploadArchives {
            repositories.mavenDeployer {
                repository(url: uri('../repo')) //仓库的路径，此处是项目根目录下的 repo 的文件夹
                pom.groupId = 'com.skio.plugin'  //groupId ，自行定义，一般是包名
                pom.artifactId = 'firstPlugin' //artifactId ，自行定义
                pom.version = '0.0.1' //version 版本号
            }
        }
    2、执行插件的上传脚本 upload/uploadArchives
    3、在主项目中添加仓库依赖：
        buildscript {
            repositories {
                google()
                jcenter()
                maven { url LOCAL_REPO_URL }
            }
        }
        并在gradle.properties 或者 local.properties中配置常量：  LOCAL_REPO_URL=./repo
    4、主项目最外层gradle依赖插件：
        dependencies {
                classpath 'com.android.tools.build:gradle:3.4.2'
                classpath 'com.skio.plugin:firstPlugin:0.0.1'
        }
    5、在主项目的app（或其他需要引用插件的模块）添加插件的引用：
        apply plugin:com.skio.myplugins.FirstPlutin
        或者：apply plugin: 'com.skio.myplugin'（配置文件名）
    6、编译项目，查看编译输出日志："==========>MyPlugin is started"

    二、将插件发布到远程仓库（阿里云）

    1、配置上传脚本（仓库地址，用户名，密码，插件版本号等）
        uploadArchives {
            configuration = configurations.archives
            repositories {
                mavenDeployer {
                    snapshotRepository(url: MAVEN_SNAPSHOT_URL) {
                        authentication(userName: NEXUS_USERNAME, password: NEXUS_PASSWORD)
                    }
                    repository(url: MAVEN_URL) {
                        authentication(userName: NEXUS_USERNAME, password: NEXUS_PASSWORD)
                    }
                    pom.project {
                        version '0.0.1'
                        artifactId 'myPlutin'
                        groupId 'com.skio.myplugin'
                        packaging 'jar'
                        description "test .."
                    }
                }
            }
        }
     2、在主项目依赖插件
        buildscript {
            repositories {
                google()
                jcenter()
                maven {
                    url MY_MAVEN_URL
                }
                maven {
                    credentials {
                        username NEXUS_USERNAME
                        password NEXUS_PASSWORD
                    }
                    url MAVEN_URL
                }
                maven {
                    credentials {
                        username NEXUS_USERNAME
                        password NEXUS_PASSWORD
                    }
                    url MAVEN_SNAPSHOT_URL
                }
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:3.4.2'
                classpath 'com.skio.myplugin:myPlutin:0.0.1'
            }
        }
     3、在需要引用插件的模块引用插件
        apply plugin: 'com.skio.myplugin'

二、 javassist使用