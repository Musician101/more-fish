package me.elsiff.morefish.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class Lang implements TagResolver {

    private final static List<String> langFileNames = List.of("command", "editor", "gui", "main");
    private final ConfigurationNode node = CommentedConfigurationNode.root();

    public String readablePath(NodePath path, CharSequence delimiter) {
        return Arrays.stream(path.array()).map(Object::toString).collect(Collectors.joining(delimiter));
    }

    public String readablePath(NodePath path) {
        return readablePath(path, ":");
    }

    public ConfigurationNode node(NodePath path) {
        return node.node(path);
    }

    public String rawString(NodePath path) {
        return node(path).getString(readablePath(path));
    }

    public String rawString(Object... path) {
        return rawString(NodePath.path(path));
    }

    public List<String> rawStrings(NodePath path) {
        List<String> defaultList = List.of(readablePath(path));
        try {
            return node(path).getList(String.class, defaultList);
        }
        catch (SerializationException e) {
            getPlugin().getComponentLogger().error(getComponent(NodePath.path("main", "list-error"), Placeholder.parsed("node-path", readablePath(path))), e);
            return defaultList;
        }
    }

    public List<String> rawStrings(Object... path) {
        return rawStrings(NodePath.path(path));
    }

    public Component getComponent(Object... path) {
        return getComponent(NodePath.path(path));
    }

    public Component getComponent(NodePath path) {
        return getComponent(path, TagResolver.empty());
    }

    public Component getComponent(TagResolver tagResolver, Object... path) {
        return getComponent(NodePath.path(path), tagResolver);
    }

    public Component getComponent(NodePath path, TagResolver tagResolver) {
        String message = "<mf-lang:" + readablePath(path) + ">";
        return parse(message, tagResolver);
    }

    public List<Component> getComponents(Object... path) {
        return getComponents(NodePath.path(path));
    }

    public List<Component> getComponents(NodePath path) {
        return getComponents(path, TagResolver.empty());
    }

    public List<Component> getComponents(TagResolver tagResolver, Object... path) {
        return getComponents(NodePath.path(path), tagResolver);
    }

    public List<Component> getComponents(NodePath path, TagResolver tagResolver) {
        List<String> lines = rawStrings(path);
        return parse(lines, tagResolver);
    }

    private Path langDir() {
        return getPlugin().getDataPath().resolve("lang");
    }

    public void load() throws Exception {
        Exception exception = new Exception("One or more errors occurred while loading language files.");
        langFileNames.stream().map(this::load).<ConfigurateException>mapMulti(Optional::ifPresent).forEach(exception::addSuppressed);
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    private YamlConfigurationLoader loader(String name) {
        return YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).path(langDir().resolve(name + ".yml")).build();
    }

    private Optional<ConfigurateException> load(String name) {
        try {
            getPlugin().saveResource("lang/" + name + ".yml", false);
            YamlConfigurationLoader loader = loader(name);
            node(NodePath.path(name)).set(loader.load());
            return Optional.empty();
        }
        catch (ConfigurateException e) {
            return Optional.of(e);
        }
    }

    public void save(String name) throws ConfigurateException {
        YamlConfigurationLoader loader = loader(name);
        loader.save(node(NodePath.path(name)));
    }

    public Component parse(String message) {
        return parse(message, TagResolver.empty());
    }

    public Component parse(String message, TagResolver tagResolver) {
        return MiniMessage.miniMessage().deserialize(message, this, tagResolver);
    }

    public List<Component> parse(List<String> message, TagResolver resolver) {
        return message.stream().map(s -> parse(s, resolver)).collect(Collectors.toList());
    }

    public List<Component> parseComponents(List<Component> message, TagResolver resolver) {
        return message.stream().map(PlainTextComponentSerializer.plainText()::serialize).map(s -> parse(s, resolver)).collect(Collectors.toList());
    }

    @Nullable
    private Tag tag(NodePath path, ArgumentQueue arguments, Context ctx) {
        ConfigurationNode node = this.node.node(path);
        if (node.isList()) {
            return TagResolverUtil.fromList(rawStrings(path), arguments, ctx, Tag::preProcessParsed);
        }
        else if (node.isMap()) {
            return TagResolverUtil.fromMap(node.childrenMap(), arguments, ctx, s -> s, n -> tag(n.path(), arguments, ctx));
        }

        String rawString = node.getString();
        if (rawString != null) {
            return Tag.selfClosingInserting(ctx.deserialize(rawString));
        }

        return null;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            NodePath path = NodePath.path(arguments.popOr("Need at least one argument for mf-lang").value());
            return tag(path, arguments, ctx);
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("mf-lang");
    }
}
