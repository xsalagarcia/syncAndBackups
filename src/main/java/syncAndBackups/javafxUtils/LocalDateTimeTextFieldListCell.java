package syncAndBackups.javafxUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import syncAndBackups.MainClass;


/**
 * Extends TextFieldListCell for a {@code ListView}.
 * Sets the StringConverter with {@code MainClass.DATE_TIME_PATTERN}.
 * @author xsala
 *
 */
public class LocalDateTimeTextFieldListCell extends TextFieldListCell<LocalDateTime> {

	public LocalDateTimeTextFieldListCell() {
		super(new StringConverter<LocalDateTime>() {

			@Override
			public String toString(LocalDateTime object) {
				
				return object.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
			}

			@Override
			public LocalDateTime fromString(String string) {
					//not necessary
				return null;
			}
		});
	}

}
