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

import javax.annotation.Priority;
import javax.inject.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.api.model.Model;
import org.apache.maven.api.services.Source;
import org.apache.maven.api.services.Sources;
import org.apache.maven.api.spi.ModelParser;
import org.apache.maven.api.spi.ModelParserException;
import org.yaml.snakeyaml.Yaml;

@Named("yaml")
@Priority(1)
public class YamlModelReader implements ModelParser {

    @Override
    public Optional<Source> locate(Path path) {
        Path pom = Files.isDirectory(path) ? path.resolve("pom.yaml") : path;
        return Files.isRegularFile(pom) ? Optional.of(Sources.fromPath(pom)) : Optional.empty();
    }

    @Override
    public Model parse(Source source, Map<String, ?> options) throws ModelParserException {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data;
            if (source.getPath() != null) {
                try (InputStream input = Files.newInputStream(source.getPath())) {
                    data = yaml.load(input);
                }
            } else {
                try (InputStream input = source.openStream()) {
                    data = yaml.load(new InputStreamReader(input, StandardCharsets.UTF_8));
                }
            }
            return new YamlReader().parseModel(data);
        } catch (IOException e) {
            throw new ModelParserException("Unable to parse: " + source.getLocation(), e);
        }
    }
}
