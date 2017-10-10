package net.tarilabs.dbprogramlistingextract;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.scene.input.*;
import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

@SuppressWarnings("restriction")
public class Ddbprogramlistingextract extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Extract from docbook programlisting");

        TextArea docbookTA = new TextArea();
        TextArea extractedTA = new TextArea();
        extractedTA.setEditable(false);
        
        // when text changes in source TextArea, JSoup to target textArea
        docbookTA.textProperty().addListener((observable,oldText,newText)->{
            extractedTA.setText(wrapWithADOCsource(extract(newText)));
        });
        
        // when press TAB in source TextArea, make focus cycle --instead of default insert tab \t in text
        docbookTA.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) docbookTA.getSkin();
                if (skin.getBehavior() instanceof TextAreaBehavior) {
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    behavior.traverseNext();
                    event.consume();
                }
            }
        });

        // when target TextArea receives focus, select all --helpful to then press CTRL+C to clipboard
        extractedTA.focusedProperty().addListener((observable,oldValue,newValue)->{
            if (oldValue == false && newValue == true) {
                extractedTA.selectAll();
            }
        });
        
        VBox vbox = new VBox(docbookTA, extractedTA);
        VBox.setVgrow(docbookTA, Priority.ALWAYS);
        VBox.setVgrow(extractedTA, Priority.ALWAYS);

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static String wrapWithADOCsource(String input) {
        return new StringBuilder("[source]\n----\n").append(input).append("\n----").toString();
    }
    
    public static String extract(String docbookSource) {
        return Jsoup.clean(docbookSource, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
