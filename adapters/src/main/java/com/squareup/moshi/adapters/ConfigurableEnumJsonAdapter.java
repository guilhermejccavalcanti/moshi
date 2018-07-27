package com.squareup.moshi.adapters;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Nullable;

/**
 * A JsonAdapter for enums that allows having a fallback enum value when a deserialized string does
 * not match any enum value.
 */
public final class ConfigurableEnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
  final Class<T> enumType;
  final String[] nameStrings;
  final T[] constants;
  final JsonReader.Options options;
  final @Nullable T fallbackValue;

  public static <T extends Enum<T>> ConfigurableEnumJsonAdapter<T> create(Class<T> enumType) {
    return new ConfigurableEnumJsonAdapter<>(enumType, null);
  }

  public ConfigurableEnumJsonAdapter<T> withFallbackValue(T fallbackValue) {
    if (fallbackValue == null) {
      throw new NullPointerException("fallbackValue == null");
    }
    return new ConfigurableEnumJsonAdapter<>(enumType, fallbackValue);
  }

  ConfigurableEnumJsonAdapter(Class<T> enumType, @Nullable T fallbackValue) {
    this.enumType = enumType;
    this.fallbackValue = fallbackValue;
    try {
      constants = enumType.getEnumConstants();
      nameStrings = new String[constants.length];
      for (int i = 0; i < constants.length; i++) {
        T constant = constants[i];
        Json annotation = enumType.getField(constant.name()).getAnnotation(Json.class);
        String name = annotation != null ? annotation.name() : constant.name();
        nameStrings[i] = name;
      }
      options = JsonReader.Options.of(nameStrings);
    } catch (NoSuchFieldException e) {
      throw new AssertionError("Missing field in " + enumType.getName(), e);
    }
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    int index = reader.selectString(options);
    if (index != -1) return constants[index];

    String name = reader.nextString();
    if (fallbackValue != null) {
      return fallbackValue;
    }
    throw new JsonDataException("Expected one of "
        + Arrays.asList(nameStrings) + " but was " + name + " at path "
        + reader.getPath());
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    writer.value(nameStrings[value.ordinal()]);
  }

  @Override public String toString() {
    return "ConfigurableEnumJsonAdapter(" + enumType.getName() + ")";
  }
}
