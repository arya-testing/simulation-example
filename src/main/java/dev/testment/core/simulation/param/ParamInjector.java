package dev.testment.core.simulation.param;

import dev.testment.core.cli.Args;
import dev.testment.core.cli.exceptions.InvalidValueFormatException;
import dev.testment.core.util.StringUtil;
import dev.testment.core.util.TypeUtil;
import dev.testment.core.simulation.TestmentSimulation;
import dev.testment.core.util.ReflectionUtil;

import java.lang.reflect.Field;

public class ParamInjector {

    public void inject(String[] args, TestmentSimulation simulation) {
        Class<?> type = simulation.getClass();
        while(type != null) {
            for(Field field : type.getDeclaredFields()) {
                Param param = field.getAnnotation(Param.class);
                if(param == null)
                    continue;

                String paramName = param.name() == null || param.name().isEmpty() ? field.getName() : param.name();
                String paramValue;

                if(TypeUtil.isAssignableFrom(Boolean.class, field.getType())) {
                    paramValue = Args.exists(args, paramName) ? "true" : "false";
                } else if(param.required() && (param.defaultValue() == null || param.defaultValue().isEmpty())) {
                    paramValue = Args.getValue(args, paramName);
                } else {
                    String temp = Args.getValue(args, paramName, param.defaultValue());
                    paramValue = temp.isEmpty() ? null : temp;
                }
                if(paramValue != null) {
                    Object parsedValue;
                    try {
                        parsedValue = StringUtil.parse(paramValue, field.getType());
                    } catch(IllegalArgumentException e) {
                        throw new InvalidValueFormatException("Argument '" + paramName + "' must be of type '" + field.getType() + "'");
                    }
                    ReflectionUtil.setField(field, simulation, parsedValue);
                }
            }
            type = type.getSuperclass();
        }
    }

}
