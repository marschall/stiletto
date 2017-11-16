package com.github.marschall.stiletto.processor;

import java.util.stream.Stream;

public class Main {

  public static void main(String[] args) {
    Stream.of("AAA","BBB","CCC", "EEE", "DDD").forEachOrdered(s->System.out.println("Output:"+s));

    Stream.of("AAA","BBB","CCC", "EEE", "DDD").parallel().forEachOrdered(s->System.out.println("Output:"+s));

  }

}
