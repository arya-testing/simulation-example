package dev.testment.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

class TestmentSimulationPlugin implements Plugin<Project> {

    private List<String> excludedArtifacts = ["META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA"]

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            for(Task jar : project.getTasksByName("jar", false)) {
                if(jar instanceof Jar) {
                    jar.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    def config = project.getConfigurations().findByName("compileClasspath")
                    jar.from(config.collect { it.isDirectory() ? it : project.zipTree(it) }).exclude(excludedArtifacts)
                    jar.getManifest().getAttributes().put("Main-Class", "dev.testment.core.TestmentMain")
                }
            }
        }
    }

}
