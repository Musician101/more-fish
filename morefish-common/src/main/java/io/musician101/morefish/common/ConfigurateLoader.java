package io.musician101.morefish.common;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.toml.TOMLConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

@FunctionalInterface
public interface ConfigurateLoader {

    ConfigurateLoader HOCON = path -> HoconConfigurationLoader.builder().setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8)).setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8)).build();
    ConfigurateLoader TOML = path -> TOMLConfigurationLoader.builder().setKeyIndent(2).setTableIndent(2).setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8)).setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8)).build();
    ConfigurateLoader YAML = path -> YAMLConfigurationLoader.builder().setFlowStyle(FlowStyle.BLOCK).setIndent(2).setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8)).setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8)).build();

    ConfigurationLoader<? extends ConfigurationNode> get(Path path);
}
