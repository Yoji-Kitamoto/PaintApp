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
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


abstract class Shape implements Serializable {
	protected static final int RECT          = 0;
	protected static final int SQUARE        = 1;
	protected static final int ROUND_RECT    = 2;
	protected static final int ROUND_SQUARE  = 3;
	protected static final int CIRCLE        = 4;
	protected static final int ARC           = 5;
	protected static final int OVAL          = 6;
	protected static final int LINE          = 7;

	protected double x1;
	protected double y1;
	protected double x2;
	protected double y2;
	protected double red;
	protected double green;
	protected double blue;

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

final class Rect extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillRect(x1, y1, x2 - x1, y2 - y1);
	}
}

final class RoundRect extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillRoundRect(x1, y1, x2 - x1, y2 - y1, 50, 50);
	}
}

final class Square extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		double length = ((x2 - x1) <= (y2 - y1)) ? (x2- x1) : (y2 - y1);
		graphicsContext.fillRect(x1, y1, length, length);
	}
}

final class RoundSquare extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		double length = ((x2 - x1) <= (y2 - y1)) ? (x2- x1) : (y2 - y1);
		graphicsContext.fillRoundRect(x1, y1, length, length, 50, 50);
	}
}

final class Oval extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillOval(x1, y1, x2 - x1, y2 - y1);
	}
}

final class Circle extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		double length = ((x2 - x1) <= (y2 - y1)) ? (x2- x1) : (y2 - y1);
		graphicsContext.fillOval(x1, y1, length, length);
	}
}

final class Arc extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setFill(Color.color(red, green, blue));
		graphicsContext.fillArc(x1, y1, x2- x1, (y2 - y1) * 2, 20, 140, ArcType.CHORD);
	}
}

final class Line extends Shape implements Serializable {
	public void draw(GraphicsContext graphicsContext) {
		graphicsContext.setStroke(Color.color(red, green, blue));
		graphicsContext.strokeLine(x1, y1, x2, y2);
	}
}

public class Main extends Application {
	private MenuBar     menuBar;
	private ColorPicker colorPicker;
	private Canvas      canvas;
	private ToolBar     toolBar;

	private String[] menuLabel = {"ファイル", "設定", "図形"};
	private Menu[]   menu      = new Menu[menuLabel.length];

	private String[]   menuItemLabel = {"開く", "保存"};
	private MenuItem[] menuItem      = new MenuItem[menuItemLabel.length];

	private String[] shapeMenuLabel = {"四角形", "円", "線"};
	private Menu[]   shapeMenu      = new Menu[shapeMenuLabel.length];

	private String[]        rectangleRadioMenuItemLabel = {"長方形", "正方形", "長方形 (角丸)", "正方形 (角丸)"};
	private RadioMenuItem[] rectangleRadioMenuItem      = new RadioMenuItem[rectangleRadioMenuItemLabel.length];

	private String[]        circleRadioMenuItemLabel = {"円", "楕円", "円弧"};
	private RadioMenuItem[] circleRadioMenuItem      = new RadioMenuItem[circleRadioMenuItemLabel.length];

	private String[]        lineRadioMenuItemLabel = {"直線"};
	private RadioMenuItem[] lineRadioMenuItem      = new RadioMenuItem[lineRadioMenuItemLabel.length];

	private ToggleGroup rectangleToggleGroup;
	private ToggleGroup circleToggleGroup;
	private ToggleGroup lineToggleGroup;

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

			for(int i = 0; i < shapeMenu.length; i++) {
				shapeMenu[i] = new Menu(shapeMenuLabel[i]);
			}

			rectangleToggleGroup = new ToggleGroup();
			for(int i = 0; i < rectangleRadioMenuItem.length; i++) {
				rectangleRadioMenuItem[i] = new RadioMenuItem(rectangleRadioMenuItemLabel[i]);
				rectangleRadioMenuItem[i].setToggleGroup(rectangleToggleGroup);
			}

			circleToggleGroup = new ToggleGroup();
			for(int i = 0; i < circleRadioMenuItem.length; i++) {
				circleRadioMenuItem[i] = new RadioMenuItem(circleRadioMenuItemLabel[i]);
				circleRadioMenuItem[i].setToggleGroup(circleToggleGroup);
			}

			lineToggleGroup = new ToggleGroup();
			for(int i = 0; i < lineRadioMenuItem.length; i++) {
				lineRadioMenuItem[i] = new RadioMenuItem(lineRadioMenuItemLabel[i]);
				lineRadioMenuItem[i].setToggleGroup(lineToggleGroup);
			}

			for(MenuItem i : menuItem) {
				menu[0].getItems().add(i);
			}

			menu[1].getItems().add(menu[2]);

			for(Menu m : shapeMenu) {
				menu[2].getItems().add(m);
			}

			for(RadioMenuItem i : rectangleRadioMenuItem) {
				shapeMenu[0].getItems().add(i);
			}

			for(RadioMenuItem i : circleRadioMenuItem) {
				shapeMenu[1].getItems().add(i);
			}

			for(RadioMenuItem i : lineRadioMenuItem) {
				shapeMenu[2].getItems().add(i);
			}

			for(int i = 0; i < (menu.length - 1); i++) {
				menuBar.getMenus().add(menu[i]);
			}

			toolBar.getItems().add(colorPicker);

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
			rectangleRadioMenuItem[0].setOnAction((actionEvent) -> {
				currentShape = Shape.RECT;
			});
			// 正方形
			rectangleRadioMenuItem[1].setOnAction((actionEvent) -> {
				currentShape = Shape.SQUARE;
			});
			// 長方形 (角丸)
			rectangleRadioMenuItem[2].setOnAction((actionEvent) -> {
				currentShape = Shape.ROUND_RECT;
			});
			// 正方形 (角丸)
			rectangleRadioMenuItem[3].setOnAction((actionEvent) -> {
				currentShape = Shape.ROUND_SQUARE;
			});

			// 円
			circleRadioMenuItem[0].setOnAction((actionEvent) -> {
				currentShape = Shape.CIRCLE;
			});
			// 楕円
			circleRadioMenuItem[1].setOnAction((actionEvent) -> {
				currentShape = Shape.OVAL;
			});
			// 円弧
			circleRadioMenuItem[2].setOnAction((actionEvent) -> {
				currentShape = Shape.ARC;
			});

			// 直線
			lineRadioMenuItem[0].setOnAction((actionEvent) -> {
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
				switch(currentShape) {
					case Shape.RECT:
						shape = new Rect();
						break;

					case Shape.SQUARE:
						shape = new Square();
						break;

					case Shape.ROUND_RECT:
						shape = new RoundRect();
						break;

					case Shape.ROUND_SQUARE:
						shape = new RoundSquare();
						break;

					case Shape.CIRCLE:
						shape = new Circle();
						break;

					case Shape.OVAL:
						shape = new Oval();
						break;

					case Shape.ARC:
						shape = new Arc();
						break;

					case Shape.LINE:
						shape = new Line();
						break;
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

				shape.draw(canvas.getGraphicsContext2D());
			});

			shapeList = new ArrayList<Shape>();
			currentShape = Shape.RECT;
			currentColor = Color.BLUE;
			colorPicker.setValue(currentColor);
			rectangleRadioMenuItem[0].setSelected(true);
			circleRadioMenuItem[0].setSelected(true);
			lineRadioMenuItem[0].setSelected(true);
			x1 = -1;
			y1 = -1;
			x2 = -1;
			y2 = -1;

			primaryStage.setScene(new Scene(borderPane, 600, 400));
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
