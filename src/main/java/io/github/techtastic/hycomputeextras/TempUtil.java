package io.github.techtastic.hycomputeextras;

import dev.cozygalvinism.hycompute.computer.LuaExecutor;
import dev.cozygalvinism.hycompute.shaded.cobalt.Constants;
import dev.cozygalvinism.hycompute.shaded.cobalt.LuaState;
import dev.cozygalvinism.hycompute.shaded.cobalt.LuaTable;
import dev.cozygalvinism.hycompute.shaded.cobalt.Varargs;
import dev.cozygalvinism.hycompute.shaded.cobalt.function.VarArgFunction;

public class TempUtil {

    public static void generateNewTempAPI(LuaExecutor executor) {
        LuaTable temp = new LuaTable();
        temp.rawset("test", new VarArgFunction() {
            @Override
            protected Varargs invoke(LuaState luaState, Varargs varargs) {
                return Constants.TRUE;
            }
        });
        executor.getGlobals().rawset("temp", temp);
    }
}
