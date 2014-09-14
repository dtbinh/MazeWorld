package assignment_mazeworld;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.MultiRobotsMazeProblem.MultiRobotsMazeNode;


public class MultiRobotsMazeDriver extends Application{
	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("simple99.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		
/*		int[][] ss = {{0,0}, {1,6}, {4,5}};
//		int[][] ss = {{4,1}, {6,2}, {6, 0}};
		int[][] gs = {{6,1}, {6,2}, {6, 0}};
		int robots = 3;
/*		int[][] ss = {{0,0}};
		int[][] gs = {{6, 0}};
		int robots = 1;
*/
		int[][] ss = {{0,0}, {1,6}, {4,4}};
		int[][] gs = {{8,1}, {8,2}, {8, 0}};
		int robots = 3;
/*		int[][] ss = {{0,0}, {0,3}, {0,7}};
		int[][] gs = {{19, 6}, {19,7}, {19,0}};
		int robots = 3;
*/		MultiRobotsMazeProblem mazeProblem = new MultiRobotsMazeProblem(maze, ss, gs, robots);
		
/*		List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		for (int i = 0; i < robots; i++) 
			animationPathList.add(new AnimationPath(mazeView, bfsPath, i));
		System.out.println("DFS:  ");
		mazeProblem.printStats();

		List<SearchNode> dfsPath = mazeProblem
				.depthFirstPathCheckingSearch(5000);
		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		System.out.println("BFS:  ");
		mazeProblem.printStats();
*/
		List<SearchNode> astarPath = mazeProblem.astarSearch();
		for (int i = 0; i < robots; i++)
			animationPathList.add(new AnimationPath(mazeView, astarPath, i));
		System.out.println("A*:  ");
		mazeProblem.printStats();

	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {
		
		initMazeView();
	
		primaryStage.setTitle("CS 76 Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.05), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private Node piece;
		private List<SearchNode> searchPath;
		private int currentMove = 0;

		private int lastX;
		private int lastY;
		
		private int robotNo;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, List<SearchNode> path, int num) {
			searchPath = path;
			MultiRobotsMazeNode firstNode = (MultiRobotsMazeNode) searchPath.get(0);
			robotNo = num;
			piece = mazeView.addPiece(firstNode.getaState(num)[0], firstNode.getaState(num)[1]);
			lastX = firstNode.getaState(num)[0];
			lastY = firstNode.getaState(num)[1];
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (currentMove < searchPath.size() && animationDone) {
				MultiRobotsMazeNode mazeNode = (MultiRobotsMazeNode) searchPath
						.get(currentMove);
				int dx = mazeNode.getaState(robotNo)[0] - lastX;
				int dy = mazeNode.getaState(robotNo)[1] - lastY;
				// System.out.println("animating " + dx + " " + dy);
				animateMove(piece, dx, dy);
				lastX = mazeNode.getaState(robotNo)[0];
				lastY = mazeNode.getaState(robotNo)[1];

				currentMove++;
			}

		}

		// move the piece n by dx, dy cells
		public void animateMove(Node n, int dx, int dy) {
			animationDone = false;
			TranslateTransition tt = new TranslateTransition(
					Duration.millis(300), n);
			tt.setByX(PIXELS_PER_SQUARE * dx);
			tt.setByY(-PIXELS_PER_SQUARE * dy);
			// set a callback to trigger when animation is finished
			tt.setOnFinished(new AnimationFinished());

			tt.play();

		}

		// when the animation is finished, set an instance variable flag
		//  that is used to see if the path is ready for the next step in the
		//  animation
		private class AnimationFinished implements EventHandler<ActionEvent> {
			@Override
			public void handle(ActionEvent event) {
				animationDone = true;
			}
		}
	}
}
