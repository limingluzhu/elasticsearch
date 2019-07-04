package com.carrotsearch.gradle.junit4

import com.carrotsearch.ant.tasks.junit4.JUnit4
import org.gradle.api.AntBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

class RandomizedTestingPlugin implements Plugin<Project> {

    void apply(Project project) {
        replaceTestTask(project.tasks)
        configureAnt(project.ant)
    }

    static void replaceTestTask(TaskContainer tasks) {
        TaskProvider<Test> oldTestProvider
        try {
            oldTestProvider = tasks.named('test')
        } catch (UnknownTaskException unused) {
            // no test task, ok, user will use testing task on their own
            return
        }
        Test oldTestTask = oldTestProvider.get()
        if (oldTestTask == null) {
            // no test task, ok, user will use testing task on their own
            return
        }

        // we still have to use replace here despite the remove above because the task container knows about the provider
        // by the same name
        RandomizedTestingTask newTestTask = tasks.replace('test', RandomizedTestingTask)
        newTestTask.configure{
            group =  JavaBasePlugin.VERIFICATION_GROUP
            description = 'Runs unit tests with the randomized testing framework'
            dependsOn oldTestTask.dependsOn, 'testClasses'
            classpath = oldTestTask.classpath
        }


        System.out.println("testClassesDir": newTestTask.testClassesDir)
        // hack so check task depends on custom test
        Task checkTask = tasks.getByName('check')
        checkTask.dependsOn.remove(oldTestTask)
        checkTask.dependsOn.remove(oldTestProvider)
        checkTask.dependsOn.add(newTestTask)
    }

    static void configureAnt(AntBuilder ant) {
        ant.project.addTaskDefinition('junit4:junit4', JUnit4.class)
    }
}
