package cookbook.ui;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogBookController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label ingredientsLabel;
    @FXML
    private Label instructionsLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea ingredientsArea;
    @FXML
    private TextArea instructionsArea;
    @FXML
    private Label nazwaLabel;
    @FXML
    private Label zakladkiLabel;
    @FXML
    private Label instrukcjeLabel;
    @FXML
    private DialogPane dialogPane;

    public void initialize() {
        boolean english = !Datasource.returnInstance().getTableName().equals("przepisy");

        if(english){
            dialogPane.setHeaderText("Fill in the info for a new recipe:");
        }else dialogPane.setHeaderText("Wpisz informacje o nowym przepisie:");
        nazwaLabel.setVisible(!english);
        zakladkiLabel.setVisible(!english);
        instrukcjeLabel.setVisible(!english);
        nameLabel.setVisible(english);
        ingredientsLabel.setVisible(english);
        instructionsLabel.setVisible(english);
    }

    boolean addNewRecipe() {
        if ((nameField.getText().isEmpty()) || (ingredientsArea.getText().isEmpty()) || (instructionsArea.getText().isEmpty()))
            return false;
        String name = nameField.getText();
        String ingredients = ingredientsArea.getText();
        String instructions = instructionsArea.getText();
        if (Datasource.returnInstance().insertIntoTable(name, ingredients, instructions))
            return true;
        return false;
    }

    void displayRecipe(Recipes recipe) {
        nameField.setText(recipe.getName());
        ingredientsArea.setText(Datasource.returnInstance().queryIngredientsByName(recipe.getName()));
        instructionsArea.setText(Datasource.returnInstance().queryInstructionsByName(recipe.getName()));
    }

    boolean updateRecipe(Recipes recipe) {
        String name = nameField.getText();
        String ingredients = ingredientsArea.getText();
        String instructions = instructionsArea.getText();
        if (Datasource.returnInstance().updateQuery(recipe.getName(), name, ingredients, instructions))
            return true;
        return false;
    }

}