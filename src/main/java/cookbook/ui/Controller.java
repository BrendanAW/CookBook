package cookbook.ui;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Controller {

    @FXML
    private TableColumn recipeColumn;
    @FXML
    private Tab ingredientsTab;
    @FXML
    private Tab instructionsTab;
    @FXML
    private Menu englishMenu;
    @FXML
    private Menu polishMenu;
    @FXML
    private TabPane recipe;
    @FXML
    private MenuItem tableLanguageSwitch;
    @FXML
    private TableView recipeNames;
    @FXML
    private TextArea ingredients;
    @FXML
    private TextArea instructions;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ComboBox searchBar;

    private final String ENGLISH = "english";
    private String language = ENGLISH;
    public static Map PL = Map.of(
            "ą", "\u0105",
            "ć", "\u0107",
            "ł", "\u0142",
            "ń", "\u0144",
            "ó", "\u00F3",
            "ś", "\u015B",
            "ż", "\u017C",
            "ź", "\u017A"
    );


    public void initialize() {
        recipeNames.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelected, newSelected) -> {
            if (newSelected != oldSelected) {
                if (newSelected instanceof Recipes)
                    getSelectedRecipe();
            }
        });
    }

    private void getSelectedRecipe() {
        Recipes recipe = (Recipes) recipeNames.getSelectionModel().getSelectedItem();

        ingredients.textProperty().set(Datasource.returnInstance().queryIngredientsByName(recipe.getName()));
        instructions.textProperty().set(Datasource.returnInstance().queryInstructionsByName(recipe.getName()));
    }

    @FXML
    void listInfo() {

        GetAllNames task = new GetAllNames();
        recipeNames.itemsProperty().bind(task.valueProperty());
        searchBar.itemsProperty().bind(task.valueProperty());

        task.setOnSucceeded(e -> {
            if (recipeNames.getItems().size() == 0) {
                ingredients.clear();
                instructions.clear();
                return;
            }
            recipeNames.getSelectionModel().selectFirst();
            getSelectedRecipe();
        });
        new Thread(task).start();
    }

    @FXML
    void showAddRecipeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(borderPane.getScene().getWindow());
        String s = language.equals(ENGLISH) ? "Add New Recipe" : "Dodaj Nowy Przepis";
        dialog.setTitle(s);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialogBook.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogBookController dialogBookController = fxmlLoader.getController();
            if (dialogBookController.addNewRecipe())
                System.out.println("It worked!");
        }
        listInfo();
    }

    @FXML
    void showUpdateRecipeDialog() {
        Recipes recipe = (Recipes) recipeNames.getSelectionModel().getSelectedItem();
        boolean isEnglish = language.equals(ENGLISH);
        String s = isEnglish ? "Edit Recipe" : "Edituj Przepis";

        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            s = isEnglish ? "No recipe selected!" : "Nie wybrano przepisu!";
            alert.setTitle(s);
            alert.setHeaderText(null);
            s = isEnglish ? "Please select the recipe you want to edit." : "Wybierz przepis, który chcesz edytowa" + PL.get("ć") + ".";
            alert.setContentText(s);
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(borderPane.getScene().getWindow());
        dialog.setTitle(s);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialogBook.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogBookController dialogBookController = fxmlLoader.getController();
        dialogBookController.displayRecipe(recipe);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (dialogBookController.updateRecipe(recipe))
                System.out.println("It worked!");
        }
        listInfo();

    }

    @FXML
    void showDeleteRecipeDialog() {
        Recipes recipe = (Recipes) recipeNames.getSelectionModel().getSelectedItem();
        boolean isEnglish = language.equals(ENGLISH);
        String s = isEnglish ? "Delete Recipe" : "Usu" + PL.get("ń") + " przepis";
        System.out.println(s);

        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            s = isEnglish ? "No recipe selected!" : "Nie wybrano przepisu!";
            alert.setTitle(s);
            alert.setHeaderText(null);
            s = isEnglish ? "Please select the recipe you want to delete." : "Wybierz przepis, który chcesz usuna" + PL.get("ć");
            alert.setContentText(s);
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(s);
        alert.setHeaderText(null);
        s = isEnglish ? "Are you sure you want to delete the selected recipe: " : "Czy na pewno chcesz usun" + PL.get("ą") + PL.get("ć") + " wybrany przepis:";
        System.out.println(s);
        alert.setContentText(s + recipe.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (Datasource.returnInstance().deleteFromTable(recipe.getName()))
                System.out.println("It worked!");
        }
        listInfo();
    }

    @FXML
    private void updateRecipesFromServer() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        boolean isEnglish = language.equals(ENGLISH);
        String s = isEnglish ? "Updating Recipes..." : "Aktualizacja Przepisów";
        alert.setTitle(s);
        alert.setHeaderText(null);
        s = isEnglish ? "Merging data between the databases, please wait" : "Scalanie danych między bazami danych, proszę czekać";
        alert.setContentText(s);
        new BookClient().start();

        s = isEnglish ? "Press OK to continue" : "Naciśnij OK, aby kontynuować";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            listInfo();
        }
    }

    @FXML
    private void searchForRecipe() {
        Object r = searchBar.getSelectionModel().getSelectedItem();
        if (r instanceof Recipes)
            recipeNames.getSelectionModel().select(r);
    }

    @FXML
    private void searchForRecipes() {
        String searched = searchBar.getEditor().getText();
        for (Object r : recipeNames.getItems()) {
            if (r instanceof Recipes) {
                String name = ((Recipes) r).getName().toLowerCase();
                if (name.matches("(\\w|\\s)*" + searched.toLowerCase() + "(\\w|\\s)*")) {
                    recipeNames.getSelectionModel().select(r);
                    return;
                }
            }
        }
    }

    @FXML
    private void setTablePolish() {
        if (language.equals(ENGLISH)) {
            tableLanguageSwitch.setText("Angielski");
            ingredientsTab.setText("Sk" + PL.get("ł") + "adniki");
            instructionsTab.setText("Instrukcje");
            recipeColumn.setText("Przepisy");
            englishMenu.setVisible(false);
            polishMenu.setVisible(true);
        } else {
            tableLanguageSwitch.setText("Polish");
            ingredientsTab.setText("Ingredients");
            instructionsTab.setText("Instructions");
            recipeColumn.setText("Recipes");
            englishMenu.setVisible(true);
            polishMenu.setVisible(false);
        }
        Datasource.returnInstance().switchTableName();
        setLanguage();
        listInfo();
    }

    @FXML
    private void updateServerWithRecipes() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        boolean isEnglish = language.equals(ENGLISH);
        String s = isEnglish ? "Sending Recipes..." : "Wysy" + PL.get("ł") + "anie Przepisów";
        alert.setTitle(s);
        alert.setHeaderText(null);
        s = isEnglish ? "Merging data between the databases, please wait" : "Scalanie danych między bazami danych, proszę czekać";
        alert.setContentText(s);
        new BookClientSender().start();

        s = isEnglish ? "Press OK to continue" : "Naciśnij OK, aby kontynuować";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            listInfo();
        }
    }

    private void setLanguage() {
        language = language.equals(ENGLISH) ? "polish" : "english";
    }
}

class GetAllNames extends Task {
    @Override
    protected Object call() {
        return FXCollections.observableArrayList(
                Datasource.returnInstance().queryRecipes());
    }
}



