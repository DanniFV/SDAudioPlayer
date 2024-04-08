package is.vidmot;
/******************************************************************************
 *  Nafn    : Ebba Þóra Hvannberg
 *  T-póstur: ebba@hi.is
 *  Viðmótsforritun 2024
 *
 *  Controller fyrir lagalistann
 *  getur:
 *
 *  -- valið lag
 *  -- play / pause
 *  -- farið heim
 *****************************************************************************/
import is.vinnsla.Lag;
import is.vinnsla.Lagalistar;
import is.vinnsla.Lagalisti;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;


public class ListiController  {

    // fastar
    private final String PAUSE = "images/pause.png";
    private final String PlAY = "images/play.png";

    // viðmótshlutir
    @FXML
    public ProgressBar fxProgressBar;   // progress bar fyrir spilun á lagi
    @FXML
    protected Slider fxSlider; // slider fyrir progress bar
    @FXML
    protected ImageView fxPlayPauseView; // mynd fyrir play/pause hnappinn
    @FXML
    protected ListView<Lag> fxListView; // lagalistinn
    @FXML
    protected ImageView fxMyndLagView;    // mynd fyrir lagið
    @FXML
    protected ImageView fxSpin;

    // vinnslan
    private Lagalisti lagalisti; // lagalistinn
    private MediaPlayer player; // ein player breyta per forritið
    private Lag validLag;       // núverandi valið lag


    /**
     * Frumstillir lagalistann og tengir hann við ListView viðmótshlut
     */

    public void initialize() {
        // setur lagalistann sem núverandi lagalista úr Lagalistar
        lagalisti = Lagalistar.getNuverandi();
        // tengdu lagalistann við ListView-ið
        fxListView.setItems(lagalisti.getListi());
        // man hvaða lag var síðast spilað á lagalistanum og setur það sem valið stak á ListView
        fxListView.getSelectionModel().select(lagalisti.getIndex());
        // setur lagið í focus
        fxListView.requestFocus();
        // // Lætur lagalista vita hvert valda lagið er í viðmótinu og uppfærir myndina fyrir lagið
        veljaLag();
        // setur upp player
        setjaPlayer();
        sliderMagic();
    }

    /**
     * Bregðast við músaratburði og spila valið lag
     *
     * @param mouseEvent
     */

    @FXML
    protected void onValidLag(MouseEvent mouseEvent) {
        System.out.println(fxListView.getSelectionModel().getSelectedItem());
        // Lætur lagalista vita hvert valda lagið er í viðmótinu og uppfærir myndina fyrir lagið
        veljaLag();
        // spila lagið
        spilaLag();

    }

    /**
     * Lagið er pásað ef það er í spilun, lagið er spilað ef það er í pásu
     *
     * @param actionEvent ónotað
     */

    @FXML
    protected void onPlayPause(ActionEvent actionEvent) {
        // ef player-inn er spilandi
        if (player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            setjaMynd(fxPlayPauseView, PlAY);   // uppfærðu myndina með play (ör)
            player.pause();                     // pásaðu spilarann
        } else if (player.getStatus().equals(MediaPlayer.Status.PAUSED)) {
            setjaMynd(fxPlayPauseView, PAUSE);  // uppfærðu myndina með pause
            player.play();                      // haltu áfram að spila
            spinna();
        }
    }

    /**
     * Fara aftur í heima view. Ef spilari er til stöðva spilarann
     *
     * @param actionEvent ónotað
     */

    @FXML
    protected void onHeim(ActionEvent actionEvent) {
        // stoppaðu player ef hann er ekki null
        if (player != null)
            player.stop();
        // farðu í HEIMA senuna með ViewSwitcher
        ViewSwitcher.switchTo(View.HEIMA, true);
    }

    @FXML
    protected void onForwardFimmtan(ActionEvent actionEvent) {
        player.seek(player.getCurrentTime().add(Duration.seconds(15)));
    }

    @FXML
    protected void onBackFimmtan(ActionEvent actionEvent) {
        player.seek(player.getCurrentTime().add(Duration.seconds(-15)));
    }

    @FXML
    protected void onSkip(ActionEvent actionEvent){
        naestaLag();
    }

    @FXML
    protected void onBack(ActionEvent actionEvent){
        double t = player.getCurrentTime().toSeconds();
        if (t > 1) {
            player.seek(player.getCurrentTime().add(Duration.seconds(-t)));
        } else {
            sidastaLag();
        }
    }

    /**
     * Lætur laga lista vita hvert valda lagið er. Uppfærir myndina fyrir lagið.
     */
    private void veljaLag() {
        // hvaða lag er valið
        validLag = fxListView.getSelectionModel().getSelectedItem();
        //  láttu lagalista vita um indexinn á völdu lagi
        lagalisti.setIndex(fxListView.getSelectionModel().getSelectedIndex());
        // uppfæra myndina fyrir lagið
        setjaMynd(fxMyndLagView, validLag.getMynd());
    }

    /**
     * Spila lagið
     */

    private void spilaLag() {
        setjaMynd(fxPlayPauseView, PAUSE);
        // Búa til nýjan player
        setjaPlayer();
        // setja spilun í gang
        player.play();
        sliderMagic();
        sliding();
        spinna();
    }

    /**
     * Setja mynd með nafni á ImageView
     *
     * @param fxImageView viðmótshluturinn sem á að uppfærast
     * @param nafnMynd    nafn á myndinni
     */

    private void setjaMynd(ImageView fxImageView, String nafnMynd) {
        System.out.println ("nafn á mynd "+nafnMynd);
        fxImageView.setImage(new Image(getClass().getResource(nafnMynd).toExternalForm()));
    }

    /**
     * Setja upp player fyrir lagið, þ.m.t. at setja handler á hvenær lagið stoppar og tengja
     * lagið við progress bar
     */

    private void setjaPlayer() {
        // Stoppa player-inn ef hann var ekki stopp
        if (player != null)
            player.stop();
        // Smíða nýjan player með nýju Media fyrir lagið
        player = new MediaPlayer(new Media(getClass().getResource(validLag.getMedia()).toExternalForm()));
        // Láta player vita hvenær lagið endar - stop time
        player.setStopTime(new Duration(validLag.getLengd()));
        // setja fall sem er keyrð þegar lagið hættir
        player.setOnEndOfMedia(this::naestaLag);
        // setja listener tengingu á milli player og progress bar
        player.currentTimeProperty().addListener((observable, old, newValue) ->
                fxProgressBar.setProgress(newValue.divide(validLag.getLengd()).toMillis()));
    }

    /**
     * Næsta lag er spilað. Kallað á þessa aðferð þegar fyrra lag á listanum endar
     */
    private void naestaLag() {
        // setja valið lag sem næsta lag á núverandi lagalista
         lagalisti.naesti();
        // uppfæra ListView til samræmis, þ.e. að næsta lag sé valið
        fxListView.getSelectionModel().selectIndices(lagalisti.getIndex());
        // velja lag
        veljaLag();
        // spila lag
        spilaLag();
    }

    private void sidastaLag() {
        lagalisti.sidasti();
        fxListView.getSelectionModel().selectIndices(lagalisti.getIndex());
        veljaLag();
        spilaLag();
    }

    public void spinna() {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), fxSpin);
        rotateTransition.setByAngle(360.0);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        player.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.PLAYING) {
                rotateTransition.play();
            } else {
                rotateTransition.stop();
            }
        });

    }
    public void sliderMagic() {
        fxSlider.setMin(0);
        fxSlider.setMax(player.getTotalDuration().toSeconds());

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5), event -> {
                    // Update the slider value every 3 seconds
                    fxSlider.setValue(player.getCurrentTime().toSeconds());
                })
        );

        // Repeat indefinitely
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    public void sliding() {
        DoubleProperty sliderValue = new SimpleDoubleProperty(0);
        fxSlider.valueProperty().bindBidirectional(sliderValue);

        // Add event handler to the slider
        fxSlider.setOnMouseReleased(event -> {
            // Only seek player if the slider's value has changed
            if (sliderValue.get() != player.getCurrentTime().toSeconds()) {
                player.seek(Duration.seconds(sliderValue.get()));
            }
        });
    }

}


