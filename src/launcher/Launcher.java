package launcher;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
public class Launcher extends Application {
	volatile GraphicsContext gameGraphics;
	public static final double SCREEN_X = 1280;
	public static final double SCREEN_Y = 720;
	public static final Affine ZERO = new Affine();
	public GameManager gm;
	public static void main(String[] args) {
		
		launch(args);
	}
	public void init() {
		gm = new GameManager();
	}
	@Override
	public void start(Stage s) throws Exception {
		// TODO Auto-generated method stub
		s.setTitle("JavaFX Game");
		s.setResizable(false);
		//Creating a scene with the flowpane as its parent. 
		FlowPane root = new FlowPane();
		Scene scene = new Scene(root,SCREEN_X,SCREEN_Y);
		//Adding event handlers to the scene. 
		s.addEventHandler(KeyEvent.KEY_PRESSED,gm.keyPressManager);
		s.addEventHandler(KeyEvent.KEY_RELEASED,gm.keyLiftManager);
		
		s.setScene(scene);
		
		Canvas gameCanvas = new Canvas(SCREEN_X,SCREEN_Y);
		gameGraphics = gameCanvas.getGraphicsContext2D();
		root.getChildren().addAll(gameCanvas);
		
		
		s.show();
		
		AnimTimer a = new AnimTimer();
		a.start();
	}
	public class AnimTimer extends AnimationTimer{
		private static final double FPS_CAP = 70.00;
		private final Font FPS_FONT = new Font(10);
		private long lastAnimation = 0;
		public AnimTimer() {
			gameGraphics.setFont(FPS_FONT);
		}
		public void handle(long time) {
			long timeDisplacement = time - lastAnimation;
			double timeMillis = (double)timeDisplacement / 1000000.0;
			double fps = 1000.0 / timeMillis;
			
			//Exit if the amount of time since the last frame is insufficient. 
			if(fps > FPS_CAP) {
				return;
			}
			gm.update(gameGraphics);
			
			//Add the FPS to the game. 
			gameGraphics.setFill(Color.BLACK);
			gameGraphics.fillText(String.format("FPS: %.2f",fps),Launcher.SCREEN_X - 60,Launcher.SCREEN_Y - 10);
			
			//Update the time at which we last animated the game. 
			lastAnimation = time;
		}
		
		
	}


	
}
