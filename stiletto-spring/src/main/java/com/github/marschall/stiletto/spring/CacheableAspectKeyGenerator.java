package com.github.marschall.stiletto.spring;

import java.util.Arrays;
import java.util.Objects;

final class CacheableAspectKeyGenerator {

  static Object generateKey(String joinpointIdentification, Object[] parameters) {
    if (parameters == null || parameters.length == 0) {
      return new NoParametersKey(joinpointIdentification);
    } else {
      return new ParametersKey(joinpointIdentification, parameters);
    }
  }

  static final class ParametersKey {

    private final String joinpointIdentification;
    private final Object[] parameters;

    ParametersKey(String joinpointIdentification, Object[] parameters) {
      Objects.requireNonNull(joinpointIdentification);
      this.joinpointIdentification = joinpointIdentification;
      this.parameters = parameters;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof ParametersKey)) {
        return false;
      }
      ParametersKey other = (ParametersKey) obj;
      return this.joinpointIdentification.equals(other.joinpointIdentification)
              && Arrays.deepEquals(this.parameters, other.parameters);
    }

    @Override
    public int hashCode() {
      int result = 17;
      result = 31 * result + this.joinpointIdentification.hashCode();
      result = 31 * result + Arrays.deepHashCode(this.parameters);
      return result;
    }

  }

  static final class NoParametersKey {

    private final String joinpointIdentification;

    NoParametersKey(String joinpointIdentification) {
      Objects.requireNonNull(joinpointIdentification);
      this.joinpointIdentification = joinpointIdentification;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof NoParametersKey)) {
        return false;
      }
      NoParametersKey other = (NoParametersKey) obj;
      return this.joinpointIdentification.equals(other.joinpointIdentification);
    }

    @Override
    public int hashCode() {
      return this.joinpointIdentification.hashCode();
    }

  }

}
