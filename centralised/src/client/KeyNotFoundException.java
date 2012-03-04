/**
 * @author csong
 */
package client;

import java.io.IOException;

public class KeyNotFoundException extends IOException {
	private static final long serialVersionUID = 1L;
	
	public KeyNotFoundException(){
		this("Bad key");
	}
	
	public KeyNotFoundException(String s){
		super(s);
	}
}
