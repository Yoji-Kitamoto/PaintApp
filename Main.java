package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


abstract class Shape implements Serializable {
	static final int RECT        = 0;
	static final int ROUND_RECT = 1;
	static final int SQUARE      = 2;
	static final int OVAL        = 3;
	static final int CIRCLE      = 4;
	static final int LINE        = 5;

	double x1;
	double y1;
	double x2;
	double y2;
	double red;
	double green;
	double blue;

	abstract public void draw(GraphicsContext graphicContext);

	public void setColor(double red, double green, double blue) {
		this.red   = red;
		this.green = green;
		this.blue  = blue;
	}

	public void setStartPoint(double x, double y) {
		x1 = x;
		y1 = y;
	}

	public void setEndPoint(double x, double y) {
		x2 = x;
		y2 = y;
	}
}

class Rect extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillRect(x1, y1, x2 - x1, y2 - y1);
	}
}

class Square extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		double length = ((x2 - x1) <= (y2 - y1)) ? (x2- x1) : (y2 - y1);
		graphicsContext.fillRect(x1, y1, length, length);
	}
}

class RoundRect extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillRoundRect(x1, y1, x2 - x1, y2 - y1, 50, 50);
	}
}

class Oval extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillOval(x1, y1, x2 - x1, y2 - y1);
	}
}

class Circle extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		double length = ((x2 - x1) <= (y2 - y1)) ? (x2- x1) : (y2 - y1);
		graphicsContext.fillOval(x1, y1, length, length);
	}
}

class Line extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setStroke(Color.color(red, green, blue));
		graphicsContext.strokeLine(x1, y1, x2, y2);
	}
}

public class Main extends Application {
	private MenuBar     menuBar;
	private ToggleGroup toggleGroup;
	private ColorPicker colorPicker;
	private Canvas      canvas;
	private ToolBar     toolBar;

	private String[] menuLabel = {"ファイル", "設定", "図形"};
	private Menu[]   menu      = new Menu[menuLabel.length];

	private String[]   menuItemLabel = {"開く", "保存"};
	private MenuItem[] menuItem      = new MenuItem[menuItemLabel.length];

	private String[] shapeMenuLabel = {"四角形", "円", "線"};
	private Menu[]   shapeMenu      = new Menu[shapeMenuLabel.length];

	private String[]        radioMenuItemLabel = {"四角形", "長方形", "長方形 (角丸)", "正方形", "楕円", "円", "直線"};
	private RadioMenuItem[] radioMenuItem      = new RadioMenuItem[radioMenuItemLabel.length];

	private String[]        rectangleRadioMenuItemLabel = {"長方形", "正方形"};
	private RadioMenuItem[] rectangleRadioMenuItem      = new RadioMenuItem[rectangleRadioMenuItemLabel.length];

	private ArrayList<Shape> shapeList;
	private int    currentShape;
	private Color  currentColor;
	private double x1;
	private double x2;
	private double y1;
	private double y2;

	@Override
	public void start(Stage primaryStage) {
		try {
			canvas      = new Canvas(600, 300);
			menuBar     = new MenuBar();
			colorPicker = new ColorPicker();
			toolBar     = new ToolBar();

			for(int i = 0; i < menu.length; i++) {
				menu[i] = new Menu(menuLabel[i]);
			}

			for(int i = 0; i < menuItem.length; i++) {
				menuItem[i] = new MenuItem(menuItemLabel[i]);
			}

			/*
			 * fillRoundRect (角丸)
			 * fillArc
			 *
			 * strokeLine
			 * strokeRect
			 * strokeRoundRect
			 * strokeOval
			 * strokeArk
			 *
			 * fillText
			 *
			 * strokeText
			 *
			 */

			for(int i = 0; i < radioMenuItem.length; i++) {
				radioMenuItem[i] = new RadioMenuItem(radioMenuItemLabel[i]);
			}

			for(int i = 0; i < menuItem.length; i++) {
				menu[0].getItems().add(menuItem[i]);
			}

			menu[1].getItems().add(menu[2]);

			for(int i = 0; i < radioMenuItem.length; i++) {
				menu[2].getItems().add(radioMenuItem[i]);
			}

			for(int i = 0; i < (menu.length - 1); i++) {
				menuBar.getMenus().add(menu[i]);
			}

			toolBar.getItems().add(colorPicker);

			toggleGroup = new ToggleGroup();
			for(int i = 0; i < radioMenuItem.length; i++) {
				radioMenuItem[i].setToggleGroup(toggleGroup);
			}

			var borderPane = new BorderPane();
			borderPane.setTop(menuBar);
			borderPane.setCenter(canvas);
			borderPane.setBottom(toolBar);

			// 開く
			menuItem[0].setOnAction((actionEvent) -> {
				try {
					var fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("グラフィックファイル", "*.g"));

					File openFile = fileChooser.showOpenDialog(new Stage());
					if(openFile != null) {
						var objectInputStream = new ObjectInputStream(new FileInputStream(openFile));

						Shape shape = null;
						shapeList.clear();
						while((shape = (Shape)objectInputStream.readObject()) != null) {
							shapeList.add(shape);
						}
						objectInputStream.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}

				GraphicsContext graphicContext = canvas.getGraphicsContext2D();
				graphicContext.clearRect(0, 0, 600, 340);
				for(Shape shape : shapeList) {
					shape.draw(graphicContext);
				}
			});
			// 保存
			menuItem[1].setOnAction((actionEvent) -> {
				try {
					var fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("グラフィックファイル", "*.g"));

					File saveFile = fileChooser.showSaveDialog(new Stage());
					if(saveFile != null) {
						var objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));
						for(Shape shape : shapeList) {
							objectOutputStream.writeObject(shape);
						}
						objectOutputStream.writeObject(null);
						objectOutputStream.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			});

			// 長方形
			radioMenuItem[1].setOnAction((actionEvent) -> {
				currentShape = Shape.RECT;
			});
			// 長方形 (角丸)
			radioMenuItem[2].setOnAction((actionEvent) -> {
				currentShape = Shape.ROUND_RECT;
			});
			// 正方形
			radioMenuItem[3].setOnAction((actionEvent) -> {
				currentShape = Shape.SQUARE;
			});
			// 楕円
			radioMenuItem[4].setOnAction((actionEvent) -> {
				currentShape = Shape.OVAL;
			});
			// 円
			radioMenuItem[5].setOnAction((actionEvent) -> {
				currentShape = Shape.CIRCLE;
			});
			// 直線
			radioMenuItem[6].setOnAction((actionEvent) -> {
				currentShape = Shape.LINE;
			});

			colorPicker.setOnAction((actionEvent) -> {
				currentColor = colorPicker.getValue();
			});

			canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
				x1 = mouseEvent.getX();
				y1 = mouseEvent.getY();
			});
			canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (mouseEvent) -> {
				x2 = mouseEvent.getX();
				y2 = mouseEvent.getY();

				if((x1 < 0) || (y1 < 0) || ((x1 == x2) && (y1 == y2))) {
					return;
				}

				Shape shape = null;
				if(currentShape == Shape.RECT) {
					shape = new Rect();
				} else if(currentShape == Shape.ROUND_RECT){
					shape = new RoundRect();
				} else if(currentShape == Shape.SQUARE){
					shape = new Square();
				} else if(currentShape == Shape.OVAL) {
					shape = new Oval();
				} else if(currentShape == Shape.CIRCLE){
					shape = new Circle();
				} else if(currentShape == Shape.LINE) {
					shape = new Line();
				}

				shape.setColor(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());

				if(currentShape != Shape.LINE) {
					if(x1 > x2) {
						x2 = x1;
						x1 = mouseEvent.getX();
					}

					if(y1 > y2) {
						y2 = y1;
						y1 = mouseEvent.getY();
					}
				}

				shape.setStartPoint(x1, y1);
				shape.setEndPoint(x2, y2);

				shapeList.add(shape);

				GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
				shape.draw(graphicsContext);
			});

			shapeList = new ArrayList<Shape>();
			currentShape = Shape.RECT;
			currentColor = Color.BLUE;
			colorPicker.setValue(currentColor);
			radioMenuItem[0].setSelected(true);
			x1 = -1;
			y1 = -1;
			x2 = -1;
			y2 = -1;

			var scene = new Scene(borderPane, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.setTitle("PaintApp");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
