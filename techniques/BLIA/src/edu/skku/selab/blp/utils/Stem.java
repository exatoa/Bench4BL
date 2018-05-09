/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */

package edu.skku.selab.blp.utils;


// Referenced classes of package utils:
//            PorterStemmer

public class Stem
{

    public Stem()
    {
    }

    public static String stem(String word)
    {
        stemmer.reset();
        stemmer.stem(word);
        return stemmer.toString();
    }

    private static PorterStemmer stemmer = new PorterStemmer();

}
