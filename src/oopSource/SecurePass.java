package oopSource;

import org.mindrot.jbcrypt.BCrypt;

import oopSource.SecurePass;


public class SecurePass {
	public boolean passChecker(String password, String hash) {
		
		
		
		boolean check = BCrypt.checkpw(password, hash);
		
		return check;
		
	}
	
	public String convPass(String password) {
		
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		
		
		return hashed;
	}
	
	
	public static void main (String[] args) {
		
		SecurePass salt = new SecurePass();
		
		
		String pass = "juan@246";
		String wrongpass = "banana";
		
		
		
		String hashed = salt.convPass(pass);
		
		System.out.println(hashed);
		
		System.out.println(salt.passChecker(pass, hashed));
		
		System.out.println(salt.passChecker(wrongpass, hashed));
		
	}
	
}