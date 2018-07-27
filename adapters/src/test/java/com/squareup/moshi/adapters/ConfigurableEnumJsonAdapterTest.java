package com.squareup.moshi.adapters;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonDataException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("CheckReturnValue")
public final class ConfigurableEnumJsonAdapterTest {
  @Test public void toAndFromJson() throws Exception {
    ConfigurableEnumJsonAdapter<Roshambo> adapter = ConfigurableEnumJsonAdapter.create(Roshambo.class);
    assertThat(adapter.fromJson("\"ROCK\"")).isEqualTo(Roshambo.ROCK);
    assertThat(adapter.toJson(Roshambo.PAPER)).isEqualTo("\"PAPER\"");
  }

  @Test public void withJsonName() throws Exception {
    ConfigurableEnumJsonAdapter<Roshambo> adapter = ConfigurableEnumJsonAdapter.create(Roshambo.class);
    assertThat(adapter.fromJson("\"scr\"")).isEqualTo(Roshambo.SCISSORS);
    assertThat(adapter.toJson(Roshambo.SCISSORS)).isEqualTo("\"scr\"");
  }

  @Test public void withoutFallbackValue() throws Exception {
    ConfigurableEnumJsonAdapter<Roshambo> adapter = ConfigurableEnumJsonAdapter.create(Roshambo.class);
    try {
      adapter.fromJson("\"SPOCK\"");
      fail();
    } catch (JsonDataException expected) {
      assertThat(expected).hasMessage(
          "Expected one of [ROCK, PAPER, scr] but was SPOCK at path $");
    }
  }

  @Test public void withFallbackValue() throws Exception {
    ConfigurableEnumJsonAdapter<Roshambo> adapter = ConfigurableEnumJsonAdapter.create(Roshambo.class)
        .withFallbackValue(Roshambo.ROCK);
    assertThat(adapter.fromJson("\"SPOCK\"")).isEqualTo(Roshambo.ROCK);
  }

  enum Roshambo {
    ROCK,
    PAPER,
    @Json(name = "scr") SCISSORS
  }
}
