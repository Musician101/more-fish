package me.elsiff.morefish.item;

import me.elsiff.morefish.fish.Fish;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FishTagType implements PersistentDataType<PersistentDataContainer, Fish> {

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<Fish> getComplexType() {
        return Fish.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(Fish complex, PersistentDataAdapterContext context) {
        PersistentDataContainer main = context.newPersistentDataContainer();
        TagKey.LENGTH.setValue(main, complex.length());
        TagKey.FISH_TYPE.setValue(main, complex.type());
        return main;
    }

    @Override
    public Fish fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
        return new Fish(TagKey.FISH_TYPE.getValue(primitive), TagKey.LENGTH.getValue(primitive));
    }
}
