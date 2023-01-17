package threefourseven.warpcorp.engine.entity;


import threefourseven.warpcorp.engine.util.Named;

public interface EntityTemplateGenerator extends Named {
  EntityTemplate generate();
}
