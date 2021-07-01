package net.yura.domination.engine;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * when using this to save, the game is saved in the new format
 */
public class RiskObjectOutputStream extends ObjectOutputStream {

	public RiskObjectOutputStream(OutputStream out) throws IOException {
		super(out);
	}

}
