import java.io.Serializable;
import java.lang.Comparable;

public class BoardPost implements Serializable, Comparable{
	String text, author;
	long time;

	/*public BoardPost(String aut, String txt){ //for blog post creation
		author = aut;
		text = txt;
		time = System.currentTimeMillis();
	}*/

	public BoardPost(String aut, String txt, long tme){
		author = aut;
		text = txt;
		time = tme;
	}

	public String toString(){
		return text+" - "+author+" ("+time+")";
	}

	public boolean equals(Object o){
		BoardPost p = null;
		if (o instanceof BoardPost)
			p = (BoardPost)o;
		else
			return false;
		return p.author.equals(author) && p.text.equals(text) && p.time==time;
	}

	public int hashCode(){
		return 0;
	}

	public int compareTo(Object o){
		if (!(o instanceof BoardPost))
			return 0;
		BoardPost p = (BoardPost)o;
		if (p.time < time)
			return 1;
		else if (p.time > time)
			return -1;
		return 0;
	}
}
