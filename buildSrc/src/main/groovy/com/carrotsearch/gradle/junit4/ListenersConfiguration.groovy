package com.carrotsearch.gradle.junit4

import com.carrotsearch.ant.tasks.junit4.listeners.AggregatedEventListener
import com.carrotsearch.ant.tasks.junit4.listeners.antxml.AntXmlReport


class ListenersConfiguration {
    RandomizedTestingTask task
    List<AggregatedEventListener> listeners = new ArrayList<>()

    void junitReport(Map<String, Object> props) {
        AntXmlReport reportListener = new AntXmlReport()
        Object dir = props == null ? null : props.get('dir')
        if (dir != null) {
            reportListener.setDir(task.project.file(dir))
        } else {
            reportListener.setDir(new File("E://code//es-junit2//" + task.project.version, 'reports' + File.separator + "xml"))
        }
        listeners.add(reportListener)
    }

    void custom(AggregatedEventListener listener) {
        listeners.add(listener)
    }
}
