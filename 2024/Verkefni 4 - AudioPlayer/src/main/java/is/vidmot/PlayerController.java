package is.vidmot;
/******************************************************************************
 *  Nafn    : Ebba Þóra Hvannberg
 *  T-póstur: ebba@hi.is
 *  Viðmótsforritun 2024
 *
 *  Controller fyrir forsíðuna
 *
 *  Getur valið lagalista
 *
 *****************************************************************************/
import is.vinnsla.Askrifandi;
import is.vinnsla.Lagalistar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class PlayerController  {

    // fastar
    public static final String ASKRIFANDI = "Áskrifandi";

    // viðmótshlutir
    @FXML
    protected Button fxAskrifandi;

    // frumstilling eftir að hlutur hefur verið smíðaður og .fxml skrá lesin
    public void initialize() {
        Lagalistar.frumstilla();
    }

    /**
     * Atburðarhandler fyrir að velja lagalista. Sá lagalisti er settur og farið í senu fyrir þann lista
     * @param mouseEvent
     */
    //@FXML
    /**
        protected void onVeljaLista(ActionEvent mouseEvent) {
        // hvaða reitur var valinn
        int i = GridPane.getRowIndex((Node) mouseEvent.getSource());
        int j = GridPane.getColumnIndex((Node) mouseEvent.getSource());
        // skiptum yfir í lagalistann í vinnslunni sem var valið
        Lagalistar.setIndex(i * 2 + j);
        // skiptum yfir í LAGALISTI view
        ViewSwitcher.switchTo(View.LAGALISTI, false);
        System.out.println("hi bitches");
    }
     */
    @FXML
    protected void onVeljaLista(ActionEvent mouseEvent) {
        // Check if the source of the event is laid out in a GridPane
        Node source = (Node) mouseEvent.getSource();
        Integer rowIndex = GridPane.getRowIndex(source);
        Integer colIndex = GridPane.getColumnIndex(source);

        // Check if both row and column indices are not null
        if (rowIndex != null && colIndex != null) {
            // Retrieve row and column indices
            int i = rowIndex.intValue();
            int j = colIndex.intValue();

            // Perform your operations with i and j here
            Lagalistar.setIndex(i * 2 + j);
            ViewSwitcher.switchTo(View.LAGALISTI, false);
            System.out.println("hi bitches");
        } else {
            // Handle the case where the node is not laid out in a GridPane
            System.out.println("The source of the event is not laid out in a GridPane.");
        }
    }

    /**
     * Loggar áskrifanda inn
     *
     * @param actionEvent
     */
    public void onLogin(ActionEvent actionEvent) {
        // býr til nýjan dialog með tómum áskrifanda
        AskrifandiDialog dialog = new AskrifandiDialog(new Askrifandi(ASKRIFANDI));
        // sýndu dialoginn
        Optional<Askrifandi> utkoma = dialog.showAndWait();
        // Ef fékkst svar úr dialognum setjum við nafnið á áskrifandanum í notendaviðmótið
        utkoma.ifPresent (a -> {
            fxAskrifandi.setText(a.getNafn());});
    }
}
