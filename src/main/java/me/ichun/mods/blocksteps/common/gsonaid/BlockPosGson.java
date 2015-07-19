package me.ichun.mods.blocksteps.common.gsonaid;

import com.google.gson.*;
import net.minecraft.util.BlockPos;

import java.lang.reflect.Type;

public class BlockPosGson implements JsonSerializer<BlockPos>, JsonDeserializer<BlockPos>, InstanceCreator<BlockPos>
{
    @Override
    public JsonElement serialize(BlockPos src, Type typeOfSrc, JsonSerializationContext context)
    {
        return null;
    }

    @Override
    public BlockPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return null;
    }

    @Override
    public BlockPos createInstance(Type type)
    {
        return null;
    }
}
