/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2010 - 2022 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    FastRfUtils.java
 *    Copyright (C) 1999-2004 University of Waikato, Hamilton, NZ (original
 *      code, Utils.java )
 *    Copyright (C) 2008 Fran Supek (adapted code)
 */

package hr.irb.fastRandomForest;

import java.util.Random;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Utility functions for sorting float (single-precision) arrays, and for
 * normalizing double arrays. Adapted from weka.core.Utils, version 1.57.
 *
 * @author Eibe Frank - original code
 * @author Yong Wang - original code
 * @author Len Trigg - original code
 * @author Julien Prados - original code
 * @author Fran Supek (fran.supek[AT]irb.hr) - adapted code
 */
public final class FastRfUtils {

  private FastRfUtils() throws InstantiationException {
      throw new InstantiationException("This class is not created for instantiation");
  }

  /**
   * Sorts a given array of floats in ascending order and returns an
   * array of integers with the positions of the elements of the
   * original array in the sorted array. NOTE THESE CHANGES: the sort
   * is no longer stable and it doesn't use safe floating-point
   * comparisons anymore. Occurrences of Double.NaN behave unpredictably in
   * sorting.
   *
   * @param array this array is not changed by the method!
   *
   * @return an array of integers with the positions in the sorted
   *         array.
   */
  public static /*@pure@*/ int[] sort(/*@non_null@*/ float[] array) {
    int[] index = new int[array.length];
    for (int i = 0; i < index.length; i++)
      index[i] = i;
    array = array.clone();
    quickSort(array, index, 0, array.length - 1);
    return index;
  }


  /**
   * Partitions the instances around a pivot. Used by quicksort and
   * kthSmallestValue.
   *
   * @param array the array of doubles to be sorted
   * @param index the index into the array of doubles
   * @param l     the first index of the subset
   * @param r     the last index of the subset
   *
   * @return the index of the middle element
   */
  private static int partition(float[] array, int[] index, int l, int r) {

    double pivot = array[index[(l + r) / 2]];
    int help;

    while (l < r) {
      while ((array[index[l]] < pivot) && (l < r)) {
        l++;
      }
      while ((array[index[r]] > pivot) && (l < r)) {
        r--;
      }
      if (l < r) {
        help = index[l];
        index[l] = index[r];
        index[r] = help;
        l++;
        r--;
      }
    }
    if ((l == r) && (array[index[r]] > pivot)) {
      r--;
    }

    return r;
  }


  /**
   * Implements quicksort according to Manber's "Introduction to
   * Algorithms".
   *
   * @param array the array of doubles to be sorted
   * @param index the index into the array of doubles
   * @param left  the first index of the subset to be sorted
   * @param right the last index of the subset to be sorted
   */
  //@ requires 0 <= first && first <= right && right < array.length;
  //@ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] && index[i] < array.length);
  //@ requires array != index;
  //  assignable index;
  private static void quickSort(/*@non_null@*/ float[] array, /*@non_null@*/ int[] index,
                                int left, int right) {

    if (left < right) {
      int middle = partition(array, index, left, right);
      quickSort(array, index, left, middle);
      quickSort(array, index, middle + 1, right);
    }
  }


  /**
   * Normalizes the doubles in the array by their sum.
   * <p>
   * If supplied an array full of zeroes, does not modify the array.
   * </p>
   *
   * @param doubles the array of double
   *
   * @throws IllegalArgumentException if sum is NaN
   */
  public static void normalize(double[] doubles) {

    double sum = 0;
    for (int i = 0; i < doubles.length; i++) {
      sum += doubles[i];
    }
    normalize(doubles, sum);
  }


  /**
   * Normalizes the doubles in the array using the given value.
   * <p>
   * If supplied an array full of zeroes, does not modify the array.
   * </p>
   *
   * @param doubles the array of double
   * @param sum     the value by which the doubles are to be normalized
   *
   * @throws IllegalArgumentException if sum is zero or NaN
   */
  private static void normalize(double[] doubles, double sum) {

    if (Double.isNaN(sum)) {
      throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
    }
    if (sum == 0) {
      return;
    }
    for (int i = 0; i < doubles.length; i++) {
      doubles[i] /= sum;
    }
  }

  /**
   * Produces a random permutation using Knuth shuffle.
   *
   * @param numElems the size of the permutation
   * @param rng      the random number generator
   *
   * @return a random permutation
   */
  public static int[] randomPermutation(int numElems, Random rng) {

    int[] permutation = new int[numElems];

    for (int i = 0; i < numElems; i++)
      permutation[i] = i;

    for (int i = 0; i < numElems - 1; i++) {
      int next = rng.nextInt(numElems);
      int tmp = permutation[i];
      permutation[i] = permutation[next];
      permutation[next] = tmp;
    }

    return permutation;
  }

  /**
   * Produces a random permutation of the values of an attribute in a dataset using Knuth shuffle.
   * <p>
   * Copies back the current values of the previously scrambled attribute and uses the given permutation
   * to scramble the values of the new attribute all by copying from the original dataset.
   * </p>
   *
   * @param src      the source dataset
   * @param dst      the scrambled dataset
   * @param attIndex the attribute index
   * @param perm     the random permutation
   *
   * @return fluent
   */
  public static Instances scramble(Instances src, Instances dst, final int attIndex, int[] perm) {

    for (int i = 0; i < src.numInstances(); i++) {

      Instance scrambled = dst.instance(i);

      if (attIndex > 0)
        scrambled.setValue(attIndex - 1, src.instance(i).value(attIndex - 1));
      scrambled.setValue(attIndex, src.instance(perm[i]).value(attIndex));
    }

    return dst;
  }

  /**
   * Load a dataset into memory.
   *
   * @param location the location of the dataset
   *
   * @return the dataset
   */
  public static Instances readInstances(String location) throws Exception {
    Instances data = new weka.core.converters.ConverterUtils.DataSource(location).getDataSet();
    if (data.classIndex() == -1)
      data.setClassIndex(data.numAttributes() - 1);
    return data;
  }
}
