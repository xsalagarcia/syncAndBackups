package syncAndBackups.javafxUtils;

import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * SVG icons from 
 * @author xsala
 *
 */
public enum SVGIcons {
	SETTINGS("m19.4 44-1-6.3q-.95-.35-2-.95t-1.85-1.25l-5.9 2.7L4 30l5.4-3.95q-.1-.45-.125-1.025Q9.25 24.45 9.25 24q0-.45.025-1.025T9.4 21.95L4 18l4.65-8.2 5.9 2.7q.8-.65 1.85-1.25t2-.9l1-6.35h9.2l1 6.3q.95.35 2.025.925Q32.7 11.8 33.45 12.5l5.9-2.7L44 18l-5.4 3.85q.1.5.125 1.075.025.575.025 1.075t-.025 1.05q-.025.55-.125 1.05L44 30l-4.65 8.2-5.9-2.7q-.8.65-1.825 1.275-1.025.625-2.025.925l-1 6.3ZM24 30.5q2.7 0 4.6-1.9 1.9-1.9 1.9-4.6 0-2.7-1.9-4.6-1.9-1.9-4.6-1.9-2.7 0-4.6 1.9-1.9 1.9-1.9 4.6 0 2.7 1.9 4.6 1.9 1.9 4.6 1.9Zm0-3q-1.45 0-2.475-1.025Q20.5 25.45 20.5 24q0-1.45 1.025-2.475Q22.55 20.5 24 20.5q1.45 0 2.475 1.025Q27.5 22.55 27.5 24q0 1.45-1.025 2.475Q25.45 27.5 24 27.5Zm0-3.5Zm-2.2 17h4.4l.7-5.6q1.65-.4 3.125-1.25T32.7 32.1l5.3 2.3 2-3.6-4.7-3.45q.2-.85.325-1.675.125-.825.125-1.675 0-.85-.1-1.675-.1-.825-.35-1.675L40 17.2l-2-3.6-5.3 2.3q-1.15-1.3-2.6-2.175-1.45-.875-3.2-1.125L26.2 7h-4.4l-.7 5.6q-1.7.35-3.175 1.2-1.475.85-2.625 2.1L10 13.6l-2 3.6 4.7 3.45q-.2.85-.325 1.675-.125.825-.125 1.675 0 .85.125 1.675.125.825.325 1.675L8 30.8l2 3.6 5.3-2.3q1.2 1.2 2.675 2.05Q19.45 35 21.1 35.4Z"),
	TRASH("M13.05 42q-1.25 0-2.125-.875T10.05 39V10.5H8v-3h9.4V6h13.2v1.5H40v3h-2.05V39q0 1.2-.9 2.1-.9.9-2.1.9Zm21.9-31.5h-21.9V39h21.9Zm-16.6 24.2h3V14.75h-3Zm8.3 0h3V14.75h-3Zm-13.6-24.2V39Z"),
	COPY("M9 43.95q-1.2 0-2.1-.9-.9-.9-.9-2.1V10.8h3v30.15h23.7v3Zm6-6q-1.2 0-2.1-.9-.9-.9-.9-2.1v-28q0-1.2.9-2.1.9-.9 2.1-.9h22q1.2 0 2.1.9.9.9.9 2.1v28q0 1.2-.9 2.1-.9.9-2.1.9Zm0-3h22v-28H15v28Zm0 0v-28 28Z"),
	DIFFERENCE("M24.5 21.7h3v-4.2h4.2v-3h-4.2v-4.2h-3v4.2h-4.2v3h4.2Zm-4.2 7.8h11.4v-3H20.3ZM13 40q-1.2 0-2.1-.9-.9-.9-.9-2.1V5q0-1.2.9-2.1.9-.9 2.1-.9h17.4L42 13.6V37q0 1.2-.9 2.1-.9.9-2.1.9Zm0-3h26V14.9L28.9 5H13v32Zm-6 9q-1.2 0-2.1-.9Q4 44.2 4 43V12.05h3V43h24.9v3Zm6-9V5v32Z"),
	SYNC("M8.35 40v-3h6.5l-.75-.6q-3.2-2.55-4.65-5.55-1.45-3-1.45-6.7 0-5.3 3.125-9.525Q14.25 10.4 19.35 8.8v3.1q-3.75 1.45-6.05 4.825T11 24.15q0 3.15 1.175 5.475 1.175 2.325 3.175 4.025l1.5 1.05v-6.2h3V40Zm20.35-.75V36.1q3.8-1.45 6.05-4.825T37 23.85q0-2.4-1.175-4.875T32.75 14.6l-1.45-1.3v6.2h-3V8h11.5v3h-6.55l.75.7q3 2.8 4.5 6t1.5 6.15q0 5.3-3.1 9.55-3.1 4.25-8.2 5.85Z"),
	WARNING("M2 42 24 4l22 38Zm5.2-3h33.6L24 10Zm17-2.85q.65 0 1.075-.425.425-.425.425-1.075 0-.65-.425-1.075-.425-.425-1.075-.425-.65 0-1.075.425Q22.7 34 22.7 34.65q0 .65.425 1.075.425.425 1.075.425Zm-1.5-5.55h3V19.4h-3Zm1.3-6.1Z");
	
	String svg;
	SVGIcons(String svg) {
		this.svg = svg;
	}
	
	public Region getRegion(int width, int height, Color color) {
		Region r = new Region();
		r.setPrefSize(width, height);
		SVGPath s = new SVGPath();
		s.setContent(svg);
		r.setShape(s);
		r.setBackground(new Background(new BackgroundFill(color, null, null)));
		return r;
	}
	public Button getButton (int width, int height, Color color) {
		Button b = new Button();
		b.setPrefSize(width, height);
		SVGPath s = new SVGPath();
		s.setContent(svg);
		b.setShape(s);
		
		b.setBackground(new Background(new BackgroundFill(color, null, null)));
		return b;
	}
}
