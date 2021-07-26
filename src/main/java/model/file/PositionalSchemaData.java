package model.file;

import java.awt.*;
import java.util.List;

/**
 * Created by tmn7 on 9/21/18.
 */
public class PositionalSchemaData {

    private final List<Point> myFieldPositions;
    private final List<String> myColumnNames;
    private final Integer DUMMY_Y_VALUE;

    public PositionalSchemaData(final List<Point> fieldPositions, final List<String> columnNames, final Integer dummyYValue) {
        myFieldPositions = fieldPositions;
        myColumnNames = columnNames;
        DUMMY_Y_VALUE = dummyYValue;
    }

    public List<Point> getMyFieldPositions() {
        return myFieldPositions;
    }
    public List<String> getMyColumnNames() {
        return myColumnNames;
    }
    public Integer getDUMMY_Y_VALUE() { return DUMMY_Y_VALUE; }
}
