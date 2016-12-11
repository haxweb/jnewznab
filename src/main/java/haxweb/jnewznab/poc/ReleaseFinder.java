package haxweb.jnewznab.poc;

public class ReleaseFinder {

	public static void main(String[] args) {
		try {
			System.out.println(ReleaseFile.buildFromHeader("[5/5] - \"Desperate Housewives S07E21 HDTV XviD-LOL .part5.rar\" yEnc (102/210)"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}
