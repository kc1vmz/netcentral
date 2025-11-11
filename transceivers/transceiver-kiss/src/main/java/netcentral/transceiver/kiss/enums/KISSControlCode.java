package netcentral.transceiver.kiss.enums;

public enum KISSControlCode {
	PRINTABLE((byte)0xFF),
	UNKNOWN((byte)0xFE),
	FEND((byte)0xC0),
	DATA((byte)0x00),
	UI((byte)0x03),
	NO_L3((byte)0xF0);
	
	private byte value;

    KISSControlCode(byte value){
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
