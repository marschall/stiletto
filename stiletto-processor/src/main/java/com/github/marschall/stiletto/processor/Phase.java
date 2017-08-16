package com.github.marschall.stiletto.processor;

enum Phase {

  AFTER_FINALLY {

    @Override
    boolean allowsBeforeValue() {
      return true;
    }

    @Override
    boolean allowsExecutionTimeMillis() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeNanos() {
      return false;
    }

    @Override
    boolean allowsMethodCall() {
      return false;
    }

    @Override
    boolean allowsReturnValue() {
      return false;
    }

    @Override
    boolean allowsThrown() {
      return false;
    }


  },

  AFTER_RETURNING {

    @Override
    boolean allowsBeforeValue() {
      return true;
    }

    @Override
    boolean allowsExecutionTimeMillis() {
      return true;
    }

    @Override
    boolean allowsExecutionTimeNanos() {
      return true;
    }

    @Override
    boolean allowsMethodCall() {
      return false;
    }

    @Override
    boolean allowsReturnValue() {
      return true;
    }

    @Override
    boolean allowsThrown() {
      return false;
    }

  },

  AFTER_THROWING {

    @Override
    boolean allowsBeforeValue() {
      return true;
    }

    @Override
    boolean allowsExecutionTimeMillis() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeNanos() {
      return false;
    }

    @Override
    boolean allowsMethodCall() {
      return false;
    }

    @Override
    boolean allowsReturnValue() {
      return false;
    }

    @Override
    boolean allowsThrown() {
      return true;
    }

  },

  AROUND {

    @Override
    boolean allowsBeforeValue() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeMillis() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeNanos() {
      return false;
    }

    @Override
    boolean allowsMethodCall() {
      return true;
    }

    @Override
    boolean allowsReturnValue() {
      return false;
    }

    @Override
    boolean allowsThrown() {
      return false;
    }

  },

  BEFORE {

    @Override
    boolean allowsBeforeValue() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeMillis() {
      return false;
    }

    @Override
    boolean allowsExecutionTimeNanos() {
      return false;
    }

    @Override
    boolean allowsMethodCall() {
      return false;
    }

    @Override
    boolean allowsReturnValue() {
      return false;
    }

    @Override
    boolean allowsThrown() {
      return false;
    }

  };

  boolean allowsArguments() {
    return true;
  }

  abstract boolean allowsBeforeValue();

  boolean allowsDeclaredAnnotation() {
    return true;
  }

  boolean allowsEvaluate() {
    return true;
  }

  abstract boolean allowsExecutionTimeMillis();

  abstract boolean allowsExecutionTimeNanos();

  boolean allowsJoinpoint() {
    return true;
  }

  abstract boolean allowsMethodCall();

  abstract boolean allowsReturnValue();

  boolean allowsTargetObject() {
    return true;
  }

  abstract boolean allowsThrown();

}
