/*
 * Copyright © 2009 Perseus Project - Tufts University <http://www.perseus.tufts.edu>
 *
 * This file is part of UniCollatorPerseus.
 *
 * AlignmentPerseus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * AlignmentPerseus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with AlignmentPerseus.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.himeros.alignment.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Aligner for generic Objects.
 *
 * @author Federico Boschetti <federico.boschetti.73@gmail.com>
 */
public class ObjectAligner<T> extends Aligner {
    protected ArrayList<T> vect1 = new ArrayList<>(100);
    protected ArrayList<T> vect2 = new ArrayList<>(100);
    //protected Object[][] alignedObjects = null;

    /**
     * Default Constructor.
     */
    public ObjectAligner() {
        super();
    }
    
    public T[][] align(List<T> objs1, List<T> objs2, Class<T> clazz){
        return align((T[])objs1.toArray(),(T[])objs2.toArray(), clazz);
    }

    /**
     * Align two object arrays.
     *
     * @param objs1 the first object array.
     * @param objs2 the second object array.
     * @return the aligned arrays.
     */
    public T[][] align(T[] objs1, T[] objs2, Class<T> clazz) {
        vect1 = new ArrayList<>(100);
        vect2 = new ArrayList<>(100);
        //Similarity matrix
        double[][] simMatrix = new double[objs1.length][objs2.length];
        double[][] matrix = new double[objs1.length + 1][objs2.length + 1];
        for (int i = 0; i < objs1.length; i++) {
            for (int j = 0; j < objs2.length; j++) {
                simMatrix[i][j] = simScore(objs1[i], objs2[j]);
            }
        }
        matrix[0][0] = 0;
        //Fill matrix marginals
        for (int i = 1; i <= objs1.length; i++) {
            matrix[i][0] = i * gapPenalty;
        }
        for (int j = 1; j <= objs2.length; j++) {
            matrix[0][j] = j * gapPenalty;
        }
        double scoreDown;
        double scoreRight;
        double scoreDiag;
        double bestScore;
        //Fill matrix
        for (int i = 1; i <= objs1.length; i++) {
            for (int j = 1; j <= objs2.length; j++) {
                scoreDown = matrix[i - 1][j] + gapPenalty;
                scoreRight = matrix[i][j - 1] + gapPenalty;
                scoreDiag = matrix[i - 1][j - 1] + simMatrix[i - 1][j - 1];
                bestScore = Math.max(Math.max(scoreDown, scoreRight), scoreDiag);
                matrix[i][j] = bestScore;
            }
        }
        //Backtrack the path
        int i = objs1.length, j = objs2.length;
        double score = 0, scoreLeft = 0, scoreDiagInv = 0;
        while (i > 0 && j > 0) {
            score = matrix[i][j];
            scoreDiagInv = matrix[i - 1][j - 1];
            scoreLeft = matrix[i - 1][j];
            if (score == scoreDiagInv + simMatrix[i - 1][j - 1]) {
                makeAlignment(objs1[i - 1], objs2[j - 1]);
                i = i - 1;
                j = j - 1;
            } else if (score == scoreLeft + gapPenalty) {
                makeAlignment(objs1[i - 1], null);
                i = i - 1;
            } else {
                makeAlignment(null, objs2[j - 1]);
                j = j - 1;
            }
        }
        while (i > 0) {
            makeAlignment(objs1[i - 1], null);
            i = i - 1;
        }
        while (j > 0) {
            makeAlignment(null, objs2[j - 1]);
            j = j - 1;
        }
        return makeResult(clazz);
    }

    /**
     * Evaluate the similarity between objects.
     *
     * @param obj1 the first object to evaluate.
     * @param obj2 the second object to evaluate.
     * @return the similarity score.
     */
    //TODO transpose the content of this method inside the eval method of an IntegerSimEvaluator!!!
    protected double simScore(T obj1, T obj2) {
        //if ((obj1 instanceof Integer) && (obj2 instanceof Integer)) {
        //    double db1 = Double.parseDouble(obj1.toString());
        //    double db2 = Double.parseDouble(obj2.toString());
        //    if (db1 == db2) {
        //        return 1.0;
        //    }
        //    double diff = Math.abs(db1 - db2);
        //    if (diff >= maxDiff) {
        //        return 0.0;
        //    } else {
        //        return 1 - (diff / maxDiff);
        //    }
        //}
        return simEval.eval(obj1, obj2);
    }

    /**
     * Add objects to the result vectors, aligning them with the suitable null object gaps.
     *
     * @param obj1 the first object (possibly null).
     * @param obj2 the second object (possibly null).
     */
    private void makeAlignment(T obj1, T obj2) {
        vect1.add(obj1);
        vect2.add(obj2);
    }

    /**
     * Make the rusult arrays.
     * @return the result arrays.
     */
    private T[][] makeResult(Class<T> clazz) {
        T[][] alignedObjects=(T[][])Array.newInstance(clazz,2,vect1.size());
        Collections.reverse(vect1);
        Collections.reverse(vect2);
        vect1.toArray(alignedObjects[0]);
        vect2.toArray(alignedObjects[1]);
        return alignedObjects;
    }
}
