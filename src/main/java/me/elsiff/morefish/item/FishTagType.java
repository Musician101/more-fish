package me.elsiff.morefish.item;

import me.elsiff.morefish.fishing.Fish;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FishTagType implements PersistentDataType<PersistentDataContainer, Fish> {

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<Fish> getComplexType() {
        return Fish.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull Fish complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.LENGTH.setValue(main, complex.length());
        TagKey.FISH_TYPE.setValue(main, complex.type());
        return main;
    }

    @Override
    public @NotNull Fish fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        return new Fish(TagKey.FISH_TYPE.getValue(primitive), TagKey.LENGTH.getValue(primitive));
    }
}
