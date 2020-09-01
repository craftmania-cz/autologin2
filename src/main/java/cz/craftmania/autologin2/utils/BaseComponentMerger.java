package cz.craftmania.autologin2.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Additional library for {@link TextComponentBuilder}. With this library you can easily
 * merge several {@link BaseComponent} together.
 * @version 1.0.0
 * @author jacobbordas / igniss
 */
public class BaseComponentMerger {

    private final List<TextComponent> textComponentList;

    /**
     * Creates empty BaseComponentMerger.
     */
    public BaseComponentMerger() {
        textComponentList = new ArrayList<>();
    }

    /**
     * @param components Array of {@link BaseComponent} to be merged together
     */
    public BaseComponentMerger(BaseComponent... components) {
        textComponentList = new ArrayList<>();
        for (BaseComponent textComponent : components) {
            this.textComponentList.add(new TextComponent(textComponent));
        }
    }

    /**
     * @param withSpaces Whether output {@link TextComponent} should be with spaces between each {@link TextComponent} merged.
     * @return Final merged {@link TextComponent}
     */
    public TextComponent output(boolean withSpaces) {
        ListIterator<TextComponent> iterator = textComponentList.listIterator();
        TextComponent textComponent = iterator.next();
        while (iterator.hasNext()) {
            if (withSpaces) textComponent.addExtra(" ");
            textComponent.addExtra(iterator.next());
        }
        return textComponent;
    }

    /**
     * Adds {@link BaseComponent} to list.
     * @param components BaseComponents to add
     */
    public void addComponent(BaseComponent... components) {
        for (BaseComponent textComponent : components) {
            this.textComponentList.add(new TextComponent(textComponent));
        }    }
}
