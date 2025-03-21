package ru.cwcode.commands;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.cwcode.commands.api.CommandsAPI;
import ru.cwcode.commands.api.Sender;
import ru.cwcode.commands.arguments.ExactStringArg;
import ru.cwcode.commands.extra.command.executor.SimpleExecutor;
import ru.cwcode.commands.test.L10PlatformTest;
import ru.cwcode.commands.test.TestConfigPlatform;
import ru.cwcode.cwutils.config.SimpleConfig;
import ru.cwcode.cwutils.l10n.L10n;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionGenerationTest {
  
  public static final SimpleExecutor<Sender> VOID_EXECUTOR = new SimpleExecutor<>((sender, parser) -> {});
  
  @BeforeAll
  public static void before() {
    L10PlatformTest l10PlatformTest = new L10PlatformTest();
    
    CommandsAPI.setPlatform(new TestConfigPlatform());
    CommandsAPI.setL10n(new L10n(l10PlatformTest));
    CommandsAPI.setConfig(new SimpleConfig("config", l10PlatformTest));
  }
  
  @Test
  void permissionGenerationTest() {
    ArgumentSet inner_inner_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet inner_inner_perm = new ArgumentSet(VOID_EXECUTOR, "inner_inner_permission");
    ArgumentSet inner_inner_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("inner_inner_exact"));
    
    ArgumentSet inner_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet inner_perm = new ArgumentSet(VOID_EXECUTOR, "inner_permission");
    ArgumentSet inner_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("inner_exact"));
    
    ArgumentSet root_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet root_perm = new ArgumentSet(VOID_EXECUTOR, "root_permission");
    ArgumentSet root_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("root_exact"));
    
    Command inner_inner_cmd = new Command("inner_inner")
      .arguments(inner_inner_as, inner_inner_perm, inner_inner_exact);
    
    Command inner_cmd = new Command("inner")
      .subCommands(inner_inner_cmd)
      .arguments(inner_as, inner_perm, inner_exact);
    
    Command cmd = new Command("root")
      .subCommands(inner_cmd)
      .arguments(root_as, root_perm, root_exact);
    
    cmd.register();
    
    assertEquals("root", cmd.getPermission());
    assertEquals("root", root_as.getPermission());
    assertEquals("root.root_exact", root_exact.getPermission());
    assertEquals("root.root_permission", root_perm.getPermission());
    
    assertEquals("root.inner", inner_cmd.getPermission());
    assertEquals("root.inner", inner_as.getPermission());
    assertEquals("root.inner.inner_exact", inner_exact.getPermission());
    assertEquals("root.inner.inner_permission", inner_perm.getPermission());
    
    assertEquals("root.inner.inner_inner", inner_inner_cmd.getPermission());
    assertEquals("root.inner.inner_inner", inner_inner_as.getPermission());
    assertEquals("root.inner.inner_inner.inner_inner_exact", inner_inner_exact.getPermission());
    assertEquals("root.inner.inner_inner.inner_inner_permission", inner_inner_perm.getPermission());
  }
  
  @Test
  void staticPermissionGenerationTest() {
    ArgumentSet inner_inner_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet inner_inner_perm = new ArgumentSet(VOID_EXECUTOR, "$inner_inner_permission");
    ArgumentSet inner_inner_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("$inner_inner_exact"));
    
    ArgumentSet inner_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet inner_perm = new ArgumentSet(VOID_EXECUTOR, "$inner_permission");
    ArgumentSet inner_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("$inner_exact"));
    
    ArgumentSet root_as = new ArgumentSet(VOID_EXECUTOR);
    ArgumentSet root_perm = new ArgumentSet(VOID_EXECUTOR, "$root_permission");
    ArgumentSet root_exact = new ArgumentSet(VOID_EXECUTOR, new ExactStringArg("$root_exact"));
    
    Command inner_inner_cmd = new Command("inner_inner", "$inner_inner")
      .arguments(inner_inner_as, inner_inner_perm, inner_inner_exact);
    
    Command inner_cmd = new Command("inner", "$inner")
      .subCommands(inner_inner_cmd)
      .arguments(inner_as, inner_perm, inner_exact);
    
    Command cmd = new Command("root", "$root")
      .subCommands(inner_cmd)
      .arguments(root_as, root_perm, root_exact);
    
    cmd.register();
    
    assertEquals("root", cmd.getPermission());
    assertEquals("root", root_as.getPermission());
    assertEquals("root_exact", root_exact.getPermission());
    assertEquals("root_permission", root_perm.getPermission());
    assertEquals("inner", inner_cmd.getPermission());
    assertEquals("inner", inner_as.getPermission());
    assertEquals("inner_exact", inner_exact.getPermission());
    assertEquals("inner_permission", inner_perm.getPermission());
    assertEquals("inner_inner", inner_inner_cmd.getPermission());
    assertEquals("inner_inner", inner_inner_as.getPermission());
    assertEquals("inner_inner_exact", inner_inner_exact.getPermission());
    assertEquals("inner_inner_permission", inner_inner_perm.getPermission());
  }
}
