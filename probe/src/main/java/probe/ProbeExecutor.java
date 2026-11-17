package probe;

import ru.cwcode.commands.paperplatform.executor.AutowiredExecutor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Records every call, so the probe can tell "dispatched to our method" from "some command answered".
 */
public class ProbeExecutor extends AutowiredExecutor {
  static final Queue<String> calls = new ConcurrentLinkedQueue<>();

  private final String name;

  public ProbeExecutor(String name) {
    this.name = name;
  }

  public void run(Integer value) {
    calls.add(name + ":" + value);
  }
}
