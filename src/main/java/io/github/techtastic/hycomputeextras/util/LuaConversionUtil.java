package io.github.techtastic.hycomputeextras.util;

import dev.cozygalvinism.hycompute.shaded.cobalt.*;
import io.github.techtastic.hycomputeextras.HyComputeExtras;
import io.github.techtastic.hycomputeextras.api.annotations.LuaFunction;
import io.github.techtastic.hycomputeextras.processor.LuaFunctionProcessor;

import java.util.*;

public class LuaConversionUtil {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_CONVERSIONS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(int.class, Integer.class),
            new AbstractMap.SimpleEntry<>(long.class, Long.class),
            new AbstractMap.SimpleEntry<>(double.class, Double.class),
            new AbstractMap.SimpleEntry<>(float.class, Float.class),
            new AbstractMap.SimpleEntry<>(boolean.class, Boolean.class),
            new AbstractMap.SimpleEntry<>(byte.class, Byte.class),
            new AbstractMap.SimpleEntry<>(short.class, Short.class),
            new AbstractMap.SimpleEntry<>(char.class, Character.class)
    );

    public static boolean isCompatible(Class<?> parameterType, Object value) {
        HyComputeExtras.get().getLogger().atInfo().log(parameterType.getName());
        HyComputeExtras.get().getLogger().atInfo().log(value.getClass().getName());

        Class<?> normalized = PRIMITIVE_CONVERSIONS.getOrDefault(parameterType, parameterType);

        try {
            normalized.cast(value);
            return true;
        } catch (ClassCastException _) {
        }

        if (value instanceof Number num) {
            if (normalized == Byte.class) return isCompatible(normalized, num.byteValue());
            if (normalized == Short.class) return isCompatible(normalized, num.shortValue());
            if (normalized == Integer.class) return isCompatible(normalized, num.intValue());
            if (normalized == Long.class) return isCompatible(normalized, num.longValue());
            if (normalized == Float.class) return isCompatible(normalized, num.floatValue());
            if (normalized == Double.class) return isCompatible(normalized, num.doubleValue());
        }

        return false;
    }

    public static Object convertCompatibleParameters(Class<?> parameterType, Object value) {
        Class<?> normalized = PRIMITIVE_CONVERSIONS.getOrDefault(parameterType, parameterType);

        try {
            return normalized.cast(value);
        } catch (ClassCastException ignored) {}

        if (value instanceof Number num) {
            if (normalized == Byte.class) return num.byteValue();
            if (normalized == Short.class) return num.shortValue();
            if (normalized == Integer.class) return num.intValue();
            if (normalized == Long.class) return num.longValue();
            if (normalized == Float.class) return num.floatValue();
            if (normalized == Double.class) return num.doubleValue();
        }

        return value;
    }

    public static LuaValue toLua(Object object) {
        if (object instanceof Number) {
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
                t.rawset(i + 1, toLua(o[i]));
            }
            return t;
        }
        if (object instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                t.rawset(i + 1, toLua(list.get(i)));
            }
            return t;
        }
        if (object instanceof Map<?, ?> map) {
            map.forEach((k,v) -> {
                try {
                    t.rawset((LuaValue) toLua(k), toLua(v));
                } catch (LuaError e) {
                    throw new RuntimeException(e);
                }
            });
            return t;
        }

        if (object != null && LuaFunctionProcessor.process(t, object, object.getClass()))
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
        return Arrays.stream(clazz.getMethods()).anyMatch(m -> m.getAnnotation(LuaFunction.class) != null) ||
                Arrays.stream(clazz.getFields()).anyMatch(f -> f.getAnnotation(LuaFunction.class) != null) ||
                clazz == Void.class || clazz == void.class || clazz == Byte.class || clazz == byte.class ||
                clazz == Short.class || clazz == short.class || clazz == Integer.class || clazz == int.class ||
                clazz == Long.class || clazz == long.class || clazz == Double.class || clazz == double.class ||
                clazz == Float.class || clazz == float.class || clazz == Boolean.class || clazz == boolean.class ||
                clazz == String.class || clazz == char.class || clazz.isArray() || Set.class.isAssignableFrom(clazz) ||
                List.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }
}