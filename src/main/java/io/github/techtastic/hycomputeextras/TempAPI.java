package io.github.techtastic.hycomputeextras;

import io.github.techtastic.hycomputeextras.api.ILuaAPI;
import io.github.techtastic.hycomputeextras.api.annotations.LuaFunction;

public class TempAPI implements ILuaAPI {
    @Override
    public String getType() {
        return "temp";
    }

    @LuaFunction
    public boolean test() {
        return true;
    }
}
