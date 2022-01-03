package io.musician101.morefish.forge.util;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class TextParser {

    private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(&[0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
    private static final Map<Character, TextFormatting> TEXT_FORMATTING_MAP = ImmutableMap.<Character, TextFormatting>builder().put('0', TextFormatting.BLACK).put('1', TextFormatting.DARK_BLUE).put('2', TextFormatting.DARK_GREEN).put('3', TextFormatting.DARK_AQUA).put('4', TextFormatting.DARK_RED).put('5', TextFormatting.DARK_PURPLE).put('6', TextFormatting.GOLD).put('7', TextFormatting.GRAY).put('8', TextFormatting.DARK_GRAY).put('9', TextFormatting.BLUE).put('a', TextFormatting.GREEN).put('b', TextFormatting.AQUA).put('c', TextFormatting.RED).put('d', TextFormatting.LIGHT_PURPLE).put('e', TextFormatting.YELLOW).put('f', TextFormatting.WHITE).put('k', TextFormatting.OBFUSCATED).put('l', TextFormatting.BOLD).put('m', TextFormatting.STRIKETHROUGH).put('n', TextFormatting.UNDERLINE).put('o', TextFormatting.ITALIC).put('r', TextFormatting.RESET).build();
    private final List<ITextComponent> list;
    private final String message;
    private final ITextComponent[] output;
    private ITextComponent currentChatComponent;
    private int currentIndex;
    private Style style;

    public TextParser(String message) {
        list = new ArrayList<>();
        currentChatComponent = new StringTextComponent("");
        style = new Style();
        this.message = message;
        if (message == null) {
            output = new ITextComponent[]{currentChatComponent};
        }
        else {
            list.add(currentChatComponent);
            Matcher matcher = INCREMENTAL_PATTERN.matcher(message);

            int groupId;
            for (String match = null; matcher.find(); currentIndex = matcher.end(groupId)) {
                groupId = 0;

                do {
                    ++groupId;
                } while ((match = matcher.group(groupId)) == null);

                appendNewComponent(matcher.start(groupId));
                TextFormatting format = (TextFormatting) TEXT_FORMATTING_MAP.get(match.toLowerCase(Locale.ENGLISH).charAt(1));
                if (format == TextFormatting.RESET) {
                    style = new Style();
                }
                else if (format.isFancyStyling()) {
                    switch (format) {
                        case OBFUSCATED:
                            style.setObfuscated(true);
                            break;
                        case BOLD:
                            style.setBold(true);
                            break;
                        case STRIKETHROUGH:
                            style.setStrikethrough(true);
                            break;
                        case UNDERLINE:
                            style.setUnderlined(true);
                            break;
                        case ITALIC:
                            style.setItalic(true);
                            break;
                        default:
                            throw new AssertionError("Unexpected message format");
                    }
                }
                else {
                    style = (new Style()).setColor(format);
                }
            }

            if (currentIndex < message.length()) {
                appendNewComponent(message.length());
            }

            output = list.toArray(new ITextComponent[0]);
        }
    }

    private void appendNewComponent(int index) {
        if (index > currentIndex) {
            ITextComponent addition = (new StringTextComponent(message.substring(currentIndex, index))).setStyle(style);
            currentIndex = index;
            style = style.createDeepCopy();
            if (currentChatComponent == null) {
                currentChatComponent = new StringTextComponent("");
                list.add(currentChatComponent);
            }

            currentChatComponent.append(addition);
        }
    }

    public ITextComponent getOutput() {
        StringTextComponent msg = new StringTextComponent("");
        Arrays.stream(output).forEach(msg::append);
        return msg;
    }

    public Function<? super Object, ? extends String> getOutputAsString() {
        return Serializer.toJsonTree(getOutput()).toString();
    }
}
