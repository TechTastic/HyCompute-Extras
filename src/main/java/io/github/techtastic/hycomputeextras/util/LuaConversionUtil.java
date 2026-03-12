package io.github.techtastic.hycomputeextras.util;

import dev.cozygalvinism.hycompute.shaded.cobalt.*;
import io.github.techtastic.hycomputeextras.processor.LuaFunctionProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LuaConversionUtil {
    public static LuaValue toLua(Object object) {
        if (object instanceof Number){
            switch (object) {
                case Byte b -> {
                    return LuaInteger.valueOf(b);
                }
                case Short b -> {
                    return LuaInteger.valueOf(b);
                }
                case Integer i -> {
                    return LuaInteger.valueOf(i);
                }
                case Long l -> {
                    return LuaInteger.valueOf(l);
                }
                case Double d -> {
                    return LuaDouble.valueOf(d);
                }
                case Float f -> {
                    return LuaDouble.valueOf(f);
                }
                default -> {}
            }
        }
        if (object instanceof Boolean b)
            return b ? Constants.TRUE : Constants.FALSE;
        if (object instanceof String s)
            return LuaString.valueOf(s);

        if (object instanceof Set<?> set) {
            return toLua(set.toArray());
        }

        LuaTable t = new LuaTable();
        if (object instanceof Object[] o) {
            for (int i = 0; i < o.length; i++) {
                t.rawset(i, toLua(o[i]));
            }
            return t;
        }
        if (object instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                t.rawset(i, toLua(list.get(i)));
            }
            return t;
        }
        if (object instanceof Map<?, ?> map) {
            map.forEach((k,v) -> {
                try {
                    t.rawset(toLua(k), toLua(v));
                } catch (LuaError e) {
                    throw new RuntimeException(e);
                }
            });
            return t;
        }

        if (LuaFunctionProcessor.process(t, object, object.getClass()))
           return t;
        return Constants.NIL;
    }

    public static Object toJava(LuaValue v) {
        try {
            return switch (v.type()) {
                case Constants.TSTRING -> v.checkString();
                case Constants.TNUMBER -> v instanceof LuaInteger ? v.checkInteger() : v.checkDouble();
                case Constants.TBOOLEAN -> v.checkBoolean();
                case Constants.TTABLE -> {
                    LuaTable table = v.checkTable();
                    Map<Object, Object> map = new HashMap<>();
                    LuaValue key = Constants.NIL;
                    while (true) {
                        Varargs next = table.next(key);
                        key = next.first();
                        if (key.isNil()) break;

                        LuaValue value = next.arg(2);
                        map.put(toJava(key), toJava(value));
                    }

                    yield map;
                }
                default -> null;
            };
        } catch (LuaError e) {
            return null;
        }
    }

    public static boolean canConvert(Class<?> clazz) {
        return clazz == Void.class || clazz == void.class ||
                clazz == Byte.class || clazz == byte.class ||
                clazz == Short.class || clazz == short.class ||
                clazz == Integer.class || clazz == int.class ||
                clazz == Long.class || clazz == long.class ||
                clazz == Double.class || clazz == double.class ||
                clazz == Float.class || clazz == float.class ||
                clazz == Boolean.class || clazz == boolean.class ||
                clazz == String.class || clazz.isArray() ||
                Set.class.isAssignableFrom(clazz) ||
                List.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz);
    }
}