package com.arya

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

class AryaSimulationPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            for(Task jar : project.getTasksByName("jar", false)) {
                if(jar instanceof Jar) {
                    jar.getManifest().getAttributes().put("Main-Class", "com.arya.AryaApplicationLoader")
                    jar.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    def compile = project.getConfigurations().findByName("compileClasspath")
                    jar.from(compile.collect { it.isDirectory() ? it : project.zipTree(it) })
                }
            }
        }
    }

}
