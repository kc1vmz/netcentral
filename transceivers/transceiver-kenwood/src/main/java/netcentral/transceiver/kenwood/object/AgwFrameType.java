package netcentral.transceiver.kenwood.object;

public class AgwFrameType {
    public static final char GET_VERSION = 'R';
    public static final char REGISTER_CALLSIGN = 'X';
    public static final char UNREGISTER_CALLSIGN = 'x';
    public static final char GET_PORT = 'G';
    public static final char GET_PORT_CAPABILITIES = 'g';
    public static final char GET_HEARD_CALLSIGNS = 'H';
    public static final char ENABLE_RECEPTION = 'm';
    public static final char INFORMATION = 'T';
    public static final char ENABLE_RAW_RECEPTION = 'k';
}
