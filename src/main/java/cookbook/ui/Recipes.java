package cookbook.ui;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Recipes {

    private SimpleStringProperty name;
    private SimpleStringProperty instructions;
    private SimpleStringProperty ingredients;
    private static int NAME = 1;
    private static int INGREDIENTS = 2;
    private static int INSTRUCTIONS = 3;

    public Recipes() {
        this.name = new SimpleStringProperty();
        this.ingredients = new SimpleStringProperty();
        this.instructions = new SimpleStringProperty();
    }

    public String getName() {
        return name.get();
    }

    public String getInstructions() {
        return instructions.get();
    }

    public String getIngredients() {
        return ingredients.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setInstructions(String instructions) {
        this.instructions.set(instructions);
    }

    public void setIngredients(String ingredients) {
        this.ingredients.set(ingredients);
    }


    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipes recipes = (Recipes) o;
        return this.getName().equals(recipes.getName()) &&
                this.getIngredients().equals(recipes.getIngredients()) &&
                this.getInstructions().equals(recipes.getInstructions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, instructions, ingredients);
    }
}
