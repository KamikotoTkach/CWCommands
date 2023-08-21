package ru.cwcode.commands.paperplatform.argument.location;


import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public enum LocationPart {
  X {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(String.valueOf(loc.getX()));
    }
  },
  Y {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(String.valueOf(loc.getY()));
    }
  },
  Z {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(String.valueOf(loc.getZ()));
    }
  },
  PITCH {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(String.valueOf(loc.getPitch()));
    }
  },
  YAW {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(String.valueOf(loc.getYaw()));
    }
  },
  WORLD {
    @Override
    List<String> getSuggestion(Location loc) {
      return Collections.singletonList(loc.getWorld().getName());
    }
  };
  
  List<String> getSuggestion(Location loc) {
    return Collections.emptyList();
  }
}
