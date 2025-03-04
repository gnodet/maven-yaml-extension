/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.yaml;

import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.api.model.Build;
import org.apache.maven.api.model.Dependency;
import org.apache.maven.api.model.Extension;
import org.apache.maven.api.model.Model;
import org.apache.maven.api.model.Parent;
import org.apache.maven.api.model.Plugin;
import org.apache.maven.api.model.PluginExecution;
import org.apache.maven.api.model.PluginManagement;
import org.apache.maven.api.model.Prerequisites;
import org.apache.maven.api.model.ReportPlugin;
import org.apache.maven.api.model.Reporting;
import org.apache.maven.internal.xml.XmlNodeImpl;
import org.apache.maven.model.v4.MavenStaxWriter;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlModelReaderTest {

    @Test
    void testParse() throws Exception {
        Model actual = loadAndParseYaml("pom.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .parent(Parent.newBuilder()
                        .groupId("org.apache.maven.extensions")
                        .artifactId("maven-extensions")
                        .version("40")
                        .build())
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .properties(mapOf(
                        "my.property", "foo",
                        "pluginVersion", "3.9"))
                .dependencies(List.of(Dependency.newBuilder()
                        .groupId("org.apache.maven")
                        .artifactId("maven-api-core")
                        .version("4.0.0-alpha-8-SNAPSHOT")
                        .build()))
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testBuildPluginManagement() throws Exception {
        Model actual = loadAndParseYaml("build-plugin-management.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .build(Build.newBuilder()
                        .pluginManagement(PluginManagement.newBuilder()
                                .plugins(List.of(
                                        Plugin.newBuilder()
                                                .groupId("org.apache.maven.plugins")
                                                .artifactId("maven-compiler-plugin")
                                                .version("3.11.0")
                                                .build(),
                                        Plugin.newBuilder()
                                                .groupId("org.apache.maven.plugins")
                                                .artifactId("maven-checkstyle-plugin")
                                                .version("3.3.0")
                                                .configuration(new XmlNodeImpl(
                                                        "configuration",
                                                        null,
                                                        null,
                                                        List.of(
                                                                new XmlNodeImpl("source", "17", null, null, null),
                                                                new XmlNodeImpl("target", "17", null, null, null)),
                                                        null))
                                                .build(),
                                        Plugin.newBuilder()
                                                .groupId("org.apache.maven.plugins")
                                                .artifactId("maven-site-plugin")
                                                .version("3.12.1")
                                                .configuration(new XmlNodeImpl(
                                                        "configuration",
                                                        null,
                                                        null,
                                                        List.of(new XmlNodeImpl("locales", "en", null, null, null)),
                                                        null))
                                                .build(),
                                        Plugin.newBuilder()
                                                .groupId("org.apache.maven.plugins")
                                                .artifactId("maven-deploy-plugin")
                                                .version("3.1.1")
                                                .configuration(new XmlNodeImpl(
                                                        "configuration",
                                                        null,
                                                        null,
                                                        List.of(new XmlNodeImpl("skip", "false", null, null, null)),
                                                        null))
                                                .build(),
                                        Plugin.newBuilder()
                                                .groupId("org.apache.maven.plugins")
                                                .artifactId("maven-javadoc-plugin")
                                                .version("3.5.0")
                                                .build()))
                                .build())
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testBuildPlugins() throws Exception {
        Model actual = loadAndParseYaml("build-plugins.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .build(Build.newBuilder()
                        .plugins(List.of(
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-compiler-plugin")
                                        .version("3.11.0")
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-checkstyle-plugin")
                                        .version("3.3.0")
                                        .configuration(new XmlNodeImpl(
                                                "configuration",
                                                null,
                                                null,
                                                List.of(
                                                        new XmlNodeImpl("source", "17", null, null, null),
                                                        new XmlNodeImpl("target", "17", null, null, null)),
                                                null))
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-site-plugin")
                                        .version("3.12.1")
                                        .configuration(new XmlNodeImpl(
                                                "configuration",
                                                null,
                                                null,
                                                List.of(new XmlNodeImpl("locales", "en", null, null, null)),
                                                null))
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-deploy-plugin")
                                        .configuration(new XmlNodeImpl(
                                                "configuration",
                                                null,
                                                null,
                                                List.of(new XmlNodeImpl("skip", "false", null, null, null)),
                                                null))
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-javadoc-plugin")
                                        .build()))
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testBuildExtensions() throws Exception {
        Model actual = loadAndParseYaml("build-extensions.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .build(Build.newBuilder()
                        .extensions(List.of(Extension.newBuilder()
                                .groupId("org.apache.maven.extensions")
                                .artifactId("maven-build-cache-extension")
                                .version("1.0.0")
                                .build()))
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testPrerequisites() throws Exception {
        Model actual = loadAndParseYaml("prerequisites.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .prerequisites(Prerequisites.newBuilder().maven("3.3.1").build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testParent() throws Exception {
        Model actual = loadAndParseYaml("parent.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .parent(Parent.newBuilder()
                        .groupId("org.apache")
                        .artifactId("apache")
                        .version("28")
                        .build())
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testProperties() throws Exception {
        Model actual = loadAndParseYaml("properties.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .properties(mapOf(
                        "project.build.sourceEncoding", "UTF-8",
                        "maven.compiler.release", "17"))
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testPomId() throws Exception {
        Model actual = loadAndParseYaml("pom-id.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testReportPlugins() throws Exception {
        Model actual = loadAndParseYaml("report-plugins.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .name("Maven YAML Extension")
                .reporting(Reporting.newBuilder()
                        .plugins(List.of(ReportPlugin.newBuilder()
                                .groupId("org.apache.maven.plugins")
                                .artifactId("maven-surefire-report-plugin")
                                .version("3.1.2")
                                .configuration(new XmlNodeImpl(
                                        "configuration",
                                        null,
                                        null,
                                        List.of(new XmlNodeImpl("showSuccess", "true", null, null, null)),
                                        null))
                                .build()))
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testDependencyId() throws Exception {
        Model actual = loadAndParseYaml("dependency-id.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .dependencies(List.of(
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-core")
                                .version("3.9.0")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.junit.jupiter")
                                .artifactId("junit-jupiter-api")
                                .version("5.9.3")
                                .scope("test")
                                .build()))
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testParentId() throws Exception {
        Model actual = loadAndParseYaml("parent-id.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .parent(Parent.newBuilder()
                        .groupId("org.apache")
                        .artifactId("apache")
                        .version("28")
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testExample() throws Exception {
        Model actual = loadAndParseYaml("example.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .parent(Parent.newBuilder()
                        .groupId("org.apache.maven.extensions")
                        .artifactId("maven-extensions")
                        .version("43")
                        .build())
                .groupId("org.apache.maven.extensions")
                .artifactId("maven-yaml-extension")
                .version("1.0.0-SNAPSHOT")
                .packaging("jar")
                .properties(mapOf(
                        "javaVersion", "17",
                        "maven.version", "4.0.0-rc-3"))
                .dependencies(List.of(
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-api-spi")
                                .version("${maven.version}")
                                .scope("provided")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-api-core")
                                .version("${maven.version}")
                                .scope("provided")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-xml")
                                .version("${maven.version}")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.yaml")
                                .artifactId("snakeyaml")
                                .version("2.4")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("javax.inject")
                                .artifactId("javax.inject")
                                .version("1")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("javax.annotation")
                                .artifactId("javax.annotation-api")
                                .version("1.3.2")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.junit.jupiter")
                                .artifactId("junit-jupiter")
                                .version("5.12.0")
                                .scope("test")
                                .build(),
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-support")
                                .version("${maven.version}")
                                .scope("test")
                                .build()))
                .build(Build.newBuilder()
                        .plugins(List.of(
                                Plugin.newBuilder()
                                        .groupId("org.apache.maven.plugins")
                                        .artifactId("maven-dependency-plugin")
                                        .version("3.8.1")
                                        .executions(List.of(PluginExecution.newBuilder()
                                                .id("copy-model")
                                                .goals(List.of("copy"))
                                                .phase("generate-sources")
                                                .configuration(new XmlNodeImpl(
                                                        "configuration",
                                                        null,
                                                        null,
                                                        List.of(new XmlNodeImpl(
                                                                "artifactItems",
                                                                null,
                                                                null,
                                                                List.of(
                                                                        new XmlNodeImpl(
                                                                                "artifactItem",
                                                                                null,
                                                                                null,
                                                                                List.of(
                                                                                        new XmlNodeImpl(
                                                                                                "groupId",
                                                                                                "org.apache.maven",
                                                                                                null,
                                                                                                null,
                                                                                                null),
                                                                                        new XmlNodeImpl(
                                                                                                "artifactId",
                                                                                                "maven-api-model",
                                                                                                null,
                                                                                                null,
                                                                                                null),
                                                                                        new XmlNodeImpl(
                                                                                                "version",
                                                                                                "${maven.version}",
                                                                                                null,
                                                                                                null,
                                                                                                null),
                                                                                        new XmlNodeImpl(
                                                                                                "type", "mdo", null,
                                                                                                null, null)),
                                                                                null)),
                                                                null)),
                                                        null))
                                                .build()))
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.codehaus.modello")
                                        .artifactId("modello-maven-plugin")
                                        .version("2.1.1")
                                        .executions(List.of(PluginExecution.newBuilder()
                                                .id("generate-yaml-reader")
                                                .goals(List.of("velocity"))
                                                .phase("generate-sources")
                                                .configuration(new XmlNodeImpl(
                                                        "configuration",
                                                        null,
                                                        null,
                                                        List.of(
                                                                new XmlNodeImpl("version", "4.2.0", null, null, null),
                                                                new XmlNodeImpl(
                                                                        "models",
                                                                        "target/dependency/maven-api-model-${maven.version}.mdo",
                                                                        null,
                                                                        null,
                                                                        null),
                                                                new XmlNodeImpl(
                                                                        "templates",
                                                                        "src/mdo/yaml-reader.vm",
                                                                        null,
                                                                        null,
                                                                        null),
                                                                new XmlNodeImpl(
                                                                        "params",
                                                                        null,
                                                                        null,
                                                                        List.of(
                                                                                new XmlNodeImpl(
                                                                                        "packageModelV4",
                                                                                        "org.apache.maven.api.model",
                                                                                        null,
                                                                                        null,
                                                                                        null)),
                                                                        null)),
                                                        null))
                                                .build()))
                                        .build(),
                                Plugin.newBuilder()
                                        .groupId("org.eclipse.sisu")
                                        .artifactId("sisu-maven-plugin")
                                        .version("0.9.0.M3")
                                        .executions(List.of(PluginExecution.newBuilder()
                                                .id("index-project")
                                                .goals(List.of("main-index"))
                                                .build()))
                                        .build()))
                        .build())
                .build();

        assertModelEquals(expected, actual);
    }

    @Test
    void testDependencyGavParsing() throws Exception {
        Model actual = loadAndParseYaml("dependency-gav.yaml");
        Model expected = Model.newBuilder()
                .modelVersion("4.0.0")
                .dependencies(List.of(
                        // Simple g:a:v
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-core")
                                .version("3.9.0")
                                .build(),
                        // g:a:scope:v
                        Dependency.newBuilder()
                                .groupId("org.junit.jupiter")
                                .artifactId("junit-jupiter")
                                .scope("test")
                                .version("5.9.3")
                                .build(),
                        // g:a:v:type
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-model")
                                .version("3.9.0")
                                .type("jar")
                                .build(),
                        // g:a:scope:v:type
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-core")
                                .scope("provided")
                                .version("3.9.0")
                                .type("jar")
                                .build(),
                        // g:a:scope:v:type:classifier
                        Dependency.newBuilder()
                                .groupId("org.apache.maven")
                                .artifactId("maven-core")
                                .scope("test")
                                .version("3.9.0")
                                .type("test-jar")
                                .classifier("tests")
                                .build()))
                .build();

        assertModelEquals(expected, actual);
    }

    private Model loadAndParseYaml(String filename) throws Exception {
        File yamlFile = new File("src/test/resources/" + filename);
        Yaml yaml = new Yaml();

        Map<String, Object> data;
        try (InputStream input = new FileInputStream(yamlFile)) {
            data = yaml.load(input);
        }

        return new YamlReader().parseModel(data);
    }

    private void assertModelEquals(Model expected, Model actual) {
        try {
            StringWriter s1 = new StringWriter(1024);
            new MavenStaxWriter().write(s1, expected);
            StringWriter s2 = new StringWriter(1024);
            new MavenStaxWriter().write(s2, actual);
            assertEquals(s1.toString(), s2.toString());
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return Collections.unmodifiableMap(map);
    }
}
