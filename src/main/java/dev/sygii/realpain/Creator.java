package dev.sygii.realpain;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Creator<T, R> {
    private final Map<Identifier, Function<T, R>> creators = new HashMap<>();

    public  R create(Identifier type, T data) {
        Function<T, R> creator = this.creators.get(type);
        if (creator != null) {
            return creator.apply(data);
        }
        throw new IllegalArgumentException("Unknown creator type: " + type);
    }

    public void registerCreator(Identifier id, Function<T, R> serializer) {
        this.creators.put(id, serializer);
    }
}
