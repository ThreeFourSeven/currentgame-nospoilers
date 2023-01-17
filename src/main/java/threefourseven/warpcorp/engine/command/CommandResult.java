package threefourseven.warpcorp.engine.command;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandResult {

  protected final Command command;
  protected boolean failure = false;
  protected final Set<String> missingArguments = new HashSet<>();

  public CommandResult(Command command) {
    this.command = command;
  }

  public CommandResult failure() {
    failure = true;
    return this;
  }

  public CommandResult missingOptional(String argument) {
    missingArguments.add(argument);
    return this;
  }

  public CommandResult missingRequired(String argument) {
    return missingOptional(argument).failure();
  }

}
