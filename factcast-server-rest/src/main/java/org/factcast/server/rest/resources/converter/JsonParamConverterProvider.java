package org.factcast.server.rest.resources.converter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Provider
public class JsonParamConverterProvider implements ParamConverterProvider {
    private final ObjectMapper objectMapper;

    @Inject
    public JsonParamConverterProvider(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @AllArgsConstructor
    public static class JsonParamConverter<T> implements ParamConverter<T> {

        private final ObjectMapper objectMapper;

        private final Class<T> clazz;

        @Override
        public T fromString(String value) {
            try {
                return objectMapper.readValue(value, clazz);
            } catch (IOException e) {
                throw new BadRequestException();
            }
        }

        @Override
        @SneakyThrows
        public String toString(T value) {
            return objectMapper.writeValueAsString(value);
        }

    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType,
            Annotation[] annotations) {
        if (annotations != null && Arrays.stream(annotations).filter(a -> a instanceof JsonParam)
                .findAny().isPresent() && !Collection.class.isAssignableFrom(rawType)) {
            return new JsonParamConverter<T>(objectMapper, rawType);
        }
        return null;
    }
}
