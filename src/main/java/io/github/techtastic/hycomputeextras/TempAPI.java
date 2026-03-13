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

    @LuaFunction
    public void otherTest(int value) {
        HyComputeExtras.get().getLogger().atInfo().log("Value is " + value);
    }

    @LuaFunction
    public Data finalTest(String str, double value) {
        return new Data(str, value);
    }

    public static class Data {
        private final String str;

        @LuaFunction
        public final double value;

        public Data(String str, double value) {
            this.str = str;
            this.value = value;
        }

        @LuaFunction
        public String getString() {
            return this.str;
        }
    }
}
