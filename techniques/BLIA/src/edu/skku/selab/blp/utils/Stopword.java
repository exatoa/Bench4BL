/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */

package edu.skku.selab.blp.utils;

import java.util.TreeSet;

public class Stopword
{

    public Stopword()
    {
    }

    // For checking source file term
    public static boolean isJavaKeyword(String word)
    {
        boolean isKeyword = JAVA_KEYWORDS_STOP_WORD_SET.contains(word);
        return isKeyword;
    }
    
    public static boolean isProjectKeyword(String word)
    {
    	// for experiment
//    	return false;
    	
        boolean isProjectKeyword = PROJECT_KEYWORDS_STOP_WORD_SET.contains(word);
        return isProjectKeyword;
    }

    // For checking source file term and bug report
    public static boolean isEnglishStopword(String word)
    {
        boolean isEnglishStopword = ENG_STOP_WORDS_SET.contains(word);
        return isEnglishStopword;
    }

    private static final TreeSet<String> JAVA_KEYWORDS_STOP_WORD_SET;
    private static final TreeSet<String> PROJECT_KEYWORDS_STOP_WORD_SET;
    private static final TreeSet<String> ENG_STOP_WORDS_SET;

    static 
    {
    	// References
    	// http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
    	// http://en.wikipedia.org/wiki/List_of_Java_keywords#Reserved_words_for_literal_values
        String javaKeywords[] = {
                "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", 
                "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", 
                "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", 
                "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", 
                "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", 
                "false", "true", "null" };
        
        JAVA_KEYWORDS_STOP_WORD_SET = new TreeSet<String>();
        for(int i = 0; i < javaKeywords.length; i++)
        {
            String word = javaKeywords[i].trim().toLowerCase();
            word = Stem.stem(word);
            JAVA_KEYWORDS_STOP_WORD_SET.add(word);
        }
        
        String projectKeywords[] = {
                "args", "method", "main", "param",
                "aspectj", "swt", "eclipse", "zxing", "string", "java", "org", "javadoc"
                };
        
        PROJECT_KEYWORDS_STOP_WORD_SET = new TreeSet<String>();
        for(int i = 0; i < projectKeywords.length; i++)
        {
            String word = projectKeywords[i].trim().toLowerCase();
            word = Stem.stem(word);
            PROJECT_KEYWORDS_STOP_WORD_SET.add(word);
        }

        String EngStopWord[] = {
            "a", "a's", "able", "about", "above", "according", "accordingly", "across", "actually", "after", 
            "afterwards", "again", "against", "ain't", "all", "allow", "allows", "almost", "alone", "along", 
            "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", 
            "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", 
            "appreciate", "appropriate", "are", "aren't", "around", "as", "aside", "ask", "asking", "associated", 
            "at", "available", "away", "awfully", "b", "be", "became", "because", "become", "becomes", 
            "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", 
            "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", 
            "c's", "came", "can", "can't", "cannot", "cant", "cause", "causes", "certain", "certainly", 
            "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", 
            "contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "currently", "d", "definitely", 
            "described", "despite", "did", "didn't", "different", "do", "does", "doesn't", "doing", "don't", 
            "done", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "either", 
            "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", 
            "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", 
            "few", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", 
            "forth", "four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", 
            "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", 
            "hadn't", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "he's", 
            "hello", "help", "hence", "her", "here", "here's", "hereafter", "hereby", "herein", "hereupon", 
            "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", 
            "however", "i", "i'd", "i'll", "i'm", "i've", "ie", "if", "ignored", "immediate", 
            "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", 
            "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's", "its", "itself", 
            "j", "just", "k", "keep", "keeps", "kept", "know", "knows", "known", "l", 
            "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", 
            "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "m", "mainly", 
            "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", 
            "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely", "nd", 
            "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", 
            "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", 
            "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", 
            "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", 
            "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", 
            "own", "p", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", 
            "presumably", "probably", "provides", "q", "que", "quite", "qv", "r", "rather", "rd", 
            "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "s", 
            "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", 
            "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", 
            "seriously", "seven", "several", "shall", "she", "should", "shouldn't", "since", "six", "so", 
            "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", 
            "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t", 
            "t's", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", 
            "that", "that's", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", 
            "there", "there's", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", 
            "they'd", "they'll", "they're", "they've", "think", "third", "this", "thorough", "thoroughly", "those", 
            "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", 
            "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", 
            "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", 
            "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various", 
            "very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", 
            "we", "we'd", "we'll", "we're", "we've", "welcome", "well", "went", "were", "weren't", 
            "what", "what's", "whatever", "when", "whence", "whenever", "where", "where's", "whereafter", "whereas", 
            "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's", 
            "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", 
            "without", "won't", "wonder", "would", "would", "wouldn't", "x", "y", "yes", "yet", 
            "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "z", 
            "zero"
        };
        ENG_STOP_WORDS_SET = new TreeSet<String>();
        for(int i = 0; i < EngStopWord.length; i++)
        {
            String word = EngStopWord[i].toLowerCase().trim();
            word = Stem.stem(word);
            ENG_STOP_WORDS_SET.add(word);
        }
    }
}
