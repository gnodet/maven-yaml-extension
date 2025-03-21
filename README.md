<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
[Apache Maven YAML Extension](https://maven.apache.org/extensions/maven-yaml-extension/)
==================================

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.maven.extensions/maven-yaml-extension.svg?label=Maven%20Central)](https://search.maven.org/artifact/org.apache.maven.extensions/maven-yaml-extension)

This project provides a YAML POM parser extension for Maven 4. It allows POMs to
be written with the [YAML](https://yaml.org/) syntax.

License
-------
This code is under the [Apache License, Version 2.0, January 2004](./LICENSE).

See the [`NOTICE`](./NOTICE) file for required notices and attributions.

Usage
-----
To use this extension, the following declaration needs to be done in your `${rootDirectory}/.mvn/extensions.xml`:
```xml
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.2.0">
    <extension>
        <groupId>org.apache.maven.extensions</groupId>
        <artifactId>maven-yaml-extension</artifactId>
        <version>@project.version@</version>
    </extension>
</extensions>
```
This allows defining a POM using YAML syntax:
```yaml
modelVersion: 4.0.0
parent: org.apache.maven.extensions:maven-extensions:43

id: org.apache.maven.extensions:maven-yaml-extension:1.0.0-SNAPSHOT
packaging: jar

properties:
  javaVersion: 17
  maven.version: 4.0.0-rc-3

dependencies:
  # runtime dependencies
  - org.apache.maven:maven-api-spi:provided:${maven.version}
  - org.apache.maven:maven-api-core:provided:${maven.version}
  - org.apache.maven:maven-xml:${maven.version}
  - org.yaml:snakeyaml:2.4
  - javax.inject:javax.inject:1
  - javax.annotation:javax.annotation-api:1.3.2
  # test dependencies
  - org.junit.jupiter:junit-jupiter:test:5.12.0
  - org.apache.maven:maven-support:test:${maven.version}

build:
  plugins:
    - id: org.apache.maven.plugins:maven-dependency-plugin:3.8.1
      executions:
        - id: copy-model
          goals: [copy]
          phase: generate-sources
          configuration:
            artifactItems:
              - artifactItem:
                  - groupId: org.apache.maven
                    artifactId: maven-api-model
                    version: ${maven.version}
                    type: mdo

    - id: org.codehaus.modello:modello-maven-plugin:2.1.1
      executions:
        - id: generate-yaml-reader
          goals: [velocity]
          phase: generate-sources
          configuration:
            version: 4.2.0
            models: ['target/dependency/maven-api-model-${maven.version}.mdo']
            templates: ['src/mdo/yaml-reader.vm']
            params:
              packageModelV4: org.apache.maven.api.model

    - id: org.eclipse.sisu:sisu-maven-plugin:0.9.0.M3
      executions:
        - id: index-project
          goals: [main-index]
```
