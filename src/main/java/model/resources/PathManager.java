package model.resources;

/**
 * Created by tmn7 on 6/29/18.
 */
public class PathManager {


//	private final static String POSITIONAL_SCHEMA_PATH = "C:\\INNOVATION\\cafe\\positional_schemas\\";
//	private final static String DELIMITED_HEADER_PATH = "C:\\INNOVATION\\cafe\\delimited_headers\\";
//	private static final String PROPERTIES_FILE_PATH = "C:\\INNOVATION\\cafe\\properties\\app_properties.ser";

    private static final String POSITIONAL_SCHEMA_PATH = "/home/tmn7/Documents/file_master/positional_schemas/";
    private static final String DELIMITED_HEADER_PATH = "/home/tmn7/Documents/file_master/delimited_headers/";
    private static final String PROPERTIES_FILE_PATH = "/home/tmn7/Documents/cafe/properties/app_properties.ser";

    public static String getPositionalSchemaPath() { return POSITIONAL_SCHEMA_PATH; }
    public static String getDelimitedHeaderPath() { return DELIMITED_HEADER_PATH; }
    public static String getPropertiesFilePath() { return PROPERTIES_FILE_PATH; }
}
