package ru.cwcode.commands;

import java.util.List;

@FunctionalInterface
public interface Receiver {
  List<String> receive();
}
