package me.elsiff.morefish.fish;

import me.elsiff.morefish.fish.PlayerAnnouncement.Type;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class FishAbstract<F extends FishAbstract<F>> implements Comparable<F>, TagResolver {

    private final String tagKey;
    private final String name;
    private String displayName;
    private PlayerAnnouncement announcement = new PlayerAnnouncement(Type.SERVER, 0.1);
    private FishConditions conditions = new FishConditions();
    private List<String> commands = List.of();
    private boolean skipItemFormat = false;
    private boolean noDisplay = false;
    private boolean firework = false;
    private boolean doNotSell = false;
    private float priceMultiplier;

    protected FishAbstract(String tagKey, String name, String displayName) {
        this(tagKey, name, displayName, 0);
    }

    protected FishAbstract(String tagKey, String name, String displayName, float priceMultiplier) {
        this.tagKey = tagKey;
        this.name = name;
        this.displayName = displayName;
        this.priceMultiplier = priceMultiplier;
    }

    public String name() {
        return name;
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
                case "commands" -> TagResolverUtil.fromList(commands, arguments, ctx, Tag::preProcessParsed);
                case "conditions" -> conditions.resolve(value, arguments, ctx);
                case "display-name" -> Tag.selfClosingInserting(ctx.deserialize(displayName));
                case "do-not-sell" -> TagResolverUtil.booleanTag(doNotSell);
                case "firework" -> TagResolverUtil.booleanTag(firework);
                case "name" -> Tag.preProcessParsed(this.name);
                case "no-display" -> TagResolverUtil.booleanTag(noDisplay);
                case "price-multiplier" -> TagResolverUtil.numberTag(value, priceMultiplier, arguments, ctx);
                case "skip-item-format" -> TagResolverUtil.booleanTag(skipItemFormat);
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
}
