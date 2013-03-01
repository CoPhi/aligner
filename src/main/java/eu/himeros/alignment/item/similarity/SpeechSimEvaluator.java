/*
 * This file is part of eu.himeros_speechaligner_jar_1.0-SNAPSHOT
 *
 * Copyright (C) 2013 federico[DOT]boschetti[DOT]73[AT]gmail[DOT]com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.himeros.alignment.item.similarity;

import eu.himeros.alignment.core.SimEvaluator;
import eu.himeros.alignment.item.model.Speech;

/**
 *
 * @author federico[DOT]boschetti[DOT]73[AT]gmail[DOT]com
 */
public class SpeechSimEvaluator extends SimEvaluator<Speech> {
    private int originMaxLength;
    private int translMaxLength;

    public int getOriginMaxLength() {
        return originMaxLength;
    }

    public void setOriginMaxLength(int originMaxLength) {
        this.originMaxLength = originMaxLength;
    }

    public int getTranslMaxLength() {
        return translMaxLength;
    }

    public void setTranslMaxLength(int translMaxLength) {
        this.translMaxLength = translMaxLength;
    }
    
    @Override
    public double eval(Speech originSp, Speech translSp){
        double originLen;
        double translLen;
        if(originSp.getSpeakerId()!=translSp.getSpeakerId()) return 0;
        try{originLen=((double)originSp.getLength())/((double)originMaxLength);}catch(Exception ex){originLen=0;}
        try{translLen=((double)translSp.getLength())/((double)translMaxLength);}catch(Exception ex){translLen=0;}
        double diff=Math.abs(originLen-translLen);
        return 1-diff;
    }
}
