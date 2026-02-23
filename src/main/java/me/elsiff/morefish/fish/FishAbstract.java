package me.elsiff.morefish.fish;

import me.elsiff.morefish.fish.PlayerAnnouncement.Type;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class FishAbstract<F extends FishAbstract<F>> implements Comparable<F>, ComponentLike, Keyed, TagResolver {

    protected final NamespacedKey key;
    private final String tagKey;
    private String displayName;
    private PlayerAnnouncement announcement = new PlayerAnnouncement(Type.SERVER, 0.1);
    private FishConditions conditions = new FishConditions();
    private List<String> commands = List.of();
    private boolean skipItemFormat = false;
    private boolean noDisplay = false;
    private boolean firework = false;
    private boolean doNotSell = false;
    private float priceMultiplier;

    protected FishAbstract(String tagKey, NamespacedKey key, String displayName) {
        this(tagKey, key, displayName, 0);
    }

    protected FishAbstract(String tagKey, NamespacedKey key, String displayName, float priceMultiplier) {
        this.tagKey = tagKey;
        this.key = key;
        this.displayName = displayName;
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public String displayName() {
        return displayName;
    }

    public void displayName(String displayName) {
        this.displayName = displayName;
    }

    public PlayerAnnouncement announcement() {
        return announcement;
    }

    public void announcement(PlayerAnnouncement catchAnnouncement) {
        this.announcement = catchAnnouncement;
    }

    public FishConditions conditions() {
        return conditions;
    }

    public void conditions(FishConditions conditions) {
        this.conditions = conditions;
    }

    public List<String> commands() {
        return this.commands;
    }

    public void commands(List<String> commands) {
        this.commands = commands;
    }

    public boolean skipItemFormat() {
        return skipItemFormat;
    }

    public void skipItemFormat(boolean skipItemFormat) {
        this.skipItemFormat = skipItemFormat;
    }

    public boolean noDisplay() {
        return noDisplay;
    }

    public void noDisplay(boolean noDisplay) {
        this.noDisplay = noDisplay;
    }

    public boolean firework() {
        return firework;
    }

    public void firework(boolean firework) {
        this.firework = firework;
    }

    public float priceMultiplier() {
        return priceMultiplier;
    }

    public void priceMultiplier(float priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            arguments.reset();
            String value = arguments.popOr(tagKey + " needs at least 1 argument.").value();
            return switch (value) {
                case "announcement" -> announcement.resolve(value, arguments, ctx);
                case "commands" ->
                        TagResolverUtil.fromList(commands, arguments, ctx, s -> Tag.selfClosingInserting(Component.text(s)));
                case "conditions" -> conditions.resolve(value, arguments, ctx);
                case "display-name" -> Tag.selfClosingInserting(ctx.deserialize(displayName));
                case "do-not-sell" -> TagResolverUtil.booleanTag(value, doNotSell, arguments, ctx);
                case "firework" -> TagResolverUtil.booleanTag(value, firework, arguments, ctx);
                case "id" -> Tag.selfClosingInserting(Component.text(key.asString()));
                case "no-display" -> TagResolverUtil.booleanTag(value, noDisplay, arguments, ctx);
                case "price-multiplier" -> TagResolverUtil.numberTag(value, priceMultiplier, arguments, ctx);
                case "skip-item-format" -> TagResolverUtil.booleanTag(value, skipItemFormat, arguments, ctx);
                default -> null;
            };
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals(tagKey) || name.equals("fish-shared");
    }

    public boolean doNotSell() {
        return doNotSell;
    }

    public void doNotSell(boolean doNotSell) {
        this.doNotSell = doNotSell;
    }

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }
}
