import javafx.application.Application;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.Node;
import javafx.application.Platform;
import java.lang.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class FrontEnd extends Application{
  private Stage mainStage;
  private Scene mainScene;
  private BorderPane mainPane;
  private BorderPane drawPane;
  private BorderPane startPane;
  private Canvas displCanvas;
  private Canvas drawCanvas;
  private TextArea lainFuckCode;
  private Label labelMode;
  private LainFuckM lainFuckM;
  private Slider mainSlider = new Slider(0,LainFuckM.DIAM-0.00000001,0);
  private int aktLayer = 0;
  private int aktC = 0;
  private Label aktLayerLabel = new Label("akt layer = 0");
  public static final Color[] bitToC = {Color.BLACK,Color.BLUE,Color.GREEN,Color.web("0x00FFFF"),Color.RED,Color.web("0xFF00FF"),Color.web("0xFFFF00"),Color.WHITE};
  public static void main(String [] args){
    launch(args);
  };  
  @Override
  public void start(Stage primaryStage){
    primaryStage.getIcons().add(new Image("LainFuckLogo.bmp"));
    this.mainSlider.setBlockIncrement(1.0);
    setSlider(mainSlider);
    mainSlider.setSnapToTicks(true);
    this.mainStage = primaryStage;
    this.lainFuckM = new LainFuckM("");
    mainStage.setTitle("Lain-Fuck");  
    this.startPane = new BorderPane();
    this.drawPane = new BorderPane();
    this.mainPane = new BorderPane();
    this.mainScene = new Scene(startPane,500,500,Color.BLACK);
    constStart();
    constMain();
    constDraw();
   // root.getStylesheets().add("utils.css");
    primaryStage.setScene(this.mainScene);
    primaryStage.show();

  };
  private void clear(GraphicsContext gc){
    gc.clearRect(0,0,gc.getCanvas().getWidth(),gc.getCanvas().getHeight());  
  };
  private void draw(GraphicsContext gc){
    int [][][] mat = this.lainFuckM.getM();
    int diam = (int)gc.getCanvas().getWidth()/LainFuckM.DIAM;
    int c = 0;
    aktLayer = (int)mainSlider.getValue();
    for(int x = 0;x<LainFuckM.DIAM;x++){
      for(int y = 0;y<LainFuckM.DIAM;y++){
	c = mat[x][y][aktLayer];
        gc.setFill(bitToC[(c%8+8)%8]);
        gc.fillRect(x*diam,y*diam,diam,diam);	
      };	
    };
    gc.fill();
  };
  public void drawPoint(int x, int y,int c,GraphicsContext gc){
    int diam = (int)gc.getCanvas().getWidth()/LainFuckM.DIAM;
    gc.setFill(bitToC[(c%8+8)%8]);
    gc.fillRect(x*diam,y*diam,diam,diam);
    gc.fill();
    lainFuckM.setM(x,y,aktLayer,c);
  };
  private void switchPane(Button b,Parent p,Scene s){
    b.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e){
	s.setRoot(p);
	draw(displCanvas.getGraphicsContext2D());
	draw(drawCanvas.getGraphicsContext2D());
      }	
    });
  };
  private void switchParentCenter(Button b, Parent p, BorderPane bord, Label l,String s){
     b.setOnAction(new EventHandler<ActionEvent>(){
       @Override
       public void handle(ActionEvent e){
	 bord.setCenter(p);  
	 l.setText(s);
       }
     }); 
  };
  private void changeC(ColorPicker c){
    c.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e){
	Color cl = c.getValue();
	int red = cl.getRed()>0.5 ? 1:0;
	int green = cl.getGreen() > 0.5?1:0;
	int blue = cl.getBlue() > 0.5?1:0;
	aktC = blue + green*2+red*4;
      };	
    });  
  };
  private void execCodeB(Button b, Canvas c){
    b.setOnAction(new EventHandler<ActionEvent>(){
      @Override 
      public void handle(ActionEvent e){
	clear(c.getGraphicsContext2D());
	String code = lainFuckCode.getText();
	code = code.replace('\n',' ');
	code = code.replace('\b',' ');
	code = code.replace('\r',' ');
	code = code.replace('\t',' ');
	System.out.println(code);
	lainFuckM.setCode(code);
	System.out.println(lainFuckM.powerOn());
	draw(c.getGraphicsContext2D());	  
      };
    }); 
  };
  private void mouseDragged(Canvas c){
    c.setOnMouseDragged(new EventHandler<MouseEvent>(){
     @Override
     public void handle(MouseEvent e){
       if(e.isPrimaryButtonDown()){
	 double xCord = e.getX();
	 double yCord = e.getY();
	 int diam = (int)c.getWidth()/LainFuckM.DIAM;
	 if(xCord < diam*LainFuckM.DIAM && yCord < diam*LainFuckM.DIAM && xCord >= 0 && yCord >= 0){
	   int x = (int)(xCord/diam);
	   int y = (int)(yCord/diam);
	   drawPoint(x%LainFuckM.DIAM,y%LainFuckM.DIAM,aktC,c.getGraphicsContext2D());
	 };
       }; 
     }   
    });  
  };
  private void setSlider(Slider s){
    s.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
	  Number old_val, Number new_val) {
	aktLayerLabel.setText("akt layer = "+(int)s.getValue());
	draw(displCanvas.getGraphicsContext2D());
	draw(drawCanvas.getGraphicsContext2D());
      }
    });
  };
  private void writeToFile(Button b){
    b.setOnAction(new EventHandler<ActionEvent>(){
      @Override 
      public void handle(ActionEvent e){	  
	String path = Debug.getInf("please enter file name + path"); 
	String erg = Parser.parse(lainFuckM.getM(),aktLayer,path);
	if(erg.charAt(0) == 's'){
	  Debug.inf(erg);  
	}else{
	  Debug.warning(erg);
	};
      };
    }); 
  };
  private void resetM(Button b){
    b.setOnAction(new EventHandler<ActionEvent>(){
       @Override
       public void handle(ActionEvent e){
	 lainFuckM = new LainFuckM("");  
         draw(displCanvas.getGraphicsContext2D());
         draw(drawCanvas.getGraphicsContext2D());
       };
    });
  };
  private void loadFile(Button b, TextArea a){
    b.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e){
	String in = Debug.getInf("pleas filename + path");
	String n = Parser.loadF(in);	  
	if(n == null){
	  Debug.warning("cant load file");  
	}else{
	  System.out.println("loaded code: " + n);
	  a.appendText(n);  
	};
      };
    });  
  };
  private void setCheckBox(CheckBox c){
    c.selectedProperty().addListener(new ChangeListener<Boolean>(){
      public void changed(ObservableValue<? extends Boolean> ov,
            Boolean old_val, Boolean new_val){
        lainFuckM.setDbg(new_val); 
      };	
    });  
  };
  private void setHelp(Button b){
    b.setOnAction(new EventHandler<ActionEvent>(){
      @Override
      public void handle(ActionEvent e){
	Debug.inf("please look at the dokumentation in the program folder");
      }; 
    });  
  };
  private void constStart(){
    HBox top = new HBox();
    Button backToDraw = new Button("draw->");
    Button backToMain = new Button("main->");
    Button resetAll = new Button("reset machine");
    Button helpLain = new Button("what is Lainfuck");
    setHelp(helpLain);
    ImageView iv = new ImageView();
    Image img = new Image("LainFuckLogo.bmp");
    if(img != null){
      iv.setImage(img);
      iv.setFitWidth(400.0);
      iv.setFitHeight(400.0);	
    };
    resetM(resetAll);
    switchPane(backToDraw,this.drawPane,mainScene);
    switchPane(backToMain,this.mainPane,mainScene);
    top.getChildren().addAll(backToMain,backToDraw,resetAll,helpLain);
    this.startPane.setCenter(iv);
    this.startPane.setTop(top);
  };
  private void constMain(){
    HBox top = new HBox();
    ToolBar t = new ToolBar();
    labelMode = new Label("current mode: img"); 
    t.getItems().add(labelMode);
    t.getItems().add(mainSlider);
    t.getItems().add(aktLayerLabel);








































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































    Group canvasP = new Group();
    Canvas canvas = new Canvas(400,400);
    lainFuckCode = new TextArea("please enter lainfuck code here");
    lainFuckCode.setCache(false);
    Button backToDraw = new Button("draw->");
    Button backToStart = new Button("<-start");
    Button saveMat = new Button("save to file");
    Button loadCode = new Button("load lainfuck");
    Button execCode = new Button("execute code");
    Button writeCode = new Button("write code");
    Button viewCan = new Button("view img");
    CheckBox cbDbg = new CheckBox("Debug");
    writeToFile(saveMat);
    setCheckBox(cbDbg);
    t.getItems().add(cbDbg);
    loadFile(loadCode,lainFuckCode);
    switchPane(backToDraw,this.drawPane,mainScene);
    switchPane(backToStart,this.startPane,mainScene);
    execCodeB(execCode,canvas);
    switchParentCenter(viewCan,canvasP,this.mainPane,labelMode,"current mode: img");
    switchParentCenter(writeCode,lainFuckCode,this.mainPane,labelMode,"current mode: write code");
    top.getChildren().addAll(backToStart,backToDraw,saveMat,loadCode,writeCode,execCode,viewCan);
    this.displCanvas = canvas;
    canvasP.getChildren().add(canvas);
    mainPane.setTop(top);
    mainPane.setBottom(t);
    mainPane.setCenter(canvasP);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    draw(gc);
  };
  private void constDraw(){
    Group canvasP = new Group();
    Canvas canvas = new Canvas(400,400);
    HBox top = new HBox();
    HBox bot = new HBox();
    Button backToMain = new Button("<-main");
    Button backToStart = new Button("<-start");
    ColorPicker colorPick = new ColorPicker(Color.BLACK);
    bot.getChildren().addAll(colorPick);
    switchPane(backToMain,this.mainPane,mainScene);
    switchPane(backToStart,this.startPane,mainScene);
    changeC(colorPick);
    mouseDragged(canvas); 
    top.getChildren().addAll(backToMain,backToStart);
    this.drawCanvas = canvas;
    canvasP.getChildren().add(canvas);
    drawPane.setTop(top);
    drawPane.setCenter(canvasP);
    drawPane.setBottom(bot);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    draw(gc);  
  };
};
