/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.translation;

public class Languages {
    public String[] languagesNamesFull;
    public String[] languagesNamesShort;

    public Languages(String[] languagesNamesFull, String[] languagesNamesShort) {
        this.languagesNamesFull = languagesNamesFull;
        this.languagesNamesShort = languagesNamesShort;
    }

//    public String getFullName(int position) {
//        if (languagesNamesFull == null) {
//            return null;
//        }
//        return position >= languagesNamesFull.length ? null : languagesNamesFull[position];
//    }
//
//    public String getShortName(int position) {
//        if (languagesNamesShort == null) {
//            return null;
//        }
//        return position >= languagesNamesShort.length ? null : languagesNamesShort[position];
//    }

}
