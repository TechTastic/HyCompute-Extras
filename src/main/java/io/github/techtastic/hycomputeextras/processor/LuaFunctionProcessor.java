package io.github.techtastic.hycomputeextras.processor;

import dev.cozygalvinism.hycompute.shaded.cobalt.*;
import dev.cozygalvinism.hycompute.shaded.cobalt.function.VarArgFunction;
import io.github.techtastic.hycomputeextras.HyComputeExtras;
import io.github.techtastic.hycomputeextras.api.annotations.LuaFunction;
import io.github.techtastic.hycomputeextras.util.LuaConversionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class LuaFunctionProcessor {
    public static boolean process(LuaTable library, Object api, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            LuaFunction function = method.getAnnotation(LuaFunction.class);
            if (function == null) continue;
            String name = function.name();
            if (name.isEmpty())
                name = method.getName();

            HyComputeExtras.get().getLogger().atInfo().log("Processing Method " + name + "...");

            if (!LuaConversionUtil.canConvert(method.getReturnType())) {
                HyComputeExtras.get().getLogger().atSevere().log("Invalid @LuaFunction method in " + clazz.getName() + ": Method " + name + " has an invalid return type of " + method.getReturnType().getName() + "!");
            }

            boolean invalidParameter = false;
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (!LuaConversionUtil.canConvert(parameterType)) {
                    invalidParameter = true;
                    HyComputeExtras.get().getLogger().atSevere().log("Invalid @LuaFunction method in " + clazz.getName() + ": Method " + name + " has an invalid parameter type of " + parameterType.getName() + "!");
                    break;
                }
            }
            if (invalidParameter)
                continue;

            library.rawset(name, new AnnotatedMethodFunction(api, method));
        }

        for (Field field : clazz.getFields()) {
            LuaFunction function = field.getAnnotation(LuaFunction.class);
            if (function == null) continue;
            String name = function.name();
            if (name.isEmpty())
                name = field.getName();

            HyComputeExtras.get().getLogger().atInfo().log("Processing Field " + name + "...");

            try {
                library.rawset(name, LuaConversionUtil.toLua(field.get(api)));
            } catch (Exception e) {
                HyComputeExtras.get().getLogger().atSevere().log("Invalid @LuaFunction field in " + clazz.getName() + ": " + e.getCause());
            }
        }
        return library.size() > 0;
    }

    static class AnnotatedMethodFunction extends VarArgFunction {
        private Object api;
        private Method method;
        private Class<?>[] parameterTypes;

        private AnnotatedMethodFunction(Object api, Method method) {
            this.api = api;
            this.method = method;
            this.parameterTypes = method.getParameterTypes();
        }

        @Override
        protected Varargs invoke(LuaState luaState, Varargs varargs) throws LuaError, UnwindThrowable {
            if (varargs.count() < this.parameterTypes.length)
                throw ErrorFactory.argError(this.parameterTypes.length, "value expected");

            ArrayList<Object> parameters = new ArrayList<>();
            for (int i = 0; i < this.parameterTypes.length; i++) {
                Class<?> parameterType = this.parameterTypes[i];
                LuaValue value = varargs.arg(i);
                Object parameter = LuaConversionUtil.toJava(value);
                if (parameterType.isInstance(parameter))
                    parameters.add(parameter);
                else
                    throw ErrorFactory.typeError(value, parameterType.getSimpleName());
            }

            try {
                Object object = this.method.invoke(this.api, parameters.toArray());
                return LuaConversionUtil.toLua(object);
            } catch (Exception e) {
                throw LuaError.wrap(e);
            }
        }
    }
}
