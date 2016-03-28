package vajracode.calocal.shared;

import vajracode.calocal.shared.constants.HTMLConstants;


public class FieldVerifier {	

	public static boolean isOkString(String s) {
		return s != null && s.length() > 0;
	}

	public static boolean isNumber(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String stripHtmlTags(String html) {	
		return html.replaceAll("(<([^>]+)>)", "");
	}

	public static String sanitizeUserInput(String text) {			
		return convertNToBr(stripHtmlTags(text));
	}

	private static String convertNToBr(String s) {
		return s.replaceAll("(\r\n|\n\r|\r|\n)", HTMLConstants.BR);
	}

	
	public static String cleanItemTitle(String string, String error) {
		return ensureNotEmpty(truncate(string, 255), error);
	}

	private static String ensureNotEmpty(String string, String error) {
		if (string.length() == 0)
			throw new RuntimeException(error);
		return string;
	}

	private static String clean(String string, int length) {
		return truncate(sanitizeUserInput(string), length);		
	}

	public static String truncate(String s, int length) {
		return s.length() > length ? s.substring(0, length) : s;
	}

	public static String cleanItemDescription(String string) {
		return clean(string, 10000);
	}

	public static String cleanComment(String string) {
		if (string == null)
			return null;
		return clean(string, 10000);
	}

	public static String convertBrToN(String s) {
		return s.replaceAll("(<br>|<br/>)", "\n");
	}

	public static void checkName(String string) {
		if (string.length() < 3 || string.length() > 20 || hasNonLetters(string))
			throw new RuntimeException("Login has incorrect length or symbols"); //TODO FieldException
	}
	
	public static void checkPass(String string) {
		if (string.length() < 3 || string.length() > 255 || hasNonLetters(string))
			throw new RuntimeException("Password has incorrect length or symbols");
	}

	private static boolean hasNonLetters(String string) {		
        return string.matches("^[a-zA-Z0-9]*$");
	}

	public static void checkPassEqual(String pass, String passConfirm) {
		if (!pass.equals(passConfirm))
			throw new RuntimeException("Passwords do not match");
	}

}
