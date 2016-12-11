package haxweb.jnewznab.poc;

import java.util.LinkedHashMap;

public class NNTPHeader extends LinkedHashMap<String, String> {

	private static final long serialVersionUID = -5743566371377696L;
	
	public static final String SUBJECT 		= "subject";
	public static final String NEWSGROUP 	= "newsgroup";
	public static final String ARTICLEID 	= "articleId";
	public static final String AUTHOR 		= "author";
	public static final String DATE 		= "date";
	public static final String MESSAGE_ID	= "message-id";
	public static final String REFERENCES	= "references";
	public static final String BYTE_COUNT	= "byte-count";
	public static final String LINE_COUNT	= "line-count";
	
}
